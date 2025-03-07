package dev.robertjohnnorman.dissections

import polynomials.all.*
import data.all.*

import cats.data.{EitherK, Tuple2K}

private[dissections] object DissectOps {

  def mindp[C, J, P[_], R[_]](
    using pDissect: Dissectable[P],
    rDissect: Dissectable[R]
  ): Result[C, J, P, pDissect.Q] => Result[C, J, [X] =>> EitherK[P, R, X], [X, Y] =>> EitherK_2[pDissect.Q, rDissect.Q, X, Y]] =
    case Result.Done(value)             => Result.Done(EitherK.left(value))
    case Result.More(joker, dissection) => Result.More(joker, EitherK_2.SumL_2(dissection))

  def mindq[C, J, P[_], R[_]](
    using pDissect: Dissectable[P],
    rDissect: Dissectable[R]
  ): Result[C, J, R, rDissect.Q] => Result[C, J, [X] =>> EitherK[P, R, X], [X, Y] =>> EitherK_2[pDissect.Q, rDissect.Q, X, Y]] =
    case Result.Done(value)             => Result.Done(EitherK.right(value))
    case Result.More(joker, dissection) => Result.More(joker, EitherK_2.SumR_2(dissection))

  def mindpd[C, J, P[_], R[_]](
    using pDissect: Dissectable[P],
    rDissect: Dissectable[R]
  ): (Result[C, J, P, pDissect.Q], R[J]) =>
    Result[
      C,
      J,
      [X] =>> Tuple2K[P, R, X],
      [X, Y] =>>
        EitherK_2[
          [a, b] =>> Tuple2K_2[pDissect.Q, [c, d] =>> Snd[R, c, d], a, b],
          [a, b] =>> Tuple2K_2[[c, d] =>> Clown[P, c, d], rDissect.Q, a, b],
          X,
          Y
        ]
    ]
  = (inResult: Result[C, J, P, pDissect.Q], rj: R[J]) =>
    inResult match
      case Result.Done(value: P[C])           => mindqd.apply(rDissect.into[C, J](rj), value)
      case Result.More(joker: J, dissection)  => Result.More(joker, EitherK_2.SumL_2(Tuple2K_2(dissection, Joker(rj))))

  def mindqd[C, J, P[_], R[_]](
    using pDissect: Dissectable[P],
    rDissect: Dissectable[R]
  ): (Result[C, J, R, rDissect.Q], P[C]) =>
    Result[
      C,
      J,
      [a] =>> Tuple2K[P, R, a],
      [a, b] =>>
        EitherK_2[
          [d, e] =>> Tuple2K_2[pDissect.Q, [f, g] =>> Joker[R, f, g], d, e],
          [d, e] =>> Tuple2K_2[[f, g] =>> Clown[P, f, g], rDissect.Q, d, e],
          a,
          b
        ]
    ]
  = (inResult: Result[C, J, R, rDissect.Q], pc: P[C]) =>
    inResult match
      case Result.Done(rc) =>
        Result.Done(Tuple2K(pc, rc))
      case Result.More(joker: J, dissection: rDissect.Q[C, J]) =>
        Result.More(joker, EitherK_2.SumR_2(Tuple2K_2(Clown(pc), dissection)))
}
