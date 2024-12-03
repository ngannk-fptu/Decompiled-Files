/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PostCollectionUpdateEvent;

public interface PostCollectionUpdateEventListener
extends Serializable {
    public void onPostUpdateCollection(PostCollectionUpdateEvent var1);
}

