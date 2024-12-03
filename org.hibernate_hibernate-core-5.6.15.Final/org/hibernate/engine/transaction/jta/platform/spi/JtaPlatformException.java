/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.transaction.jta.platform.spi;

import org.hibernate.HibernateException;

public class JtaPlatformException
extends HibernateException {
    public JtaPlatformException(String s) {
        super(s);
    }

    public JtaPlatformException(String string, Throwable root) {
        super(string, root);
    }
}

