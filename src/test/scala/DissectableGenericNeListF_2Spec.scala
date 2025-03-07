package dev.robertjohnnorman.dissections

import data.*
import data.all.*
import polynomials.all.*
import syntax.all.*

import cats.Id
import cats.data.{Const, EitherK, Tuple2K}
//import syntax.debug.all.*

import cats.Monad
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.derived.auto.functor.given 
import dev.robertjohnnorman.dissections.algebras.TransAlgebras
import higherkindness.droste.data.{Fix, Mu}
import higherkindness.droste.data.list.{ConsF, ListF, NilF}
import higherkindness.droste.{Algebra, AlgebraM, data as drosteData, *}
import org.scalatest.freespec.{AnyFreeSpec, AsyncFreeSpec}
import org.scalatest.matchers.should.Matchers

class DissectableGenericNeListF_2Spec extends AsyncFreeSpec with AsyncIOSpec with Matchers:

  "Dissect" - {
    "should run an Algebra as expected" in {

      val input: Mu[GenericNeListF[Int]] =
        Mu(
          EitherK.right(
            Tuple2K(
              Const(1),
              Mu(
                EitherK.right(
                  Tuple2K(
                    Const(2),
                    Mu(
                      EitherK.left(
                        Const(3)
                      )
                      )
                    )
              )
              )
          )
        )
        )
        
      val input2: Mu[GenericNeListF[Int]] =
        Mu(EitherK.left(Const(9)))
        
      val expected: String = "123"

      val strAlg: Algebra[GenericNeListF[Int], String] = Algebra:
        _.run match
          case Left(pa) => pa.toString
          case Right(Tuple2K(a, b)) =>
            s"${a}${b}"


      val actual: String =
        input
          .cata(using GenericNeListF_2.dissect[Int])
          .apply(strAlg)
          //.apply(sumAlg)(using GenericNeListF.derivesFunctor[Int])


      println(actual)

      actual shouldEqual expected
    }

    "should run an Algebra as expected (implicit)" in {

      val input: Mu[GenericNeListF[Int]] =
        Mu(
          EitherK.right(
            Tuple2K(
              Const(1),
              Mu(
                EitherK.right(
                  Tuple2K(
                    Const(2),
                    Mu(
                      EitherK.left(
                        Const(3)
                      )
                    )
                    )
                  )
                )
              )
            )
        )

      val input2: Mu[GenericNeListF[Int]] =
        Mu(EitherK.left(Const(9)))

      val expected: String = "123"

      val strAlg: Algebra[GenericNeListF[Int], String] = Algebra:
        _.run match
          case Left(pa) => pa.toString
          case Right(Tuple2K(a, b)) =>
            s"${a}${b}"


      val actual: String =
        input
          .cata(using Dissectable[GenericNeListF[Int]])
          .apply(strAlg)


      println(actual)

      actual shouldEqual expected
    }
  }

