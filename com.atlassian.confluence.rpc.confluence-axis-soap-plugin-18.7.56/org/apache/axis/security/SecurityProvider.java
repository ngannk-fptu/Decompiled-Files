/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.security;

import org.apache.axis.MessageContext;
import org.apache.axis.security.AuthenticatedUser;

public interface SecurityProvider {
    public AuthenticatedUser authenticate(MessageContext var1);

    public boolean userMatches(AuthenticatedUser var1, String var2);
}

