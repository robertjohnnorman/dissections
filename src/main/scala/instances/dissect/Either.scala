package dev.robertjohnnorman.dissections
package instances.dissect
import data.Result
import polynomials.all.*

import cats.Bifunctor

object Either:

  given dissectable[A]: Dissectable[[x] =>> Either[A, x]] with

    type Q = [a, b] =>> Const_2[Unit, a, b]

    override def into[C, J](pj: Either[A, J]): Result[C, J, [x] =>> Either[A, x], Q] = pj match
      case Left(a)  => Result.Done(Left.apply[A, C](a))
      case Right(j) => Result.More(j, Const_2[Unit, C, J](()))

    override def next[C, J](qcj: Q[C, J], c: C): Result[C, J, [x] =>> Either[A, x], Q] =
      Result.Done(Right(c))
