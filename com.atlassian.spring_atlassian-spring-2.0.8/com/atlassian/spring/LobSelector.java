/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.support.lob.LobHandler
 */
package com.atlassian.spring;

import org.springframework.jdbc.support.lob.LobHandler;

public class LobSelector {
    LobHandler defaultHandler;

    public LobHandler getLobHandler() {
        return this.defaultHandler;
    }

    public void setDefaultHandler(LobHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }
}

