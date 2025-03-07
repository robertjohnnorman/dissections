package dev.robertjohnnorman.dissections
package data

enum Result[C, J, P[_], Q[_, _]]:
  case Done(value: P[C])
  case More(joker: J, dissection: Q[C, J])
