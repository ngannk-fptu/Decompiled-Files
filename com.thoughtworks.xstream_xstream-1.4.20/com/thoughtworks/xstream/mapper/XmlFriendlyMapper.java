/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.mapper.AbstractXmlFriendlyMapper;
import com.thoughtworks.xstream.mapper.Mapper;

public class XmlFriendlyMapper
extends AbstractXmlFriendlyMapper {
    public XmlFriendlyMapper(Mapper wrapped) {
        super(wrapped);
    }

    public String serializedClass(Class type) {
        return this.escapeClassName(super.serializedClass(type));
    }

    public Class realClass(String elementName) {
        return super.realClass(this.unescapeClassName(elementName));
    }

    public String serializedMember(Class type, String memberName) {
        return this.escapeFieldName(super.serializedMember(type, memberName));
    }

    public String realMember(Class type, String serialized) {
        return this.unescapeFieldName(super.realMember(type, serialized));
    }

    public String mapNameToXML(String javaName) {
        return this.escapeFieldName(javaName);
    }

    public String mapNameFromXML(String xmlName) {
        return this.unescapeFieldName(xmlName);
    }
}

