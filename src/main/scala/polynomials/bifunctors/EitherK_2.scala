package dev.robertjohnnorman.dissections
package polynomials.bifunctors

import cats.Bifunctor
import cats.implicits.*


enum EitherK_2[P[_, _], Q[_, _], A, B]:
  case SumL_2(pab: P[A, B])
  case SumR_2(qab: Q[A, B])

object EitherK_2:
  
  given derivesBifunctor[P[_, _]: Bifunctor, Q[_, _]: Bifunctor]: Bifunctor[[A, B] =>> EitherK_2[P, Q, A, B]] with
    override def bimap[A, B, C, D](fab: EitherK_2[P, Q, A, B])(f: A => C, g: B => D): EitherK_2[P, Q, C, D] = fab match
      case EitherK_2.SumL_2(pab) => EitherK_2.SumL_2(pab.bimap(f, g))
      case EitherK_2.SumR_2(qab) => EitherK_2.SumR_2(qab.bimap(f, g))
