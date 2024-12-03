/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.ArrayTypeSignature;
import io.github.classgraph.BaseTypeSignature;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassRefTypeSignature;
import io.github.classgraph.ScanResult;
import io.github.classgraph.ScanResultObject;
import io.github.classgraph.TypeSignature;
import nonapi.io.github.classgraph.types.ParseException;

public class AnnotationClassRef
extends ScanResultObject {
    private String typeDescriptorStr;
    private transient TypeSignature typeSignature;
    private transient String className;

    AnnotationClassRef() {
    }

    AnnotationClassRef(String typeDescriptorStr) {
        this.typeDescriptorStr = typeDescriptorStr;
    }

    public String getName() {
        return this.getClassName();
    }

    private TypeSignature getTypeSignature() {
        if (this.typeSignature == null) {
            try {
                this.typeSignature = TypeSignature.parse(this.typeDescriptorStr, null);
                this.typeSignature.setScanResult(this.scanResult);
            }
            catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return this.typeSignature;
    }

    @Override
    public Class<?> loadClass(boolean ignoreExceptions) {
        this.getTypeSignature();
        if (this.typeSignature instanceof BaseTypeSignature) {
            return ((BaseTypeSignature)this.typeSignature).getType();
        }
        if (this.typeSignature instanceof ClassRefTypeSignature) {
            return this.typeSignature.loadClass(ignoreExceptions);
        }
        if (this.typeSignature instanceof ArrayTypeSignature) {
            return this.typeSignature.loadClass(ignoreExceptions);
        }
        throw new IllegalArgumentException("Got unexpected type " + this.typeSignature.getClass().getName() + " for ref type signature: " + this.typeDescriptorStr);
    }

    @Override
    public Class<?> loadClass() {
        return this.loadClass(false);
    }

    @Override
    protected String getClassName() {
        if (this.className == null) {
            this.getTypeSignature();
            if (this.typeSignature instanceof BaseTypeSignature) {
                this.className = ((BaseTypeSignature)this.typeSignature).getTypeStr();
            } else if (this.typeSignature instanceof ClassRefTypeSignature) {
                this.className = ((ClassRefTypeSignature)this.typeSignature).getFullyQualifiedClassName();
            } else if (this.typeSignature instanceof ArrayTypeSignature) {
                this.className = this.typeSignature.getClassName();
            } else {
                throw new IllegalArgumentException("Got unexpected type " + this.typeSignature.getClass().getName() + " for ref type signature: " + this.typeDescriptorStr);
            }
        }
        return this.className;
    }

    @Override
    public ClassInfo getClassInfo() {
        this.getTypeSignature();
        return this.typeSignature.getClassInfo();
    }

    @Override
    void setScanResult(ScanResult scanResult) {
        super.setScanResult(scanResult);
        if (this.typeSignature != null) {
            this.typeSignature.setScanResult(scanResult);
        }
    }

    public int hashCode() {
        return this.getTypeSignature().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AnnotationClassRef)) {
            return false;
        }
        return this.getTypeSignature().equals(((AnnotationClassRef)obj).getTypeSignature());
    }

    @Override
    protected void toString(boolean useSimpleNames, StringBuilder buf) {
        buf.append(this.getTypeSignature().toString(useSimpleNames)).append(".class");
    }
}

