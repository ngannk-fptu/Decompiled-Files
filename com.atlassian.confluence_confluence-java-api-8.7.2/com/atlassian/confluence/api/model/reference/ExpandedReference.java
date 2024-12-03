/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.codehaus.jackson.annotate.JsonCreator
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.reference.Reference;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;

@Internal
public final class ExpandedReference<T>
extends Reference<T> {
    private Map<Object, Object> idProperties;
    private T object;

    @JsonCreator
    ExpandedReference(T value) {
        super(true);
        this.object = value;
    }

    @Override
    public Iterator<T> iterator() {
        return this.object == null ? Collections.emptyIterator() : Collections.singletonList(this.object).iterator();
    }

    @Override
    public T get() {
        return this.object;
    }

    public Optional<T> asOptional() {
        return Optional.ofNullable(this.object);
    }

    @Override
    public boolean exists() {
        return this.object != null;
    }

    @Override
    public boolean isExpanded() {
        return true;
    }

    @Override
    public Map<Object, Object> getIdProperties() {
        if (this.idProperties == null) {
            this.idProperties = ExpandedReference.resolveIdProps(this.get());
        }
        return this.idProperties;
    }

    @Override
    public Class referentClass() {
        return this.object.getClass();
    }

    public String toString() {
        return "ExpandedReference{idProperties=" + this.getIdProperties() + ", object=" + this.object + '}';
    }
}

