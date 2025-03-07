package dev.robertjohnnorman.dissections
package data

import polynomials.all.*

import cats.Bifunctor
import cats.implicits.*

type GenericTreeF_2 = [N, M] =>>
  EitherK_2[
    [a, b] =>> Tuple2K_2[
      [c, d] =>> Const_2[c, d, N],
      [c, d] =>> Const_2[c, d, M],
      a,
      b
    ],
    [a, b] =>> EitherK_2[
      [c, d] =>> Tuple2K_2[
        [e, f] =>> Const_2[e, f, N],
        [e, f] =>> Const_2[e, f, M],
        c,
        d
      ],
      [c, d] =>> Tuple2K_2[
        [e, f] =>> Const_2[e, f, N],
        [e, f] =>> Const_2[e, f, N],
        c,
        d
      ],
      a,
      b
    ],
    N,
    M
  ]

object GenericTreeF_2:

  given derivesBifunctor: Bifunctor[GenericTreeF_2] with
    override def bimap[A, B, C, D](fab: GenericTreeF_2[A, B])(f: A => C, g: B => D): GenericTreeF_2[C, D] =
      fab.bimap(f, g)
