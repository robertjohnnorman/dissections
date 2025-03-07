package dev.robertjohnnorman.dissections

import higherkindness.droste.{data => _, *}
import higherkindness.droste.data.*

package object data:
  
  type CVAlgebraM[M[_], F[_], A] = 
    GAlgebraM[M, F, Attr[F, A], A]

  type CVCoalgebraM[M[_], F[_], A] = 
    GCoalgebraM[M, F, A, Coattr[F, A]]

  object all:
    export data.Result
    export data.CVAlgebraM
    export data.CVCoalgebraM
