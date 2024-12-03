/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import javax.xml.namespace.QName;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.TypeValidator;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xs.datatypes.XSQName;

public class QNameDV
extends TypeValidator {
    private static final String EMPTY_STRING = "".intern();

    @Override
    public short getAllowedFacets() {
        return 2079;
    }

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        String string2;
        String string3;
        int n = string.indexOf(":");
        if (n > 0) {
            string3 = validationContext.getSymbol(string.substring(0, n));
            string2 = string.substring(n + 1);
        } else {
            string3 = EMPTY_STRING;
            string2 = string;
        }
        if (string3.length() > 0 && !XMLChar.isValidNCName(string3)) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "QName"});
        }
        if (!XMLChar.isValidNCName(string2)) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "QName"});
        }
        String string4 = validationContext.getURI(string3);
        if (string3.length() > 0 && string4 == null) {
            throw new InvalidDatatypeValueException("UndeclaredPrefix", new Object[]{string, string3});
        }
        return new XQName(string3, validationContext.getSymbol(string2), validationContext.getSymbol(string), string4);
    }

    @Override
    public int getDataLength(Object object) {
        return ((XQName)object).rawname.length();
    }

    private static final class XQName
    extends org.apache.xerces.xni.QName
    implements XSQName {
        public XQName(String string, String string2, String string3, String string4) {
            this.setValues(string, string2, string3, string4);
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof org.apache.xerces.xni.QName) {
                org.apache.xerces.xni.QName qName = (org.apache.xerces.xni.QName)object;
                return this.uri == qName.uri && this.localpart == qName.localpart;
            }
            return false;
        }

        @Override
        public String toString() {
            return this.rawname;
        }

        @Override
        public QName getJAXPQName() {
            return new QName(this.uri, this.localpart, this.prefix);
        }

        @Override
        public org.apache.xerces.xni.QName getXNIQName() {
            return this;
        }
    }
}

