package dev.robertjohnnorman.dissections
package polynomials.bifunctors

import cats.Bifunctor

final case class Const_2[A, B, C](a: A)

object Const_2:
  
  given derivesBifunctor[X]: Bifunctor[[A, B] =>> Const_2[X, A, B]] with
    override def bimap[A, B, C, D](fab: Const_2[X, A, B])(f: A => C, g: B => D): Const_2[X, C, D] =
      Const_2(fab.a)
