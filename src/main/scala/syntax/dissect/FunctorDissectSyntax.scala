package dev.robertjohnnorman.dissections
package syntax.dissect

import data.Result

import cats.{Bifunctor, Functor, Monad}

import scala.annotation.tailrec

object FunctorDissectSyntax:

  extension[F[_], A](fa: F[A])(using dissect: Dissectable[F])

    // TODO: Create type-class instead of method?
    // TODO: Applicative?
    def traverse[B, M[_]: Monad](f: A => M[B]): M[F[B]] =
      Monad[M].tailRecM(dissect.into[B, A](fa)) {
        case Result.Done(value) => Monad[M].pure(Right(value))
        case Result.More(joker, dissection) =>
          Monad[M]
            .map(f(joker)) { b =>
              Left(dissect.next(dissection, b))
            }
      }

  // TODO Monad of Applicative?
  extension[F[_]: Functor, M[_]: Monad, A] (fma: F[M[A]])(using Dissectable[F])

    def sequence: M[F[A]] =
      traverse(fma)(identity)

