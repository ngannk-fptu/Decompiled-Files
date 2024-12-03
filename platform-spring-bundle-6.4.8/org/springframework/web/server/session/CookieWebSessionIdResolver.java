/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.server.session;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.WebSessionIdResolver;

public class CookieWebSessionIdResolver
implements WebSessionIdResolver {
    private String cookieName = "SESSION";
    private Duration cookieMaxAge = Duration.ofSeconds(-1L);
    @Nullable
    private Consumer<ResponseCookie.ResponseCookieBuilder> cookieInitializer = null;

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

    public void addCookieInitializer(Consumer<ResponseCookie.ResponseCookieBuilder> initializer) {
        this.cookieInitializer = this.cookieInitializer != null ? this.cookieInitializer.andThen(initializer) : initializer;
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
        ResponseCookie cookie = this.initSessionCookie(exchange2, id, this.getCookieMaxAge());
        exchange2.getResponse().getCookies().set(this.cookieName, cookie);
    }

    @Override
    public void expireSession(ServerWebExchange exchange2) {
        ResponseCookie cookie = this.initSessionCookie(exchange2, "", Duration.ZERO);
        exchange2.getResponse().getCookies().set(this.cookieName, cookie);
    }

    private ResponseCookie initSessionCookie(ServerWebExchange exchange2, String id, Duration maxAge) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(this.cookieName, id).path(exchange2.getRequest().getPath().contextPath().value() + "/").maxAge(maxAge).httpOnly(true).secure("https".equalsIgnoreCase(exchange2.getRequest().getURI().getScheme())).sameSite("Lax");
        if (this.cookieInitializer != null) {
            this.cookieInitializer.accept(cookieBuilder);
        }
        return cookieBuilder.build();
    }
}

