/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PreCollectionUpdateEvent;

public interface PreCollectionUpdateEventListener
extends Serializable {
    public void onPreUpdateCollection(PreCollectionUpdateEvent var1);
}

