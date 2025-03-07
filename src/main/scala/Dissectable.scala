package dev.robertjohnnorman.dissections

import cats.implicits.*
import cats.syntax.*
import cats.{Bifunctor, Foldable, Functor, Id, Traverse, ~>}
import data.all.*
import polynomials.all.*

import cats.data.{Const, EitherK, Nested, Tuple2K}
import dev.robertjohnnorman
import dev.robertjohnnorman.dissections

import scala.annotation.tailrec
import scala.compiletime.summonInline


trait Dissectable[P[_]] extends Functor[P] { self =>

  type Q[_, _]

  def into[C, J](pj: P[J]): Result[C, J, P, Q]

  def next[C, J](qcj: Q[C, J], c: C): Result[C, J, P, Q]


  override def map[A, B](fa: P[A])(f: A => B): P[B] =

    @tailrec
    def go(result: Result[B, A, P, Q]): P[B] = result match
      case Result.Done(value) => value
      case Result.More(joker, dissection) => go(next[B, A](dissection, f(joker)))

    go(into[B, A](fa))

}

object Dissectable:

  import DissectOps.*

  @inline def apply[P[_]](using instance: Dissectable[P]): Dissectable[P] = instance

  def from[P[_], S[_,_]: Bifunctor](template: DissectTemplate[P, S]): Dissectable[P] =
    new Dissectable[P] {
      type Q = [a, b] =>> S[a, b]

      override def into[C, J](pj: P[J]): Result[C, J, P, Q] =
        template.init(pj)

      override def next[C, J](qcj: S[C, J], c: C): Result[C, J, P, Q] =
        template.next(qcj, c)

    }

  given const[A]: Dissectable[[a] =>> Const[A, a]] with
    type Q = [x, y] =>> Const_2[Nothing, x, y]

    override def into[C, J](pj: Const[A, J]): Result[C, J, [a] =>> Const[A, a], Q] =
      Result.Done[C, J, [a] =>> Const[A, a], Q](Const(pj.getConst))

    override def next[C, J](qcj: Q[C, J], c: C): Result[C, J, [a] =>> Const[A, a], Q] =
      throw new Exception("This really _really_ should never be reachable...")


  given id: Dissectable[Id] with
    type Q = [A, B] =>> Const_2[Unit, A, B]

    override def into[C, J](pj: Id[J]): Result[C, J, Id, Q] =
      Result.More[C, J, Id, Q](pj, Const_2[Unit, C, J](()))

    override def next[C, J](qcj: Const_2[Unit, C, J], c: C): Result[C, J, Id, Q] =
      Result.Done[C, J, Id, Q](c)


  given sum[R[_], S[_]](using rDissect: Dissectable[R], sDissect: Dissectable[S]): Dissectable[[Z] =>> EitherK[R, S, Z]]() with
    type Q = [a, b] =>> EitherK_2[rDissect.Q, sDissect.Q, a, b]

    override def into[C, J](pj: EitherK[R, S, J]): Result[C, J, [Z] =>> EitherK[R, S, Z], Q] = pj.run match
      case Left(pa: R[J]) =>
        mindp(using rDissect, sDissect)(rDissect.into(pa))
      case Right(qa: S[J]) =>
        mindq(using rDissect, sDissect)(sDissect.into(qa))


    override def next[C, J](qcj: EitherK_2[rDissect.Q, sDissect.Q, C, J], c: C): Result[C, J, [Z] =>> EitherK[R, S, Z], Q] = qcj match
      case EitherK_2.SumL_2(pab: rDissect.Q[C, J]) =>
        mindp(using rDissect, sDissect)(rDissect.next(pab, c))
      case EitherK_2.SumR_2(qab: sDissect.Q[C, J]) =>
        mindq(using rDissect, sDissect)(sDissect.next(qab, c))


  given product[R[_], S[_]](using rDissect: Dissectable[R], sDissect: Dissectable[S]): Dissectable[[Z] =>> Tuple2K[R, S, Z]] with
    type Q = [a, b] =>> EitherK_2[
      [c, d] =>> Tuple2K_2[rDissect.Q, [e, f] =>> Joker[S, e, f], c, d],
      [c, d] =>> Tuple2K_2[[e,f] =>> Clown[R, e, f], sDissect.Q, c, d],
      a,
      b
    ]

    override def into[C, J](pj: Tuple2K[R, S, J]): Result[C, J, [Z] =>> Tuple2K[R, S, Z], Q] =
      mindpd(using rDissect, sDissect)(rDissect.into(pj.first), pj.second)

    override def next[C, J](qcj: Q[C, J], c: C): Result[C, J, [Z] =>> Tuple2K[R, S, Z], Q] = qcj match
      case EitherK_2.SumL_2(value: Tuple2K_2[rDissect.Q, [e, f] =>> Joker[S, e, f], C, J]) =>
        val joker: Joker[S, C, J] = value.qab

        mindpd(using rDissect, sDissect)(rDissect.next(value.pab, c), joker.gb)

      case EitherK_2.SumR_2(qab: Tuple2K_2[[e, f] =>> Clown[R, e, f], sDissect.Q, C, J]) =>
        val clown: Clown[R, C, J] = qab.pab

        mindqd(using rDissect, sDissect)(sDissect.next(qab.qab, c), clown.fa)


  given nested[F[_], G[_]](using fDissect: Dissectable[F], gDissect: Dissectable[G]): Dissectable[[x] =>> Nested[F, G, x]] with
    type Q = [a, b] =>> Tuple2K_2[
      [c, d] =>> Nested_2[
        [e, f] =>> fDissect.Q[e, f],
        [g] =>> G[g],
        c,
        d
      ],
      [e, f] =>> gDissect.Q[e, f],
      a,
      b
    ]

    override def into[C, J](pj: Nested[F, G, J]): Result[C, J, [x] =>> Nested[F, G, x], Q] =

      @tailrec
      def pump(joker: G[J], dissection: fDissect.Q[G[C], G[J]]): Result[C, J, [x] =>> Nested[F, G, x], Q] =
        gDissect.into[C, J](joker) match
          case Result.More(jokerPrime, dissectionPrime) =>
            Result.More(jokerPrime, Tuple2K_2(Nested_2(dissection), dissectionPrime))
          case Result.Done(value: G[C]) =>
            fDissect.next[G[C], G[J]](dissection, value) match
              case Result.Done(fg)      => Result.Done(Nested(fg))
              case Result.More(gj, gd)  => pump(gj, gd)

      fDissect.into[G[C], G[J]](pj.value) match
        case Result.Done(value: F[G[C]]) =>
          Result.Done(Nested(value))
        case Result.More(joker: G[J], dissection) =>
          pump(joker, dissection)


    override def next[C, J](qcj: Q[C, J], c: C): Result[C, J, [x] =>> Nested[F, G, x], Q] =
      val Tuple2K_2(cfg @ Nested_2(fg), gd) = qcj

      @tailrec
      def finish(gc: G[C]): Result[C, J, [x] =>> Nested[F, G, x], Q] =
        fDissect.next[G[C], G[J]](fg, gc) match
          case Result.Done(f)       => Result.Done(Nested(f))
          case Result.More(gj, gd)  => gDissect.into[C, J](gj) match
            case Result.Done(gc)          => finish(gc)
            case Result.More(j, gdPrime)  => Result.More(j, Tuple2K_2(Nested_2(gd), gdPrime))


      gDissect.next[C, J](gd, c) match
        case Result.Done(gc)    => finish(gc)
        case Result.More(j, gd) => Result.More(j, Tuple2K_2(cfg, gd))











