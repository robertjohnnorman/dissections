package dev.robertjohnnorman.dissections
package data

import cats.{Bifunctor, Functor}
import cats.implicits.*
import polynomials.all.*

import cats.data.{Const, EitherK, Tuple2K}

/*
Sum_2[
  Const_2[Nothing, ?, ?],
  Sum_2[
    Product_2[
      Const_2[Nothing, ?, ?],
      Joker[S, ?, ?],
    ?, ?],
    Product_2[
      Clown[R, ?, ?],
      Const_2[Unit, ?, ?],
    ?, ?]
  ],
?, ?]
*/

//case class NeListF_2[A, N, M](a: A)
type GenericNeListF_2[Z] =
  [A, B] =>> Fst[[a] =>> Const[Z, a], A, B]


object GenericNeListF_2:

  given derivesBifunctor[Z]: Bifunctor[GenericNeListF_2[Z]] with
    override def bimap[A, B, C, D](fab: GenericNeListF_2[Z][A, B])(f: A => C, g: B => D): GenericNeListF_2[Z][C, D] =
      Fst(Const(fab.fa.getConst))


  def dissect[A]: Dissectable[GenericNeListF[A]] =
    new Dissectable[GenericNeListF[A]]:

      type Q[a, b] = GenericNeListF_2[A][a, b]

      override def into[C, J](pj: GenericNeListF[A][J]): Result[C, J, GenericNeListF[A], GenericNeListF_2[A]] = pj.run match
        case Left(a) => Result.Done(EitherK.left(Const(a.getConst)))
        case Right(Tuple2K(ac, bc)) => Result.More(bc, Fst(Const(ac.getConst)))


      override def next[C, J](qcj: GenericNeListF_2[A][C, J], c: C): Result[C, J, GenericNeListF[A], GenericNeListF_2[A]] =
        Result.Done(EitherK.right(Tuple2K(qcj.fa, c)))

