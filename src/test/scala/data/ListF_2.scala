package dev.robertjohnnorman.dissections
package data

import cats.{Bifunctor, Functor}
import higherkindness.droste.data.list.{ConsF, ListF, NilF}


case class ListF_2[A, N, M](a: A)

object ListF_2:

  given derivesBiFunctor[Z]: Bifunctor[[a, b] =>> ListF_2[Z, a, b]] with
    override def bimap[A, B, C, D](fab: ListF_2[Z, A, B])(f: A => C, g: B => D): ListF_2[Z, C, D] =
      ListF_2(fab.a)

  given derivesDissectable[Z]: Dissectable[[a] =>> ListF[Z, a]] with
    type Q[a, b] = ListF_2[Z, a, b]

    override def into[C, J](pj: ListF[Z, J]): Result[C, J, [a] =>> ListF[Z, a], [a, b] =>> ListF_2[Z, a, b]] = pj match
      case NilF => Result.Done(NilF)
      case ConsF(head, tail) =>
        Result.More(tail, ListF_2(head))

    override def next[C, J](qcj: ListF_2[Z, C, J], c: C): Result[C, J, [a] =>> ListF[Z, a], [a, b] =>> ListF_2[Z, a, b]] =
      Result.Done(ConsF(qcj.a, c))
