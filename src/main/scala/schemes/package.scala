package dev.robertjohnnorman.dissections

import data.*

import cats.data.Nested
import cats.Monad
import cats.syntax.all.*
import cats.implicits.*
import higherkindness.droste.{kernel as _, *}

package object schemes {

  object all:
    export Folds.*
    export Unfolds.*
    export Refolds.*

  object kernel:

    private[dissections] def hylo[F[_]: Dissectable, A, B](
      algebra: F[B] => B,
      coalgebra: A => F[A]
    ): A => B =
      val dissectable = Dissectable[F] // TODO: Is bound name really required for path-dependant type?

      def go(stack: List[dissectable.Q[B, A]], current: Result[B, A, F, dissectable.Q]): B =
        (stack, current) match
          case (Nil, Result.Done(value)) =>
            algebra(value)
          case (head :: tail, Result.Done(value)) =>
            go(tail, dissectable.next(head, algebra(value)))
          case (currentStack, Result.More(joker, dissection)) =>
            go(dissection :: currentStack, dissectable.into(coalgebra(joker)))

      (a: A) => go(Nil, dissectable.into(coalgebra(a)))


    private[dissections] def hyloM[M[_]: Monad, F[_]: Dissectable, A, B](
      algebraM: F[B] => M[B],
      coalgebraM: A => M[F[A]]
    ): A => M[B] =
      val dissectable = Dissectable[F]
      
      (a: A) => coalgebraM(a).flatMap { pa =>
        Monad[M].tailRecM((List.empty[dissectable.Q[B, A]], dissectable.into[B, A](pa))) {
          case (Nil, Result.Done(value)) =>
            Monad[M].map(algebraM(value))(Right.apply)
          case (head :: tail, Result.Done(value)) =>
            Monad[M].map(algebraM(value)) { b => Left((tail, dissectable.next(head, b))) }
          case (currentStack, Result.More(joker, dissection)) =>
            coalgebraM(joker).map { pa => Left((dissection :: currentStack, dissectable.into(pa))) }
        }
      }

    @inline private[dissections] def hyloC[F[_], G[_], A, B](using dissectF: Dissectable[F], dissectG: Dissectable[G])(
      algebra: F[G[B]] => B,
      coalgebra: A => F[G[A]]
    ): A => B =
      hylo[[x] =>> Nested[F, G, x], A, B](algebra.compose(_.value), coalgebra.andThen(Nested.apply))

    // TODO: @inline?
    private[dissections] def hyloMC[M[_]: Monad, F[_], G[_], A, B](using dissectF: Dissectable[F], dissectG: Dissectable[G])(
      algebraM: F[G[B]] => M[B],
      coalgebraM: A => M[F[G[A]]]
    ): A => M[B] =
      hyloM[M, [x] =>> Nested[F, G, x], A, B](algebraM.compose(_.value), coalgebraM.andThen(_.map(Nested.apply)))

}
