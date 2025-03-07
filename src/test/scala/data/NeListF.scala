package dev.robertjohnnorman.dissections
package data

import cats.Functor
import cats.derived.*


enum NeListF[A, B] derives Functor:
  case NeLastF(value: A)
  case NeConsF(head: A, tail: B)

object NeListF:

  given derivesDissect[Z]: Dissectable[[a] =>> NeListF[Z, a]] = {
    println(1)
    val template: DissectTemplate[[a] =>> NeListF[Z, a], [a, b] =>> NeListF_2[Z, a, b]] =
      new DissectTemplate[[a] =>> NeListF[Z, a], [a, b] =>> NeListF_2[Z, a, b]] {
        override def init[C, J](pj: NeListF[Z, J]): Result[C, J, [a] =>> NeListF[Z, a], [a, b] =>> NeListF_2[Z, a, b]] = pj match
          case NeListF.NeLastF(value) => Result.Done(NeListF.NeLastF(value))
          case NeListF.NeConsF(head, tail) => Result.More(tail, NeListF_2(head))

        override def next[C, J](qcj: NeListF_2[Z, C, J], c: C): Result[C, J, [a] =>> NeListF[Z, a], [a, b] =>> NeListF_2[Z, a, b]] =
          Result.Done(NeListF.NeConsF(qcj.a, c))
      }

    println(2)

    Dissectable.from(template)
  }
