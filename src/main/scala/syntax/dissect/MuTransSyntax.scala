package dev.robertjohnnorman.dissections
package syntax.dissect

import cats.{Bifunctor, Functor, Monad}
import higherkindness.droste.*
import higherkindness.droste.data.{Fix, Mu, Nu}
import higherkindness.droste.prelude.*
import higherkindness.droste.syntax.all.*

object MuTransSyntax {

  import MuFoldSyntax.*

  // TODO: These should be moved to schemes.
  
  // TODO: Should be Mu
  //extension[P[_]](fixP: Mu[P])

  //  def transCata[R[_]](using pDissect: Dissect[P], rDissect: Dissect[R])(trans: Trans[P, R, Mu[R]]): Mu[R] =
  //    implicit val basis: Basis[R, Mu[R]] =
  //      Mu.drosteBasisForMu[R](using rDissect.implicits.pFunctor)

  //    fixP.cata(trans.algebra)

  //  def transCataM[R[_], M[_]: Monad](using pDissect: Dissect[P], rDissect: Dissect[R])(trans: TransM[M, P, R, Mu[R]]): M[Mu[R]] =
  //    implicit val basis: Basis[R, Mu[R]] =
  //      Mu.drosteBasisForMu[R](using rDissect.implicits.pFunctor)

  //    fixP.cataM(trans.algebra)
}
