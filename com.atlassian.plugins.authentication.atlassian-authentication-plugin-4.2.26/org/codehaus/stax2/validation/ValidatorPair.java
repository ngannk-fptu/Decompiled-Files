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

    public ValidatorPair(XMLValidator xMLValidator, XMLValidator xMLValidator2) {
        this.mFirst = xMLValidator;
        this.mSecond = xMLValidator2;
    }

    public XMLValidationSchema getSchema() {
        return null;
    }

    public void validateElementStart(String string, String string2, String string3) throws XMLStreamException {
        this.mFirst.validateElementStart(string, string2, string3);
        this.mSecond.validateElementStart(string, string2, string3);
    }

    public String validateAttribute(String string, String string2, String string3, String string4) throws XMLStreamException {
        String string5 = this.mFirst.validateAttribute(string, string2, string3, string4);
        if (string5 != null) {
            string4 = string5;
        }
        return this.mSecond.validateAttribute(string, string2, string3, string4);
    }

    public String validateAttribute(String string, String string2, String string3, char[] cArray, int n, int n2) throws XMLStreamException {
        String string4 = this.mFirst.validateAttribute(string, string2, string3, cArray, n, n2);
        if (string4 != null) {
            return this.mSecond.validateAttribute(string, string2, string3, string4);
        }
        return this.mSecond.validateAttribute(string, string2, string3, cArray, n, n2);
    }

    public int validateElementAndAttributes() throws XMLStreamException {
        int n;
        int n2 = this.mFirst.validateElementAndAttributes();
        return n2 < (n = this.mSecond.validateElementAndAttributes()) ? n2 : n;
    }

    public int validateElementEnd(String string, String string2, String string3) throws XMLStreamException {
        int n;
        int n2 = this.mFirst.validateElementEnd(string, string2, string3);
        return n2 < (n = this.mSecond.validateElementEnd(string, string2, string3)) ? n2 : n;
    }

    public void validateText(String string, boolean bl) throws XMLStreamException {
        this.mFirst.validateText(string, bl);
        this.mSecond.validateText(string, bl);
    }

    public void validateText(char[] cArray, int n, int n2, boolean bl) throws XMLStreamException {
        this.mFirst.validateText(cArray, n, n2, bl);
        this.mSecond.validateText(cArray, n, n2, bl);
    }

    public void validationCompleted(boolean bl) throws XMLStreamException {
        this.mFirst.validationCompleted(bl);
        this.mSecond.validationCompleted(bl);
    }

    public String getAttributeType(int n) {
        String string;
        String string2 = this.mFirst.getAttributeType(n);
        if ((string2 == null || string2.length() == 0 || string2.equals(ATTR_TYPE_DEFAULT)) && (string = this.mSecond.getAttributeType(n)) != null && string.length() > 0) {
            return string;
        }
        return string2;
    }

    public int getIdAttrIndex() {
        int n = this.mFirst.getIdAttrIndex();
        if (n < 0) {
            return this.mSecond.getIdAttrIndex();
        }
        return n;
    }

    public int getNotationAttrIndex() {
        int n = this.mFirst.getNotationAttrIndex();
        if (n < 0) {
            return this.mSecond.getNotationAttrIndex();
        }
        return n;
    }

    public static boolean removeValidator(XMLValidator xMLValidator, XMLValidationSchema xMLValidationSchema, XMLValidator[] xMLValidatorArray) {
        if (xMLValidator instanceof ValidatorPair) {
            return ((ValidatorPair)xMLValidator).doRemoveValidator(xMLValidationSchema, xMLValidatorArray);
        }
        if (xMLValidator.getSchema() == xMLValidationSchema) {
            xMLValidatorArray[0] = xMLValidator;
            xMLValidatorArray[1] = null;
            return true;
        }
        return false;
    }

    public static boolean removeValidator(XMLValidator xMLValidator, XMLValidator xMLValidator2, XMLValidator[] xMLValidatorArray) {
        if (xMLValidator == xMLValidator2) {
            xMLValidatorArray[0] = xMLValidator;
            xMLValidatorArray[1] = null;
            return true;
        }
        if (xMLValidator instanceof ValidatorPair) {
            return ((ValidatorPair)xMLValidator).doRemoveValidator(xMLValidator2, xMLValidatorArray);
        }
        return false;
    }

    private boolean doRemoveValidator(XMLValidationSchema xMLValidationSchema, XMLValidator[] xMLValidatorArray) {
        if (ValidatorPair.removeValidator(this.mFirst, xMLValidationSchema, xMLValidatorArray)) {
            XMLValidator xMLValidator = xMLValidatorArray[1];
            if (xMLValidator == null) {
                xMLValidatorArray[1] = this.mSecond;
            } else {
                this.mFirst = xMLValidator;
                xMLValidatorArray[1] = this;
            }
            return true;
        }
        if (ValidatorPair.removeValidator(this.mSecond, xMLValidationSchema, xMLValidatorArray)) {
            XMLValidator xMLValidator = xMLValidatorArray[1];
            if (xMLValidator == null) {
                xMLValidatorArray[1] = this.mFirst;
            } else {
                this.mSecond = xMLValidator;
                xMLValidatorArray[1] = this;
            }
            return true;
        }
        return false;
    }

    private boolean doRemoveValidator(XMLValidator xMLValidator, XMLValidator[] xMLValidatorArray) {
        if (ValidatorPair.removeValidator(this.mFirst, xMLValidator, xMLValidatorArray)) {
            XMLValidator xMLValidator2 = xMLValidatorArray[1];
            if (xMLValidator2 == null) {
                xMLValidatorArray[1] = this.mSecond;
            } else {
                this.mFirst = xMLValidator2;
                xMLValidatorArray[1] = this;
            }
            return true;
        }
        if (ValidatorPair.removeValidator(this.mSecond, xMLValidator, xMLValidatorArray)) {
            XMLValidator xMLValidator3 = xMLValidatorArray[1];
            if (xMLValidator3 == null) {
                xMLValidatorArray[1] = this.mFirst;
            } else {
                this.mSecond = xMLValidator3;
                xMLValidatorArray[1] = this;
            }
            return true;
        }
        return false;
    }
}

