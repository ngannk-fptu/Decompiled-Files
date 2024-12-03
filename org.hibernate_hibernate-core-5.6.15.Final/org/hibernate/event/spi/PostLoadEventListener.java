/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.event.spi.PostLoadEvent;

public interface PostLoadEventListener
extends Serializable {
    public void onPostLoad(PostLoadEvent var1);
}

