package dev.robertjohnnorman.dissections
package polynomials.bifunctors

import cats.{Functor, Bifunctor}
import cats.implicits.*
import cats.syntax.*

case class Nested_2[F[_, _], G[_], C, J](fgcgj: F[G[C], G[J]])

object Nested_2:

  given derivesBifunctor[F[_, _]: Bifunctor, G[_]: Functor]: Bifunctor[[a, b] =>> Nested_2[F, G, a, b]] with
    override def bimap[A, B, C, D](fab: Nested_2[F, G, A, B])(f: A => C, g: B => D): Nested_2[F, G, C, D] =
      Nested_2(fab.fgcgj.bimap(_.map(f), _.map(g)))



