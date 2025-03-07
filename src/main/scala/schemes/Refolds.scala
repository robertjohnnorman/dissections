package dev.robertjohnnorman.dissections
package schemes

import data.all.*

import cats.Monad
import cats.implicits.*
import higherkindness.droste.{kernel as _, *}
import higherkindness.droste.data.prelude.*
import higherkindness.droste.data.{Attr, Coattr}

object Refolds:

  def hylo[F[_]: Dissectable, A, B](
    algebra: Algebra[F, B], 
    coalgebra: Coalgebra[F, A]
  ): A => B =
    kernel.hylo(algebra.run, coalgebra.run)
    
  def hyloM[M[_]: Monad, F[_]: Dissectable, A, B](
    algebraM: AlgebraM[M, F, B], 
    coalgebraM: CoalgebraM[M, F, A]
  ): A => M[B] =
    kernel.hyloM(algebraM.run, coalgebraM.run)

  def dyna[F[_]: Dissectable, A, B](
    algebra: CVAlgebra[F, B], 
    coalgebra: Coalgebra[F, A]
  ): A => B =
    kernel
      .hylo[F, A, Attr[F, B]](
        fb => Attr(algebra(fb), fb),
        coalgebra.run
      )
      .andThen(_.head)

  def dynaM[M[_] : Monad, F[_]: Dissectable, A, B](
    algebraM: CVAlgebraM[M, F, B], 
    coalgebraM: CoalgebraM[M, F, A]
  ): A => M[B] =
    kernel
      .hyloM[M, F, A, Attr[F, B]](
        fb => Monad[M].map(algebraM(fb))(Attr(_, fb)),
        coalgebraM.run
      )
      .andThen(_.map(_.head))

  def codyna[M[_] : Monad, F[_]: Dissectable, A, B](
    algebra: Algebra[F, B], 
    coalgebra: CVCoalgebra[F, A]
  ): A => B = 
    kernel
      .hylo[F, Coattr[F, A], B](
        algebra.run,
        _.fold(coalgebra.run, identity)
      )
      .compose(Coattr.pure)

  def codynaM[M[_] : Monad, F[_]: Dissectable, A, B](
    algebraM: AlgebraM[M, F, B], 
    coalgebraM: CVCoalgebraM[M, F, A]
  ): A => M[B] =
    kernel
      .hyloM[M, F, Coattr[F, A], B](
        algebraM.run,
        _.fold(coalgebraM.run, Monad[M].pure)
      )
      .compose(Coattr.pure)

  def chrono[F[_]: Dissectable, A, B](
    algebra: CVAlgebra[F, B], 
    coalgebra: CVCoalgebra[F, A]
  ): A => B =
    kernel
      .hylo[F, Coattr[F, A], Attr[F, B]](
        fb => Attr(algebra(fb), fb),
        _.fold(coalgebra.run, identity)
      )
      .andThen(_.head)
      .compose(Coattr.pure)

  def chronoM[M[_] : Monad, F[_]: Dissectable, A, B](
    algebraM: CVAlgebraM[M, F, B], 
    coalgebraM: CVCoalgebraM[M, F, A]
  ): A => M[B] =
    kernel
      .hyloM[M, F, Coattr[F, A], Attr[F, B]](
        fb => Monad[M].map(algebraM(fb))(Attr(_, fb)),
        _.fold(coalgebraM.run, Monad[M].pure)
      )
      .andThen(_.map(_.head))
      .compose(Coattr.pure)
