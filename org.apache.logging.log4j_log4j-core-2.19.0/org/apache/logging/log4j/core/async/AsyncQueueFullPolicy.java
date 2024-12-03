/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 */
package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.async.EventRoute;

public interface AsyncQueueFullPolicy {
    public EventRoute getRoute(long var1, Level var3);
}

