/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast;

import org.hibernate.QueryException;
import org.hibernate.hql.internal.ast.ErrorReporter;

public interface ParseErrorHandler
extends ErrorReporter {
    public int getErrorCount();

    public void throwQueryException() throws QueryException;
}

