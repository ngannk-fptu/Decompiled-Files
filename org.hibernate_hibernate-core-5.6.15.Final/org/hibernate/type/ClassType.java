/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.ClassTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class ClassType
extends AbstractSingleColumnStandardBasicType<Class> {
    public static final ClassType INSTANCE = new ClassType();

    public ClassType() {
        super(VarcharTypeDescriptor.INSTANCE, ClassTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "class";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }
}

