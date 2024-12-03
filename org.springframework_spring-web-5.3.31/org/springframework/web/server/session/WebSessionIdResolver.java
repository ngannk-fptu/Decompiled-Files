/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server.session;

import java.util.List;
import org.springframework.web.server.ServerWebExchange;

public interface WebSessionIdResolver {
    public List<String> resolveSessionIds(ServerWebExchange var1);

    public void setSessionId(ServerWebExchange var1, String var2);

    public void expireSession(ServerWebExchange var1);
}

