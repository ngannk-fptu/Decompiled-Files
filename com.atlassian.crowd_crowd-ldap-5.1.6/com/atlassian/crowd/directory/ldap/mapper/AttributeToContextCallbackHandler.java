/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.ldap.core.ContextMapper
 *  org.springframework.ldap.core.ContextMapperCallbackHandler
 *  org.springframework.ldap.core.DirContextAdapter
 *  org.springframework.ldap.core.ObjectRetrievalException
 */
package com.atlassian.crowd.directory.ldap.mapper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import javax.naming.Binding;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.ContextMapperCallbackHandler;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.ObjectRetrievalException;

public class AttributeToContextCallbackHandler<T>
extends ContextMapperCallbackHandler<T> {
    private final ContextMapper<T> mapper;

    public AttributeToContextCallbackHandler(ContextMapper<T> mapper) {
        super(mapper);
        Objects.requireNonNull(mapper, "Mapper must not be empty");
        this.mapper = mapper;
    }

    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input")
    public T getObjectFromNameClassPair(NameClassPair nameClassPair) throws NamingException {
        if (!(nameClassPair instanceof Binding)) {
            throw new IllegalArgumentException("Parameter must be an instance of Binding");
        }
        Binding binding = (Binding)nameClassPair;
        Object object = binding.getObject();
        if (object == null) {
            if (nameClassPair instanceof SearchResult) {
                object = new DirContextAdapter(((SearchResult)nameClassPair).getAttributes(), (Name)new LdapName(nameClassPair.getNameInNamespace()));
            } else {
                throw new ObjectRetrievalException("Binding did not contain any object.");
            }
        }
        return (T)this.mapper.mapFromContext(object);
    }
}

