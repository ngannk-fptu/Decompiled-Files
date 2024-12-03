/*
 * Decompiled with CFR 0.152.
 */
package com.onelogin.saml2.util;

import java.net.URL;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;

public abstract class SchemaFactory {
    public static final URL SAML_SCHEMA_METADATA_2_0 = SchemaFactory.class.getResource("/schemas/saml-schema-metadata-2.0.xsd");
    public static final URL SAML_SCHEMA_PROTOCOL_2_0 = SchemaFactory.class.getResource("/schemas/saml-schema-protocol-2.0.xsd");

    private SchemaFactory() {
    }

    public static Schema loadFromUrl(URL schemaUrl) throws SAXException {
        return javax.xml.validation.SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(schemaUrl);
    }
}

