/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PreUpdateEvent;

public interface PreUpdateEventListener
extends Serializable {
    public boolean onPreUpdate(PreUpdateEvent var1);
}

