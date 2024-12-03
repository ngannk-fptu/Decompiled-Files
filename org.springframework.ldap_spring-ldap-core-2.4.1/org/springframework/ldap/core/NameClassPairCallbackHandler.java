/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.NameClassPair;
import javax.naming.NamingException;

public interface NameClassPairCallbackHandler {
    public void handleNameClassPair(NameClassPair var1) throws NamingException;
}

