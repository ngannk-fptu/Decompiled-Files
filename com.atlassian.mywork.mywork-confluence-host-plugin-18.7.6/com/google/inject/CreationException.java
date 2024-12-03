/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject;

import com.google.inject.internal.Errors;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.Message;
import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CreationException
extends RuntimeException {
    private final $ImmutableSet<Message> messages;
    private static final long serialVersionUID = 0L;

    public CreationException(Collection<Message> messages) {
        this.messages = $ImmutableSet.copyOf(messages);
        $Preconditions.checkArgument(!this.messages.isEmpty());
        this.initCause(Errors.getOnlyCause(this.messages));
    }

    public Collection<Message> getErrorMessages() {
        return this.messages;
    }

    @Override
    public String getMessage() {
        return Errors.format("Guice creation errors", this.messages);
    }
}

