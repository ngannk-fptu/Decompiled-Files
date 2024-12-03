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
public final class ProvisionException
extends RuntimeException {
    private final ImmutableSet<Message> messages;
    private static final long serialVersionUID = 0L;

    public ProvisionException(Iterable<Message> messages) {
        this.messages = ImmutableSet.copyOf(messages);
        Preconditions.checkArgument((!this.messages.isEmpty() ? 1 : 0) != 0);
        this.initCause(Errors.getOnlyCause(this.messages));
    }

    public ProvisionException(String message, Throwable cause) {
        super(cause);
        this.messages = ImmutableSet.of((Object)new Message(message, cause));
    }

    public ProvisionException(String message) {
        this.messages = ImmutableSet.of((Object)new Message(message));
    }

    public Collection<Message> getErrorMessages() {
        return this.messages;
    }

    @Override
    public String getMessage() {
        return Errors.format("Guice provision errors", this.messages);
    }
}

