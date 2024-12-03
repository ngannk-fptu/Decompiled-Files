/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.ApplicationCertificate;
import java.security.Principal;

@Deprecated
public interface UserResolver {
    public Principal resolve(ApplicationCertificate var1);
}

