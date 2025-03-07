package dev.robertjohnnorman.dissections

import polynomials.all.*

import dev.robertjohnnorman.dissections
import dev.robertjohnnorman.dissections.data.Result
import higherkindness.droste.data.Fix
import higherkindness.droste.syntax.all.*

trait Zipper[P[_]] extends Dissectable[P] {

  type Focus = Fix[P]
  type Context = List[Q[Fix[P], Fix[P]]]

  case class Location(focus: Focus, context: Context)

  def plug[A](qaa: Q[A, A], a: A): P[A]
  
  // TODO: Result isn't symmetrical in direction? Done vs Start?
  //       Also, Done should never be reachable?
  def previous[C, J](qcj: Q[C, J], j: J): Result[C, J, P, Q] 

  extension (location: Location)

    def up: Option[Location] = location match
      case Location(focus, Nil)       => None
      case Location(focus, pd :: pds) => Some(Location(Fix(plug(pd, focus)), pds))

    def down: Option[Location] =
      into[Focus, Focus](location.focus.unfix) match
        case Result.Done(value)             => None
        case Result.More(joker, dissection) => Some(Location(joker, dissection :: location.context))

    def right: Option[Location] = location match
      case Location(focus, Nil)       => None
      case Location(focus, pd :: pds) => next[Focus, Focus](pd, focus) match
        case Result.Done(value)             => None
        case Result.More(joker, dissection) => Some(Location(joker, dissection :: pds))

    //def left: Option[Location] = location match
    //  case Location(focus, Nil) => None
    //  case Location(focus, pd :: pds) => previous[Fix[P], Fix[P]](pd, focus) match
    //    case dissections.data.Result.Done(value) => // Done? Should be start?
    //    case dissections.data.Result.More(joker, dissection) => ???

}
