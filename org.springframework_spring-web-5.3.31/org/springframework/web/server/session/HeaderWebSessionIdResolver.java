/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.web.server.session;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.WebSessionIdResolver;

public class HeaderWebSessionIdResolver
implements WebSessionIdResolver {
    public static final String DEFAULT_HEADER_NAME = "SESSION";
    private String headerName = "SESSION";

    public void setHeaderName(String headerName) {
        Assert.hasText((String)headerName, (String)"'headerName' must not be empty");
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return this.headerName;
    }

    @Override
    public List<String> resolveSessionIds(ServerWebExchange exchange2) {
        HttpHeaders headers = exchange2.getRequest().getHeaders();
        return (List)headers.getOrDefault(this.getHeaderName(), Collections.emptyList());
    }

    @Override
    public void setSessionId(ServerWebExchange exchange2, String id) {
        Assert.notNull((Object)id, (String)"'id' is required.");
        exchange2.getResponse().getHeaders().set(this.getHeaderName(), id);
    }

    @Override
    public void expireSession(ServerWebExchange exchange2) {
        this.setSessionId(exchange2, "");
    }
}

