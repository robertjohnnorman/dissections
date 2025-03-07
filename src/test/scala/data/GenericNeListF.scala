package dev.robertjohnnorman.dissections
package data

import cats.{Functor, Id}
import cats.implicits.*
import polynomials.all.*

import cats.data.*
import cats.derived.auto.functor.given


type GenericNeListF[Z] =
  [A] =>>
    EitherK[
      [a] =>> Const[Z, a],
      [a] =>> Tuple2K[
        [b] =>> Const[Z, b],
        Id,
        a
      ],
      A
    ]


object GenericNeListF:

  import cats.data.EitherK.catsDataFunctorForEitherK


  given derivesFunctor[Z]: Functor[GenericNeListF[Z]] with
    override def map[A, B](fa: GenericNeListF[Z][A])(f: A => B): GenericNeListF[Z][B] =
      fa.map(f)


  // TODO: Work out how to summon non-recursively.
  def dissect[A]: Dissectable[GenericNeListF[A]] = 
    Dissectable[GenericNeListF[A]](using Dissectable[GenericNeListF[A]])
