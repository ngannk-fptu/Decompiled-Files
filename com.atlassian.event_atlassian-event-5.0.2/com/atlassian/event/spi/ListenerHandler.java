/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.event.spi;

import com.atlassian.event.spi.ListenerInvoker;
import java.util.List;

public interface ListenerHandler {
    public List<ListenerInvoker> getInvokers(Object var1);
}

