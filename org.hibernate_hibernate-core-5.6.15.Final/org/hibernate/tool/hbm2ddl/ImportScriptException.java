/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import org.hibernate.HibernateException;

@Deprecated
public class ImportScriptException
extends HibernateException {
    public ImportScriptException(String s) {
        super(s);
    }

    public ImportScriptException(String string, Throwable root) {
        super(string, root);
    }
}

