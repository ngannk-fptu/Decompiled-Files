/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Visitor;

public class AnnotationEntry
implements Node {
    public static final AnnotationEntry[] EMPTY_ARRAY = new AnnotationEntry[0];
    private final int typeIndex;
    private final ConstantPool constantPool;
    private final boolean isRuntimeVisible;
    private List<ElementValuePair> elementValuePairs;

    public static AnnotationEntry[] createAnnotationEntries(Attribute[] attrs) {
        return (AnnotationEntry[])Stream.of(attrs).filter(Annotations.class::isInstance).flatMap(e -> Stream.of(((Annotations)e).getAnnotationEntries())).toArray(AnnotationEntry[]::new);
    }

    public static AnnotationEntry read(DataInput input, ConstantPool constantPool, boolean isRuntimeVisible) throws IOException {
        AnnotationEntry annotationEntry = new AnnotationEntry(input.readUnsignedShort(), constantPool, isRuntimeVisible);
        int numElementValuePairs = input.readUnsignedShort();
        annotationEntry.elementValuePairs = new ArrayList<ElementValuePair>();
        for (int i = 0; i < numElementValuePairs; ++i) {
            annotationEntry.elementValuePairs.add(new ElementValuePair(input.readUnsignedShort(), ElementValue.readElementValue(input, constantPool), constantPool));
        }
        return annotationEntry;
    }

    public AnnotationEntry(int typeIndex, ConstantPool constantPool, boolean isRuntimeVisible) {
        this.typeIndex = typeIndex;
        this.constantPool = constantPool;
        this.isRuntimeVisible = isRuntimeVisible;
    }

    @Override
    public void accept(Visitor v) {
        v.visitAnnotationEntry(this);
    }

    public void addElementNameValuePair(ElementValuePair elementNameValuePair) {
        this.elementValuePairs.add(elementNameValuePair);
    }

    public void dump(DataOutputStream dos) throws IOException {
        dos.writeShort(this.typeIndex);
        dos.writeShort(this.elementValuePairs.size());
        for (ElementValuePair envp : this.elementValuePairs) {
            envp.dump(dos);
        }
    }

    public String getAnnotationType() {
        return this.constantPool.getConstantUtf8(this.typeIndex).getBytes();
    }

    public int getAnnotationTypeIndex() {
        return this.typeIndex;
    }

    public ConstantPool getConstantPool() {
        return this.constantPool;
    }

    public ElementValuePair[] getElementValuePairs() {
        return this.elementValuePairs.toArray(ElementValuePair.EMPTY_ARRAY);
    }

    public final int getNumElementValuePairs() {
        return this.elementValuePairs.size();
    }

    public int getTypeIndex() {
        return this.typeIndex;
    }

    public boolean isRuntimeVisible() {
        return this.isRuntimeVisible;
    }

    public String toShortString() {
        StringBuilder result = new StringBuilder();
        result.append("@");
        result.append(this.getAnnotationType());
        ElementValuePair[] evPairs = this.getElementValuePairs();
        if (evPairs.length > 0) {
            result.append("(");
            for (ElementValuePair element : evPairs) {
                result.append(element.toShortString());
                result.append(", ");
            }
            result.setLength(result.length() - 2);
            result.append(")");
        }
        return result.toString();
    }

    public String toString() {
        return this.toShortString();
    }
}

