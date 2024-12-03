/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.BaseTypeSignature;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.Classfile;
import io.github.classgraph.HierarchicalTypeSignature;
import io.github.classgraph.ReferenceTypeSignature;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.utils.LogNode;

public abstract class TypeSignature
extends HierarchicalTypeSignature {
    protected TypeSignature() {
    }

    protected void findReferencedClassNames(Set<String> refdClassNames) {
        String className = this.getClassName();
        if (className != null && !className.isEmpty()) {
            refdClassNames.add(this.getClassName());
        }
    }

    @Override
    protected final void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
        HashSet<String> refdClassNames = new HashSet<String>();
        this.findReferencedClassNames(refdClassNames);
        for (String refdClassName : refdClassNames) {
            ClassInfo classInfo = ClassInfo.getOrCreateClassInfo(refdClassName, classNameToClassInfo);
            classInfo.scanResult = this.scanResult;
            refdClassInfo.add(classInfo);
        }
    }

    @Override
    public AnnotationInfoList getTypeAnnotationInfo() {
        return this.typeAnnotationInfo;
    }

    public abstract boolean equalsIgnoringTypeParams(TypeSignature var1);

    static TypeSignature parse(Parser parser, String definingClass) throws ParseException {
        ReferenceTypeSignature referenceTypeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClass);
        if (referenceTypeSignature != null) {
            return referenceTypeSignature;
        }
        BaseTypeSignature baseTypeSignature = BaseTypeSignature.parse(parser);
        if (baseTypeSignature != null) {
            return baseTypeSignature;
        }
        return null;
    }

    static TypeSignature parse(String typeDescriptor, String definingClass) throws ParseException {
        Parser parser = new Parser(typeDescriptor);
        TypeSignature typeSignature = TypeSignature.parse(parser, definingClass);
        if (typeSignature == null) {
            throw new ParseException(parser, "Could not parse type signature");
        }
        if (parser.hasMore()) {
            throw new ParseException(parser, "Extra characters at end of type descriptor");
        }
        return typeSignature;
    }

    @Override
    protected abstract void addTypeAnnotation(List<Classfile.TypePathNode> var1, AnnotationInfo var2);
}

