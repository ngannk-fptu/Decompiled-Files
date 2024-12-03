/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.bcel.classfile;

import org.apache.tomcat.util.bcel.classfile.AnnotationEntry;
import org.apache.tomcat.util.bcel.classfile.ClassFormatException;
import org.apache.tomcat.util.bcel.classfile.ConstantPool;
import org.apache.tomcat.util.bcel.classfile.ElementValue;

public class AnnotationElementValue
extends ElementValue {
    private final AnnotationEntry annotationEntry;

    AnnotationElementValue(int type, AnnotationEntry annotationEntry, ConstantPool cpool) {
        super(type, cpool);
        if (type != 64) {
            throw new ClassFormatException("Only element values of type annotation can be built with this ctor - type specified: " + type);
        }
        this.annotationEntry = annotationEntry;
    }

    public AnnotationEntry getAnnotationEntry() {
        return this.annotationEntry;
    }

    @Override
    public String stringifyValue() {
        return this.annotationEntry.toString();
    }
}

