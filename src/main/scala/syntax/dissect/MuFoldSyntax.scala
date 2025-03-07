package dev.robertjohnnorman.dissections
package syntax.dissect

import data.{CVAlgebraM, Result}
import schemes.Folds

import cats.implicits.*
import cats.syntax.*
import cats.{Bifunctor, Functor, Monad, ~>}
import higherkindness.droste.*
import higherkindness.droste.data.prelude.*
import higherkindness.droste.data.{Attr, AttrF, Mu}
import higherkindness.droste.prelude.*
import higherkindness.droste.syntax.all.*

import scala.annotation.tailrec

object MuFoldSyntax {

  // TODO: Redundant?

  // TODO: Do we really want one of these Syntax files for each of the fixed-point operators? I guess it's kinda general already...
  extension [F[_]](muF: Mu[F])

    def cata[B](using dissect: Dissectable[F])(alg: Algebra[F, B]): B =
      Folds
        .cata(using Mu.drosteBasisForMu[F](using dissect))
        .apply(alg)
        .apply(muF)

    def cataM[M[_]: Monad, B](using dissect: Dissectable[F])(algM: AlgebraM[M, F, B]): M[B] =
      Folds
        .cataM(using Mu.drosteBasisForMu[F](using dissect))
        .apply(algM)
        .apply(muF)

    def para[B](using dissect: Dissectable[F])(rAlg: RAlgebra[Mu[F], F, B]): B =
      Folds
        .para(using Mu.drosteBasisForMu[F](using dissect))
        .apply(rAlg)
        .apply(muF)


    def paraM[M[_]: Monad, B](using dissect: Dissectable[F])(rAlgM: RAlgebraM[Mu[F], M, F, B]): M[B] =
      Folds
        .paraM(using Mu.drosteBasisForMu[F](using dissect))
        .apply(rAlgM)
        .apply(muF)

    def histo[B](using dissect: Dissectable[F])(cvAlg: CVAlgebra[F, B]): B =
      Folds
        .histo(using Mu.drosteBasisForMu[F](using dissect))
        .apply(cvAlg)
        .apply(muF)

    def histoM[M[_]: Monad, B](using dissect: Dissectable[F])(cvAlgM: CVAlgebraM[M, F, B]): M[B] =
      Folds
        .histoM(using Mu.drosteBasisForMu[F](using dissect))
        .apply(cvAlgM)
        .apply(muF)

    def prepro[B](using dissect: Dissectable[F])(nt: F ~> F, alg: Algebra[F, B]): B =
      Folds
        .prepro(using Mu.drosteBasisForMu[F](using dissect))
        .apply(nt, alg)
        .apply(muF)

    def zygo[A, B](using dissect: Dissectable[F])(alg: Algebra[F, A], auxAlg: RAlgebra[A, F, B]): B =
      Folds
        .zygo(using Mu.drosteBasisForMu[F](using dissect))
        .apply(alg, auxAlg)
        .apply(muF)

    def zygoM[M[_]: Monad, A, B](using dissect: Dissectable[F])(algM: AlgebraM[M, F, A], auxAlgM: RAlgebraM[A, M, F, B]): M[B] =
      Folds
        .zygoM(using Mu.drosteBasisForMu[F](using dissect))
        .apply(algM, auxAlgM)
        .apply(muF)

    def mutu[A, B](using dissect: Dissectable[F])(lAlg: RAlgebra[B, F, A], rAlg: RAlgebra[A, F, B]): B =
      Folds
        .mutu(using Mu.drosteBasisForMu[F](using dissect))
        .apply(lAlg, rAlg)
        .apply(muF)

    def mutuM[M[_]: Monad, A, B](using dissect: Dissectable[F])(lAlgM: RAlgebraM[B, M, F, A], rAlgM: RAlgebraM[A, M, F, B]): M[B] =
      Folds
        .mutuM(using Mu.drosteBasisForMu[F](using dissect))
        .apply(lAlgM, rAlgM)
        .apply(muF)

}
