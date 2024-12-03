/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.hsqldb;

import com.atlassian.hsqldb.ServerState;
import java.net.URI;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface ServerView {
    public ServerState getState();

    public URI getUri();
}

