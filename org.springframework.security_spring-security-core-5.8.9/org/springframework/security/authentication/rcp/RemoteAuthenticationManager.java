/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.rcp;

import java.util.Collection;
import org.springframework.security.authentication.rcp.RemoteAuthenticationException;
import org.springframework.security.core.GrantedAuthority;

@Deprecated
public interface RemoteAuthenticationManager {
    public Collection<? extends GrantedAuthority> attemptAuthentication(String var1, String var2) throws RemoteAuthenticationException;
}

