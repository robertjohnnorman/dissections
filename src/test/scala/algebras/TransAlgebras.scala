package dev.robertjohnnorman.dissections
package algebras


import data.NeListF
import higherkindness.droste.{Trans, TransM}
import higherkindness.droste.data.Mu
import higherkindness.droste.data.list.*
import higherkindness.droste.syntax.all.*
import higherkindness.droste.prelude.*

object TransAlgebras:
  
  // converting a non-empty list to a list can't fail, so we use Trans
  def transNeListToList[A]: Trans[[a] =>> NeListF[A, a], [a] =>> ListF[A, a], Mu[[a] =>> ListF[A, a]]] = Trans {
    case NeListF.NeConsF(head, tail) => ConsF(head, tail)
    case NeListF.NeLastF(last) => ConsF(last, Mu(NilF))
  }


  // converting a list to a non-empty list can fail, so we use TransM
  //def transListToNeList[A]: TransM[Option, [a] =>> ListF[A, a], [a] =>> NeListF[A, a], Fix[[a] =>>  ListF[A, a]]] = TransM {
  //  case ConsF(head, tail) => Fix.unfix(tail) match {
  //    case NilF =>  NeListF.NeLastF(head).some
  //    case _    =>  NeListF.NeConsF(head, tail).some
  //  }
  //  case NilF              => None
  //}

