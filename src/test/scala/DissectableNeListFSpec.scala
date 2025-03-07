package dev.robertjohnnorman.dissections

import algebras.TransAlgebras
import data.*
import data.all.*
import polynomials.all.*
import syntax.all.*
//import syntax.debug.all.*

import cats.Monad
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import higherkindness.droste.data.Mu
import higherkindness.droste.data.list.{ConsF, ListF, NilF}
import higherkindness.droste.{Algebra, AlgebraM, data as drosteData, *}
import org.scalatest.freespec.{AnyFreeSpec, AsyncFreeSpec}
import org.scalatest.matchers.should.Matchers

class DissectableNeListFSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers:

  "Dissect" - {
    "should run an Algebra as expected" in {

      val input: Mu[[a] =>> NeListF[Int, a]] =
        Mu(
          NeListF.NeConsF(
            1,
            Mu(
              NeListF.NeConsF(
                2,
                Mu(
                  NeListF.NeLastF(3)
                )
              )
            )
          )
        )

      val expected: String = "123"

      val strAlg: Algebra[[a] =>> NeListF[Int, a], String] = Algebra:
        case NeListF.NeLastF(value) => value.toString
        case NeListF.NeConsF(head, tail) => s"${head}${tail}"

          

      val actual: String =
        input
          .cata(using NeListF.derivesDissect[Int])
          .apply(strAlg)


      actual shouldEqual expected
    }
  }

