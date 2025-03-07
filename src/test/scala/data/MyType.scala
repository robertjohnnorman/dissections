package dev.robertjohnnorman.dissections
package data

import cats.Functor
import cats.derived.*

enum MyType[A] derives Functor:
  case MyId(unit: Unit)
  case MyConst(a : A)
