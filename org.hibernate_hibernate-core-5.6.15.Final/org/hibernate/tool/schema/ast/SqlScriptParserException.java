/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.ast;

import org.hibernate.HibernateException;

public class SqlScriptParserException
extends HibernateException {
    public SqlScriptParserException(String message) {
        super(message);
    }

    public SqlScriptParserException(String message, Throwable cause) {
        super(message, cause);
    }
}

