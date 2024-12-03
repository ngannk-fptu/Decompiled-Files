/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.annotation.AnnotationHandler
 *  io.micrometer.common.annotation.NoOpValueResolver
 *  io.micrometer.common.annotation.ValueExpressionResolver
 *  io.micrometer.common.annotation.ValueResolver
 *  io.micrometer.common.util.StringUtils
 */
package io.micrometer.core.aop;

import io.micrometer.common.KeyValue;
import io.micrometer.common.annotation.AnnotationHandler;
import io.micrometer.common.annotation.NoOpValueResolver;
import io.micrometer.common.annotation.ValueExpressionResolver;
import io.micrometer.common.annotation.ValueResolver;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.aop.MeterTag;
import io.micrometer.core.instrument.Timer;
import java.util.function.Function;

public class MeterTagAnnotationHandler
extends AnnotationHandler<Timer.Builder> {
    public MeterTagAnnotationHandler(Function<Class<? extends ValueResolver>, ? extends ValueResolver> resolverProvider, Function<Class<? extends ValueExpressionResolver>, ? extends ValueExpressionResolver> expressionResolverProvider) {
        super((keyValue, builder) -> builder.tag(keyValue.getKey(), keyValue.getValue()), resolverProvider, expressionResolverProvider, MeterTag.class, (annotation, o) -> {
            if (!(annotation instanceof MeterTag)) {
                return null;
            }
            MeterTag meterTag = (MeterTag)annotation;
            return KeyValue.of((String)MeterTagAnnotationHandler.resolveTagKey(meterTag), (String)MeterTagAnnotationHandler.resolveTagValue(meterTag, o, resolverProvider, expressionResolverProvider));
        });
    }

    private static String resolveTagKey(MeterTag annotation) {
        return StringUtils.isNotBlank((String)annotation.value()) ? annotation.value() : annotation.key();
    }

    static String resolveTagValue(MeterTag annotation, Object argument, Function<Class<? extends ValueResolver>, ? extends ValueResolver> resolverProvider, Function<Class<? extends ValueExpressionResolver>, ? extends ValueExpressionResolver> expressionResolverProvider) {
        String value = null;
        if (annotation.resolver() != NoOpValueResolver.class) {
            ValueResolver valueResolver = resolverProvider.apply(annotation.resolver());
            value = valueResolver.resolve(argument);
        } else if (StringUtils.isNotBlank((String)annotation.expression())) {
            value = expressionResolverProvider.apply(ValueExpressionResolver.class).resolve(annotation.expression(), argument);
        } else if (argument != null) {
            value = argument.toString();
        }
        return value == null ? "" : value;
    }
}

