/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.io.Serializable;
import org.apache.jackrabbit.spi.Event;

public interface EventFilter
extends Serializable {
    public boolean accept(Event var1, boolean var2);
}

