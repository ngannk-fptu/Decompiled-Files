/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.Immutable
 */
package com.github.rholder.retry;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.WaitStrategy;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

public final class WaitStrategies {
    private static final WaitStrategy NO_WAIT_STRATEGY = new FixedWaitStrategy(0L);

    private WaitStrategies() {
    }

    public static WaitStrategy noWait() {
        return NO_WAIT_STRATEGY;
    }

    public static WaitStrategy fixedWait(long sleepTime, @Nonnull TimeUnit timeUnit) throws IllegalStateException {
        Preconditions.checkNotNull((Object)((Object)timeUnit), (Object)"The time unit may not be null");
        return new FixedWaitStrategy(timeUnit.toMillis(sleepTime));
    }

    public static WaitStrategy randomWait(long maximumTime, @Nonnull TimeUnit timeUnit) {
        Preconditions.checkNotNull((Object)((Object)timeUnit), (Object)"The time unit may not be null");
        return new RandomWaitStrategy(0L, timeUnit.toMillis(maximumTime));
    }

    public static WaitStrategy randomWait(long minimumTime, @Nonnull TimeUnit minimumTimeUnit, long maximumTime, @Nonnull TimeUnit maximumTimeUnit) {
        Preconditions.checkNotNull((Object)((Object)minimumTimeUnit), (Object)"The minimum time unit may not be null");
        Preconditions.checkNotNull((Object)((Object)maximumTimeUnit), (Object)"The maximum time unit may not be null");
        return new RandomWaitStrategy(minimumTimeUnit.toMillis(minimumTime), maximumTimeUnit.toMillis(maximumTime));
    }

    public static WaitStrategy incrementingWait(long initialSleepTime, @Nonnull TimeUnit initialSleepTimeUnit, long increment, @Nonnull TimeUnit incrementTimeUnit) {
        Preconditions.checkNotNull((Object)((Object)initialSleepTimeUnit), (Object)"The initial sleep time unit may not be null");
        Preconditions.checkNotNull((Object)((Object)incrementTimeUnit), (Object)"The increment time unit may not be null");
        return new IncrementingWaitStrategy(initialSleepTimeUnit.toMillis(initialSleepTime), incrementTimeUnit.toMillis(increment));
    }

    public static WaitStrategy exponentialWait() {
        return new ExponentialWaitStrategy(1L, Long.MAX_VALUE);
    }

    public static WaitStrategy exponentialWait(long maximumTime, @Nonnull TimeUnit maximumTimeUnit) {
        Preconditions.checkNotNull((Object)((Object)maximumTimeUnit), (Object)"The maximum time unit may not be null");
        return new ExponentialWaitStrategy(1L, maximumTimeUnit.toMillis(maximumTime));
    }

    public static WaitStrategy exponentialWait(long multiplier, long maximumTime, @Nonnull TimeUnit maximumTimeUnit) {
        Preconditions.checkNotNull((Object)((Object)maximumTimeUnit), (Object)"The maximum time unit may not be null");
        return new ExponentialWaitStrategy(multiplier, maximumTimeUnit.toMillis(maximumTime));
    }

    public static WaitStrategy fibonacciWait() {
        return new FibonacciWaitStrategy(1L, Long.MAX_VALUE);
    }

    public static WaitStrategy fibonacciWait(long maximumTime, @Nonnull TimeUnit maximumTimeUnit) {
        Preconditions.checkNotNull((Object)((Object)maximumTimeUnit), (Object)"The maximum time unit may not be null");
        return new FibonacciWaitStrategy(1L, maximumTimeUnit.toMillis(maximumTime));
    }

    public static WaitStrategy fibonacciWait(long multiplier, long maximumTime, @Nonnull TimeUnit maximumTimeUnit) {
        Preconditions.checkNotNull((Object)((Object)maximumTimeUnit), (Object)"The maximum time unit may not be null");
        return new FibonacciWaitStrategy(multiplier, maximumTimeUnit.toMillis(maximumTime));
    }

    public static <T extends Throwable> WaitStrategy exceptionWait(@Nonnull Class<T> exceptionClass, @Nonnull Function<T, Long> function) {
        Preconditions.checkNotNull(exceptionClass, (Object)"exceptionClass may not be null");
        Preconditions.checkNotNull(function, (Object)"function may not be null");
        return new ExceptionWaitStrategy<T>(exceptionClass, function);
    }

    public static WaitStrategy join(WaitStrategy ... waitStrategies) {
        Preconditions.checkState((waitStrategies.length > 0 ? 1 : 0) != 0, (Object)"Must have at least one wait strategy");
        ArrayList waitStrategyList = Lists.newArrayList((Object[])waitStrategies);
        Preconditions.checkState((!waitStrategyList.contains(null) ? 1 : 0) != 0, (Object)"Cannot have a null wait strategy");
        return new CompositeWaitStrategy(waitStrategyList);
    }

    @Immutable
    private static final class ExceptionWaitStrategy<T extends Throwable>
    implements WaitStrategy {
        private final Class<T> exceptionClass;
        private final Function<T, Long> function;

        public ExceptionWaitStrategy(@Nonnull Class<T> exceptionClass, @Nonnull Function<T, Long> function) {
            this.exceptionClass = exceptionClass;
            this.function = function;
        }

        @Override
        public long computeSleepTime(Attempt lastAttempt) {
            Throwable cause;
            if (lastAttempt.hasException() && this.exceptionClass.isAssignableFrom((cause = lastAttempt.getExceptionCause()).getClass())) {
                return (Long)this.function.apply((Object)cause);
            }
            return 0L;
        }
    }

