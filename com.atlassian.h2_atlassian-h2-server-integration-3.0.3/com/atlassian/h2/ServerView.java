/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.h2;

import java.net.URI;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface ServerView {
    public boolean isRunning();

    public URI getUri();
}

