/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.xml;

import org.hibernate.HibernateException;

public class XmlInfrastructureException
extends HibernateException {
    public XmlInfrastructureException(String message) {
        super(message);
    }

    public XmlInfrastructureException(String message, Throwable root) {
        super(message, root);
    }
}

