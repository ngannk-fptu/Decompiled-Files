/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.internal.XmlWriter;
import com.amazonaws.services.s3.model.Tag;

final class BucketConfigurationXmlFactoryFunctions {
    private BucketConfigurationXmlFactoryFunctions() {
    }

    static void addParameterIfNotNull(XmlWriter xml, String xmlTagName, Object value) {
        if (value != null) {
            xml.start(xmlTagName).value(String.valueOf(value)).end();
        }
    }

    static void writePrefix(XmlWriter xml, String prefix) {
        BucketConfigurationXmlFactoryFunctions.addParameterIfNotNull(xml, "Prefix", prefix);
    }

    static void writeTag(XmlWriter xml, Tag tag) {
        if (tag == null) {
            return;
        }
        xml.start("Tag");
        xml.start("Key").value(tag.getKey()).end();
        xml.start("Value").value(tag.getValue()).end();
        xml.end();
    }

    static void writeObjectSizeGreaterThan(XmlWriter xml, Long value) {
        BucketConfigurationXmlFactoryFunctions.addParameterIfNotNull(xml, "ObjectSizeGreaterThan", value);
    }

    static void writeObjectSizeLessThan(XmlWriter xml, Long value) {
        BucketConfigurationXmlFactoryFunctions.addParameterIfNotNull(xml, "ObjectSizeLessThan", value);
    }
}

