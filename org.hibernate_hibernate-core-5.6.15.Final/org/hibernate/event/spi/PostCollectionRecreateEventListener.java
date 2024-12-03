/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PostCollectionRecreateEvent;

public interface PostCollectionRecreateEventListener
extends Serializable {
    public void onPostRecreateCollection(PostCollectionRecreateEvent var1);
}

