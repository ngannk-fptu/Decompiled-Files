/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidator;

public interface XMLValidationSchema {
    public static final String SCHEMA_ID_DTD = "http://www.w3.org/XML/1998/namespace";
    public static final String SCHEMA_ID_RELAXNG = "http://relaxng.org/ns/structure/0.9";
    public static final String SCHEMA_ID_W3C_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    public static final String SCHEMA_ID_TREX = "http://www.thaiopensource.com/trex";

    public XMLValidator createValidator(ValidationContext var1) throws XMLStreamException;

    public String getSchemaType();
}

