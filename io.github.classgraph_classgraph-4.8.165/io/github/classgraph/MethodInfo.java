/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.ArrayTypeSignature;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ClassMemberInfo;
import io.github.classgraph.Classfile;
import io.github.classgraph.MethodParameterInfo;
import io.github.classgraph.MethodTypeSignature;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeParameter;
import io.github.classgraph.TypeSignature;
import io.github.classgraph.TypeVariableSignature;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.TypeUtils;
import nonapi.io.github.classgraph.utils.Assert;
import nonapi.io.github.classgraph.utils.LogNode;

public class MethodInfo
extends ClassMemberInfo
implements Comparable<MethodInfo> {
    private transient MethodTypeSignature typeDescriptor;
    private transient MethodTypeSignature typeSignature;
    private String[] parameterNames;
    private int[] parameterModifiers;
    AnnotationInfo[][] parameterAnnotationInfo;
    private transient MethodParameterInfo[] parameterInfo;
    private boolean hasBody;
    private int minLineNum;
    private int maxLineNum;
    private transient List<Classfile.MethodTypeAnnotationDecorator> typeAnnotationDecorators;
    private String[] thrownExceptionNames;
    private transient ClassInfoList thrownExceptions;

    MethodInfo() {
    }

    MethodInfo(String definingClassName, String methodName, AnnotationInfoList methodAnnotationInfo, int modifiers, String typeDescriptorStr, String typeSignatureStr, String[] parameterNames, int[] parameterModifiers, AnnotationInfo[][] parameterAnnotationInfo, boolean hasBody, int minLineNum, int maxLineNum, List<Classfile.MethodTypeAnnotationDecorator> methodTypeAnnotationDecorators, String[] thrownExceptionNames) {
        super(definingClassName, methodName, modifiers, typeDescriptorStr, typeSignatureStr, methodAnnotationInfo);
        this.parameterNames = parameterNames;
        this.parameterModifiers = parameterModifiers;
        this.parameterAnnotationInfo = parameterAnnotationInfo;
        this.hasBody = hasBody;
        this.minLineNum = minLineNum;
        this.maxLineNum = maxLineNum;
        this.typeAnnotationDecorators = methodTypeAnnotationDecorators;
        this.thrownExceptionNames = thrownExceptionNames;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getModifiersStr() {
        StringBuilder buf = new StringBuilder();
        TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.METHOD, this.isDefault(), buf);
        return buf.toString();
    }

    @Override
    public MethodTypeSignature getTypeDescriptor() {
        if (this.typeDescriptor == null) {
            try {
                this.typeDescriptor = MethodTypeSignature.parse(this.typeDescriptorStr, this.declaringClassName);
                this.typeDescriptor.setScanResult(this.scanResult);
                if (this.typeAnnotationDecorators != null) {
                    int sigNumParam = 0;
                    MethodTypeSignature sig = this.getTypeSignature();
                    if (sig == null) {
                        for (Classfile.MethodTypeAnnotationDecorator decorator : this.typeAnnotationDecorators) {
                            decorator.decorate(this.typeDescriptor);
                        }
                    } else {
                        sigNumParam = sig.getParameterTypeSignatures().size();
                        int descNumParam = this.typeDescriptor.getParameterTypeSignatures().size();
                        int numImplicitPrefixParams = descNumParam - sigNumParam;
                        if (numImplicitPrefixParams < 0) {
                            throw new IllegalArgumentException("Fewer params in method type descriptor than in method type signature");
                        }
                        if (numImplicitPrefixParams == 0) {
                            for (Classfile.MethodTypeAnnotationDecorator decorator : this.typeAnnotationDecorators) {
                                decorator.decorate(this.typeDescriptor);
                            }
                        } else {
                            List<TypeSignature> paramSigs = this.typeDescriptor.getParameterTypeSignatures();
                            List<TypeSignature> strippedParamSigs = paramSigs.subList(0, numImplicitPrefixParams);
                            for (int i = 0; i < numImplicitPrefixParams; ++i) {
                                paramSigs.remove(0);
                            }
                            for (Classfile.MethodTypeAnnotationDecorator decorator : this.typeAnnotationDecorators) {
                                decorator.decorate(this.typeDescriptor);
                            }
                            for (int i = numImplicitPrefixParams - 1; i >= 0; --i) {
                                paramSigs.add(0, strippedParamSigs.get(i));
                            }
                        }
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
    public MethodTypeSignature getTypeSignature() {
        if (this.typeSignature == null && this.typeSignatureStr != null) {
            try {
                this.typeSignature = MethodTypeSignature.parse(this.typeSignatureStr, this.declaringClassName);
                this.typeSignature.setScanResult(this.scanResult);
                if (this.typeAnnotationDecorators != null) {
                    for (Classfile.MethodTypeAnnotationDecorator decorator : this.typeAnnotationDecorators) {
                        decorator.decorate(this.typeSignature);
                    }
                }
            }
            catch (ParseException e) {
                throw new IllegalArgumentException("Invalid type signature for method " + this.getClassName() + "." + this.getName() + (this.getClassInfo() != null ? " in classpath element " + this.getClassInfo().getClasspathElementURI() : "") + " : " + this.typeSignatureStr, e);
            }
        }
        return this.typeSignature;
    }

    @Override
    public MethodTypeSignature getTypeSignatureOrTypeDescriptor() {
        MethodTypeSignature typeSig = null;
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

    public ClassInfoList getThrownExceptions() {
        if (this.thrownExceptions == null && this.thrownExceptionNames != null) {
            this.thrownExceptions = new ClassInfoList(this.thrownExceptionNames.length);
            for (String thrownExceptionName : this.thrownExceptionNames) {
                ClassInfo classInfo = this.scanResult.getClassInfo(thrownExceptionName);
                if (classInfo == null) continue;
                this.thrownExceptions.add(classInfo);
                classInfo.setScanResult(this.scanResult);
            }
        }
        return this.thrownExceptions == null ? ClassInfoList.EMPTY_LIST : this.thrownExceptions;
    }

    public String[] getThrownExceptionNames() {
        return this.thrownExceptionNames == null ? new String[]{} : this.thrownExceptionNames;
    }

    public boolean isConstructor() {
        return "<init>".equals(this.name);
    }

    public boolean isSynchronized() {
        return Modifier.isSynchronized(this.modifiers);
    }

    public boolean isBridge() {
        return (this.modifiers & 0x40) != 0;
    }

    public boolean isVarArgs() {
        return (this.modifiers & 0x80) != 0;
    }

    public boolean isNative() {
        return Modifier.isNative(this.modifiers);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this.modifiers);
    }

    public boolean isStrict() {
        return Modifier.isStrict(this.modifiers);
    }

    public boolean hasBody() {
        return this.hasBody;
    }

    public int getMinLineNum() {
        return this.minLineNum;
    }

    public int getMaxLineNum() {
        return this.maxLineNum;
    }

    public boolean isDefault() {
        ClassInfo classInfo = this.getClassInfo();
        return classInfo != null && classInfo.isInterface() && this.hasBody;
    }

    public MethodParameterInfo[] getParameterInfo() {
        if (this.parameterInfo == null) {
            int i;
            int numParams;
            List<TypeSignature> paramTypeSignatures = null;
            MethodTypeSignature typeSig = this.getTypeSignature();
            if (typeSig != null) {
                paramTypeSignatures = typeSig.getParameterTypeSignatures();
            }
            List<TypeSignature> paramTypeDescriptors = null;
            try {
                MethodTypeSignature typeDesc = this.getTypeDescriptor();
                if (typeDesc != null) {
                    paramTypeDescriptors = typeDesc.getParameterTypeSignatures();
                }
            }
            catch (Exception typeDesc) {
                // empty catch block
            }
            int n = numParams = paramTypeSignatures == null ? 0 : paramTypeSignatures.size();
            if (paramTypeDescriptors != null && paramTypeDescriptors.size() > numParams) {
                numParams = paramTypeDescriptors.size();
            }
            if (this.parameterNames != null && this.parameterNames.length > numParams) {
                numParams = this.parameterNames.length;
            }
            if (this.parameterModifiers != null && this.parameterModifiers.length > numParams) {
                numParams = this.parameterModifiers.length;
            }
            if (this.parameterAnnotationInfo != null && this.parameterAnnotationInfo.length > numParams) {
                numParams = this.parameterAnnotationInfo.length;
            }
            String[] paramNamesAligned = null;
            if (this.parameterNames != null && this.parameterNames.length > 0) {
                if (this.parameterNames.length == numParams) {
                    paramNamesAligned = this.parameterNames;
                } else {
                    paramNamesAligned = new String[numParams];
                    int lenDiff = numParams - this.parameterNames.length;
                    for (int i2 = 0; i2 < this.parameterNames.length; ++i2) {
                        paramNamesAligned[lenDiff + i2] = this.parameterNames[i2];
                    }
                }
            }
            int[] paramModifiersAligned = null;
            if (this.parameterModifiers != null && this.parameterModifiers.length > 0) {
                if (this.parameterModifiers.length == numParams) {
                    paramModifiersAligned = this.parameterModifiers;
                } else {
                    paramModifiersAligned = new int[numParams];
                    int lenDiff = numParams - this.parameterModifiers.length;
                    for (int i3 = 0; i3 < this.parameterModifiers.length; ++i3) {
                        paramModifiersAligned[lenDiff + i3] = this.parameterModifiers[i3];
                    }
                }
            }
            AnnotationInfo[][] paramAnnotationInfoAligned = null;
            if (this.parameterAnnotationInfo != null && this.parameterAnnotationInfo.length > 0) {
                if (this.parameterAnnotationInfo.length == numParams) {
                    paramAnnotationInfoAligned = this.parameterAnnotationInfo;
                } else {
                    paramAnnotationInfoAligned = new AnnotationInfo[numParams][];
                    int lenDiff = numParams - this.parameterAnnotationInfo.length;
                    for (int i4 = 0; i4 < this.parameterAnnotationInfo.length; ++i4) {
                        paramAnnotationInfoAligned[lenDiff + i4] = this.parameterAnnotationInfo[i4];
                    }
                }
            }
            List<TypeSignature> paramTypeSignaturesAligned = null;
            if (paramTypeSignatures != null && paramTypeSignatures.size() > 0) {
                if (paramTypeSignatures.size() == numParams) {
                    paramTypeSignaturesAligned = paramTypeSignatures;
                } else {
                    paramTypeSignaturesAligned = new ArrayList<TypeSignature>(numParams);
                    int lenDiff = numParams - paramTypeSignatures.size();
                    for (int i5 = 0; i5 < lenDiff; ++i5) {
                        paramTypeSignaturesAligned.add(null);
                    }
                    paramTypeSignaturesAligned.addAll(paramTypeSignatures);
                }
            }
            List<TypeSignature> paramTypeDescriptorsAligned = null;
            if (paramTypeDescriptors != null && paramTypeDescriptors.size() > 0) {
                if (paramTypeDescriptors.size() == numParams) {
                    paramTypeDescriptorsAligned = paramTypeDescriptors;
                } else {
                    paramTypeDescriptorsAligned = new ArrayList<TypeSignature>(numParams);
                    int lenDiff = numParams - paramTypeDescriptors.size();
                    for (i = 0; i < lenDiff; ++i) {
                        paramTypeDescriptorsAligned.add(null);
                    }
                    paramTypeDescriptorsAligned.addAll(paramTypeDescriptors);
                }
            }
            this.parameterInfo = new MethodParameterInfo[numParams];
            for (i = 0; i < numParams; ++i) {
                this.parameterInfo[i] = new MethodParameterInfo(this, paramAnnotationInfoAligned == null ? null : paramAnnotationInfoAligned[i], paramModifiersAligned == null ? 0 : paramModifiersAligned[i], paramTypeDescriptorsAligned == null ? null : paramTypeDescriptorsAligned.get(i), paramTypeSignaturesAligned == null ? null : paramTypeSignaturesAligned.get(i), paramNamesAligned == null ? null : paramNamesAligned[i]);
                this.parameterInfo[i].setScanResult(this.scanResult);
            }
        }
        return this.parameterInfo;
    }

    public boolean hasParameterAnnotation(Class<? extends Annotation> annotation) {
        Assert.isAnnotation(annotation);
        return this.hasParameterAnnotation(annotation.getName());
    }

    public boolean hasParameterAnnotation(String annotationName) {
        for (MethodParameterInfo methodParameterInfo : this.getParameterInfo()) {
            if (!methodParameterInfo.hasAnnotation(annotationName)) continue;
            return true;
        }
        return false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Class<?>[] loadParameterClasses() {
        MethodParameterInfo[] allParameterInfo = this.getParameterInfo();
        ArrayList parameterClasses = new ArrayList(allParameterInfo.length);
        for (MethodParameterInfo mpi : allParameterInfo) {
            TypeSignature actualParameterType;
            TypeSignature parameterType = mpi.getTypeSignatureOrTypeDescriptor();
            if (parameterType instanceof TypeVariableSignature) {
                TypeVariableSignature tvs = (TypeVariableSignature)parameterType;
                TypeParameter t = tvs.resolve();
                if (t.classBound != null) {
                    actualParameterType = t.classBound;
                } else {
                    if (t.interfaceBounds == null || t.interfaceBounds.isEmpty()) throw new IllegalArgumentException("TypeVariableSignature has no bounds");
                    actualParameterType = t.interfaceBounds.get(0);
                }
            } else {
                actualParameterType = parameterType;
            }
            parameterClasses.add(actualParameterType.loadClass());
        }
        return parameterClasses.toArray(new Class[0]);
    }

    public Method loadClassAndGetMethod() throws IllegalArgumentException {
        if (this.isConstructor()) {
            throw new IllegalArgumentException("Need to call loadClassAndGetConstructor() for constructors, not loadClassAndGetMethod()");
        }
        Class<?>[] parameterClassesArr = this.loadParameterClasses();
        try {
            return this.loadClass().getMethod(this.getName(), parameterClassesArr);
        }
        catch (NoSuchMethodException e1) {
            try {
                return this.loadClass().getDeclaredMethod(this.getName(), parameterClassesArr);
            }
            catch (NoSuchMethodException es2) {
                throw new IllegalArgumentException("Method not found: " + this.getClassName() + "." + this.getName());
            }
        }
    }

    public Constructor<?> loadClassAndGetConstructor() throws IllegalArgumentException {
        if (!this.isConstructor()) {
            throw new IllegalArgumentException("Need to call loadClassAndGetMethod() for non-constructor methods, not loadClassAndGetConstructor()");
        }
        Class<?>[] parameterClassesArr = this.loadParameterClasses();
        try {
            return this.loadClass().getConstructor(parameterClassesArr);
        }
        catch (NoSuchMethodException e1) {
            try {
                return this.loadClass().getDeclaredConstructor(parameterClassesArr);
            }
            catch (NoSuchMethodException es2) {
                throw new IllegalArgumentException("Constructor not found for class " + this.getClassName());
            }
        }
    }

    void handleRepeatableAnnotations(Set<String> allRepeatableAnnotationNames) {
        if (this.annotationInfo != null) {
            this.annotationInfo.handleRepeatableAnnotations(allRepeatableAnnotationNames, this.getClassInfo(), ClassInfo.RelType.METHOD_ANNOTATIONS, ClassInfo.RelType.CLASSES_WITH_METHOD_ANNOTATION, ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_METHOD_ANNOTATION);
        }
        if (this.parameterAnnotationInfo != null) {
            for (int i = 0; i < this.parameterAnnotationInfo.length; ++i) {
                AnnotationInfo[] pai = this.parameterAnnotationInfo[i];
                if (pai == null || pai.length <= 0) continue;
                boolean hasRepeatableAnnotation = false;
                for (AnnotationInfo ai : pai) {
                    if (!allRepeatableAnnotationNames.contains(ai.getName())) continue;
                    hasRepeatableAnnotation = true;
                    break;
                }
                if (!hasRepeatableAnnotation) continue;
                AnnotationInfoList aiList = new AnnotationInfoList(pai.length);
                aiList.addAll(Arrays.asList(pai));
                aiList.handleRepeatableAnnotations(allRepeatableAnnotationNames, this.getClassInfo(), ClassInfo.RelType.METHOD_PARAMETER_ANNOTATIONS, ClassInfo.RelType.CLASSES_WITH_METHOD_PARAMETER_ANNOTATION, ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_METHOD_PARAMETER_ANNOTATION);
                this.parameterAnnotationInfo[i] = aiList.toArray(new AnnotationInfo[0]);
            }
        }
    }

    @Override
    void setScanResult(ScanResult scanResult) {
        super.setScanResult(scanResult);
        if (this.typeDescriptor != null) {
            this.typeDescriptor.setScanResult(scanResult);
        }
        if (this.typeSignature != null) {
            this.typeSignature.setScanResult(scanResult);
        }
        if (this.annotationInfo != null) {
            for (AnnotationInfo ai : this.annotationInfo) {
                ai.setScanResult(scanResult);
            }
        }
        if (this.parameterAnnotationInfo != null) {
            for (AnnotationInfo[] pai : this.parameterAnnotationInfo) {
                if (pai == null) continue;
                for (Object object : pai) {
                    ((AnnotationInfo)object).setScanResult(scanResult);
                }
            }
        }
        if (this.parameterInfo != null) {
            for (MethodParameterInfo mpi : this.parameterInfo) {
                mpi.setScanResult(scanResult);
            }
        }
        if (this.thrownExceptions != null) {
            for (ClassInfo thrownException : this.thrownExceptions) {
                if (thrownException.scanResult != null) continue;
                thrownException.setScanResult(scanResult);
            }
        }
    }

    @Override
    protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
        ClassInfoList thrownExceptions;
        block13: {
            block12: {
                try {
                    MethodTypeSignature methodSig = this.getTypeSignature();
                    if (methodSig != null) {
                        methodSig.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
                    }
                }
                catch (IllegalArgumentException e) {
                    if (log == null) break block12;
                    log.log("Illegal type signature for method " + this.getClassName() + "." + this.getName() + ": " + this.getTypeSignatureStr());
                }
            }
            try {
                MethodTypeSignature methodDesc = this.getTypeDescriptor();
                if (methodDesc != null) {
                    methodDesc.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
                }
            }
            catch (IllegalArgumentException e) {
                if (log == null) break block13;
                log.log("Illegal type descriptor for method " + this.getClassName() + "." + this.getName() + ": " + this.getTypeDescriptorStr());
            }
        }
        if (this.annotationInfo != null) {
            for (AnnotationInfo ai : this.annotationInfo) {
                ai.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
            }
        }
        for (MethodParameterInfo mpi : this.getParameterInfo()) {
            AnnotationInfo[] aiArr = mpi.annotationInfo;
            if (aiArr == null) continue;
            for (AnnotationInfo ai : aiArr) {
                ai.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
            }
        }
        if (this.thrownExceptionNames != null && (thrownExceptions = this.getThrownExceptions()) != null) {
            for (int i = 0; i < thrownExceptions.size(); ++i) {
                classNameToClassInfo.put(this.thrownExceptionNames[i], (ClassInfo)thrownExceptions.get(i));
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MethodInfo)) {
            return false;
        }
        MethodInfo other = (MethodInfo)obj;
        return this.declaringClassName.equals(other.declaringClassName) && this.typeDescriptorStr.equals(other.typeDescriptorStr) && this.name.equals(other.name);
    }

    public int hashCode() {
        return this.name.hashCode() + this.typeDescriptorStr.hashCode() * 11 + this.declaringClassName.hashCode() * 57;
    }

    @Override
    public int compareTo(MethodInfo other) {
        int diff0 = this.declaringClassName.compareTo(other.declaringClassName);
        if (diff0 != 0) {
            return diff0;
        }
        int diff1 = this.name.compareTo(other.name);
        if (diff1 != 0) {
            return diff1;
        }
        return this.typeDescriptorStr.compareTo(other.typeDescriptorStr);
    }

    @Override
    protected void toString(boolean useSimpleNames, StringBuilder buf) {
        block34: {
            int i;
            block33: {
                List<TypeParameter> typeParameters;
                MethodTypeSignature methodType = this.getTypeSignatureOrTypeDescriptor();
                if (this.annotationInfo != null) {
                    for (AnnotationInfo annotation : this.annotationInfo) {
                        if (buf.length() > 0) {
                            buf.append(' ');
                        }
                        annotation.toString(useSimpleNames, buf);
                    }
                }
                if (this.modifiers != 0) {
                    if (buf.length() > 0) {
                        buf.append(' ');
                    }
                    TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.METHOD, this.isDefault(), buf);
                }
                if (!(typeParameters = methodType.getTypeParameters()).isEmpty()) {
                    if (buf.length() > 0) {
                        buf.append(' ');
                    }
                    buf.append('<');
                    for (int i2 = 0; i2 < typeParameters.size(); ++i2) {
                        if (i2 > 0) {
                            buf.append(", ");
                        }
                        typeParameters.get(i2).toString(useSimpleNames, buf);
                    }
                    buf.append('>');
                }
                if (!this.isConstructor()) {
                    if (buf.length() > 0) {
                        buf.append(' ');
                    }
                    methodType.getResultType().toStringInternal(useSimpleNames, this.annotationInfo, buf);
                }
                if (buf.length() > 0) {
                    buf.append(' ');
                }
                if (this.name != null) {
                    buf.append(useSimpleNames ? ClassInfo.getSimpleName(this.name) : this.name);
                }
                MethodParameterInfo[] allParamInfo = this.getParameterInfo();
                boolean hasParamNames = false;
                for (MethodParameterInfo methodParamInfo : allParamInfo) {
                    if (methodParamInfo.getName() == null) continue;
                    hasParamNames = true;
                    break;
                }
                int varArgsParamIndex = -1;
                if (this.isVarArgs()) {
                    for (i = allParamInfo.length - 1; i >= 0; --i) {
                        TypeSignature paramType;
                        int mods = allParamInfo[i].getModifiers();
                        if ((mods & 0x1000) != 0 || (mods & 0x8000) != 0 || !((paramType = allParamInfo[i].getTypeSignatureOrTypeDescriptor()) instanceof ArrayTypeSignature)) continue;
                        varArgsParamIndex = i;
                        break;
                    }
                }
                buf.append('(');
                int numParams = allParamInfo.length;
                for (i = 0; i < numParams; ++i) {
                    String paramName;
                    MethodParameterInfo paramInfo = allParamInfo[i];
                    if (i > 0) {
                        buf.append(", ");
                    }
                    if (paramInfo.annotationInfo != null) {
                        for (AnnotationInfo ai : paramInfo.annotationInfo) {
                            ai.toString(useSimpleNames, buf);
                            buf.append(' ');
                        }
                    }
                    MethodParameterInfo.modifiersToString(paramInfo.getModifiers(), buf);
                    TypeSignature paramTypeSignature = paramInfo.getTypeSignatureOrTypeDescriptor();
                    if (paramTypeSignature != null) {
                        if (i == varArgsParamIndex) {
                            if (!(paramTypeSignature instanceof ArrayTypeSignature)) {
                                throw new IllegalArgumentException("Got non-array type for last parameter of varargs method " + this.name);
                            }
                            ArrayTypeSignature arrayType = (ArrayTypeSignature)paramTypeSignature;
                            if (arrayType.getNumDimensions() == 0) {
                                throw new IllegalArgumentException("Got a zero-dimension array type for last parameter of varargs method " + this.name);
                            }
                            arrayType.getElementTypeSignature().toString(useSimpleNames, buf);
                            for (int j = 0; j < arrayType.getNumDimensions() - 1; ++j) {
                                buf.append("[]");
                            }
                            buf.append("...");
                        } else {
                            AnnotationInfoList annotationsToExclude;
                            if (paramInfo.annotationInfo == null || paramInfo.annotationInfo.length == 0) {
                                annotationsToExclude = null;
                            } else {
                                annotationsToExclude = new AnnotationInfoList(paramInfo.annotationInfo.length);
                                annotationsToExclude.addAll(Arrays.asList(paramInfo.annotationInfo));
                            }
                            paramTypeSignature.toStringInternal(useSimpleNames, annotationsToExclude, buf);
                        }
                    }
                    if (!hasParamNames || (paramName = paramInfo.getName()) == null) continue;
                    if (buf.charAt(buf.length() - 1) != ' ') {
                        buf.append(' ');
                    }
                    buf.append(paramName);
                }
                buf.append(')');
                if (methodType.getThrowsSignatures().isEmpty()) break block33;
                buf.append(" throws ");
                for (i = 0; i < methodType.getThrowsSignatures().size(); ++i) {
                    if (i > 0) {
                        buf.append(", ");
                    }
                    methodType.getThrowsSignatures().get(i).toString(useSimpleNames, buf);
                }
                break block34;
            }
            if (this.thrownExceptionNames == null || this.thrownExceptionNames.length <= 0) break block34;
            buf.append(" throws ");
            for (i = 0; i < this.thrownExceptionNames.length; ++i) {
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(useSimpleNames ? ClassInfo.getSimpleName(this.thrownExceptionNames[i]) : this.thrownExceptionNames[i]);
            }
        }
    }
}

