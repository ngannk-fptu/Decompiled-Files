/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;

public interface PostCommitInsertEventListener
extends PostInsertEventListener {
    public void onPostInsertCommitFailed(PostInsertEvent var1);
}

