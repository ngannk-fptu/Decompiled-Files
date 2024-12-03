/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public class ValidatorPair
extends XMLValidator {
    public static final String ATTR_TYPE_DEFAULT = "CDATA";
    protected XMLValidator mFirst;
    protected XMLValidator mSecond;

    public ValidatorPair(XMLValidator first, XMLValidator second) {
        this.mFirst = first;
        this.mSecond = second;
    }

    @Override
    public XMLValidationSchema getSchema() {
        return null;
    }

    @Override
    public void validateElementStart(String localName, String uri, String prefix) throws XMLStreamException {
        this.mFirst.validateElementStart(localName, uri, prefix);
        this.mSecond.validateElementStart(localName, uri, prefix);
    }

    @Override
    public String validateAttribute(String localName, String uri, String prefix, String value) throws XMLStreamException {
        String retVal = this.mFirst.validateAttribute(localName, uri, prefix, value);
        if (retVal != null) {
            value = retVal;
        }
        return this.mSecond.validateAttribute(localName, uri, prefix, value);
    }

    @Override
    public String validateAttribute(String localName, String uri, String prefix, char[] valueChars, int valueStart, int valueEnd) throws XMLStreamException {
        String retVal = this.mFirst.validateAttribute(localName, uri, prefix, valueChars, valueStart, valueEnd);
        if (retVal != null) {
            return this.mSecond.validateAttribute(localName, uri, prefix, retVal);
        }
        return this.mSecond.validateAttribute(localName, uri, prefix, valueChars, valueStart, valueEnd);
    }

    @Override
    public int validateElementAndAttributes() throws XMLStreamException {
        int textType2;
        int textType1 = this.mFirst.validateElementAndAttributes();
        return textType1 < (textType2 = this.mSecond.validateElementAndAttributes()) ? textType1 : textType2;
    }

    @Override
    public int validateElementEnd(String localName, String uri, String prefix) throws XMLStreamException {
        int textType2;
        int textType1 = this.mFirst.validateElementEnd(localName, uri, prefix);
        return textType1 < (textType2 = this.mSecond.validateElementEnd(localName, uri, prefix)) ? textType1 : textType2;
    }

    @Override
    public void validateText(String text, boolean lastTextSegment) throws XMLStreamException {
        this.mFirst.validateText(text, lastTextSegment);
        this.mSecond.validateText(text, lastTextSegment);
    }

    @Override
    public void validateText(char[] cbuf, int textStart, int textEnd, boolean lastTextSegment) throws XMLStreamException {
        this.mFirst.validateText(cbuf, textStart, textEnd, lastTextSegment);
        this.mSecond.validateText(cbuf, textStart, textEnd, lastTextSegment);
    }

    @Override
    public void validationCompleted(boolean eod) throws XMLStreamException {
        this.mFirst.validationCompleted(eod);
        this.mSecond.validationCompleted(eod);
    }

    @Override
    public String getAttributeType(int index) {
        String type2;
        String type = this.mFirst.getAttributeType(index);
        if ((type == null || type.length() == 0 || type.equals(ATTR_TYPE_DEFAULT)) && (type2 = this.mSecond.getAttributeType(index)) != null && type2.length() > 0) {
            return type2;
        }
        return type;
    }

    @Override
    public int getIdAttrIndex() {
        int index = this.mFirst.getIdAttrIndex();
        if (index < 0) {
            return this.mSecond.getIdAttrIndex();
        }
        return index;
    }

    @Override
    public int getNotationAttrIndex() {
        int index = this.mFirst.getNotationAttrIndex();
        if (index < 0) {
            return this.mSecond.getNotationAttrIndex();
        }
        return index;
    }

    public static boolean removeValidator(XMLValidator root, XMLValidationSchema schema, XMLValidator[] results) {
        if (root instanceof ValidatorPair) {
            return ((ValidatorPair)root).doRemoveValidator(schema, results);
        }
        if (root.getSchema() == schema) {
            results[0] = root;
            results[1] = null;
            return true;
        }
        return false;
    }

    public static boolean removeValidator(XMLValidator root, XMLValidator vld, XMLValidator[] results) {
        if (root == vld) {
            results[0] = root;
            results[1] = null;
            return true;
        }
        if (root instanceof ValidatorPair) {
            return ((ValidatorPair)root).doRemoveValidator(vld, results);
        }
        return false;
    }

    private boolean doRemoveValidator(XMLValidationSchema schema, XMLValidator[] results) {
        if (ValidatorPair.removeValidator(this.mFirst, schema, results)) {
            XMLValidator newFirst = results[1];
            if (newFirst == null) {
                results[1] = this.mSecond;
            } else {
                this.mFirst = newFirst;
                results[1] = this;
            }
            return true;
        }
        if (ValidatorPair.removeValidator(this.mSecond, schema, results)) {
            XMLValidator newSecond = results[1];
            if (newSecond == null) {
                results[1] = this.mFirst;
            } else {
                this.mSecond = newSecond;
                results[1] = this;
            }
            return true;
        }
        return false;
    }

    private boolean doRemoveValidator(XMLValidator vld, XMLValidator[] results) {
        if (ValidatorPair.removeValidator(this.mFirst, vld, results)) {
            XMLValidator newFirst = results[1];
            if (newFirst == null) {
                results[1] = this.mSecond;
            } else {
                this.mFirst = newFirst;
                results[1] = this;
            }
            return true;
        }
        if (ValidatorPair.removeValidator(this.mSecond, vld, results)) {
            XMLValidator newSecond = results[1];
            if (newSecond == null) {
                results[1] = this.mFirst;
            } else {
                this.mSecond = newSecond;
                results[1] = this;
            }
            return true;
        }
        return false;
    }
}

