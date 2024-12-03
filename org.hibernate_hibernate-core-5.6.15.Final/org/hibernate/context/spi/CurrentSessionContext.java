/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.context.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public interface CurrentSessionContext
extends Serializable {
    public Session currentSession() throws HibernateException;
}

