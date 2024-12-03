/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;

public abstract class XMLValidator {
    public static final int CONTENT_ALLOW_NONE = 0;
    public static final int CONTENT_ALLOW_WS = 1;
    public static final int CONTENT_ALLOW_WS_NONSTRICT = 2;
    public static final int CONTENT_ALLOW_VALIDATABLE_TEXT = 3;
    public static final int CONTENT_ALLOW_ANY_TEXT = 4;
    public static final int CONTENT_ALLOW_UNDEFINED = 5;

    protected XMLValidator() {
    }

    public String getSchemaType() {
        XMLValidationSchema xMLValidationSchema = this.getSchema();
        return xMLValidationSchema == null ? null : xMLValidationSchema.getSchemaType();
    }

    public abstract XMLValidationSchema getSchema();

    public abstract void validateElementStart(String var1, String var2, String var3) throws XMLStreamException;

    public abstract String validateAttribute(String var1, String var2, String var3, String var4) throws XMLStreamException;

    public abstract String validateAttribute(String var1, String var2, String var3, char[] var4, int var5, int var6) throws XMLStreamException;

    public abstract int validateElementAndAttributes() throws XMLStreamException;

    public abstract int validateElementEnd(String var1, String var2, String var3) throws XMLStreamException;

    public abstract void validateText(String var1, boolean var2) throws XMLStreamException;

    public abstract void validateText(char[] var1, int var2, int var3, boolean var4) throws XMLStreamException;

    public abstract void validationCompleted(boolean var1) throws XMLStreamException;

    public abstract String getAttributeType(int var1);

    public abstract int getIdAttrIndex();

    public abstract int getNotationAttrIndex();
}

