/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.jaas;

import java.security.Principal;
import java.util.Set;

public interface AuthorityGranter {
    public Set<String> grant(Principal var1);
}

