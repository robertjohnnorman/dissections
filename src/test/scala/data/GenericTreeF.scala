package dev.robertjohnnorman.dissections
package data

import polynomials.all.*

import cats.Id
import cats.Functor
import cats.data.{Const, EitherK, Tuple2K}
import cats.implicits.*

type GenericTreeF =
  [A] =>>
    EitherK[
      [a] =>> Const[Unit, a],
      [a] =>> Tuple2K[
        Id,
        [b] =>> Tuple2K[
          Id,
          Id,
          b
        ],
        a
      ],
      A
    ]

object GenericTreeF:
  
  given derivesFunctor: Functor[GenericTreeF] with
    override def map[A, B](fa: GenericTreeF[A])(f: A => B): GenericTreeF[B] =
      fa.map(f)

  given derivesDissect: Dissectable[GenericTreeF] = {

    val step1: Dissectable[[b] =>> Tuple2K[Id, Id, b]] =
      Dissectable.product[Id, Id](using Dissectable.id, Dissectable.id)

    val step2: Dissectable[[a] =>> Tuple2K[Id, [b] =>> Tuple2K[Id, Id, b], a]] =
      Dissectable.product[Id, [b] =>> Tuple2K[Id, Id, b]](using Dissectable.id, step1)

    val step3: Dissectable[
      [A] =>>
        EitherK[
          [a] =>> Const[Unit, a],
          [a] =>> Tuple2K[
            Id,
            [b] =>> Tuple2K[
              Id,
              Id,
              b
            ],
            a
          ],
          A
        ]
    ] =
      Dissectable.sum[
        [a] =>> Const[Unit, a],
        [a] =>> Tuple2K[
          Id,
          [b] =>> Tuple2K[
            Id,
            Id,
            b
          ],
          a
        ]
      ](using Dissectable.const[Unit], step2)

    val step4: Dissectable[GenericTreeF] = step3

    step4
  }
