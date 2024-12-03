/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.springframework.ldap.core.CollectingNameClassPairCallbackHandler
 *  org.springframework.ldap.core.DirContextAdapter
 */
package com.atlassian.crowd.directory.ldap.mapper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.naming.Binding;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler;
import org.springframework.ldap.core.DirContextAdapter;

public class LookupCallbackHandler<T>
extends CollectingNameClassPairCallbackHandler<T> {
    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input")
    public T getObjectFromNameClassPair(NameClassPair nameClassPair) throws NamingException {
        if (!(nameClassPair instanceof Binding)) {
            throw new IllegalArgumentException("Parameter must be an instance of Binding");
        }
        Binding binding = (Binding)nameClassPair;
        Object object = binding.getObject();
        if (object == null) {
            object = new DirContextAdapter(((SearchResult)nameClassPair).getAttributes(), (Name)new LdapName(nameClassPair.getNameInNamespace()));
        }
        return (T)object;
    }
}

