/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import java.lang.reflect.Field;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.runtime.reflect.MemberSignatureImpl;
import org.aspectj.runtime.reflect.StringMaker;

public class FieldSignatureImpl
extends MemberSignatureImpl
implements FieldSignature {
    Class fieldType;
    private Field field;

    FieldSignatureImpl(int modifiers, String name, Class declaringType, Class fieldType) {
        super(modifiers, name, declaringType);
        this.fieldType = fieldType;
    }

    FieldSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override
    public Class getFieldType() {
        if (this.fieldType == null) {
            this.fieldType = this.extractType(3);
        }
        return this.fieldType;
    }

    @Override
    protected String createToString(StringMaker sm) {
        StringBuffer buf = new StringBuffer();
        buf.append(sm.makeModifiersString(this.getModifiers()));
        if (sm.includeArgs) {
            buf.append(sm.makeTypeName(this.getFieldType()));
        }
        if (sm.includeArgs) {
            buf.append(" ");
        }
        buf.append(sm.makePrimaryTypeName(this.getDeclaringType(), this.getDeclaringTypeName()));
        buf.append(".");
        buf.append(this.getName());
        return buf.toString();
    }

    @Override
    public Field getField() {
        if (this.field == null) {
            try {
                this.field = this.getDeclaringType().getDeclaredField(this.getName());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return this.field;
    }
}

