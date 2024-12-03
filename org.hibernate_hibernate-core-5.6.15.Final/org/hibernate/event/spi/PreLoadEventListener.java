/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PreLoadEvent;

public interface PreLoadEventListener
extends Serializable {
    public void onPreLoad(PreLoadEvent var1);
}

