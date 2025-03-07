package dev.robertjohnnorman.dissections
package schemes

import data.{CVAlgebraM, Result}

import cats.implicits.*
import cats.syntax.*
import cats.{Bifunctor, Functor, Monad, ~>}
import higherkindness.droste.{kernel as _, *}
import higherkindness.droste.data.prelude.*
import higherkindness.droste.data.*
import higherkindness.droste.prelude.*
import higherkindness.droste.syntax.all.*

import scala.annotation.tailrec

object Folds:
  
  def cata[F[_]: Dissectable, R, B](using project: Project[F, R])(
    algebra: Algebra[F, B]
  ): R => B =
    kernel.hylo(algebra.run, project.coalgebra.run)

  def cataM[M[_] : Monad, F[_]: Dissectable, R, B](using project: Project[F, R])(
    algebraM: AlgebraM[M, F, B]
  ): R => M[B] =
    kernel.hyloM(algebraM.run, project.coalgebra.lift[M].run)

  def para[F[_]: Dissectable, R, B](using project: Project[F, R])(
    algebra: RAlgebra[R, F, B]
  ): R => B =
    kernel.hyloC(using Dissectable[F], instances.dissect.Tuple2.dissectable[R])(
      algebra.run,
      project.coalgebra.run.andThen(_.map(r => (r, r)))
    )

  def paraM[M[_] : Monad, F[_]: Dissectable, R, B](using project: Project[F, R])(
    algebraM: RAlgebraM[R, M, F, B]
  ): R => M[B] =
    kernel.hyloMC(using Dissectable[F], instances.dissect.Tuple2.dissectable[R])(
      algebraM.run,
      project.coalgebra.lift[M].run.andThen(_.map(_.map(r => (r, r))))
    )

  def histo[F[_]: Dissectable, R, B](using project: Project[F, R])(
    algebra: CVAlgebra[F, B]
  ): R => B =
    kernel
      .hylo[F, R, Attr[F, B]](
        fb => Attr(algebra(fb), fb),
        project.coalgebra.run
      )
      .andThen(_.head)

  def histoM[M[_] : Monad, F[_]: Dissectable, R, B](using project: Project[F, R])(
    algebraM: CVAlgebraM[M, F, B]
  ): R => M[B] =
    kernel
      .hyloM[M, F, R, Attr[F, B]](
        fb => Functor[M].map(algebraM(fb))(Attr(_, fb)),
        project.coalgebra.lift[M].run
      )
      .andThen(_.map(_.head))

  def prepro[F[_]: Dissectable, R, B](using project: Project[F, R])(
    nt: F ~> F,
    algebra: Algebra[F, B]
  ): R => B =
    kernel.hylo(
      nt.apply[B] andThen algebra.run,
      project.coalgebra.run
    )

  def zygo[F[_]: Dissectable, R, A, B](using project: Project[F, R])(
    algebra: Algebra[F, A],
    rAlgebra: RAlgebra[A, F, B]
  ): R => B =
    kernel
      .hylo[F, R, (A, B)](
        fab => (algebra.run(fab.map(_._1)), rAlgebra.run(fab)),
        project.coalgebra.run
      )
      .andThen(_._2)

  def zygoM[M[_]: Monad, F[_]: Dissectable, R, A, B](using project: Project[F, R])(
    algebraM: AlgebraM[M, F, A],
    rAlgebraM: RAlgebraM[A, M, F, B]
  ): R => M[B] =
    kernel
      .hyloM[M, F, R, (A, B)](
        fab => algebraM.run(fab.map(_._1)).product(rAlgebraM.run(fab)),
        project.coalgebra.lift[M].run
      )
      .andThen(_.map(_._2))

  def mutu[F[_]: Dissectable, R, A, B](using project: Project[F, R])(
    lAlgebra: RAlgebra[B, F, A],
    rAlgebra: RAlgebra[A, F, B]
  ): R => B =
    kernel
      .hylo[F, R, (A, B)](
        fab => (lAlgebra.run(fab.map(_.swap)), rAlgebra.run(fab)),
        project.coalgebra.run
      )
      .andThen(_._2)

  def mutuM[M[_] : Monad, F[_]: Dissectable, R, A, B](using project: Project[F, R])(
    lAlgebraM: RAlgebraM[B, M, F, A],
    rAlgebraM: RAlgebraM[A, M, F, B]
  ): R => M[B] =
    kernel
      .hyloM[M, F, R, (A, B)](
        fab => Monad[M].product(lAlgebraM.run(fab.map(_.swap)), rAlgebraM.run(fab)),
        project.coalgebra.lift[M].run
      )
      .andThen(_.map(_._2))
