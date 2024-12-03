/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.util.ShortListImpl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSValue;

public class ValidatedInfo
implements XSValue {
    public String normalizedValue;
    public Object actualValue;
    public short actualValueType;
    public XSSimpleType actualType;
    public XSSimpleType memberType;
    public XSSimpleType[] memberTypes;
    public ShortList itemValueTypes;

    public void reset() {
        this.normalizedValue = null;
        this.actualValue = null;
        this.actualValueType = (short)45;
        this.actualType = null;
        this.memberType = null;
        this.memberTypes = null;
        this.itemValueTypes = null;
    }

    public String stringValue() {
        if (this.actualValue == null) {
            return this.normalizedValue;
        }
        return this.actualValue.toString();
    }

    public static boolean isComparable(ValidatedInfo validatedInfo, ValidatedInfo validatedInfo2) {
        short s;
        short s2 = ValidatedInfo.convertToPrimitiveKind(validatedInfo.actualValueType);
        if (s2 != (s = ValidatedInfo.convertToPrimitiveKind(validatedInfo2.actualValueType))) {
            return s2 == 1 && s == 2 || s2 == 2 && s == 1;
        }
        if (s2 == 44 || s2 == 43) {
            int n;
            ShortList shortList = validatedInfo.itemValueTypes;
            ShortList shortList2 = validatedInfo2.itemValueTypes;
            int n2 = shortList != null ? shortList.getLength() : 0;
            int n3 = n = shortList2 != null ? shortList2.getLength() : 0;
            if (n2 != n) {
                return false;
            }
            for (int i = 0; i < n2; ++i) {
                short s3;
                short s4 = ValidatedInfo.convertToPrimitiveKind(shortList.item(i));
                if (s4 == (s3 = ValidatedInfo.convertToPrimitiveKind(shortList2.item(i))) || s4 == 1 && s3 == 2 || s4 == 2 && s3 == 1) continue;
                return false;
            }
        }
        return true;
    }

    private static short convertToPrimitiveKind(short s) {
        if (s <= 20) {
            return s;
        }
        if (s <= 29) {
            return 2;
        }
        if (s <= 42) {
            return 4;
        }
        return s;
    }

    @Override
    public Object getActualValue() {
        return this.actualValue;
    }

    @Override
    public short getActualValueType() {
        return this.actualValueType;
    }

    @Override
    public ShortList getListValueTypes() {
        return this.itemValueTypes == null ? ShortListImpl.EMPTY_LIST : this.itemValueTypes;
    }

    @Override
    public XSObjectList getMemberTypeDefinitions() {
        if (this.memberTypes == null) {
            return XSObjectListImpl.EMPTY_LIST;
        }
        return new XSObjectListImpl(this.memberTypes, this.memberTypes.length);
    }

    @Override
    public String getNormalizedValue() {
        return this.normalizedValue;
    }

    @Override
    public XSSimpleTypeDefinition getTypeDefinition() {
        return this.actualType;
    }

    @Override
    public XSSimpleTypeDefinition getMemberTypeDefinition() {
        return this.memberType;
    }

    public void copyFrom(XSValue xSValue) {
        if (xSValue == null) {
            this.reset();
        } else if (xSValue instanceof ValidatedInfo) {
            ValidatedInfo validatedInfo = (ValidatedInfo)xSValue;
            this.normalizedValue = validatedInfo.normalizedValue;
            this.actualValue = validatedInfo.actualValue;
            this.actualValueType = validatedInfo.actualValueType;
            this.actualType = validatedInfo.actualType;
            this.memberType = validatedInfo.memberType;
            this.memberTypes = validatedInfo.memberTypes;
            this.itemValueTypes = validatedInfo.itemValueTypes;
        } else {
            XSSimpleType xSSimpleType;
            this.normalizedValue = xSValue.getNormalizedValue();
            this.actualValue = xSValue.getActualValue();
            this.actualValueType = xSValue.getActualValueType();
            this.actualType = (XSSimpleType)xSValue.getTypeDefinition();
            this.memberType = (XSSimpleType)xSValue.getMemberTypeDefinition();
            XSSimpleType xSSimpleType2 = xSSimpleType = this.memberType == null ? this.actualType : this.memberType;
            if (xSSimpleType != null && xSSimpleType.getBuiltInKind() == 43) {
                XSObjectList xSObjectList = xSValue.getMemberTypeDefinitions();
                this.memberTypes = new XSSimpleType[xSObjectList.getLength()];
                for (int i = 0; i < xSObjectList.getLength(); ++i) {
                    this.memberTypes[i] = (XSSimpleType)xSObjectList.get(i);
                }
            } else {
                this.memberTypes = null;
            }
            this.itemValueTypes = xSValue.getListValueTypes();
        }
    }
}

