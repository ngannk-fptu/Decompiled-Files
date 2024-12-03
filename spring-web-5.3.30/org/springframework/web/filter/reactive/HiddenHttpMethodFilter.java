/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.filter.reactive;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class HiddenHttpMethodFilter
implements WebFilter {
    private static final List<HttpMethod> ALLOWED_METHODS = Collections.unmodifiableList(Arrays.asList(HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH));
    public static final String DEFAULT_METHOD_PARAMETER_NAME = "_method";
    private String methodParamName = "_method";

    public void setMethodParamName(String methodParamName) {
        Assert.hasText((String)methodParamName, (String)"'methodParamName' must not be empty");
        this.methodParamName = methodParamName;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange2, WebFilterChain chain) {
        if (exchange2.getRequest().getMethod() != HttpMethod.POST) {
            return chain.filter(exchange2);
        }
        return exchange2.getFormData().map(formData -> {
            String method = (String)formData.getFirst((Object)this.methodParamName);
            return StringUtils.hasLength((String)method) ? this.mapExchange(exchange2, method) : exchange2;
        }).flatMap(chain::filter);
    }

    private ServerWebExchange mapExchange(ServerWebExchange exchange2, String methodParamValue) {
        HttpMethod httpMethod = HttpMethod.resolve(methodParamValue.toUpperCase(Locale.ENGLISH));
        Assert.notNull((Object)((Object)httpMethod), () -> "HttpMethod '" + methodParamValue + "' not supported");
        if (ALLOWED_METHODS.contains((Object)httpMethod)) {
            return exchange2.mutate().request(builder -> builder.method(httpMethod)).build();
        }
        return exchange2;
    }
}

