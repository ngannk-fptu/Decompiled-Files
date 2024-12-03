/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 */
package io.micrometer.core.aop;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Predicate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
@NonNullApi
public class CountedAspect {
    private static final Predicate<ProceedingJoinPoint> DONT_SKIP_ANYTHING = pjp -> false;
    public final String DEFAULT_EXCEPTION_TAG_VALUE = "none";
    public final String RESULT_TAG_FAILURE_VALUE = "failure";
    public final String RESULT_TAG_SUCCESS_VALUE = "success";
    private static final String RESULT_TAG = "result";
    private static final String EXCEPTION_TAG = "exception";
    private final MeterRegistry registry;
    private final Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint;
    private final Predicate<ProceedingJoinPoint> shouldSkip;

    public CountedAspect() {
        this(Metrics.globalRegistry);
    }

    public CountedAspect(MeterRegistry registry) {
        this(registry, DONT_SKIP_ANYTHING);
    }

    public CountedAspect(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint) {
        this(registry, tagsBasedOnJoinPoint, DONT_SKIP_ANYTHING);
    }

    public CountedAspect(MeterRegistry registry, Predicate<ProceedingJoinPoint> shouldSkip) {
        this(registry, pjp -> Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringTypeName(), "method", pjp.getStaticPart().getSignature().getName()), shouldSkip);
    }

    public CountedAspect(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint, Predicate<ProceedingJoinPoint> shouldSkip) {
        this.registry = registry;
        this.tagsBasedOnJoinPoint = tagsBasedOnJoinPoint;
        this.shouldSkip = shouldSkip;
    }

    @Around(value="@annotation(counted)", argNames="pjp,counted")
    public Object interceptAndRecord(ProceedingJoinPoint pjp, Counted counted) throws Throwable {
        if (this.shouldSkip.test(pjp)) {
            return pjp.proceed();
        }
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        boolean stopWhenCompleted = CompletionStage.class.isAssignableFrom(method.getReturnType());
        if (stopWhenCompleted) {
            try {
                return ((CompletionStage)pjp.proceed()).whenComplete((result, throwable) -> this.recordCompletionResult(pjp, counted, (Throwable)throwable));
            }
            catch (Throwable e) {
                this.record(pjp, counted, e.getClass().getSimpleName(), "failure");
                throw e;
            }
        }
        try {
            Object result2 = pjp.proceed();
            if (!counted.recordFailuresOnly()) {
                this.record(pjp, counted, "none", "success");
            }
            return result2;
        }
        catch (Throwable e) {
            this.record(pjp, counted, e.getClass().getSimpleName(), "failure");
            throw e;
        }
    }

    private void recordCompletionResult(ProceedingJoinPoint pjp, Counted counted, Throwable throwable) {
        if (throwable != null) {
            String exceptionTagValue = throwable.getCause() == null ? throwable.getClass().getSimpleName() : throwable.getCause().getClass().getSimpleName();
            this.record(pjp, counted, exceptionTagValue, "failure");
        } else if (!counted.recordFailuresOnly()) {
            this.record(pjp, counted, "none", "success");
        }
    }

    private void record(ProceedingJoinPoint pjp, Counted counted, String exception, String result) {
        this.counter(pjp, counted).tag(EXCEPTION_TAG, exception).tag(RESULT_TAG, result).tags(counted.extraTags()).register(this.registry).increment();
    }

    private Counter.Builder counter(ProceedingJoinPoint pjp, Counted counted) {
        Counter.Builder builder = Counter.builder(counted.value()).tags(this.tagsBasedOnJoinPoint.apply(pjp));
        String description = counted.description();
        if (!description.isEmpty()) {
            builder.description(description);
        }
        return builder;
    }
}

