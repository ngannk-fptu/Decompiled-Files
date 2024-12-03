/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import org.jdom2.IllegalDataException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum AttributeType {
    UNDECLARED,
    CDATA,
    ID,
    IDREF,
    IDREFS,
    ENTITY,
    ENTITIES,
    NMTOKEN,
    NMTOKENS,
    NOTATION,
    ENUMERATION;


    @Deprecated
    public static final AttributeType byIndex(int index) {
        if (index < 0) {
            throw new IllegalDataException("No such AttributeType " + index);
        }
        if (index >= AttributeType.values().length) {
            throw new IllegalDataException("No such AttributeType " + index + ", max is " + (AttributeType.values().length - 1));
        }
        return AttributeType.values()[index];
    }

    public static final AttributeType getAttributeType(String typeName) {
        if (typeName == null) {
            return UNDECLARED;
        }
        try {
            return AttributeType.valueOf(typeName);
        }
        catch (IllegalArgumentException iae) {
            if (typeName.length() > 0 && typeName.trim().charAt(0) == '(') {
                return ENUMERATION;
            }
            return UNDECLARED;
        }
    }
}

