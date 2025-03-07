package dev.robertjohnnorman.dissections

import data.*
import syntax.all.*
//import syntax.debug.all.*

import cats.{Functor, Monad}
import cats.effect.IO
import cats.implicits.*
import cats.syntax.*
import cats.effect.testing.scalatest.AsyncIOSpec
import higherkindness.droste.data.Mu
import higherkindness.droste.data.Nu
import higherkindness.droste.{Algebra, AlgebraM, data as drosteData, *}
import higherkindness.droste.{Trans, TransM}
import higherkindness.droste.data.list.*
import higherkindness.droste.implicits.*
import higherkindness.droste.prelude.*
import higherkindness.droste.syntax.all.*
import org.scalatest.freespec.{AnyFreeSpec, AsyncFreeSpec}
import org.scalatest.matchers.should.Matchers
import algebras.TransAlgebras

import polynomials.functors.all.*

class DissectableSyntaxSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers:

  "Dissect" - {
    "should run transCata as expected" in {

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

      val expected: Mu[[a] =>> ListF[Int, a]] =
        Mu(ConsF(1, Mu(ConsF(2, Mu(ConsF(3, Mu(NilF)))))))


        
      val alg: Trans[[a] =>> NeListF[Int, a], [a] =>> ListF[Int, a], Mu[[a] =>> ListF[Int, a]]] = TransAlgebras.transNeListToList[Int]

      // TODO: Reintroduce
      //val actual: Mu[[a] =>> ListF[Int, a]] =
      //  input
      //    .transCata(using NeListF.derivesDissect[Int],ListF_2.derivesDissect[Int])
      //    .apply(alg)
      val actual = None

      println(input)
      println(actual)
      actual shouldEqual expected
    }

    // TODO: This wasn't working before re-factor, so dont worry about it yet.
    "should run transAna as expected" in {

      val input: Nu[[a] =>> ListF[Int, a]] =
        Nu(
          ConsF(
            1,
            Nu(
              ConsF(
                2,
                Nu(
                  ConsF(
                    3,
                    Nu(NilF)
                  )
                )
              )
            )
          )
        )

      val expected: Option[Nu[[a] =>> NeListF[Int, a]]] =
        Some(
          Nu(
            NeListF.NeConsF(
              1,
              Nu(
                NeListF.NeConsF(
                  2,
                  Nu(
                    NeListF.NeLastF(3)
                  )
                )
              )
            )
          )
        )

      def transListToNeList[A]: TransM[Option, [a] =>> ListF[Int, a], [a] =>> NeListF[Int, a], Nu[[a] =>> ListF[Int, a]]] = TransM {
        case ConsF(head, Nu(tail)) => tail match {
          case NilF => Some(NeListF.NeLastF(head))
          case _ => Some(NeListF.NeConsF(head, Nu(tail)))
        }
        case NilF => None
      }

      val coalg: TransM[Option, [a] =>> ListF[Int, a], [a] =>> NeListF[Int, a], Nu[[a] =>> ListF[Int, a]]] =
        transListToNeList[Int]


      // TODO Investiate why amgibous without using clsue.
      //val actual: Option[Nu[[a] =>> NeListF[Int, a]]] =
      //  input
      //    .transAnaM(using ListF_2.derivesDissect[Int], NeListF.derivesDissect[Int])
      //    .apply(coalg)
      val actual = None

      println(input)
      println(actual)
      actual shouldEqual expected
    }


  }

