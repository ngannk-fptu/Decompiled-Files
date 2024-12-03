/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.MethodParameter;
import org.apache.bcel.classfile.Visitor;

public class MethodParameters
extends Attribute
implements Iterable<MethodParameter> {
    private static final MethodParameter[] EMPTY_METHOD_PARAMETER_ARRAY = new MethodParameter[0];
    private MethodParameter[] parameters = EMPTY_METHOD_PARAMETER_ARRAY;

    MethodParameters(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        super((byte)21, nameIndex, length, constantPool);
        int parameterCount = input.readUnsignedByte();
        this.parameters = new MethodParameter[parameterCount];
        for (int i = 0; i < parameterCount; ++i) {
            this.parameters[i] = new MethodParameter(input);
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visitMethodParameters(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        MethodParameters c = (MethodParameters)this.clone();
        c.parameters = new MethodParameter[this.parameters.length];
        Arrays.setAll(c.parameters, i -> this.parameters[i].copy());
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeByte(this.parameters.length);
        for (MethodParameter parameter : this.parameters) {
            parameter.dump(file);
        }
    }

    public MethodParameter[] getParameters() {
        return this.parameters;
    }

    @Override
    public Iterator<MethodParameter> iterator() {
        return Stream.of(this.parameters).iterator();
    }

    public void setParameters(MethodParameter[] parameters) {
        this.parameters = parameters;
    }
}

