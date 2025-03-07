package dev.robertjohnnorman.dissections
package data

import cats.Functor
import cats.derived.*

import higherkindness.droste.data.Fix

enum TreeF[+N] derives Functor:
  case Leaf extends TreeF[Nothing]
  case Fork(l: N, m: N, r: N) extends TreeF[N]

object TreeF:
  given derivesDissect: Dissectable[TreeF] =
    Dissectable.from(
      new DissectTemplate[TreeF, TreeF_2]:
        def init[C, J](pj: TreeF[J]): Result[C, J, TreeF, TreeF_2] = pj match
          case TreeF.Leaf => Result.Done(TreeF.Leaf)
          case TreeF.Fork(l, m, r) => Result.More(l, TreeF_2.ForkRR(m, r))

        def next[C, J](qcj: TreeF_2[C, J], c: C): Result[C, J, TreeF, TreeF_2] = qcj match
          case TreeF_2.ForkRR(m, r) => Result.More(m, TreeF_2.ForkLR(c, r))
          case TreeF_2.ForkLR(l, r) => Result.More(r, TreeF_2.ForkLL(l, c))
          case TreeF_2.ForkLL(l, m) => Result.Done(TreeF.Fork(l, m, c))
    )


// TODO: Explore and standardise
type Tree = Fix[TreeF]

object Tree:
  val leaf: Tree = Fix(TreeF.Leaf)
  def fork[A](l: Tree, m: Tree, r: Tree): Tree = Fix(TreeF.Fork(l, m, r))

