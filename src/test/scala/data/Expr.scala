package dev.robertjohnnorman.dissections
package data

import cats.Functor
import cats.derived.*
import higherkindness.droste.data.Fix


type Expr = Fix[Expr.ExprF]

object Expr:
  
  enum ExprF[+A] derives Functor:
    case Val(value: Int)
    case Add(left: A, right: A)

