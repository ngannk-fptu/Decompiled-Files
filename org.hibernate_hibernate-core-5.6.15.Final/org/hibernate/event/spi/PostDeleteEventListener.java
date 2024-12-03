/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PostActionEventListener;
import org.hibernate.event.spi.PostDeleteEvent;

public interface PostDeleteEventListener
extends Serializable,
PostActionEventListener {
    public void onPostDelete(PostDeleteEvent var1);
}

