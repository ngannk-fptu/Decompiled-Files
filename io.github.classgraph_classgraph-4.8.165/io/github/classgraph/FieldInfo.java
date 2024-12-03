/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassMemberInfo;
import io.github.classgraph.Classfile;
import io.github.classgraph.ObjectTypedValueWrapper;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeSignature;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.TypeUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class FieldInfo
extends ClassMemberInfo
implements Comparable<FieldInfo> {
    private transient TypeSignature typeSignature;
    private transient TypeSignature typeDescriptor;
    private ObjectTypedValueWrapper constantInitializerValue;
    private transient List<Classfile.TypeAnnotationDecorator> typeAnnotationDecorators;

    FieldInfo() {
    }

    FieldInfo(String definingClassName, String fieldName, int modifiers, String typeDescriptorStr, String typeSignatureStr, Object constantInitializerValue, AnnotationInfoList annotationInfo, List<Classfile.TypeAnnotationDecorator> typeAnnotationDecorators) {
        super(definingClassName, fieldName, modifiers, typeDescriptorStr, typeSignatureStr, annotationInfo);
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must not be null");
        }
        this.constantInitializerValue = constantInitializerValue == null ? null : new ObjectTypedValueWrapper(constantInitializerValue);
        this.typeAnnotationDecorators = typeAnnotationDecorators;
    }

    @Deprecated
    public String getModifierStr() {
        return this.getModifiersStr();
    }

    @Override
    public String getModifiersStr() {
        StringBuilder buf = new StringBuilder();
        TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.FIELD, false, buf);
        return buf.toString();
    }

    public boolean isTransient() {
        return Modifier.isTransient(this.modifiers);
    }

    public boolean isEnum() {
        return (this.modifiers & 0x4000) != 0;
    }

    @Override
    public TypeSignature getTypeDescriptor() {
        if (this.typeDescriptorStr == null) {
            return null;
        }
        if (this.typeDescriptor == null) {
            try {
                this.typeDescriptor = TypeSignature.parse(this.typeDescriptorStr, this.declaringClassName);
                this.typeDescriptor.setScanResult(this.scanResult);
                if (this.typeAnnotationDecorators != null) {
                    for (Classfile.TypeAnnotationDecorator decorator : this.typeAnnotationDecorators) {
                        decorator.decorate(this.typeDescriptor);
                    }
                }
            }
            catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return this.typeDescriptor;
    }

    @Override
    public TypeSignature getTypeSignature() {
        if (this.typeSignatureStr == null) {
            return null;
        }
        if (this.typeSignature == null) {
            try {
                this.typeSignature = TypeSignature.parse(this.typeSignatureStr, this.declaringClassName);
                this.typeSignature.setScanResult(this.scanResult);
                if (this.typeAnnotationDecorators != null) {
                    for (Classfile.TypeAnnotationDecorator decorator : this.typeAnnotationDecorators) {
                        decorator.decorate(this.typeSignature);
                    }
                }
            }
            catch (ParseException e) {
                throw new IllegalArgumentException("Invalid type signature for field " + this.getClassName() + "." + this.getName() + (this.getClassInfo() != null ? " in classpath element " + this.getClassInfo().getClasspathElementURI() : "") + " : " + this.typeSignatureStr, e);
            }
        }
        return this.typeSignature;
    }

    @Override
    public TypeSignature getTypeSignatureOrTypeDescriptor() {
        TypeSignature typeSig = null;
        try {
            typeSig = this.getTypeSignature();
            if (typeSig != null) {
                return typeSig;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return this.getTypeDescriptor();
    }

    public Object getConstantInitializerValue() {
        if (!this.scanResult.scanSpec.enableStaticFinalFieldConstantInitializerValues) {
            throw new IllegalArgumentException("Please call ClassGraph#enableStaticFinalFieldConstantInitializerValues() before #scan()");
        }
        return this.constantInitializerValue == null ? null : this.constantInitializerValue.get();
    }

    public Field loadClassAndGetField() throws IllegalArgumentException {
        try {
            return this.loadClass().getField(this.getName());
        }
        catch (NoSuchFieldException e1) {
            try {
                return this.loadClass().getDeclaredField(this.getName());
            }
            catch (NoSuchFieldException e2) {
                throw new IllegalArgumentException("No such field: " + this.getClassName() + "." + this.getName());
            }
        }
    }

    void handleRepeatableAnnotations(Set<String> allRepeatableAnnotationNames) {
        if (this.annotationInfo != null) {
            this.annotationInfo.handleRepeatableAnnotations(allRepeatableAnnotationNames, this.getClassInfo(), ClassInfo.RelType.FIELD_ANNOTATIONS, ClassInfo.RelType.CLASSES_WITH_FIELD_ANNOTATION, ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION);
        }
    }

    @Override
    void setScanResult(ScanResult scanResult) {
        super.setScanResult(scanResult);
        if (this.typeSignature != null) {
            this.typeSignature.setScanResult(scanResult);
        }
        if (this.typeDescriptor != null) {
            this.typeDescriptor.setScanResult(scanResult);
        }
        if (this.annotationInfo != null) {
            for (AnnotationInfo ai : this.annotationInfo) {
                ai.setScanResult(scanResult);
            }
        }
    }

    @Override
    protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
        block9: {
            block8: {
                try {
                    TypeSignature fieldSig = this.getTypeSignature();
                    if (fieldSig != null) {
                        fieldSig.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
                    }
                }
                catch (IllegalArgumentException e) {
                    if (log == null) break block8;
                    log.log("Illegal type signature for field " + this.getClassName() + "." + this.getName() + ": " + this.getTypeSignatureStr());
                }
            }
            try {
                TypeSignature fieldDesc = this.getTypeDescriptor();
                if (fieldDesc != null) {
                    fieldDesc.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
                }
            }
            catch (IllegalArgumentException e) {
                if (log == null) break block9;
                log.log("Illegal type descriptor for field " + this.getClassName() + "." + this.getName() + ": " + this.getTypeDescriptorStr());
            }
        }
        if (this.annotationInfo != null) {
            for (AnnotationInfo ai : this.annotationInfo) {
                ai.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FieldInfo)) {
            return false;
        }
        FieldInfo other = (FieldInfo)obj;
        return this.declaringClassName.equals(other.declaringClassName) && this.name.equals(other.name);
    }

    public int hashCode() {
        return this.name.hashCode() + this.declaringClassName.hashCode() * 11;
    }

    @Override
    public int compareTo(FieldInfo other) {
        int diff = this.declaringClassName.compareTo(other.declaringClassName);
        if (diff != 0) {
            return diff;
        }
        return this.name.compareTo(other.name);
    }

    void toString(boolean includeModifiers, boolean useSimpleNames, StringBuilder buf) {
        if (this.annotationInfo != null) {
            for (AnnotationInfo annotation : this.annotationInfo) {
                if (buf.length() > 0 && buf.charAt(buf.length() - 1) != ' ' && buf.charAt(buf.length() - 1) != '(') {
                    buf.append(' ');
                }
                annotation.toString(useSimpleNames, buf);
            }
        }
        if (this.modifiers != 0 && includeModifiers) {
            if (buf.length() > 0 && buf.charAt(buf.length() - 1) != ' ' && buf.charAt(buf.length() - 1) != '(') {
                buf.append(' ');
            }
            TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.FIELD, false, buf);
        }
        if (buf.length() > 0 && buf.charAt(buf.length() - 1) != ' ' && buf.charAt(buf.length() - 1) != '(') {
            buf.append(' ');
        }
        TypeSignature typeSig = this.getTypeSignatureOrTypeDescriptor();
        typeSig.toStringInternal(useSimpleNames, this.annotationInfo, buf);
        buf.append(' ');
        buf.append(this.name);
        if (this.constantInitializerValue != null) {
            Object val = this.constantInitializerValue.get();
            buf.append(" = ");
            if (val instanceof String) {
                buf.append('\"').append(((String)val).replace("\\", "\\\\").replace("\"", "\\\"")).append('\"');
            } else if (val instanceof Character) {
                buf.append('\'').append(((Character)val).toString().replace("\\", "\\\\").replaceAll("'", "\\'")).append('\'');
            } else {
                buf.append(val == null ? "null" : val.toString());
            }
        }
    }

    @Override
    protected void toString(boolean useSimpleNames, StringBuilder buf) {
        this.toString(true, useSimpleNames, buf);
    }
}

