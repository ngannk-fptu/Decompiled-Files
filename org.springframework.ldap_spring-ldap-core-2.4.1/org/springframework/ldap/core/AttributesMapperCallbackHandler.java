/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.support.LdapUtils;

public class AttributesMapperCallbackHandler<T>
extends CollectingNameClassPairCallbackHandler<T> {
    private AttributesMapper<T> mapper;

    public AttributesMapperCallbackHandler(AttributesMapper<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T getObjectFromNameClassPair(NameClassPair nameClassPair) {
        if (!(nameClassPair instanceof SearchResult)) {
            throw new IllegalArgumentException("Parameter must be an instance of SearchResult");
        }
        SearchResult searchResult = (SearchResult)nameClassPair;
        Attributes attributes = searchResult.getAttributes();
        try {
            return this.mapper.mapFromAttributes(attributes);
        }
        catch (NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
    }
}

