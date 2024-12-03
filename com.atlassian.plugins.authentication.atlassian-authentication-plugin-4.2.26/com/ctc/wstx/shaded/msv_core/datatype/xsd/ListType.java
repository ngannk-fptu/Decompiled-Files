/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Discrete;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ListValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
import java.util.StringTokenizer;

public final class ListType
extends ConcreteType
implements Discrete {
    public final XSDatatypeImpl itemType;
    private static final long serialVersionUID = 1L;

    public ListType(String nsUri, String newTypeName, XSDatatypeImpl itemType) throws DatatypeException {
        super(nsUri, newTypeName);
        if (itemType.isFinal(2)) {
            throw new DatatypeException(ListType.localize("BadTypeException.InvalidItemType"));
        }
        this.itemType = itemType;
    }

    public final String displayName() {
        String name = this.getName();
        if (name != null) {
            return name;
        }
        return this.itemType.displayName() + "-list";
    }

    public final int getVariety() {
        return 2;
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    public boolean isContextDependent() {
        return this.itemType.isContextDependent();
    }

    public int getIdType() {
        switch (this.itemType.getIdType()) {
            case 0: {
                return 0;
            }
            case 1: {
                return 0;
            }
            case 2: {
                return 3;
            }
            case 3: {
                return 3;
            }
        }
        throw new Error();
    }

    public final boolean isFinal(int derivationType) {
        if (derivationType == 2) {
            return true;
        }
        return this.itemType.isFinal(derivationType);
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("length") || facetName.equals("minLength") || facetName.equals("maxLength") || facetName.equals("enumeration") || facetName.equals("pattern")) {
            return 0;
        }
        return -2;
    }

    protected final boolean checkFormat(String content, ValidationContext context) {
        StringTokenizer tokens = new StringTokenizer(content);
        while (tokens.hasMoreTokens()) {
            if (this.itemType.isValid(tokens.nextToken(), context)) continue;
            return false;
        }
        return true;
    }

    public Object _createValue(String content, ValidationContext context) {
        StringTokenizer tokens = new StringTokenizer(content);
        Object[] values = new Object[tokens.countTokens()];
        int i = 0;
        while (tokens.hasMoreTokens()) {
            values[i++] = this.itemType._createValue(tokens.nextToken(), context);
            if (values[i++] != null) continue;
            return null;
        }
        return new ListValueType(values);
    }

    public Class getJavaObjectType() {
        return Object[].class;
    }

    public final int countLength(Object value) {
        return ((ListValueType)value).values.length;
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof ListValueType)) {
            throw new IllegalArgumentException();
        }
        ListValueType lv = (ListValueType)value;
        StringBuffer r = new StringBuffer();
        for (int i = 0; i < lv.values.length; ++i) {
            if (i != 0) {
                r.append(' ');
            }
            r.append(this.itemType.convertToLexicalValue(lv.values[i], context));
        }
        return r.toString();
    }

    protected void _checkValid(String content, ValidationContext context) throws DatatypeException {
        StringTokenizer tokens = new StringTokenizer(content);
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            this.itemType.checkValid(token, context);
        }
    }
}

