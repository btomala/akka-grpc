package akka.grpc.internal

import java.util.concurrent.{ CompletableFuture, Executor }
import com.google.common.util.concurrent.{ FutureCallback, Futures, ListenableFuture }
import scala.concurrent.{ ExecutionContext, Future }
import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.annotation.InternalApi
import io.grpc.stub.StreamObserver

/**
 * INTERNAL API
 * Include some helpers to convert types from Channel API to Java JDK API
 */
@InternalApi
object JavaChannelApiHelpers {

  /**
   * INTERNAL API
   *
   * Converts a Guava [[ListenableFuture]] to a Scala [[Future]]
   */
  @InternalApi
  def toCompletableFuture[A](guavaFuture: ListenableFuture[A], ec: ExecutionContext): CompletableFuture[A] = {

    val cf = new CompletableFuture[A]
    val callback = new FutureCallback[A] {
      override def onFailure(t: Throwable): Unit = cf.completeExceptionally(t)
      override def onSuccess(a: A): Unit = cf.complete(a)
    }

    val javaExecutor = ec match {
      case exc: Executor => exc // Akka Dispatcher is an Executor
      case _ =>
        new Executor {
          override def execute(command: Runnable): Unit = ec.execute(command)
        }
    }

    Futures.addCallback(guavaFuture, callback, javaExecutor)
    cf
  }

  /**
   * INTERNAL API
   *
   * Builds a akka stream [[Flow]] from a function `StreamObserver[O] => StreamObserver[I]`
   */
  @InternalApi
  def buildFlow[I, O](name: String, operator: StreamObserver[O] => StreamObserver[I]): Flow[I, O, NotUsed] =
    Flow.fromGraph(new AkkaGrpcGraphStage(name, operator))
}
