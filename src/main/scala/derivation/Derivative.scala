package dev.robertjohnnorman.dissections
package derivation

import data.Result
import polynomials.bifunctors.Const_2

import cats.data.Const
import cats.{Eval, Foldable, Id}
import higherkindness.droste.data.Fix
import higherkindness.droste.derivation.Derived
import shapeless3.deriving.*
import shapeless3.deriving.K1.LiftP

import scala.compiletime.*
import scala.compiletime.ops.*
import scala.deriving.*

trait Derivative[F[_]] {
  type Q[_, _]
}

//type MyFix[F[_]] = F[Fix[F]]
//
//type MyConst = [A] =>> [x] =>> Const[A, x]
//
////type Differentiate[F[_]] =
////  MyFix[F] match
////    case Id[MyFix[Id]] => [x, y] =>> Const_2[Nothing, x, y]
////    case MyConst[A, Fix[[B] =>> MyConst[B]]] =>
//
//transparent inline def differentiate[F[_]]: Any =
//  inline erasedValue[F] match
//    case _: Id => [x, y] =>> Const_2[Nothing, x, y]
//    case Const[A] =>
//
//object Derivative:
//  
//  given const[A]: Derivative[[a] =>> Const[A, a]] with
//    type Q = [x, y] =>> Const_2[Nothing, x, y]
//
//  given id: Derivative[Id] with
//    type Q = [A, B] =>> Const_2[Unit, A, B]
//
//  given coproduct[T[x[_]] <: Derivative[x], F[_], ElemTypes <: NonEmptyTuple](
//    using inst: K1.CoproductInstances[T, F],
//    m: K1.CoproductGeneric[F] { type MirroredElemTypes = NonEmptyTuple }
//  ): Derivative[F] with
//    val x = inst.
//    type Q = Tuple.Fold[Tail[ElemTypes], Head[ElemTypes]]
//
//
//  //inline def derivative[F[_]](f: F[_]): Derivative[F] =
//  //  inline erasedValue[F] match
//  //    case x: Const[A, F] => const[F]
//
//  //def coproduct[T[x[_]] <: Derivative[x], F[_]](
//  //  using inst: K1.CoproductInstances[T, F],
//  //  mirror: K1.CoproductGeneric[F],
//  //  a : Fold[]
//  //): Derivative[F] = new Derivative[F] {
//  //  type Q = Tuple.Fold[inst.]
//  //}
//
//  inline def summonKindAsList[T <: Tuple, K[_[_]]]: List[K[[X] =>> Any]] =
//    inline erasedValue[T] match
//      case _: EmptyTuple => Nil
//      case _: (t *: ts) =>
//        summonInline[t].asInstanceOf[K[[X] =>> Any]] :: summonKindAsList[ts, K]
//
//
//  trait Is[F[_]]
//
//  //inline def decodeTuple[T <: Tuple]: Derivative[T] =
//  //  inline erasedValue[Is[T]] match // 2
//  //    case _: Is[EmptyTuple] => Decoder.const(EmptyTuple) // 3
//  //    case _: Is[h *: t] => // 4
//  //      val decoder = summonInline[Decoder[h]]
//
//  //      combineDecoders(decoder, decodeTuple[t])
//
//  //def combineDecoders[H, T <: Tuple](dh: Decoder[H], dt: Decoder[T]): Decoder[H *: T] = // 7
//  //  dh.product(dt).map(_ *: _)
//
//  //type MkString[T <: Tuple] =
//  //  Tuple.Fold[
//  //    Tuple.Init[T],
//  //    Tuple.Last[T],
//  //    [a, b] =>> a ++ ", " ++ b
//  //  ] // 2
//  
