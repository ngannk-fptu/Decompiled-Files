/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server.session;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.WebSessionIdResolver;

public class CookieWebSessionIdResolver
implements WebSessionIdResolver {
    private String cookieName = "SESSION";
    private Duration cookieMaxAge = Duration.ofSeconds(-1L);

    public void setCookieName(String cookieName) {
        Assert.hasText(cookieName, "'cookieName' must not be empty");
        this.cookieName = cookieName;
    }

    public String getCookieName() {
        return this.cookieName;
    }

    public void setCookieMaxAge(Duration maxAge) {
        this.cookieMaxAge = maxAge;
    }

    public Duration getCookieMaxAge() {
        return this.cookieMaxAge;
    }

    @Override
    public List<String> resolveSessionIds(ServerWebExchange exchange2) {
        MultiValueMap<String, HttpCookie> cookieMap = exchange2.getRequest().getCookies();
        List cookies = (List)cookieMap.get(this.getCookieName());
        if (cookies == null) {
            return Collections.emptyList();
        }
        return cookies.stream().map(HttpCookie::getValue).collect(Collectors.toList());
    }

    @Override
    public void setSessionId(ServerWebExchange exchange2, String id) {
        Assert.notNull((Object)id, "'id' is required");
        this.setSessionCookie(exchange2, id, this.getCookieMaxAge());
    }

    @Override
    public void expireSession(ServerWebExchange exchange2) {
        this.setSessionCookie(exchange2, "", Duration.ofSeconds(0L));
    }

    private void setSessionCookie(ServerWebExchange exchange2, String id, Duration maxAge) {
        String name = this.getCookieName();
        boolean secure = "https".equalsIgnoreCase(exchange2.getRequest().getURI().getScheme());
        String path = exchange2.getRequest().getPath().contextPath().value() + "/";
        exchange2.getResponse().getCookies().set(name, ResponseCookie.from(name, id).path(path).maxAge(maxAge).httpOnly(true).secure(secure).build());
    }
}

