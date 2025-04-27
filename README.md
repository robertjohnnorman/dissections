# Stack-Safe Recursion Schemes for Scala

A (very much WIP) attempt to implement in Scala the concepts from Conor McBride's [Clowns and Jokers](https://personal.cis.strath.ac.uk/conor.mcbride/Dissect.pdf) paper. 

Implemented in terms of [Droste](https://github.com/higherkindness/droste) primitives (effectively by replacing the hylomorphisms in Droste's kernel with [Dissectable-based implementations](src/main/scala/schemes/package.scala)).

Heavily inspired/borrowed from [purescript-ssrs](https://github.com/purefunctor/purescript-ssrs) and [purescript-dissect](https://github.com/PureFunctor/purescript-dissect) as reference implementations.

## How to use

That's the neat part, you don't! 

But in all seriousness, this project was for fun to try and understand these concepts better through implementing them in the language I think in. I wouldn't recommend using this project as-is in a production environment.

Nonetheless, for the brave, a stack-safe set of recursion-schemes can be imported from [`dev.robertjohnnorman.dissections.schemes.all`](src/main/scala/schemes/package.scala).

If we use `cata` as an example scheme, we can compare the signatures between [this implementation](src/main/scala/schemes/Folds.scala) ... 

```scala
def cata[F[_]: Functor, R, B](algebra: Algebra[F, B])(implicit project: Project[F, R]): R => B = ???
```
... and [the one in droste](https://github.com/higherkindness/droste/blob/193dff211372cad473efff0e1ee0d02a887bbf43/modules/core/src/main/scala/higherkindness/droste/scheme.scala#L55).

```scala
def cata[F[_]: Dissectable, R, B](using project: Project[F, R])(algebra: Algebra[F, B]): R => B = ???
```

Ignoring the re-ordering of argument clauses, they are identical except for the context-bound on `F[_]`, where we require a `Dissectable` type-class in-place of `Functor`.

The [`Dissectable`](src/main/scala/Dissectable.scala) type-class has the following structure:

```scala
trait Dissectable[P[_]] extends Functor[P] { self =>

  type Q[_, _]

  def into[C, J](pj: P[J]): Result[C, J, P, Q]

  def next[C, J](qcj: Q[C, J], c: C): Result[C, J, P, Q]


  override def map[A, B](fa: P[A])(f: A => B): P[B] = ???

}
```

where [`Result`](src/main/scala/data/Result.scala) is either a fully 'mapped' (sub-)structure, or the current 'dissection' of the structure as given by the polynomial Bifunctor `Q[_, _]` coupled with a 'Joker' element that was removed from the position that the dissection provides [the one-hole context](http://strictlypositive.org/diff.pdf) for.

```scala
enum Result[C, J, P[_], Q[_, _]]:
  case Done(value: P[C])
  case More(joker: J, dissection: Q[C, J])
```

> **_NOTE:_** Our definitions here do not constrain `Q` to be a Bifunctor, but this is required in practice.

Although we won't elaborate here what exactly `into` and `next` do, and therefore how 'dissection' allows us to incrementally traverse a recursive structure, suffice to say that so long as for any (polynomial) type `F[_]` there exists a `Dissectable` instance, all recursion schemes can be used as they typically would.

If `F[_]` is defined specifically in terms of Cats [polynomial functors](src/main/scala/polynomials/functors/package.scala) then an instance will be derived implicitly (see [givens](src/main/scala/Dissectable.scala))

Otherwise, defining an appropriate instance of `Dissectable` can be done by hand, and is largely just an exercise in reasoning about structure, albeit a mildly laborious one.

Unfortunately, at time of writing I've not been able to work out how to avoid this by deriving the type-class for any polynomial type 'via' its general representation, in the same way droste/kittens can derive a Functor instance (or in an analogous way that Haskell has `deriving via` capability). During attempts with `shapeless3` and/or `scala.compiletime`, the abstract member `type Q[_, _]` proved difficult to handle and the sticking point for me.

## What works

- Core idea from paper of Dissectable Functors, and using the derivative polynomial bifunctors as the data-type in a stack-machine for stack-safe traversal of recursive data structures
- Implementation of Droste kernel hylomorphism in terms of this stack-machine, with [full-set of working recursion-schemes](src/main/scala/schemes/) based on this definition
- Using typeclass-derivation to perform, at compile-time, taking the derivative of a generic polynomial functor (read: Recursive data type), and building a Dissectable typeclass instance for that type.


## What doesn't work (yet)

- Deriving (either by implicit derivation, or by metaprogramming), the Generic form of a given fixed-point data-structure from it's non-fixed-point form (or even from any encoding other than the one the current Dissectable derivations which are defined in terms of Cats [(Polynomial) Functors](src/main/scala/polynomials/functors/package.scala))
- Recovering 'Huet's Zipper' as a further `Zipper` type-class that extends the requirements of `Dissectable`.
- Anything else in [TODO.md](TODO.md)

## Credits

Much of what I've learnt about this topic has been largely due to the excellent work of the following projects from which this one has benefited enormously as references:

- droste (https://github.com/higherkindness/droste)
- purescript-dissect (https://github.com/PureFunctor/purescript-dissect)
- purescript-ssrs (https://github.com/PureFunctor/purescript-ssrs)

And of course the original papers from Conor McBride: http://strictlypositive.org/

