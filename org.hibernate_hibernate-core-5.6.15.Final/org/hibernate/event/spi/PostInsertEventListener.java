/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PostActionEventListener;
import org.hibernate.event.spi.PostInsertEvent;

public interface PostInsertEventListener
extends Serializable,
PostActionEventListener {
    public void onPostInsert(PostInsertEvent var1);
}

