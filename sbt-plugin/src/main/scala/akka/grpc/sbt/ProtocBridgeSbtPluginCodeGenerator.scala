/*
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

package akka.grpc.sbt

import protocbridge.Artifact

/**
 * Adapts existing [[akka.grpc.gen.CodeGenerator]] into the protocbridge required type
 */
class ProtocBridgeSbtPluginCodeGenerator(impl: akka.grpc.gen.CodeGenerator) extends protocbridge.ProtocCodeGenerator {
  override def run(request: Array[Byte]): Array[Byte] = impl.run(request)
  override def suggestedDependencies: Seq[Artifact] = impl.suggestedDependencies
  override def toString = s"ProtocBridgeSbtPluginCodeGenerator(${impl.name}: $impl)"
}
