/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.beans.container.internal;

import org.hibernate.HibernateException;

public class NotYetReadyException
extends HibernateException {
    public NotYetReadyException(Throwable cause) {
        super("CDI BeanManager not (yet) ready to use", cause);
    }
}

