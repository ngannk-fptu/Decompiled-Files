/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.core.util.FastField;
import com.thoughtworks.xstream.mapper.ElementIgnoringMapper;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FieldAliasingMapper
extends MapperWrapper {
    protected final Map fieldToAliasMap = new HashMap();
    protected final Map aliasToFieldMap = new HashMap();
    private final ElementIgnoringMapper elementIgnoringMapper = (ElementIgnoringMapper)this.lookupMapperOfType(ElementIgnoringMapper.class);
    static /* synthetic */ Class class$java$lang$Object;

    public FieldAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    public void addFieldAlias(String alias, Class type, String fieldName) {
        this.fieldToAliasMap.put(this.key(type, fieldName), alias);
        this.aliasToFieldMap.put(this.key(type, alias), fieldName);
    }

    public void addFieldsToIgnore(Pattern pattern) {
        if (this.elementIgnoringMapper != null) {
            this.elementIgnoringMapper.addElementsToIgnore(pattern);
        }
    }

    public void omitField(Class definedIn, String fieldName) {
        if (this.elementIgnoringMapper != null) {
            this.elementIgnoringMapper.omitField(definedIn, fieldName);
        }
    }

    private Object key(Class type, String name) {
        return new FastField(type, name);
    }

    public String serializedMember(Class type, String memberName) {
        String alias = this.getMember(type, memberName, this.fieldToAliasMap);
        if (alias == null) {
            return super.serializedMember(type, memberName);
        }
        return alias;
    }

    public String realMember(Class type, String serialized) {
        String real = this.getMember(type, serialized, this.aliasToFieldMap);
        if (real == null) {
            return super.realMember(type, serialized);
        }
        return real;
    }

    private String getMember(Class type, String name, Map map) {
        String member = null;
        for (Class declaringType = type; member == null && declaringType != (class$java$lang$Object == null ? FieldAliasingMapper.class$("java.lang.Object") : class$java$lang$Object) && declaringType != null; declaringType = declaringType.getSuperclass()) {
            member = (String)map.get(this.key(declaringType, name));
        }
        return member;
    }
}

