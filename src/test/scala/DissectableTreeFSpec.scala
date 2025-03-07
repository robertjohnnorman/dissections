package dev.robertjohnnorman.dissections

import data.*
import syntax.all.*
//import syntax.debug.all.*

import cats.syntax.*
import cats.implicits.*
import cats.{Bifunctor, Monad}
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.{AnyFreeSpec, AsyncFreeSpec}
import org.scalatest.matchers.should.Matchers
import higherkindness.droste.{data as drosteData, *}
import higherkindness.droste.data.Mu
import higherkindness.droste.{Algebra, AlgebraM}

class DissectableTreeFSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers:

  "Dissect" - {
    "should run an Algebra as expected" in {
      val testTree: TreeF[TreeF[TreeF[Nothing]]] =
        TreeF.Fork(
          TreeF.Fork(
            TreeF.Leaf,
            TreeF.Leaf,
            TreeF.Leaf,
          ),
          TreeF.Leaf,
          TreeF.Leaf
        )

      val testTreeMu: Mu[TreeF] =
        Mu(
          TreeF.Fork(
            Mu(
              TreeF.Fork(
                Mu(TreeF.Leaf),
                Mu(TreeF.Leaf),
                Mu(TreeF.Leaf),
              ),
            ),
            Mu(TreeF.Leaf),
            Mu(TreeF.Leaf)
          )
        )


      def mapTestTree(): TreeF[Int] =
        testTree
          .map { a =>
            println("HELLO")
            1
          }

      def traverseTestTree(): IO[TreeF[Int]] =
        testTree
          .traverse(using Dissectable[TreeF]){ a =>
            IO.println("HELLO") >> IO.pure(2)
          }

      val countLeafsAlg: Algebra[TreeF, Int] = Algebra:
        case TreeF.Leaf => 1
        case TreeF.Fork(l, m, r) => l + m + r

      val countLeafsLogAlgM: AlgebraM[IO, TreeF, Int] = AlgebraM:
        case TreeF.Leaf => IO.pure(1)
        case TreeF.Fork(l, m, r) =>
          IO.println(s"FORK!: ${l}, ${m}, ${r}").as(l + m + r)


      testTreeMu
        .cataM(using Dissectable[TreeF])
        .apply(countLeafsLogAlgM)
        .map(_ shouldEqual 5)
    }
  }

