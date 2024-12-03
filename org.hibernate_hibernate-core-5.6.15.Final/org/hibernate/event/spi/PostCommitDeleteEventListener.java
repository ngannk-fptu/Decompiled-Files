/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;

public interface PostCommitDeleteEventListener
extends PostDeleteEventListener {
    public void onPostDeleteCommitFailed(PostDeleteEvent var1);
}

