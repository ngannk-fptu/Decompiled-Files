/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy;

import java.io.Serializable;
import org.hibernate.proxy.LazyInitializer;

public interface HibernateProxy
extends Serializable {
    public Object writeReplace();

    public LazyInitializer getHibernateLazyInitializer();
}

