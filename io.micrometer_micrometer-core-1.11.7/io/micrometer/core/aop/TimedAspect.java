/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.Nullable
 *  org.aspectj.lang.ProceedingJoinPoint
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 *  org.aspectj.lang.reflect.MethodSignature
 */
package io.micrometer.core.aop;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.aop.MeterTagAnnotationHandler;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Predicate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
@NonNullApi
@Incubating(since="1.0.0")
public class TimedAspect {
    private static final Predicate<ProceedingJoinPoint> DONT_SKIP_ANYTHING = pjp -> false;
    public static final String DEFAULT_METRIC_NAME = "method.timed";
    public static final String DEFAULT_EXCEPTION_TAG_VALUE = "none";
    public static final String EXCEPTION_TAG = "exception";
    private final MeterRegistry registry;
    private final Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint;
    private final Predicate<ProceedingJoinPoint> shouldSkip;
    private MeterTagAnnotationHandler meterTagAnnotationHandler;

    public TimedAspect() {
        this(Metrics.globalRegistry);
    }

    public TimedAspect(MeterRegistry registry) {
        this(registry, DONT_SKIP_ANYTHING);
    }

    public TimedAspect(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint) {
        this(registry, tagsBasedOnJoinPoint, DONT_SKIP_ANYTHING);
    }

    public TimedAspect(MeterRegistry registry, Predicate<ProceedingJoinPoint> shouldSkip) {
        this(registry, pjp -> Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringTypeName(), "method", pjp.getStaticPart().getSignature().getName()), shouldSkip);
    }

    public TimedAspect(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint, Predicate<ProceedingJoinPoint> shouldSkip) {
        this.registry = registry;
        this.tagsBasedOnJoinPoint = tagsBasedOnJoinPoint;
        this.shouldSkip = shouldSkip;
    }

    @Around(value="@within(io.micrometer.core.annotation.Timed)")
    @Nullable
    public Object timedClass(ProceedingJoinPoint pjp) throws Throwable {
        if (this.shouldSkip.test(pjp)) {
            return pjp.proceed();
        }
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Class<?> declaringClass = method.getDeclaringClass();
        if (!declaringClass.isAnnotationPresent(Timed.class)) {
            declaringClass = pjp.getTarget().getClass();
        }
        Timed timed = declaringClass.getAnnotation(Timed.class);
        return this.perform(pjp, timed, method);
    }

    @Around(value="execution (@io.micrometer.core.annotation.Timed * *.*(..))")
    @Nullable
    public Object timedMethod(ProceedingJoinPoint pjp) throws Throwable {
        if (this.shouldSkip.test(pjp)) {
            return pjp.proceed();
        }
        Method method = ((MethodSignature)pjp.getSignature()).getMethod();
        Timed timed = method.getAnnotation(Timed.class);
        if (timed == null) {
            method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            timed = method.getAnnotation(Timed.class);
        }
        return this.perform(pjp, timed, method);
    }

    private Object perform(ProceedingJoinPoint pjp, Timed timed, Method method) throws Throwable {
        String metricName = timed.value().isEmpty() ? DEFAULT_METRIC_NAME : timed.value();
        boolean stopWhenCompleted = CompletionStage.class.isAssignableFrom(method.getReturnType());
        if (!timed.longTask()) {
            return this.processWithTimer(pjp, timed, metricName, stopWhenCompleted);
        }
        return this.processWithLongTaskTimer(pjp, timed, metricName, stopWhenCompleted);
    }

    private Object processWithTimer(ProceedingJoinPoint pjp, Timed timed, String metricName, boolean stopWhenCompleted) throws Throwable {
        Timer.Sample sample = Timer.start(this.registry);
        if (stopWhenCompleted) {
            try {
                return ((CompletionStage)pjp.proceed()).whenComplete((result, throwable) -> this.record(pjp, timed, metricName, sample, this.getExceptionTag((Throwable)throwable)));
            }
            catch (Exception ex) {
                this.record(pjp, timed, metricName, sample, ex.getClass().getSimpleName());
                throw ex;
            }
        }
        String exceptionClass = DEFAULT_EXCEPTION_TAG_VALUE;
        try {
            Object object = pjp.proceed();
            return object;
        }
        catch (Exception ex) {
            exceptionClass = ex.getClass().getSimpleName();
            throw ex;
        }
        finally {
            this.record(pjp, timed, metricName, sample, exceptionClass);
        }
    }

    private void record(ProceedingJoinPoint pjp, Timed timed, String metricName, Timer.Sample sample, String exceptionClass) {
        try {
            sample.stop(this.recordBuilder(pjp, timed, metricName, exceptionClass).register(this.registry));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private Timer.Builder recordBuilder(ProceedingJoinPoint pjp, Timed timed, String metricName, String exceptionClass) {
        Timer.Builder builder = ((Timer.Builder)Timer.builder(metricName).description(timed.description().isEmpty() ? null : timed.description()).tags(timed.extraTags()).tags(EXCEPTION_TAG, exceptionClass).tags((Iterable)this.tagsBasedOnJoinPoint.apply(pjp))).publishPercentileHistogram(timed.histogram()).publishPercentiles(timed.percentiles().length == 0 ? null : timed.percentiles());
        if (this.meterTagAnnotationHandler != null) {
            this.meterTagAnnotationHandler.addAnnotatedParameters(builder, pjp);
        }
        return builder;
    }

    private String getExceptionTag(Throwable throwable) {
        if (throwable == null) {
            return DEFAULT_EXCEPTION_TAG_VALUE;
        }
        if (throwable.getCause() == null) {
            return throwable.getClass().getSimpleName();
        }
        return throwable.getCause().getClass().getSimpleName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object processWithLongTaskTimer(ProceedingJoinPoint pjp, Timed timed, String metricName, boolean stopWhenCompleted) throws Throwable {
        Optional<LongTaskTimer.Sample> sample = this.buildLongTaskTimer(pjp, timed, metricName).map(LongTaskTimer::start);
        if (stopWhenCompleted) {
            try {
                return ((CompletionStage)pjp.proceed()).whenComplete((result, throwable) -> sample.ifPresent(this::stopTimer));
            }
            catch (Exception ex) {
                sample.ifPresent(this::stopTimer);
                throw ex;
            }
        }
        try {
            Object object = pjp.proceed();
            return object;
        }
        finally {
            sample.ifPresent(this::stopTimer);
        }
    }

    private void stopTimer(LongTaskTimer.Sample sample) {
        try {
            sample.stop();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private Optional<LongTaskTimer> buildLongTaskTimer(ProceedingJoinPoint pjp, Timed timed, String metricName) {
        try {
            return Optional.of(LongTaskTimer.builder(metricName).description(timed.description().isEmpty() ? null : timed.description()).tags(timed.extraTags()).tags(this.tagsBasedOnJoinPoint.apply(pjp)).register(this.registry));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    public void setMeterTagAnnotationHandler(MeterTagAnnotationHandler meterTagAnnotationHandler) {
        this.meterTagAnnotationHandler = meterTagAnnotationHandler;
    }
}

