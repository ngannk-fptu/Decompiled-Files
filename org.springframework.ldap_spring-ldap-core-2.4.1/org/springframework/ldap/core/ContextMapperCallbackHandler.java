/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.core;

import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.ObjectRetrievalException;
import org.springframework.util.Assert;

public class ContextMapperCallbackHandler<T>
extends CollectingNameClassPairCallbackHandler<T> {
    private ContextMapper<T> mapper;

    public ContextMapperCallbackHandler(ContextMapper<T> mapper) {
        Assert.notNull(mapper, (String)"Mapper must not be empty");
        this.mapper = mapper;
    }

    @Override
    public T getObjectFromNameClassPair(NameClassPair nameClassPair) throws NamingException {
        if (!(nameClassPair instanceof Binding)) {
            throw new IllegalArgumentException("Parameter must be an instance of Binding");
        }
        Binding binding = (Binding)nameClassPair;
        Object object = binding.getObject();
        if (object == null) {
            throw new ObjectRetrievalException("Binding did not contain any object.");
        }
        return this.mapper.mapFromContext(object);
    }
}

