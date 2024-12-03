/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.auth;

import java.security.Principal;

public interface Credentials {
    public Principal getUserPrincipal();

    public char[] getPassword();
}

