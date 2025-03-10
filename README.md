# Stack-Safe Recursion Schemes for Scala

A (very much WIP) attempt to implement in Scala the concepts from Conor McBride's [Clowns and Jokers](https://personal.cis.strath.ac.uk/conor.mcbride/Dissect.pdf) paper. 

Implemented in terms of [Droste](https://github.com/higherkindness/droste) primitives (effectively by replacing the hylomorphisms in Droste's kernel with [Dissectable-based](src/main/scala/Dissectable.scala) implementations).

Heavily inspired/borrowed from [purescript-ssrs](https://github.com/purefunctor/purescript-ssrs) and [purescript-dissect](https://github.com/PureFunctor/purescript-dissect) as reference implementations.

## How to use

That's the neat part, you don't! 

But in all seriousness, this project was for fun to try and understand these concepts better through implementing them in the language I think in. I wouldn't recommend using this project as-is in a production environment.

## What works

- Core idea from paper of Dissectable Functors, and using the derivative polynomial bifunctors as the data-type in a stack-machine for stack-safe traversal of recursive data structures
- Implementation of Droste kernel hylomorphism in terms of this stack-machine, with [full-set of working recursion-schemes](src/main/scala/schemes/) based on this definition
- Using typeclass-derivation to perform, at compile-time, taking the derivative of a generic polynomial functor (read: Recursive data type), and building a Dissectable typeclass instance for that type.


## What doesn't work (yet)

- Deriving (either by implicit derivation, or by metaprogramming), the Generic form of a given fixed-point data-structure from it's non-fixed-point form (or even from any encoding other than the one the current Dissectable derivations which are defined in terms of Cats [(Polynomial) Functors](src/main/scala/polynomials/functors/package.scala))
- Anything else in [TODO.md](TODO.md)
