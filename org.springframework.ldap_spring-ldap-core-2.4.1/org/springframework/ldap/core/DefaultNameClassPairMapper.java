/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.NameClassPair;
import javax.naming.NamingException;
import org.springframework.ldap.core.NameClassPairMapper;

public class DefaultNameClassPairMapper
implements NameClassPairMapper<String> {
    @Override
    public String mapFromNameClassPair(NameClassPair nameClassPair) throws NamingException {
        return nameClassPair.getName();
    }
}

