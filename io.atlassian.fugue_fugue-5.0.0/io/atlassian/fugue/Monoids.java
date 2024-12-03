/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Either;
import io.atlassian.fugue.Functions;
import io.atlassian.fugue.Iterables;
import io.atlassian.fugue.Monoid;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import io.atlassian.fugue.Semigroup;
import io.atlassian.fugue.Unit;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class Monoids {
    public static final Monoid<Integer> intAddition = new Monoid<Integer>(){

        @Override
        public Integer append(Integer i1, Integer i2) {
            return i1 + i2;
        }

        @Override
        public Integer zero() {
            return 0;
        }

        @Override
        public Integer multiply(int n, Integer i) {
            return n <= 0 ? 0 : n * i;
        }
    };
    public static final Monoid<Integer> intMultiplication = new Monoid<Integer>(){

        @Override
        public Integer append(Integer i1, Integer i2) {
            return i1 * i2;
        }

        @Override
        public Integer zero() {
            return 1;
        }
    };
    public static final Monoid<BigInteger> bigintAddition = new Monoid<BigInteger>(){

        @Override
        public BigInteger append(BigInteger a1, BigInteger a2) {
            return a1.add(a2);
        }

        @Override
        public BigInteger zero() {
            return BigInteger.ZERO;
        }

        @Override
        public BigInteger multiply(int n, BigInteger b) {
            return n <= 0 ? BigInteger.ZERO : b.multiply(BigInteger.valueOf(n));
        }
    };
    public static final Monoid<BigInteger> bigintMultiplication = new Monoid<BigInteger>(){

        @Override
        public BigInteger append(BigInteger a1, BigInteger a2) {
            return a1.multiply(a2);
        }

        @Override
        public BigInteger zero() {
            return BigInteger.ONE;
        }

        @Override
        public BigInteger multiply(int n, BigInteger b) {
            return n <= 0 ? BigInteger.ONE : b.pow(n);
        }
    };
    public static final Monoid<Long> longAddition = new Monoid<Long>(){

        @Override
        public Long append(Long a1, Long a2) {
            return a1 + a2;
        }

        @Override
        public Long zero() {
            return 0L;
        }

        @Override
        public Long multiply(int n, Long l) {
            return n <= 0 ? 0L : l * (long)n;
        }
    };
    public static final Monoid<Long> longMultiplication = new Monoid<Long>(){

        @Override
        public Long append(Long a1, Long a2) {
            return a1 * a2;
        }

        @Override
        public Long zero() {
            return 1L;
        }
    };
    public static final Monoid<Boolean> disjunction = new Monoid<Boolean>(){

        @Override
        public Boolean append(Boolean a1, Boolean a2) {
            return a1 != false || a2 != false;
        }

        @Override
        public Boolean zero() {
            return false;
        }

        @Override
        public Boolean sum(Iterable<Boolean> bs) {
            return Iterables.filter(bs, b -> b).iterator().hasNext();
        }

        @Override
        public Boolean multiply(int n, Boolean b) {
            return n <= 0 ? false : b;
        }
    };
    public static final Monoid<Boolean> exclusiveDisjunction = new Monoid<Boolean>(){

        @Override
        public Boolean append(Boolean a1, Boolean a2) {
            return a1 ^ a2;
        }

        @Override
        public Boolean zero() {
            return false;
        }

        @Override
        public Boolean multiply(int n, Boolean b) {
            return b != false && n == 1;
        }
    };
    public static final Monoid<Boolean> conjunction = new Monoid<Boolean>(){

        @Override
        public Boolean append(Boolean a1, Boolean a2) {
            return a1 != false && a2 != false;
        }

        @Override
        public Boolean zero() {
            return true;
        }

        @Override
        public Boolean sum(Iterable<Boolean> bs) {
            return !Iterables.filter(bs, b -> b == false).iterator().hasNext();
        }
    };
    public static final Monoid<String> string = new Monoid<String>(){

        @Override
        public String append(String a1, String a2) {
            return a1.concat(a2);
        }

        @Override
        public String zero() {
            return "";
        }

        @Override
        public String sum(Iterable<String> ss) {
            StringBuilder sb = new StringBuilder();
            for (String s : ss) {
                sb.append(s);
            }
            return sb.toString();
        }
    };
    public static final Monoid<Unit> unit = new Monoid<Unit>(){

        @Override
        public Unit append(Unit a1, Unit a2) {
            return Unit.Unit();
        }

        @Override
        public Unit zero() {
            return Unit.Unit();
        }

        @Override
        public Unit multiply(int n, Unit unit) {
            return Unit.Unit();
        }

        @Override
        public Unit multiply1p(int n, Unit unit) {
            return Unit.Unit();
        }
    };

    private Monoids() {
    }

    public static <A, B> Monoid<Function<A, B>> function(final Monoid<B> mb) {
        return new Monoid<Function<A, B>>(){

            @Override
            public Function<A, B> append(Function<A, B> a1, Function<A, B> a2) {
                return a -> mb.append(a1.apply(a), a2.apply(a));
            }

            @Override
            public Function<A, B> zero() {
                return a -> mb.zero();
            }

            @Override
            public Function<A, B> sum(Iterable<Function<A, B>> fs) {
                return a -> mb.sum(Iterables.map(fs, Functions.apply(a)));
            }

            @Override
            public Function<A, B> multiply(int n, Function<A, B> f) {
                return a -> mb.multiply(n, f.apply(a));
            }
        };
    }

    public static <A> Monoid<List<A>> list() {
        return new Monoid<List<A>>(){

            @Override
            public List<A> append(List<A> l1, List<A> l2) {
                List sumList;
                if (l1.isEmpty()) {
                    sumList = l2;
                } else if (l2.isEmpty()) {
                    sumList = l1;
                } else {
                    sumList = new ArrayList(l1.size() + l2.size());
                    sumList.addAll(l1);
                    sumList.addAll(l2);
                }
                return sumList;
            }

            @Override
            public List<A> zero() {
                return Collections.emptyList();
            }
        };
    }

    public static <A> Monoid<Iterable<A>> iterable() {
        return new Monoid<Iterable<A>>(){

            @Override
            public Iterable<A> append(Iterable<A> a1, Iterable<A> a2) {
                return Iterables.concat(a1, a2);
            }

            @Override
            public Iterable<A> zero() {
                return Collections.emptyList();
            }

            @Override
            public Iterable<A> sum(Iterable<Iterable<A>> iterable) {
                return Iterables.join(iterable);
            }
        };
    }

    public static <A> Monoid<Option<A>> firstOption() {
        return new Monoid<Option<A>>(){

            @Override
            public Option<A> append(Option<A> a1, Option<A> a2) {
                return a1.isDefined() ? a1 : a2;
            }

            @Override
            public Option<A> zero() {
                return Option.none();
            }

            @Override
            public Option<A> sum(Iterable<Option<A>> os) {
                return Iterables.first(Options.filterNone(os)).getOrElse(Option.none());
            }
        };
    }

    public static <A> Monoid<Option<A>> lastOption() {
        return new Monoid<Option<A>>(){

            @Override
            public Option<A> append(Option<A> a1, Option<A> a2) {
                return a2.isDefined() ? a2 : a1;
            }

            @Override
            public Option<A> zero() {
                return Option.none();
            }
        };
    }

    public static <A> Monoid<Option<A>> option(final Semigroup<A> semigroup) {
        return new Monoid<Option<A>>(){

            @Override
            public Option<A> append(Option<A> o1, Option<A> o2) {
                return o1.fold(() -> o2, a1 -> o2.fold(() -> o1, a2 -> Option.some(semigroup.append(a1, a2))));
            }

            @Override
            public Option<A> zero() {
                return Option.none();
            }

            @Override
            public Option<A> sum(Iterable<Option<A>> os) {
                Iterable memoized = Iterables.memoize(Options.flatten(os));
                return Iterables.first(memoized).fold(Option::none, a -> Option.some(semigroup.sumNonEmpty(a, Iterables.drop(1, memoized))));
            }

            @Override
            public Option<A> multiply(int n, Option<A> as) {
                return n <= 0 ? Option.none() : as.fold(Option::none, a -> Option.some(semigroup.multiply1p(n - 1, a)));
            }
        };
    }

    public static <L, R> Monoid<Either<L, R>> either(final Semigroup<L> lS, final Monoid<R> rM) {
        final Either zero = Either.right(rM.zero());
        return new Monoid<Either<L, R>>(){

            @Override
            public Either<L, R> append(Either<L, R> e1, Either<L, R> e2) {
                return e1.fold(l1 -> e2.fold(l2 -> Either.left(lS.append(l1, l2)), r2 -> e1), r1 -> e2.fold(l2 -> e2, r2 -> Either.right(rM.append(r1, r2))));
            }

            @Override
            public Either<L, R> zero() {
                return zero;
            }
        };
    }
}

