package dev.robertjohnnorman.dissections
package instances.dissect

import data.Result
import polynomials.all.*
import polynomials.bifunctors

import cats.Bifunctor

object Tuple2:

  given dissectable[A]: Dissectable[[x] =>> (A, x)] with

    type Q = [a, b] =>> Const_2[A, a, b]

    override def into[C, J](pj: (A, J)): Result[C, J, [x] =>> (A, x), Q] =
      Result.More(pj._2, Const_2[A, C, J](pj._1))

    override def next[C, J](qcj: Q[C, J], c: C): Result[C, J, [x] =>> (A, x), Q] =
      Result.Done((qcj.a, c))

