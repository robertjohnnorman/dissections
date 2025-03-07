package dev.robertjohnnorman.dissections
package syntax.dissect

import cats.{Bifunctor, Functor, Monad}
import higherkindness.droste.*
import higherkindness.droste.data.{Fix, Mu, Nu}
import higherkindness.droste.prelude.*
import higherkindness.droste.syntax.all.*

object NuTransSyntax {

  import MuFoldSyntax.*

  // TODO: Should be nuP
  //extension[P[_]](fixP: Nu[P])

  //  def transAna[R[_]](using pDissect: Dissect[P], rDissect: Dissect[R])(trans: Trans[P, R, Nu[P]]): Nu[R] =
  //    implicit val basis: Basis[P, Nu[P]] =
  //      Nu.drosteBasisForNu[P](using pDissect.implicits.pFunctor)

  //    fixP
  //      .ana(using rDissect)
  //      .apply(trans.coalgebra)

  //
  //  def transAnaM[R[_], M[_]: Monad](using pDissect: Dissect[P], rDissect: Dissect[R])(trans: TransM[M, P, R, Nu[P]]): M[Nu[R]] =
  //    implicit val basis: Basis[P, Nu[P]] =
  //      Nu.drosteBasisForNu[P](using pDissect.implicits.pFunctor)

  //    fixP.anaM(trans.coalgebra)
}
