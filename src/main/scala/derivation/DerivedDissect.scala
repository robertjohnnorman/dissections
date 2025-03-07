package dev.robertjohnnorman.dissections
package derivation

import data.Result
import polynomials.bifunctors.Const_2

import cats.data.Const
import cats.{Eval, Foldable, Id}
import higherkindness.droste.derivation.Derived
import shapeless3.deriving.*

import scala.compiletime.*
import scala.compiletime.ops.*

type DerivedDissectable[F[_]] = Derived[Dissectable[F]]

object DerivedDissectable:
  type Or[F[_]] = Derived.Or[Dissectable[F]]
  inline def apply[F[_]]: Dissectable[F] =
    summonInline[DerivedDissectable[F]].instance

  //given [T]: DerivedDissectable[Const[T]] = Dissectable.const[T]

  //given [F[_], G[_]](using F: Or[F], G: Or[G]): DerivedDissectable[[x] =>> F[G[x]]] =
  //  Dissectable.compose[F, G](using F.unify, G.unify)

  //given [F[_]](using inst: K1.ProductInstances[Or, F]): DerivedDissectable[F] =
  //  new Product(using inst.unify) {}

  //given [F[_]](using inst: => K1.CoproductInstances[Or, F]): DerivedDissectable[F] =
  //  new Coproduct(using inst.unify) {}

  //trait Product[T[x[_]] <: Dissectable[x], F[_]](using inst: K1.ProductInstances[T, F])
  //  extends Dissectable[F]:


  //  override def into[C, J](pj: F[J]): Result[C, J, F, Q] =
  //    inst.fold

  //  def foldLeft[A, B](fa: F[A], b: B)(f: (B, A) => B): B =
  //    inst.foldLeft[A, B](fa)(b) { [f[_]] => (acc: B, tf: T[f], fa: f[A]) =>
  //      Continue(tf.foldLeft(fa, acc)(f))
  //    }

  //  def foldRight[A, B](fa: F[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
  //    inst.foldRight[A, Eval[B]](fa)(lb) { [f[_]] => (tf: T[f], fa: f[A], acc: Eval[B]) =>
  //      Continue(Eval.defer(tf.foldRight(fa, acc)(f)))
  //    }
    
  //transparent inline def derivative[F[_]]: Derivative[F] =
  //  summonFrom {
  //    case df: Derivative[F] => df
  //    case sum: K1.CoproductInstances[F]
  //    case other =>
  //      inline erasedValue[F] match
  //        case _: Id =>
  //
  //  }


  //trait Coproduct[T[x[_]] <: Dissectable[x], F[_]](
  //  using inst: K1.CoproductInstances[T, F],
  //  cMirror: K1.CoproductGeneric[F]
  //)
  //  extends Dissectable[F]:
  //
  //
  //  type Q = cMirror.
  //
  //  override def into[C, J](pj: F[J]): Result[C, J, F, Q] =
  //    inst.fold[J, Result[C, J, F, Q]](pj)([t] => (ft, ta) => ta)

  //  override def next[C, J](qcj: Q[C, J], c: C): Result[C, J, F, Q] =
  //    inst.fold((qcj, c))(Dissectable[F[J]].next.tupled)

