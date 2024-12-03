/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.node.stax;

import java.util.Arrays;
import java.util.List;

public final class XmlFactoryException
extends RuntimeException {
    private final List<Throwable> throwables;

    public XmlFactoryException(String s, Throwable ... throwables) {
        super(s, throwables[throwables.length - 1]);
        this.throwables = Arrays.asList(throwables);
    }

    public List<Throwable> getThrowables() {
        return this.throwables;
    }
}

