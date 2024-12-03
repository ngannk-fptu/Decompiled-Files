/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core.support;

import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.ldap.HasControls;
import org.springframework.ldap.core.ContextMapperCallbackHandler;
import org.springframework.ldap.core.ObjectRetrievalException;
import org.springframework.ldap.core.support.ContextMapperWithControls;

public class ContextMapperCallbackHandlerWithControls<T>
extends ContextMapperCallbackHandler<T> {
    private ContextMapperWithControls<T> mapper = null;

    public ContextMapperCallbackHandlerWithControls(ContextMapperWithControls<T> mapper) {
        super(mapper);
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
        Object result = nameClassPair instanceof HasControls ? this.mapper.mapFromContextWithControls(object, (HasControls)((Object)nameClassPair)) : this.mapper.mapFromContext(object);
        return result;
    }
}

