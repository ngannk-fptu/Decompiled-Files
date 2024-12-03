/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence;

import com.atlassian.plugins.authentication.impl.web.filter.authentication.confluence.ConfluenceActionResolver;
import java.util.Optional;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;

public class StaticConfluenceActionResolver
implements ConfluenceActionResolver {
    private final String loginActionClassName;
    private final String signUpActionClassName;

    StaticConfluenceActionResolver(String loginActionClassName, String signUpActionClassName) {
        this.loginActionClassName = loginActionClassName;
        this.signUpActionClassName = signUpActionClassName;
    }

    @Override
    public Optional<String> getActionConfigClassName(HttpServletRequest request) {
        return StaticConfluenceActionResolver.getResolverForClassNames(this.loginActionClassName, this.signUpActionClassName).apply(request);
    }

    public static Function<HttpServletRequest, Optional<String>> getResolverForClassNames(String loginClassName, String signUpClassName) {
        return request -> Optional.ofNullable(request.getServletPath()).map(path -> {
            if (path.equals("/login.action") || path.equals("/dologin.action")) {
                return loginClassName;
            }
            if (path.equals("/signup.action") || path.equals("/dosignup.action")) {
                return signUpClassName;
            }
            return null;
        });
    }
}

