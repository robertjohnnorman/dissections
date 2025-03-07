package dev.robertjohnnorman.dissections
package data

import cats.Bifunctor

enum TreeF_2[N, M]:
  case ForkRR(m: M, r: M)
  case ForkLR(l: N, r: M)
  case ForkLL(l: N, m: N)

object TreeF_2:
  given Bifunctor[TreeF_2] with
    def bimap[A, B, C, D](fab: TreeF_2[A, B])(f: A => C, g: B => D): TreeF_2[C, D] = fab match
      case TreeF_2.ForkRR(m, r) => TreeF_2.ForkRR(g(m), g(m))
      case TreeF_2.ForkLR(l, r) => TreeF_2.ForkLR(f(l), g(r))
      case TreeF_2.ForkLL(l, m) => TreeF_2.ForkLL(f(l), f(m))


