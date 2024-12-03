/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.DirtyCheckEvent;

public interface DirtyCheckEventListener
extends Serializable {
    public void onDirtyCheck(DirtyCheckEvent var1) throws HibernateException;
}

