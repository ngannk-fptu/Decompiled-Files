/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.mapper.AbstractXmlFriendlyMapper;
import com.thoughtworks.xstream.mapper.Mapper;

public class XStream11XmlFriendlyMapper
extends AbstractXmlFriendlyMapper {
    public XStream11XmlFriendlyMapper(Mapper wrapped) {
        super(wrapped);
    }

    public Class realClass(String elementName) {
        return super.realClass(this.unescapeClassName(elementName));
    }

    public String realMember(Class type, String serialized) {
        return this.unescapeFieldName(super.realMember(type, serialized));
    }

    public String mapNameFromXML(String xmlName) {
        return this.unescapeFieldName(xmlName);
    }
}

