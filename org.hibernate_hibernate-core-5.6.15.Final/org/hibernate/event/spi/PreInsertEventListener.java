/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PreInsertEvent;

public interface PreInsertEventListener
extends Serializable {
    public boolean onPreInsert(PreInsertEvent var1);
}

