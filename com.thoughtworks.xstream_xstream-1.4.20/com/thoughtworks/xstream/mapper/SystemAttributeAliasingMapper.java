/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.mapper.AbstractAttributeAliasingMapper;
import com.thoughtworks.xstream.mapper.Mapper;

public class SystemAttributeAliasingMapper
extends AbstractAttributeAliasingMapper {
    public SystemAttributeAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    public String aliasForSystemAttribute(String attribute) {
        String alias = (String)this.nameToAlias.get(attribute);
        if (alias == null && !this.nameToAlias.containsKey(attribute) && (alias = super.aliasForSystemAttribute(attribute)) == attribute) {
            alias = super.aliasForAttribute(attribute);
        }
        return alias;
    }
}

