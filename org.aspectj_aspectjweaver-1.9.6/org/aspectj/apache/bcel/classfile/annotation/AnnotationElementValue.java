/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile.annotation;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.ElementValue;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;

public class AnnotationElementValue
extends ElementValue {
    private AnnotationGen a;

    public AnnotationElementValue(AnnotationGen a, ConstantPool cpool) {
        super(64, cpool);
        this.a = a;
    }

    public AnnotationElementValue(int type, AnnotationGen annotation, ConstantPool cpool) {
        super(type, cpool);
        assert (type == 64);
        this.a = annotation;
    }

    public AnnotationElementValue(AnnotationElementValue value, ConstantPool cpool, boolean copyPoolEntries) {
        super(64, cpool);
        this.a = new AnnotationGen(value.getAnnotation(), cpool, copyPoolEntries);
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
        dos.writeByte(this.type);
        this.a.dump(dos);
    }

    @Override
    public String stringifyValue() {
        StringBuffer sb = new StringBuffer();
        ConstantUtf8 cu8 = (ConstantUtf8)this.cpool.getConstant(this.a.getTypeIndex(), (byte)1);
        sb.append(cu8.getValue());
        List<NameValuePair> pairs = this.a.getValues();
        if (pairs != null && pairs.size() > 0) {
            sb.append("(");
            for (int p = 0; p < pairs.size(); ++p) {
                if (p > 0) {
                    sb.append(",");
                }
                sb.append(pairs.get(p).getNameString()).append("=").append(pairs.get(p).getValue().stringifyValue());
            }
            sb.append(")");
        }
        return sb.toString();
    }

    public AnnotationGen getAnnotation() {
        return this.a;
    }
}

