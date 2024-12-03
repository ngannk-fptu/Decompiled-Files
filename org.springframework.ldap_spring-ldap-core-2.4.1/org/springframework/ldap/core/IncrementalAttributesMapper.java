/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import org.springframework.ldap.core.AttributesMapper;

public interface IncrementalAttributesMapper<T extends IncrementalAttributesMapper>
extends AttributesMapper<T> {
    public List<Object> getValues(String var1);

    public Attributes getCollectedAttributes();

    public boolean hasMore();

    public String[] getAttributesForLookup();

    @Override
    public T mapFromAttributes(Attributes var1) throws NamingException;
}

