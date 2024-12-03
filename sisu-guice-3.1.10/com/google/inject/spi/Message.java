/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.google.inject.spi;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.internal.Errors;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Message
implements Serializable,
Element {
    private final String message;
    private final Throwable cause;
    private final List<Object> sources;
    private static final long serialVersionUID = 0L;

    public Message(List<Object> sources, String message, Throwable cause) {
        this.sources = ImmutableList.copyOf(sources);
        this.message = (String)Preconditions.checkNotNull((Object)message, (Object)"message");
        this.cause = cause;
    }

    public Message(String message, Throwable cause) {
        this((List<Object>)ImmutableList.of(), message, cause);
    }

    public Message(Object source, String message) {
        this((List<Object>)ImmutableList.of((Object)source), message, null);
    }

    public Message(String message) {
        this((List<Object>)ImmutableList.of(), message, null);
    }

    @Override
    public String getSource() {
        return this.sources.isEmpty() ? SourceProvider.UNKNOWN_SOURCE.toString() : Errors.convert(this.sources.get(this.sources.size() - 1)).toString();
    }

    public List<Object> getSources() {
        return this.sources;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Throwable getCause() {
        return this.cause;
    }

    public String toString() {
        return this.message;
    }

    public int hashCode() {
        return this.message.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Message)) {
            return false;
        }
        Message e = (Message)o;
        return this.message.equals(e.message) && Objects.equal((Object)this.cause, (Object)e.cause) && ((Object)this.sources).equals(e.sources);
    }

    @Override
    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).addError(this);
    }

    private Object writeReplace() throws ObjectStreamException {
        Object[] sourcesAsStrings = this.sources.toArray();
        for (int i = 0; i < sourcesAsStrings.length; ++i) {
            sourcesAsStrings[i] = Errors.convert(sourcesAsStrings[i]).toString();
        }
        return new Message((List<Object>)ImmutableList.copyOf((Object[])sourcesAsStrings), this.message, this.cause);
    }
}

