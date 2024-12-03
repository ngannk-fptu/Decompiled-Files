/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.ClearEvent;

public interface ClearEventListener
extends Serializable {
    public void onClear(ClearEvent var1);
}

