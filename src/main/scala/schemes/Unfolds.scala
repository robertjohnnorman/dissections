package dev.robertjohnnorman.dissections
package schemes

import data.{CVAlgebraM, CVCoalgebraM, Result}

import cats.implicits.*
import cats.syntax.*
import cats.{Bifunctor, Functor, Monad, ~>}
import higherkindness.droste.{kernel as _, *}
import higherkindness.droste.data.prelude.*
import higherkindness.droste.data.{Attr, AttrF, Coattr, Mu, Nu}
import higherkindness.droste.prelude.*
import higherkindness.droste.syntax.all.*
import higherkindness.droste.syntax.embed.*

object Unfolds:

  def ana[F[_]: Dissectable, R, A](using embed: Embed[F, R])(
    coalgebra: Coalgebra[F, A]
  ): A => R =
    kernel.hylo(embed.algebra.run, coalgebra.run)

  def anaM[M[_] : Monad, F[_]: Dissectable, R, A](using embed: Embed[F, R])(
    coalgebraM: CoalgebraM[M, F, A]
  ): A => M[R] =
    kernel.hyloM(embed.algebra.lift[M].run, coalgebraM.run)

  def apo[F[_]: Dissectable, R, A](using embed: Embed[F, R])(
    coalgebra: RCoalgebra[R, F, A]
  ): A => R =
    kernel.hyloC(using Dissectable[F], instances.dissect.Either.dissectable[R])(
      embed.algebra.run.compose((frr: F[(R Either R)]) => frr.map(_.merge)),
      coalgebra.run
    )

  def apoM[M[_] : Monad, F[_]: Dissectable, R, A](using embed: Embed[F, R])(
    coalgebraM: RCoalgebraM[R, M, F, A]
  ): A => M[R] =
    kernel.hyloMC(using Dissectable[F], instances.dissect.Either.dissectable[R])(
      embed.algebra.lift[M].run.compose((frr: F[(R Either R)]) => frr.map(_.merge)),
      coalgebraM.run
    )

  def futu[F[_]: Dissectable, R, A](using embed: Embed[F, R])(
    coalgebra: CVCoalgebra[F, A]
  ): A => R =
    kernel
      .hylo[F, Coattr[F, A], R](
        embed.algebra.run,
        _.fold(coalgebra.run, identity)
      )
      .compose(Coattr.pure)

  def futuM[M[_] : Monad, F[_]: Dissectable, R, A](using embed: Embed[F, R])(
    coalgebraM: CVCoalgebraM[M, F, A]
  ): A => M[R] =
    kernel
      .hyloM[M, F, Coattr[F, A], R](
        embed.algebra.lift[M].run,
        _.fold(coalgebraM.run, Monad[M].pure)
      )
      .compose(Coattr.pure)

  def postpro[F[_]: Dissectable, R, A](using embed: Embed[F, R])(
    nt: F ~> F,
    coalgebra: Coalgebra[F, A]
  ): A => R =
    kernel.hylo(
      embed.algebra.run,
      coalgebra.run andThen nt.apply
    )

