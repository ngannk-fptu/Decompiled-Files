/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import java.lang.reflect.Field;
import org.aspectj.lang.reflect.MemberSignature;

public interface FieldSignature
extends MemberSignature {
    public Class getFieldType();

    public Field getField();
}

