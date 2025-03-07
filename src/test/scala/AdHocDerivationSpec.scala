package dev.robertjohnnorman.dissections

import data.*
import polynomials.all.*

import cats.*
import cats.derived.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*
import cats.syntax.all.*
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import shapeless3.deriving.{Id as ShapelessId, *}
import shapeless3.typeable.Typeable
import shapeless3.deriving.Const

import scala.compiletime.*
import scala.deriving.*


class AdHocDerivationSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers:


  case class ConstInt(value: Int)
  case class ConstA[A](value: A)

  val exprF: Expr.ExprF[Int] = Expr.ExprF.Val(1)

  import K2.{*, given}

  //inline def getTypeLabel[A[_, _]](using gen: K2.Generic[A]): String =
  //  constValue[Typeable[NeListF_2.dissect.Q]]

  val x: K1.Kind[Mirror, ConstA] {type MirroredElemLabels <: Tuple} = summon[K1.Generic[ConstA] {type MirroredElemLabels <: Tuple}]

  val mirror = summon[ Mirror { type MirroredType[T] = ConstA[T] }]

  val prodGen: K1.Kind[Mirror.Product, ConstA] = K1.ProductGeneric[ConstA]

  //val m = summon[Mirror.Of[ConstA[Int]]]
  //val mMono = summon[Typeable[m.MirroredMonoType]].describe

  //val z = summon[Typeable[m.MirroredElemTypes]].describe

  val m2 = summon[K1.Generic[ConstA]]

  val m = m2

  val m2Labels = summon[Typeable[m2.MirroredElemLabels]].describe

  val m2Types = summon[Typeable[m2.MirroredElemTypes[Nothing]]].describe

  inline def convert[T <: Tuple]: List[Any] =
    inline erasedValue[T] match
      case _: (h *: t)    => constValue[h] :: convert[t]
      case _: EmptyTuple  => Nil



  //val z2 = summon[Typeable[m2.MirroredElemTypes]]

  //val a = prodGen.toRepr(MyType.MyConst(1))


  //val b = a.map[Id] { [T] => (t: T) => Id(t) }


  //val gen: K2.Generic[NeListF_2.dissect.Q] = summon[K2.Generic[NeListF_2.dissect.Q]]
  //val x = Typeable[NeListF_2.dissect.Q[Int, String]]

  //val m   = summon[Mirror.Of[NeListF_2.dissect.Q[Int, String]]]
  //val x = summon[Typeable[m.MirroredElemTypes]].describe

  "AdHocDerivationTest" - {
    "should run" in {
      for
        _ <- IO.println("Hello World")
        //_ <- IO.println(x)
        //_ <- IO.println(prodGen)
        //_ <- IO.println(mMono)
        //_ <- IO.println(z)
        _ <- IO.println(m2)
        _ <- IO.println(m2Labels)
        _ <- IO.println(m2Types)
        //_ <- IO.println(z1)
        //_ <- IO.println(z2)
        //_ <- IO.println(mirror)
        //_ <- IO.println(dShow)
        //_ <- IO.println(of[NeListF_2.dissect.Q[Int, String]])
      yield () shouldEqual ()
    }
    // Expected
    // Sum_2[Const_2[Nothing, ?, ?], Sum_2[Product_2[Const_2[Nothing, ?, ?], Joker[S, ?, ?], ?, ?], Product_2[Clown[R, ?, ?], Const_2[Unit, ?, ?], ?, ?]], ?, ?]
  }
