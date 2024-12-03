/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Discrete;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.QnameValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnicodeUtil;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XmlNames;

public class QnameType
extends BuiltinAtomicType
implements Discrete {
    public static final QnameType theInstance = new QnameType();
    private static final long serialVersionUID = 1L;

    private QnameType() {
        super("QName");
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    public boolean isContextDependent() {
        return true;
    }

    protected boolean checkFormat(String value, ValidationContext context) {
        int first = value.indexOf(58);
        if (first <= 0) {
            return XmlNames.isUnqualifiedName(value);
        }
        int last = value.lastIndexOf(58);
        if (last != first) {
            return false;
        }
        String prefix = value.substring(0, first);
        return XmlNames.isUnqualifiedName(prefix) && XmlNames.isUnqualifiedName(value.substring(first + 1)) && context.resolveNamespacePrefix(prefix) != null;
    }

    public Object _createValue(String value, ValidationContext context) {
        String localPart;
        String uri;
        int first = value.indexOf(58);
        if (first <= 0) {
            if (!XmlNames.isUnqualifiedName(value)) {
                return null;
            }
            uri = context.resolveNamespacePrefix("");
            localPart = value;
        } else {
            int last = value.lastIndexOf(58);
            if (last != first) {
                return null;
            }
            String prefix = value.substring(0, first);
            localPart = value.substring(first + 1);
            if (!XmlNames.isUnqualifiedName(prefix) || !XmlNames.isUnqualifiedName(localPart)) {
                return null;
            }
            uri = context.resolveNamespacePrefix(prefix);
        }
        if (uri == null) {
            return null;
        }
        return new QnameValueType(uri, localPart);
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("pattern") || facetName.equals("enumeration") || facetName.equals("whiteSpace") || facetName.equals("length") || facetName.equals("maxLength") || facetName.equals("minLength")) {
            return 0;
        }
        return -2;
    }

    public final int countLength(Object value) {
        QnameValueType v = (QnameValueType)value;
        return UnicodeUtil.countLength(v.namespaceURI) + UnicodeUtil.countLength(v.localPart);
    }

    public String convertToLexicalValue(Object o, SerializationContext context) {
        if (!(o instanceof QnameValueType)) {
            throw new UnsupportedOperationException();
        }
        QnameValueType v = (QnameValueType)o;
        return this.serialize(v.namespaceURI, v.localPart, context);
    }

    public String serializeJavaObject(Object value, SerializationContext context) {
        if (!(value instanceof String[])) {
            throw new IllegalArgumentException();
        }
        String[] input = (String[])value;
        if (input.length != 2) {
            throw new IllegalArgumentException();
        }
        return this.serialize(input[0], input[1], context);
    }

    private String serialize(String uri, String local, SerializationContext context) {
        String prefix = context.getNamespacePrefix(uri);
        if (prefix == null) {
            return local;
        }
        return prefix + ":" + local;
    }

    public Object _createJavaObject(String literal, ValidationContext context) {
        QnameValueType v = (QnameValueType)this.createValue(literal, context);
        if (v == null) {
            return null;
        }
        return new String[]{v.namespaceURI, v.localPart};
    }

    public Class getJavaObjectType() {
        return String[].class;
    }
}

