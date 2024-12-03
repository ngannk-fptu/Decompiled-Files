/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.xml;

import org.hibernate.HibernateException;

public class XsdException
extends HibernateException {
    private final String xsdName;

    public XsdException(String message, String xsdName) {
        super(message);
        this.xsdName = xsdName;
    }

    public XsdException(String message, Throwable root, String xsdName) {
        super(message, root);
        this.xsdName = xsdName;
    }

    public String getXsdName() {
        return this.xsdName;
    }
}

