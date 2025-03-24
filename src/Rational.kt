import java.math.BigInteger

class Rational(private val n: BigInteger, private val d: BigInteger) {
  constructor(n: Int, d: Int) : this(n.toBigInteger(), d.toBigInteger())
  constructor(n: Long, d: Long) : this(n.toBigInteger(), d.toBigInteger())

  operator fun times(other: Rational) = Rational(n * other.n, other.d * d)
  operator fun plus(other: Rational) = Rational(n * other.d + other.n * d, other.d * d)
  operator fun minus(other: Rational) = Rational(n * other.d - other.n * d, other.d * d)
  operator fun div(other: Rational) = Rational(n * other.d, d * other.n)
  fun toLong() = (n / d).toLong()
}
