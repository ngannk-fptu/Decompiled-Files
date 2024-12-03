/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractAttributeAliasingMapper
extends MapperWrapper {
    protected final Map aliasToName = new HashMap();
    protected transient Map nameToAlias = new HashMap();

    public AbstractAttributeAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    public void addAliasFor(String attributeName, String alias) {
        this.aliasToName.put(alias, attributeName);
        this.nameToAlias.put(attributeName, alias);
    }

    Object readResolve() {
        this.nameToAlias = new HashMap();
        Iterator iter = this.aliasToName.keySet().iterator();
        while (iter.hasNext()) {
            Object alias = iter.next();
            this.nameToAlias.put(this.aliasToName.get(alias), alias);
        }
        return this;
    }
}

