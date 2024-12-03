/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.session;

import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public interface WebSessionStore {
    public Mono<WebSession> createWebSession();

    public Mono<WebSession> retrieveSession(String var1);

    public Mono<Void> removeSession(String var1);

    public Mono<WebSession> updateLastAccessTime(WebSession var1);
}

