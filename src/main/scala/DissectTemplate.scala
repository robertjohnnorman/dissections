package dev.robertjohnnorman.dissections

import data.Result

import cats.{Bifunctor, Functor}

trait DissectTemplate[P[_], Q[_, _] : Bifunctor] {

  def init[C, J](pj: P[J]): Result[C, J, P, Q]

  def next[C, J](qcj: Q[C, J], c: C): Result[C, J, P, Q]
}
