/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 */
package com.google.inject;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.Errors;
import com.google.inject.spi.Message;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ConfigurationException
extends RuntimeException {
    private final ImmutableSet<Message> messages;
    private Object partialValue = null;
    private static final long serialVersionUID = 0L;

    public ConfigurationException(Iterable<Message> messages) {
        this.messages = ImmutableSet.copyOf(messages);
        this.initCause(Errors.getOnlyCause(this.messages));
    }

    public ConfigurationException withPartialValue(Object partialValue) {
        Preconditions.checkState((this.partialValue == null ? 1 : 0) != 0, (String)"Can't clobber existing partial value %s with %s", (Object[])new Object[]{this.partialValue, partialValue});
        ConfigurationException result = new ConfigurationException((Iterable<Message>)this.messages);
        result.partialValue = partialValue;
        return result;
    }

    public Collection<Message> getErrorMessages() {
        return this.messages;
    }

    public <E> E getPartialValue() {
        return (E)this.partialValue;
    }

    @Override
    public String getMessage() {
        return Errors.format("Guice configuration errors", this.messages);
    }
}

