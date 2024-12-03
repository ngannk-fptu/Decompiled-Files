/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.time.Duration;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class DurationJavaDescriptor
extends AbstractTypeDescriptor<Duration> {
    public static final DurationJavaDescriptor INSTANCE = new DurationJavaDescriptor();

    public DurationJavaDescriptor() {
        super(Duration.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public String toString(Duration value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value.toNanos());
    }

    @Override
    public Duration fromString(String string) {
        if (string == null) {
            return null;
        }
        return Duration.ofNanos(Long.parseLong(string));
    }

    @Override
    public <X> X unwrap(Duration duration, Class<X> type, WrapperOptions options) {
        if (duration == null) {
            return null;
        }
        if (Duration.class.isAssignableFrom(type)) {
            return (X)duration;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)duration.toString();
        }
        if (Long.class.isAssignableFrom(type)) {
            return (X)Long.valueOf(duration.toNanos());
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> Duration wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (Duration.class.isInstance(value)) {
            return (Duration)value;
        }
        if (Long.class.isInstance(value)) {
            return Duration.ofNanos((Long)value);
        }
        if (String.class.isInstance(value)) {
            return Duration.parse((String)value);
        }
        throw this.unknownWrap(value.getClass());
    }
}

