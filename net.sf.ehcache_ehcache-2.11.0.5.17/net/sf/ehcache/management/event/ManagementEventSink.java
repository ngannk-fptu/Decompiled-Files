/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.event;

import java.io.Serializable;

public interface ManagementEventSink {
    public void sendManagementEvent(Serializable var1, String var2);
}

