/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PreCollectionRemoveEvent;

public interface PreCollectionRemoveEventListener
extends Serializable {
    public void onPreRemoveCollection(PreCollectionRemoveEvent var1);
}

