/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PostActionEventListener;
import org.hibernate.event.spi.PostUpdateEvent;

public interface PostUpdateEventListener
extends Serializable,
PostActionEventListener {
    public void onPostUpdate(PostUpdateEvent var1);
}

