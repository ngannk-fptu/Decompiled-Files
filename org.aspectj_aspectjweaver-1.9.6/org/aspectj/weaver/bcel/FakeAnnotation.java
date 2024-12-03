/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;

public class FakeAnnotation
extends AnnotationGen {
    private String name;
    private String sig;
    private boolean isRuntimeVisible;

    public FakeAnnotation(String name, String sig, boolean isRuntimeVisible) {
        super(null, null, true, null);
        this.name = name;
        this.sig = sig;
        this.isRuntimeVisible = isRuntimeVisible;
    }

    @Override
    public String getTypeName() {
        return this.name;
    }

    @Override
    public String getTypeSignature() {
        return this.sig;
    }

    @Override
    public void addElementNameValuePair(NameValuePair evp) {
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException {
    }

    @Override
    public int getTypeIndex() {
        return 0;
    }

    public List getValues() {
        return null;
    }

    @Override
    public boolean isRuntimeVisible() {
        return this.isRuntimeVisible;
    }

    protected void setIsRuntimeVisible(boolean b) {
    }

    @Override
    public String toShortString() {
        return "@" + this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

