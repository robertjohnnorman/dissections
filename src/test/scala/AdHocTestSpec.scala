package dev.robertjohnnorman.dissections

import data.*
import polynomials.all.*

import cats.*
import cats.data.EitherK
import cats.derived.*
import cats.implicits.*
import cats.syntax.all.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{ExitCode, IO, IOApp}
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import shapeless3.deriving.{Id as ShapelessId, *}
import shapeless3.typeable.Typeable

import higherkindness.droste.*
import higherkindness.droste.data.prelude.*
import higherkindness.droste.data.*
import higherkindness.droste.prelude.*
import higherkindness.droste.syntax.all.*

import scala.deriving.*
import scala.compiletime.*

enum CoreF[+A] derives Functor, Traverse:
  case ValueF(x: Int) extends CoreF[A]
  case AddF(l: A, r: A) extends CoreF[A]

type CoreLang = Fix[CoreF]

object CoreLang:
  def value[F[_]](x: Int)(using InjectK[CoreF, F]): Fix[F] =
    Fix(InjectK[CoreF, F].inj(CoreF.ValueF(x)))

  def add[F[_]](l: Fix[F], r: Fix[F])(using InjectK[CoreF, F]): Fix[F] =
    Fix(InjectK[CoreF, F].inj(CoreF.AddF(l, r)))


enum ExtrasF[+A] derives Functor, Traverse:
  case MulF(l: A, r: A) extends ExtrasF[A]

type ExtraLang = Fix[ExtrasF]

object ExtraLang:
  def mul[F[_]](l: Fix[F], r: Fix[F])(using InjectK[ExtrasF, F]): Fix[F] =
    Fix(InjectK[ExtrasF, F].inj(ExtrasF.MulF(l, r)))

enum MoreF[+A] derives Functor, Traverse:
  case MaxF(l: A, r: A) extends MoreF[A]

type MoreLang = Fix[MoreF]

object MoreLang:
  def max[F[_]](l: Fix[F], r: Fix[F])(using InjectK[MoreF, F]): Fix[F] =
    Fix(InjectK[MoreF, F].inj(MoreF.MaxF(l, r)))



infix type :+:[F[_], G[_]] = [a] =>> EitherK[F, G, a]

//type FullLangF = MoreF :+: ExtrasF :+: CoreF
type FullLangF[A] = (MoreF :+: ExtrasF :+: CoreF)[A]

type FullLang = Fix[FullLangF]


val evalExplicitFullLangF = Algebra[FullLangF, Int]: (a: FullLangF[Int]) =>
  a.run.map(_.run) match
    case Right(Right(CoreF.ValueF(x)))    => x
    case Right(Right(CoreF.AddF(l, r)))   => l + r
    case Right(Left(ExtrasF.MulF(l, r)))  => l * r
    case Left(MoreF.MaxF(l, r))           => l max r


trait EvalAlg[M[_], F[_]]:
  def alg: AlgebraM[M, F, Int]

object EvalAlg:
  def apply[M[_], F[_]](using evalAlg: EvalAlg[M, F]): EvalAlg[M, F] = evalAlg


given [M[_], F[_], G[_]](using EvalAlg[M, F], EvalAlg[M, G]): EvalAlg[M, F :+: G] with
  def alg: AlgebraM[M, F :+: G, Int] = AlgebraM[M, F :+: G, Int]: efg =>
    efg.run match
      case Left(fInt)   => EvalAlg[M, F].alg(fInt)
      case Right(gInt)  => EvalAlg[M, G].alg(gInt)

given EvalAlg[Id, CoreF] with
  def alg: AlgebraM[Id, CoreF, Int] = AlgebraM[Id, CoreF, Int]:
    case CoreF.ValueF(x)    => x
    case CoreF.AddF(l, r)   => l + r

given EvalAlg[Id, ExtrasF] with
  def alg: AlgebraM[Id, ExtrasF, Int] = AlgebraM[Id, ExtrasF, Int]:
    case ExtrasF.MulF(l, r) => l * r


given EvalAlg[Id, MoreF] with
  def alg: AlgebraM[Id, MoreF, Int] = AlgebraM[Id, MoreF, Int]:
    case MoreF.MaxF(l, r) => l max r


val fullLangEvalAlg: EvalAlg[Id, FullLangF] = summon[EvalAlg[Id, FullLangF]]

trait Interpreter[F[_]: Functor, A]:
  def alg: Algebra[F, A]
  
  def interpret[R](r: R)(using Project[F, R]): A =
    scheme.cata(alg).apply(r)

//trait GInterpreterM[F[_]; Functor, ]
  

class AdHocTestSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers:

  val simpleCoreExpr: CoreLang =
    CoreLang.add(
      CoreLang.add(
        CoreLang.value(1),
        CoreLang.add(
          CoreLang.value(2),
          CoreLang.value(3)
        )
      ),
      CoreLang.value(4)
    )

  val simpleCoreEvaled: Int =
    scheme.cataM(EvalAlg[Id, CoreF].alg).apply(simpleCoreExpr)

  val simpleFullLangExpr: FullLang =
    CoreLang.add(
      ExtraLang.mul(
        CoreLang.value(1),
        MoreLang.max(
          CoreLang.value(2),
          CoreLang.value(3)
        )
      ),
      CoreLang.value(3)
    )

  val simpleFullLangExprExplicitEvaled: Int =
    scheme.cata(evalExplicitFullLangF).apply(simpleFullLangExpr)

  val simpleFullLangExprGivenEvaled: Int =
    scheme.cataM(fullLangEvalAlg.alg).apply(simpleFullLangExpr)

  "AdHocTest" - {
    "should run" in {
      for
        _ <- IO.println("Hello World")
        _ <- IO.println(simpleCoreExpr)
        _ <- IO.println(simpleCoreEvaled)
        _ <- IO.println("--------")
        _ <- IO.println(simpleFullLangExpr)
        _ <- IO.println(simpleFullLangExprExplicitEvaled)
        _ <- IO.println(simpleFullLangExprGivenEvaled)
        _ <- IO.println("--------")
      yield () shouldEqual ()
    }
  }