    @Immutable
    private static final class CompositeWaitStrategy
    implements WaitStrategy {
        private final List<WaitStrategy> waitStrategies;

        public CompositeWaitStrategy(List<WaitStrategy> waitStrategies) {
            Preconditions.checkState((!waitStrategies.isEmpty() ? 1 : 0) != 0, (Object)"Need at least one wait strategy");
            this.waitStrategies = waitStrategies;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            long waitTime = 0L;
            for (WaitStrategy waitStrategy : this.waitStrategies) {
                waitTime += waitStrategy.computeSleepTime(failedAttempt);
            }
            return waitTime;
        }
    }

    @Immutable
    private static final class FibonacciWaitStrategy
    implements WaitStrategy {
        private final long multiplier;
        private final long maximumWait;

        public FibonacciWaitStrategy(long multiplier, long maximumWait) {
            Preconditions.checkArgument((multiplier > 0L ? 1 : 0) != 0, (String)"multiplier must be > 0 but is %d", (Object[])new Object[]{multiplier});
            Preconditions.checkArgument((maximumWait >= 0L ? 1 : 0) != 0, (String)"maximumWait must be >= 0 but is %d", (Object[])new Object[]{maximumWait});
            Preconditions.checkArgument((multiplier < maximumWait ? 1 : 0) != 0, (String)"multiplier must be < maximumWait but is %d", (Object[])new Object[]{multiplier});
            this.multiplier = multiplier;
            this.maximumWait = maximumWait;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            long fib = this.fib(failedAttempt.getAttemptNumber());
            long result = this.multiplier * fib;
            if (result > this.maximumWait || result < 0L) {
                result = this.maximumWait;
            }
            return result >= 0L ? result : 0L;
        }

        private long fib(long n) {
            if (n == 0L) {
                return 0L;
            }
            if (n == 1L) {
                return 1L;
            }
            long prevPrev = 0L;
            long prev = 1L;
            long result = 0L;
            for (long i = 2L; i <= n; ++i) {
                result = prev + prevPrev;
                prevPrev = prev;
                prev = result;
            }
            return result;
        }
    }

    @Immutable
    private static final class ExponentialWaitStrategy
    implements WaitStrategy {
        private final long multiplier;
        private final long maximumWait;

        public ExponentialWaitStrategy(long multiplier, long maximumWait) {
            Preconditions.checkArgument((multiplier > 0L ? 1 : 0) != 0, (String)"multiplier must be > 0 but is %d", (Object[])new Object[]{multiplier});
            Preconditions.checkArgument((maximumWait >= 0L ? 1 : 0) != 0, (String)"maximumWait must be >= 0 but is %d", (Object[])new Object[]{maximumWait});
            Preconditions.checkArgument((multiplier < maximumWait ? 1 : 0) != 0, (String)"multiplier must be < maximumWait but is %d", (Object[])new Object[]{multiplier});
            this.multiplier = multiplier;
            this.maximumWait = maximumWait;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            double exp = Math.pow(2.0, failedAttempt.getAttemptNumber());
            long result = Math.round((double)this.multiplier * exp);
            if (result > this.maximumWait) {
                result = this.maximumWait;
            }
            return result >= 0L ? result : 0L;
        }
    }

    @Immutable
    private static final class IncrementingWaitStrategy
    implements WaitStrategy {
        private final long initialSleepTime;
        private final long increment;

        public IncrementingWaitStrategy(long initialSleepTime, long increment) {
            Preconditions.checkArgument((initialSleepTime >= 0L ? 1 : 0) != 0, (String)"initialSleepTime must be >= 0 but is %d", (Object[])new Object[]{initialSleepTime});
            this.initialSleepTime = initialSleepTime;
            this.increment = increment;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            long result = this.initialSleepTime + this.increment * (failedAttempt.getAttemptNumber() - 1L);
            return result >= 0L ? result : 0L;
        }
    }

    @Immutable
    private static final class RandomWaitStrategy
    implements WaitStrategy {
        private static final Random RANDOM = new Random();
        private final long minimum;
        private final long maximum;

        public RandomWaitStrategy(long minimum, long maximum) {
            Preconditions.checkArgument((minimum >= 0L ? 1 : 0) != 0, (String)"minimum must be >= 0 but is %d", (Object[])new Object[]{minimum});
            Preconditions.checkArgument((maximum > minimum ? 1 : 0) != 0, (String)"maximum must be > minimum but maximum is %d and minimum is", (Object[])new Object[]{maximum, minimum});
            this.minimum = minimum;
            this.maximum = maximum;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            long t = Math.abs(RANDOM.nextLong()) % (this.maximum - this.minimum);
            return t + this.minimum;
        }
    }

    @Immutable
    private static final class FixedWaitStrategy
    implements WaitStrategy {
        private final long sleepTime;

        public FixedWaitStrategy(long sleepTime) {
            Preconditions.checkArgument((sleepTime >= 0L ? 1 : 0) != 0, (String)"sleepTime must be >= 0 but is %d", (Object[])new Object[]{sleepTime});
            this.sleepTime = sleepTime;
        }

        @Override
        public long computeSleepTime(Attempt failedAttempt) {
            return this.sleepTime;
        }
    }
}

