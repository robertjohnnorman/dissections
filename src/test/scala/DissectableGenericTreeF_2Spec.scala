package dev.robertjohnnorman.dissections

import cats.Monad
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.derived.*
import cats.derived.auto.functor.given
import data.*
import data.all.*
import polynomials.all.*
import syntax.all.*

import cats.data.{Const, EitherK, Tuple2K}
//import syntax.debug.all.*

import higherkindness.droste.{data as drosteData, *}
import higherkindness.droste.data.Mu
import higherkindness.droste.{Algebra, AlgebraM}
import org.scalatest.freespec.{AnyFreeSpec, AsyncFreeSpec}
import org.scalatest.matchers.should.Matchers

class DissectableGenericTreeF_2Spec extends AsyncFreeSpec with AsyncIOSpec with Matchers:

  "Dissect" - {
    "should run an Algebra as expected" in {

      val myTreeFLeaf: Mu[GenericTreeF] =
        Mu(
          EitherK.left(
            Const(())
          )
        )

      val myTreeFValue: Mu[GenericTreeF] =
        Mu(
          EitherK.right(
            Tuple2K(
              myTreeFLeaf,
              Tuple2K(
                myTreeFLeaf,
                myTreeFLeaf
              )
            )
          )
        )

      val countLeafsGenericTreeFAlg: Algebra[GenericTreeF, Int] = Algebra:
        _.run match
          case Left(unit) => 1
          case Right(Tuple2K(a, Tuple2K(b, c))) => a + b + c

      val counted: Int =
        myTreeFValue
          .cata(using GenericTreeF.derivesDissect)
          .apply(countLeafsGenericTreeFAlg)


      counted shouldEqual 3
    }
  }

