package dev.robertjohnnorman.dissections
package utils

object TypeName:
  import scala.quoted.*
  import scala.reflect.*

  inline def of[A]: String = ${impl[A]}

  def impl[A](using Type[A], Quotes): Expr[String] = {
    import quotes.reflect.*

    val tpe: TypeRepr = TypeRepr.of[A].dealias

    Expr(tpe.show)
  }

  inline def describe2[A[_, _]]: String = ${ describeImpl2[A] }

  def describeImpl2[T[_, _]: Type](using Quotes): Expr[String] = {
    import quotes.reflect.*
    Literal(StringConstant(TypeRepr.of[T].dealias.dealias.show)).asExprOf[String]
  }

