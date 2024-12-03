/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.DefaultServerWebExchangeBuilder;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public interface ServerWebExchange {
    public static final String LOG_ID_ATTRIBUTE = ServerWebExchange.class.getName() + ".LOG_ID";

    public ServerHttpRequest getRequest();

    public ServerHttpResponse getResponse();

    public Map<String, Object> getAttributes();

    @Nullable
    default public <T> T getAttribute(String name) {
        return (T)this.getAttributes().get(name);
    }

    default public <T> T getRequiredAttribute(String name) {
        T value = this.getAttribute(name);
        Assert.notNull(value, () -> "Required attribute '" + name + "' is missing");
        return value;
    }

    default public <T> T getAttributeOrDefault(String name, T defaultValue) {
        return (T)this.getAttributes().getOrDefault(name, defaultValue);
    }

    public Mono<WebSession> getSession();

    public <T extends Principal> Mono<T> getPrincipal();

    public Mono<MultiValueMap<String, String>> getFormData();

    public Mono<MultiValueMap<String, Part>> getMultipartData();

    public LocaleContext getLocaleContext();

    @Nullable
    public ApplicationContext getApplicationContext();

    public boolean isNotModified();

    public boolean checkNotModified(Instant var1);

    public boolean checkNotModified(String var1);

    public boolean checkNotModified(@Nullable String var1, Instant var2);

    public String transformUrl(String var1);

    public void addUrlTransformer(Function<String, String> var1);

    public String getLogPrefix();

    default public Builder mutate() {
        return new DefaultServerWebExchangeBuilder(this);
    }

    public static interface Builder {
        public Builder request(Consumer<ServerHttpRequest.Builder> var1);

        public Builder request(ServerHttpRequest var1);

        public Builder response(ServerHttpResponse var1);

        public Builder principal(Mono<Principal> var1);

        public ServerWebExchange build();
    }
}

