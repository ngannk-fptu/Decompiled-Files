/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PreDeleteEvent;

public interface PreDeleteEventListener
extends Serializable {
    public boolean onPreDelete(PreDeleteEvent var1);
}

