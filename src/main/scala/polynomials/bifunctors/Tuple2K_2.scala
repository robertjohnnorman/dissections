package dev.robertjohnnorman.dissections
package polynomials.bifunctors

import cats.Bifunctor
import cats.implicits.*

case class Tuple2K_2[P[_, _], Q[_, _], A, B](pab: P[A, B], qab: Q[A, B])

object Tuple2K_2:

  given derivesBifunctor[P[_, _]: Bifunctor, Q[_, _]: Bifunctor]: Bifunctor[[a, b] =>> Tuple2K_2[P, Q, a, b]] with
    override def bimap[A, B, C, D](fab: Tuple2K_2[P, Q, A, B])(f: A => C, g: B => D): Tuple2K_2[P, Q, C, D] =
      Tuple2K_2(fab.pab.bimap(f, g), fab.qab.bimap(f, g))

