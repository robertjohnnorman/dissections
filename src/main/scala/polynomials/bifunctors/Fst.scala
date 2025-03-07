package dev.robertjohnnorman.dissections
package polynomials.bifunctors

import cats.implicits.*
import cats.{Bifunctor, Functor}

case class Fst[F[_], A, B](fa: F[A])

object Fst:

  given derivesBifunctor[G[_]: Functor]: Bifunctor[[a, b] =>> Fst[G, a, b]] with
    override def bimap[A, B, C, D](fab: Fst[G, A, B])(f: A => C, g: B => D): Fst[G, C, D] =
      Fst(fab.fa.map(f))

