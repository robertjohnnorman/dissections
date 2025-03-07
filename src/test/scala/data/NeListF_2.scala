package dev.robertjohnnorman.dissections
package data

import cats.{Bifunctor, Functor}
import cats.derived.*
import cats.implicits.*

import polynomials.all.*


case class NeListF_2[A, N, M](a: A)

object NeListF_2:

  given derivesBiFunctor[Z]: Bifunctor[[a, b] =>> NeListF_2[Z, a, b]] with
    override def bimap[A, B, C, D](fab: NeListF_2[Z, A, B])(f: A => C, g: B => D): NeListF_2[Z, C, D] =
      NeListF_2(fab.a)





