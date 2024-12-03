/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;

public interface PostCommitUpdateEventListener
extends PostUpdateEventListener {
    public void onPostUpdateCommitFailed(PostUpdateEvent var1);
}

