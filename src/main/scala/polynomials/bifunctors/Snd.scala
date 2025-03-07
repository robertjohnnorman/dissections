package dev.robertjohnnorman.dissections
package polynomials.bifunctors

import cats.implicits.*
import cats.{Bifunctor, Functor}

case class Snd[G[_], A, B](gb: G[B])

object Snd:

  given derivesBifunctor[G[_]: Functor]: Bifunctor[[a, b] =>> Snd[G, a, b]] with
    override def bimap[A, B, C, D](fab: Snd[G, A, B])(f: A => C, g: B => D): Snd[G, C, D] =
      Snd(fab.gb.map(g))
