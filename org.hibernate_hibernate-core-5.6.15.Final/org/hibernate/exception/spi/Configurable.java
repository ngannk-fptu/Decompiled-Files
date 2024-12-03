/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.exception.spi;

import java.util.Properties;
import org.hibernate.HibernateException;

public interface Configurable {
    public void configure(Properties var1) throws HibernateException;
}

