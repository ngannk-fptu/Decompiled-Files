/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authorization.method;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

final class ReactiveAuthenticationUtils {
    private static final Authentication ANONYMOUS = new AnonymousAuthenticationToken("key", (Object)"anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    static Mono<Authentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).defaultIfEmpty((Object)ANONYMOUS);
    }

    private ReactiveAuthenticationUtils() {
    }
}

