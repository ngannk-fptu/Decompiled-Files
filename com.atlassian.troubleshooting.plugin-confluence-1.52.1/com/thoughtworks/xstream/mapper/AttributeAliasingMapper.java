/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.mapper.AbstractAttributeAliasingMapper;
import com.thoughtworks.xstream.mapper.Mapper;

public class AttributeAliasingMapper
extends AbstractAttributeAliasingMapper {
    public AttributeAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    public String aliasForAttribute(String attribute) {
        String alias = (String)this.nameToAlias.get(attribute);
        return alias == null ? super.aliasForAttribute(attribute) : alias;
    }

    public String attributeForAlias(String alias) {
        String name = (String)this.aliasToName.get(alias);
        return name == null ? super.attributeForAlias(alias) : name;
    }
}

