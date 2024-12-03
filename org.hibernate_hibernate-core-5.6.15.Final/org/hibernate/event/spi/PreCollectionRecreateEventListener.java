/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PreCollectionRecreateEvent;

public interface PreCollectionRecreateEventListener
extends Serializable {
    public void onPreRecreateCollection(PreCollectionRecreateEvent var1);
}

