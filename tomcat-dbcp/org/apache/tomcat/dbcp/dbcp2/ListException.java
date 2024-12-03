/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.util.List;

public class ListException
extends Exception {
    private static final long serialVersionUID = 1L;
    private final List<Throwable> exceptionList;

    public ListException(String message, List<Throwable> exceptionList) {
        super(message);
        this.exceptionList = exceptionList;
    }

    public List<Throwable> getExceptionList() {
        return this.exceptionList;
    }
}

