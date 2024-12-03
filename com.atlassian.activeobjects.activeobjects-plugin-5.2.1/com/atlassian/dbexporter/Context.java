/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class Context {
    private final Collection<Object> context = new LinkedList<Object>();

    public Context(Object ... objects) {
        this.context.addAll(Arrays.asList(objects));
    }

    public <T> T get(Class<T> type) {
        Collection<T> zeroOrOne = this.getZeroOrOneElement(type);
        return zeroOrOne.isEmpty() ? null : (T)Context.first(zeroOrOne);
    }

    public <T> Collection<T> getAll(Class<T> type) {
        return this.filter(type);
    }

    public Context put(Object obj) {
        this.context.add(obj);
        return this;
    }

    public Context putAll(Collection<?> objs) {
        this.context.addAll(objs);
        return this;
    }

    private <T> Collection<T> getZeroOrOneElement(Class<T> type) {
        return this.checkZeroOrOneElement(type, this.getAll(type));
    }

    private <T> Collection<T> checkZeroOrOneElement(Class<T> type, Collection<T> c) {
        if (c.size() > 1) {
            throw new IllegalStateException("Found more than one element of type " + type.getName() + " in import context!");
        }
        return c;
    }

    private <T> Collection<T> filter(Class<T> type) {
        return this.context.stream().filter(type::isInstance).collect(Collectors.toList());
    }

    private static <T> T first(Iterable<T> c) {
        return StreamSupport.stream(c.spliterator(), false).collect(Collectors.toList()).get(0);
    }
}

