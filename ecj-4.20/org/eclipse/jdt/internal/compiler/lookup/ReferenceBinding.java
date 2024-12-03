/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationHolder;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

public abstract class ReferenceBinding
extends TypeBinding {
    public char[][] compoundName;
    public char[] sourceName;
    public int modifiers;
    public PackageBinding fPackage;
    char[] fileName;
    char[] constantPoolName;
    char[] signature;
    private SimpleLookupTable compatibleCache;
    int typeBits;
    protected MethodBinding[] singleAbstractMethod;
    public static final ReferenceBinding LUB_GENERIC = new ReferenceBinding(){
        {
            this.id = 0;
        }

        @Override
        public boolean hasTypeBit(int bit) {
            return false;
        }
    };
    private static final Comparator<FieldBinding> FIELD_COMPARATOR = new Comparator<FieldBinding>(){

        @Override
        public int compare(FieldBinding o1, FieldBinding o2) {
            char[] n1 = o1.name;
            char[] n2 = o2.name;
            return ReferenceBinding.compare(n1, n2, n1.length, n2.length);
        }
    };
    private static final Comparator<MethodBinding> METHOD_COMPARATOR = new Comparator<MethodBinding>(){

        @Override
        public int compare(MethodBinding m1, MethodBinding m2) {
            char[] s1 = m1.selector;
            char[] s2 = m2.selector;
            int c = ReferenceBinding.compare(s1, s2, s1.length, s2.length);
            return c == 0 ? m1.parameters.length - m2.parameters.length : c;
        }
    };
    protected static ProblemMethodBinding samProblemBinding = new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 17);
    static final Comparator<ReferenceBinding> BASIC_MEMBER_TYPES_COMPARATOR = (o1, o2) -> {
        char[] n1 = o1.sourceName;
        char[] n2 = o2.sourceName;
        if (n1 == null) {
            if (n2 == null) {
                return 0;
            }
            return -1;
        }
        if (n2 == null) {
            return 1;
        }
        return ReferenceBinding.compare(n1, n2, n1.length, n2.length);
    };

    public ReferenceBinding(ReferenceBinding prototype) {
        super(prototype);
        this.compoundName = prototype.compoundName;
        this.sourceName = prototype.sourceName;
        this.modifiers = prototype.modifiers;
        this.fPackage = prototype.fPackage;
        this.fileName = prototype.fileName;
        this.constantPoolName = prototype.constantPoolName;
        this.signature = prototype.signature;
        this.compatibleCache = prototype.compatibleCache;
        this.typeBits = prototype.typeBits;
        this.singleAbstractMethod = prototype.singleAbstractMethod;
    }

    public ReferenceBinding() {
    }

    public static FieldBinding binarySearch(char[] name, FieldBinding[] sortedFields) {
        if (sortedFields == null) {
            return null;
        }
        int max = sortedFields.length;
        if (max == 0) {
            return null;
        }
        int left = 0;
        int right = max - 1;
        int nameLength = name.length;
        int mid = 0;
        while (left <= right) {
            mid = left + (right - left) / 2;
            char[] midName = sortedFields[mid].name;
            int compare = ReferenceBinding.compare(name, sortedFields[mid].name, nameLength, midName.length);
            if (compare < 0) {
                right = mid - 1;
                continue;
            }
            if (compare > 0) {
                left = mid + 1;
                continue;
            }
            return sortedFields[mid];
        }
        return null;
    }

    public static long binarySearch(char[] selector, MethodBinding[] sortedMethods) {
        if (sortedMethods == null) {
            return -1L;
        }
        int max = sortedMethods.length;
        if (max == 0) {
            return -1L;
        }
        int left = 0;
        int right = max - 1;
        int selectorLength = selector.length;
        int mid = 0;
        while (left <= right) {
            mid = left + (right - left) / 2;
            char[] midSelector = sortedMethods[mid].selector;
            int compare = ReferenceBinding.compare(selector, sortedMethods[mid].selector, selectorLength, midSelector.length);
            if (compare < 0) {
                right = mid - 1;
                continue;
            }
            if (compare > 0) {
                left = mid + 1;
                continue;
            }
            int start = mid;
            int end = mid;
            while (start > left && CharOperation.equals(sortedMethods[start - 1].selector, selector)) {
                --start;
            }
            while (end < right && CharOperation.equals(sortedMethods[end + 1].selector, selector)) {
                ++end;
            }
            return (long)start + ((long)end << 32);
        }
        return -1L;
    }

    static int compare(char[] str1, char[] str2, int len1, int len2) {
        int n = Math.min(len1, len2);
        int i = 0;
        while (n-- != 0) {
            char c2;
            char c1 = str1[i];
            if (c1 == (c2 = str2[i++])) continue;
            return c1 - c2;
        }
        return len1 - len2;
    }

    public static void sortFields(FieldBinding[] sortedFields, int left, int right) {
        Arrays.sort(sortedFields, left, right, FIELD_COMPARATOR);
    }

    public static void sortMethods(MethodBinding[] sortedMethods, int left, int right) {
        Arrays.sort(sortedMethods, left, right, METHOD_COMPARATOR);
    }

    static void sortMemberTypes(ReferenceBinding[] sortedMemberTypes, int left, int right) {
        Arrays.sort(sortedMemberTypes, left, right, BASIC_MEMBER_TYPES_COMPARATOR);
    }

    public FieldBinding[] availableFields() {
        return this.fields();
    }

    public MethodBinding[] availableMethods() {
        return this.methods();
    }

    public boolean hasHierarchyCheckStarted() {
        return (this.tagBits & 0x100L) != 0L;
    }

    public void setHierarchyCheckDone() {
    }

    @Override
    public boolean canBeInstantiated() {
        return (this.modifiers & 0x6600) == 0;
    }

    public boolean canBeSeenBy(PackageBinding invocationPackage) {
        if (this.isPublic()) {
            return true;
        }
        if (this.isPrivate()) {
            return false;
        }
        return invocationPackage == this.fPackage;
    }

    public boolean canBeSeenBy(ReferenceBinding receiverType, ReferenceBinding invocationType) {
        block19: {
            block20: {
                block21: {
                    if (this.isPublic()) {
                        return true;
                    }
                    if (this.isStatic() && (receiverType.isRawType() || receiverType.isParameterizedType())) {
                        receiverType = receiverType.actualType();
                    }
                    if (TypeBinding.equalsEquals(invocationType, this) && TypeBinding.equalsEquals(invocationType, receiverType)) {
                        return true;
                    }
                    if (this.isProtected()) {
                        if (TypeBinding.equalsEquals(invocationType, this)) {
                            return true;
                        }
                        if (invocationType.fPackage == this.fPackage) {
                            return true;
                        }
                        TypeBinding currentType = invocationType.erasure();
                        TypeBinding declaringClass = this.enclosingType().erasure();
                        if (TypeBinding.equalsEquals(declaringClass, invocationType)) {
                            return true;
                        }
                        if (declaringClass == null) {
                            return false;
                        }
                        do {
                            if (currentType.findSuperTypeOriginatingFrom(declaringClass) == null) continue;
                            return true;
                        } while ((currentType = currentType.enclosingType()) != null);
                        return false;
                    }
                    if (!this.isPrivate()) break block19;
                    if (TypeBinding.equalsEquals(receiverType, this) || TypeBinding.equalsEquals(receiverType, this.enclosingType())) break block20;
                    if (!receiverType.isTypeVariable()) break block21;
                    TypeVariableBinding typeVariable = (TypeVariableBinding)receiverType;
                    if (typeVariable.environment.globalOptions.complianceLevel <= 0x320000L && (typeVariable.isErasureBoundTo(this.erasure()) || typeVariable.isErasureBoundTo(this.enclosingType().erasure()))) break block20;
                }
                return false;
            }
            if (TypeBinding.notEquals(invocationType, this)) {
                ReferenceBinding outerInvocationType = invocationType;
                ReferenceBinding temp = outerInvocationType.enclosingType();
                while (temp != null) {
                    outerInvocationType = temp;
                    temp = temp.enclosingType();
                }
                ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.erasure();
                temp = outerDeclaringClass.enclosingType();
                while (temp != null) {
                    outerDeclaringClass = temp;
                    temp = temp.enclosingType();
                }
                if (TypeBinding.notEquals(outerInvocationType, outerDeclaringClass)) {
                    return false;
                }
            }
            return true;
        }
        if (invocationType.fPackage != this.fPackage) {
            return false;
        }
        ReferenceBinding currentType = receiverType;
        TypeBinding originalDeclaringClass = (this.enclosingType() == null ? this : this.enclosingType()).original();
        do {
            if (currentType.isCapture() ? TypeBinding.equalsEquals(originalDeclaringClass, currentType.erasure().original()) : TypeBinding.equalsEquals(originalDeclaringClass, currentType.original())) {
                return true;
            }
            PackageBinding currentPackage = currentType.fPackage;
            if (currentPackage == null || currentPackage == this.fPackage) continue;
            return false;
        } while ((currentType = currentType.superclass()) != null);
        return false;
    }

    @Override
    public boolean canBeSeenBy(Scope scope) {
        if (this.isPublic()) {
            return true;
        }
        SourceTypeBinding invocationType = scope.enclosingSourceType();
        if (TypeBinding.equalsEquals(invocationType, this)) {
            return true;
        }
        if (invocationType == null) {
            return !this.isPrivate() && scope.getCurrentPackage() == this.fPackage;
        }
        if (this.isProtected()) {
            if (invocationType.fPackage == this.fPackage) {
                return true;
            }
            TypeBinding declaringClass = this.enclosingType();
            if (declaringClass == null) {
                return false;
            }
            declaringClass = declaringClass.erasure();
            TypeBinding currentType = invocationType.erasure();
            do {
                if (TypeBinding.equalsEquals(declaringClass, invocationType)) {
                    return true;
                }
                if (currentType.findSuperTypeOriginatingFrom(declaringClass) == null) continue;
                return true;
            } while ((currentType = currentType.enclosingType()) != null);
            return false;
        }
        if (this.isPrivate()) {
            ReferenceBinding outerInvocationType = invocationType;
            ReferenceBinding temp = outerInvocationType.enclosingType();
            while (temp != null) {
                outerInvocationType = temp;
                temp = temp.enclosingType();
            }
            ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.erasure();
            temp = outerDeclaringClass.enclosingType();
            while (temp != null) {
                outerDeclaringClass = temp;
                temp = temp.enclosingType();
            }
            return TypeBinding.equalsEquals(outerInvocationType, outerDeclaringClass);
        }
        return invocationType.fPackage == this.fPackage;
    }

    public char[] computeGenericTypeSignature(TypeVariableBinding[] typeVariables) {
        char[] typeSig;
        boolean isMemberOfGeneric;
        boolean bl = isMemberOfGeneric = this.isMemberType() && this.hasEnclosingInstanceContext() && (this.enclosingType().modifiers & 0x40000000) != 0;
        if (typeVariables == Binding.NO_TYPE_VARIABLES && !isMemberOfGeneric) {
            return this.signature();
        }
        StringBuffer sig = new StringBuffer(10);
        if (isMemberOfGeneric) {
            typeSig = this.enclosingType().genericTypeSignature();
            sig.append(typeSig, 0, typeSig.length - 1);
            sig.append('.');
            sig.append(this.sourceName);
        } else {
            typeSig = this.signature();
            sig.append(typeSig, 0, typeSig.length - 1);
        }
        if (typeVariables == Binding.NO_TYPE_VARIABLES) {
            sig.append(';');
        } else {
            sig.append('<');
            int i = 0;
            int length = typeVariables.length;
            while (i < length) {
                sig.append(typeVariables[i].genericTypeSignature());
                ++i;
            }
            sig.append(">;");
        }
        int sigLength = sig.length();
        char[] result = new char[sigLength];
        sig.getChars(0, sigLength, result, 0);
        return result;
    }

    public void computeId() {
        block0 : switch (this.compoundName.length) {
            case 3: {
                char[] packageName = this.compoundName[0];
                switch (packageName.length) {
                    case 3: {
                        if (CharOperation.equals(TypeConstants.ORG_JUNIT_ASSERT, this.compoundName)) {
                            this.id = 70;
                        } else if (CharOperation.equals(TypeConstants.JDK_INTERNAL_PREVIEW_FEATURE, this.compoundName)) {
                            this.id = 94;
                        }
                        return;
                    }
                    case 4: {
                        if (CharOperation.equals(TypeConstants.JAVA, packageName)) break;
                        return;
                    }
                    case 5: {
                        switch (packageName[1]) {
                            case 'a': {
                                if (CharOperation.equals(TypeConstants.JAVAX_ANNOTATION_INJECT_INJECT, this.compoundName)) {
                                    this.id = 80;
                                }
                                return;
                            }
                            case 'u': {
                                if (CharOperation.equals(TypeConstants.JUNIT_FRAMEWORK_ASSERT, this.compoundName)) {
                                    this.id = 69;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    default: {
                        return;
                    }
                }
                packageName = this.compoundName[1];
                if (packageName.length == 0) {
                    return;
                }
                char[] typeName = this.compoundName[2];
                if (typeName.length == 0) {
                    return;
                }
                if (!CharOperation.equals(TypeConstants.LANG, this.compoundName[1])) {
                    switch (packageName[0]) {
                        case 'i': {
                            if (CharOperation.equals(packageName, TypeConstants.IO)) {
                                switch (typeName[0]) {
                                    case 'C': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_CLOSEABLE[2])) {
                                            this.typeBits |= 2;
                                        }
                                        return;
                                    }
                                    case 'E': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_EXTERNALIZABLE[2])) {
                                            this.id = 56;
                                        }
                                        return;
                                    }
                                    case 'I': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_IOEXCEPTION[2])) {
                                            this.id = 58;
                                        }
                                        return;
                                    }
                                    case 'O': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_OBJECTSTREAMEXCEPTION[2])) {
                                            this.id = 57;
                                        }
                                        return;
                                    }
                                    case 'P': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_PRINTSTREAM[2])) {
                                            this.id = 53;
                                        }
                                        return;
                                    }
                                    case 'S': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_SERIALIZABLE[2])) {
                                            this.id = 37;
                                        }
                                        return;
                                    }
                                }
                            }
                            return;
                        }
                        case 'u': {
                            if (CharOperation.equals(packageName, TypeConstants.UTIL)) {
                                switch (typeName[0]) {
                                    case 'C': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_COLLECTION[2])) {
                                            this.id = 59;
                                            this.typeBits |= 0x200;
                                        }
                                        return;
                                    }
                                    case 'I': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_ITERATOR[2])) {
                                            this.id = 39;
                                        }
                                        return;
                                    }
                                    case 'L': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_LIST[2])) {
                                            this.id = 92;
                                            this.typeBits |= 0x400;
                                        }
                                        return;
                                    }
                                    case 'M': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_MAP[2])) {
                                            this.id = 91;
                                            this.typeBits |= 0x100;
                                        }
                                        return;
                                    }
                                    case 'O': {
                                        if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_OBJECTS[2])) {
                                            this.id = 74;
                                        }
                                        return;
                                    }
                                }
                            }
                            return;
                        }
                    }
                    return;
                }
                switch (typeName[0]) {
                    case 'A': {
                        switch (typeName.length) {
                            case 13: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_AUTOCLOSEABLE[2])) {
                                    this.id = 62;
                                    this.typeBits |= 1;
                                }
                                return;
                            }
                            case 14: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ASSERTIONERROR[2])) {
                                    this.id = 35;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    case 'B': {
                        switch (typeName.length) {
                            case 4: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_BYTE[2])) {
                                    this.id = 26;
                                }
                                return;
                            }
                            case 7: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_BOOLEAN[2])) {
                                    this.id = 33;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    case 'C': {
                        switch (typeName.length) {
                            case 5: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLASS[2])) {
                                    this.id = 16;
                                }
                                return;
                            }
                            case 9: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CHARACTER[2])) {
                                    this.id = 28;
                                } else if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLONEABLE[2])) {
                                    this.id = 36;
                                }
                                return;
                            }
                            case 22: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLASSNOTFOUNDEXCEPTION[2])) {
                                    this.id = 23;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    case 'D': {
                        switch (typeName.length) {
                            case 6: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_DOUBLE[2])) {
                                    this.id = 32;
                                }
                                return;
                            }
                            case 10: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_DEPRECATED[2])) {
                                    this.id = 44;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    case 'E': {
                        switch (typeName.length) {
                            case 4: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ENUM[2])) {
                                    this.id = 41;
                                }
                                return;
                            }
                            case 5: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ERROR[2])) {
                                    this.id = 19;
                                }
                                return;
                            }
                            case 9: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_EXCEPTION[2])) {
                                    this.id = 25;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    case 'F': {
                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_FLOAT[2])) {
                            this.id = 31;
                        } else if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_FUNCTIONAL_INTERFACE[2])) {
                            this.id = 77;
                        }
                        return;
                    }
                    case 'I': {
                        switch (typeName.length) {
                            case 7: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_INTEGER[2])) {
                                    this.id = 29;
                                }
                                return;
                            }
                            case 8: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ITERABLE[2])) {
                                    this.id = 38;
                                }
                                return;
                            }
                            case 24: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ILLEGALARGUMENTEXCEPTION[2])) {
                                    this.id = 42;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    case 'L': {
                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_LONG[2])) {
                            this.id = 30;
                        }
                        return;
                    }
                    case 'N': {
                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_NOCLASSDEFERROR[2])) {
                            this.id = 22;
                        }
                        return;
                    }
                    case 'O': {
                        switch (typeName.length) {
                            case 6: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_OBJECT[2])) {
                                    this.id = 1;
                                }
                                return;
                            }
                            case 8: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_OVERRIDE[2])) {
                                    this.id = 47;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    case 'R': {
                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_RUNTIMEEXCEPTION[2])) {
                            this.id = 24;
                        }
                        if (!CharOperation.equals(typeName, TypeConstants.JAVA_LANG_RECORD[2])) break block0;
                        this.id = 93;
                        break block0;
                    }
                    case 'S': {
                        switch (typeName.length) {
                            case 5: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SHORT[2])) {
                                    this.id = 27;
                                }
                                return;
                            }
                            case 6: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRING[2])) {
                                    this.id = 11;
                                } else if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SYSTEM[2])) {
                                    this.id = 18;
                                }
                                return;
                            }
                            case 11: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SAFEVARARGS[2])) {
                                    this.id = 60;
                                }
                                return;
                            }
                            case 12: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRINGBUFFER[2])) {
                                    this.id = 17;
                                }
                                return;
                            }
                            case 13: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRINGBUILDER[2])) {
                                    this.id = 40;
                                }
                                return;
                            }
                            case 16: {
                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SUPPRESSWARNINGS[2])) {
                                    this.id = 49;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    case 'T': {
                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_THROWABLE[2])) {
                            this.id = 21;
                        }
                        return;
                    }
                    case 'V': {
                        if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_VOID[2])) {
                            this.id = 34;
                        }
                        return;
                    }
                }
                break;
            }
            case 4: {
                if (CharOperation.equals(TypeConstants.COM_GOOGLE_INJECT_INJECT, this.compoundName)) {
                    this.id = 81;
                    return;
                }
                if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0])) {
                    return;
                }
                char[] packageName = this.compoundName[1];
                if (packageName.length == 0) {
                    return;
                }
                packageName = this.compoundName[2];
                if (packageName.length == 0) {
                    return;
                }
                char[] typeName = this.compoundName[3];
                if (typeName.length == 0) {
                    return;
                }
                switch (packageName[0]) {
                    case 'a': {
                        if (CharOperation.equals(packageName, TypeConstants.ANNOTATION)) {
                            switch (typeName[0]) {
                                case 'A': {
                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION[3])) {
                                        this.id = 43;
                                    }
                                    return;
                                }
                                case 'D': {
                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_DOCUMENTED[3])) {
                                        this.id = 45;
                                    }
                                    return;
                                }
                                case 'E': {
                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_ELEMENTTYPE[3])) {
                                        this.id = 52;
                                    }
                                    return;
                                }
                                case 'I': {
                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_INHERITED[3])) {
                                        this.id = 46;
                                    }
                                    return;
                                }
                                case 'R': {
                                    switch (typeName.length) {
                                        case 9: {
                                            if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_RETENTION[3])) {
                                                this.id = 48;
                                            }
                                            return;
                                        }
                                        case 10: {
                                            if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_REPEATABLE[3])) {
                                                this.id = 90;
                                            }
                                            return;
                                        }
                                        case 15: {
                                            if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_RETENTIONPOLICY[3])) {
                                                this.id = 51;
                                            }
                                            return;
                                        }
                                    }
                                    return;
                                }
                                case 'T': {
                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_TARGET[3])) {
                                        this.id = 50;
                                    }
                                    return;
                                }
                            }
                        }
                        return;
                    }
                    case 'i': {
                        if (CharOperation.equals(packageName, TypeConstants.INVOKE)) {
                            if (typeName.length == 0) {
                                return;
                            }
                            switch (typeName[0]) {
                                case 'M': {
                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_$_POLYMORPHICSIGNATURE[3])) {
                                        this.id = 61;
                                    }
                                    return;
                                }
                            }
                        }
                        return;
                    }
                    case 'r': {
                        if (CharOperation.equals(packageName, TypeConstants.REFLECT)) {
                            switch (typeName[0]) {
                                case 'C': {
                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_CONSTRUCTOR[2])) {
                                        this.id = 20;
                                    }
                                    return;
                                }
                                case 'F': {
                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_FIELD[2])) {
                                        this.id = 54;
                                    }
                                    return;
                                }
                                case 'M': {
                                    if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_METHOD[2])) {
                                        this.id = 55;
                                    }
                                    return;
                                }
                            }
                        }
                        return;
                    }
                }
                break;
            }
            case 5: {
                char[] packageName = this.compoundName[0];
                switch (packageName[0]) {
                    case 'j': {
                        if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0])) {
                            return;
                        }
                        packageName = this.compoundName[1];
                        if (packageName.length == 0) {
                            return;
                        }
                        if (CharOperation.equals(TypeConstants.LANG, packageName)) {
                            packageName = this.compoundName[2];
                            if (packageName.length == 0) {
                                return;
                            }
                            switch (packageName[0]) {
                                case 'i': {
                                    if (CharOperation.equals(packageName, TypeConstants.INVOKE)) {
                                        char[] typeName = this.compoundName[3];
                                        if (typeName.length == 0) {
                                            return;
                                        }
                                        switch (typeName[0]) {
                                            case 'M': {
                                                char[] memberTypeName = this.compoundName[4];
                                                if (memberTypeName.length == 0) {
                                                    return;
                                                }
                                                if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE[3]) && CharOperation.equals(memberTypeName, TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE[4])) {
                                                    this.id = 61;
                                                }
                                                return;
                                            }
                                        }
                                    }
                                    return;
                                }
                            }
                            return;
                        }
                        return;
                    }
                    case 'o': {
                        if (!CharOperation.equals(TypeConstants.ORG, this.compoundName[0])) {
                            return;
                        }
                        packageName = this.compoundName[1];
                        if (packageName.length == 0) {
                            return;
                        }
                        switch (packageName[0]) {
                            case 'e': {
                                if (CharOperation.equals(TypeConstants.ECLIPSE, packageName)) {
                                    packageName = this.compoundName[2];
                                    if (packageName.length == 0) {
                                        return;
                                    }
                                    switch (packageName[0]) {
                                        case 'c': {
                                            if (CharOperation.equals(packageName, TypeConstants.CORE)) {
                                                char[] typeName = this.compoundName[3];
                                                if (typeName.length == 0) {
                                                    return;
                                                }
                                                switch (typeName[0]) {
                                                    case 'r': {
                                                        char[] memberTypeName = this.compoundName[4];
                                                        if (memberTypeName.length == 0) {
                                                            return;
                                                        }
                                                        if (CharOperation.equals(typeName, TypeConstants.ORG_ECLIPSE_CORE_RUNTIME_ASSERT[3]) && CharOperation.equals(memberTypeName, TypeConstants.ORG_ECLIPSE_CORE_RUNTIME_ASSERT[4])) {
                                                            this.id = 68;
                                                        }
                                                        return;
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                    }
                                    return;
                                }
                                return;
                            }
                            case 'a': {
                                if (CharOperation.equals(TypeConstants.APACHE, packageName) && CharOperation.equals(TypeConstants.COMMONS, this.compoundName[2])) {
                                    if (CharOperation.equals(TypeConstants.ORG_APACHE_COMMONS_LANG_VALIDATE, this.compoundName)) {
                                        this.id = 71;
                                    } else if (CharOperation.equals(TypeConstants.ORG_APACHE_COMMONS_LANG3_VALIDATE, this.compoundName)) {
                                        this.id = 72;
                                    }
                                }
                                return;
                            }
                            case 'j': {
                                if (CharOperation.equals(TypeConstants.ORG_JUNIT_JUPITER_API_ASSERTIONS, this.compoundName)) {
                                    this.id = 75;
                                }
                                return;
                            }
                        }
                        return;
                    }
                    case 'c': {
                        if (!CharOperation.equals(TypeConstants.COM, this.compoundName[0])) {
                            return;
                        }
                        if (CharOperation.equals(TypeConstants.COM_GOOGLE_COMMON_BASE_PRECONDITIONS, this.compoundName)) {
                            this.id = 73;
                        }
                        return;
                    }
                }
                break;
            }
            case 6: {
                if (!CharOperation.equals(TypeConstants.ORG, this.compoundName[0])) break;
                if (CharOperation.equals(TypeConstants.SPRING, this.compoundName[1])) {
                    if (CharOperation.equals(TypeConstants.AUTOWIRED, this.compoundName[5]) && CharOperation.equals(TypeConstants.ORG_SPRING_AUTOWIRED, this.compoundName)) {
                        this.id = 82;
                    }
                    return;
                }
                if (CharOperation.equals(TypeConstants.JUNIT, this.compoundName[1])) {
                    if (CharOperation.equals(TypeConstants.METHOD_SOURCE, this.compoundName[5]) && CharOperation.equals(TypeConstants.ORG_JUNIT_METHOD_SOURCE, this.compoundName)) {
                        this.id = 93;
                    }
                    return;
                }
                if (!CharOperation.equals(TypeConstants.JDT, this.compoundName[2]) || !CharOperation.equals(TypeConstants.ITYPEBINDING, this.compoundName[5])) {
                    return;
                }
                if (!CharOperation.equals(TypeConstants.ORG_ECLIPSE_JDT_CORE_DOM_ITYPEBINDING, this.compoundName)) break;
                this.typeBits |= 0x10;
                break;
            }
            case 7: {
                if (!CharOperation.equals(TypeConstants.JDT, this.compoundName[2]) || !CharOperation.equals(TypeConstants.TYPEBINDING, this.compoundName[6])) {
                    return;
                }
                if (!CharOperation.equals(TypeConstants.ORG_ECLIPSE_JDT_INTERNAL_COMPILER_LOOKUP_TYPEBINDING, this.compoundName)) break;
                this.typeBits |= 0x10;
            }
        }
    }

    public void computeId(LookupEnvironment environment) {
        environment.getUnannotatedType(this);
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        if (!isLeaf) {
            return this.signature();
        }
        return this.genericTypeSignature();
    }

    @Override
    public char[] constantPoolName() {
        if (this.constantPoolName != null) {
            return this.constantPoolName;
        }
        this.constantPoolName = CharOperation.concatWith(this.compoundName, '/');
        return this.constantPoolName;
    }

    @Override
    public String debugName() {
        return this.compoundName != null ? (this.hasTypeAnnotations() ? this.annotatedDebugName() : new String(this.readableName())) : "UNNAMED TYPE";
    }

    @Override
    public int depth() {
        int depth = 0;
        ReferenceBinding current = this;
        while ((current = current.enclosingType()) != null) {
            ++depth;
        }
        return depth;
    }

    public boolean detectAnnotationCycle() {
        if ((this.tagBits & 0x100000000L) == 0x100000000L) {
            return false;
        }
        if ((this.tagBits & 0x80000000L) == 0x80000000L) {
            return true;
        }
        this.tagBits |= 0x80000000L;
        MethodBinding[] currentMethods = this.methods();
        boolean inCycle = false;
        int i = 0;
        int l = currentMethods.length;
        while (i < l) {
            MethodDeclaration decl;
            TypeBinding returnType = currentMethods[i].returnType.leafComponentType().erasure();
            if (TypeBinding.equalsEquals(this, returnType)) {
                if (this instanceof SourceTypeBinding) {
                    decl = (MethodDeclaration)currentMethods[i].sourceMethod();
                    ((SourceTypeBinding)this).scope.problemReporter().annotationCircularity(this, this, decl != null ? decl.returnType : null);
                }
            } else if (returnType.isAnnotationType() && ((ReferenceBinding)returnType).detectAnnotationCycle()) {
                if (this instanceof SourceTypeBinding) {
                    decl = (MethodDeclaration)currentMethods[i].sourceMethod();
                    ((SourceTypeBinding)this).scope.problemReporter().annotationCircularity(this, returnType, decl != null ? decl.returnType : null);
                }
                inCycle = true;
            }
            ++i;
        }
        if (inCycle) {
            return true;
        }
        this.tagBits &= 0xFFFFFFFF7FFFFFFFL;
        this.tagBits |= 0x100000000L;
        return false;
    }

    public final ReferenceBinding enclosingTypeAt(int relativeDepth) {
        ReferenceBinding current = this;
        while (relativeDepth-- > 0 && current != null) {
            current = current.enclosingType();
        }
        return current;
    }

    public int enumConstantCount() {
        int count = 0;
        FieldBinding[] fields = this.fields();
        int i = 0;
        int length = fields.length;
        while (i < length) {
            if ((fields[i].modifiers & 0x4000) != 0) {
                ++count;
            }
            ++i;
        }
        return count;
    }

    public int fieldCount() {
        return this.fields().length;
    }

    public FieldBinding[] fields() {
        return Binding.NO_FIELDS;
    }

    public RecordComponentBinding[] components() {
        return Binding.NO_COMPONENTS;
    }

    public final int getAccessFlags() {
        return this.modifiers & 0xFFFF;
    }

    @Override
    public AnnotationBinding[] getAnnotations() {
        return this.retrieveAnnotations(this);
    }

    @Override
    public long getAnnotationTagBits() {
        return this.tagBits;
    }

    public int getEnclosingInstancesSlotSize() {
        if (this.isStatic()) {
            return 0;
        }
        return this.enclosingType() == null ? 0 : 1;
    }

    public MethodBinding getExactConstructor(TypeBinding[] argumentTypes) {
        return null;
    }

    public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope) {
        return null;
    }

    public FieldBinding getField(char[] fieldName, boolean needResolve) {
        return null;
    }

    public char[] getFileName() {
        return this.fileName;
    }

    public ReferenceBinding getMemberType(char[] typeName) {
        ReferenceBinding[] memberTypes = this.memberTypes();
        int memberTypeIndex = ReferenceBinding.binarySearch(typeName, memberTypes);
        if (memberTypeIndex >= 0) {
            return memberTypes[memberTypeIndex];
        }
        return null;
    }

    static int binarySearch(char[] sourceName, ReferenceBinding[] sortedMemberTypes) {
        if (sortedMemberTypes == null) {
            return -1;
        }
        int max = sortedMemberTypes.length;
        int nameLength = sourceName.length;
        if (max == 0) {
            return -1;
        }
        int left = 0;
        int right = max - 1;
        while (left <= right) {
            int compare;
            int mid = left + (right - left) / 2;
            char[] midName = sortedMemberTypes[mid].sourceName;
            int n = compare = midName == null ? 1 : ReferenceBinding.compare(sourceName, midName, nameLength, midName.length);
            if (compare < 0) {
                right = mid - 1;
                continue;
            }
            if (compare > 0) {
                left = mid + 1;
                continue;
            }
            return mid;
        }
        return -1;
    }

    @Override
    public MethodBinding[] getMethods(char[] selector) {
        return Binding.NO_METHODS;
    }

    public MethodBinding[] getMethods(char[] selector, int suggestedParameterLength) {
        return this.getMethods(selector);
    }

    public int getOuterLocalVariablesSlotSize() {
        return 0;
    }

    @Override
    public PackageBinding getPackage() {
        return this.fPackage;
    }

    public TypeVariableBinding getTypeVariable(char[] variableName) {
        TypeVariableBinding[] typeVariables = this.typeVariables();
        int i = typeVariables.length;
        while (--i >= 0) {
            if (!CharOperation.equals(typeVariables[i].sourceName, variableName)) continue;
            return typeVariables[i];
        }
        return null;
    }

    public int hashCode() {
        return this.compoundName == null || this.compoundName.length == 0 ? super.hashCode() : CharOperation.hashCode(this.compoundName[this.compoundName.length - 1]);
    }

    final int identityHashCode() {
        return super.hashCode();
    }

    public boolean hasIncompatibleSuperType(ReferenceBinding otherType) {
        TypeBinding match;
        if (TypeBinding.equalsEquals(this, otherType)) {
            return false;
        }
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        ReferenceBinding currentType = this;
        do {
            if ((match = otherType.findSuperTypeOriginatingFrom(currentType)) != null && match.isProvablyDistinct(currentType)) {
                return true;
            }
            ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces == null || itsInterfaces == Binding.NO_SUPERINTERFACES) continue;
            if (interfacesToVisit == null) {
                interfacesToVisit = itsInterfaces;
                nextPosition = interfacesToVisit.length;
                continue;
            }
            int itsLength = itsInterfaces.length;
            if (nextPosition + itsLength >= interfacesToVisit.length) {
                ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
            }
            int a = 0;
            while (a < itsLength) {
                block16: {
                    ReferenceBinding next = itsInterfaces[a];
                    int b = 0;
                    while (b < nextPosition) {
                        if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                            ++b;
                            continue;
                        }
                        break block16;
                    }
                    interfacesToVisit[nextPosition++] = next;
                }
                ++a;
            }
        } while ((currentType = currentType.superclass()) != null);
        int i = 0;
        while (i < nextPosition) {
            currentType = interfacesToVisit[i];
            if (TypeBinding.equalsEquals(currentType, otherType)) {
                return false;
            }
            match = otherType.findSuperTypeOriginatingFrom(currentType);
            if (match != null && match.isProvablyDistinct(currentType)) {
                return true;
            }
            ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                int itsLength = itsInterfaces.length;
                if (nextPosition + itsLength >= interfacesToVisit.length) {
                    ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                    interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                    System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                }
                int a = 0;
                while (a < itsLength) {
                    block17: {
                        ReferenceBinding next = itsInterfaces[a];
                        int b = 0;
                        while (b < nextPosition) {
                            if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                ++b;
                                continue;
                            }
                            break block17;
                        }
                        interfacesToVisit[nextPosition++] = next;
                    }
                    ++a;
                }
            }
            ++i;
        }
        return false;
    }

    public boolean hasMemberTypes() {
        return false;
    }

    boolean hasNonNullDefaultFor(int location, int sourceStart) {
        ReferenceBinding currentType = this;
        while (currentType != null) {
            int nullDefault = ((ReferenceBinding)currentType.original()).getNullDefault();
            if (nullDefault != 0) {
                return (nullDefault & location) != 0;
            }
            currentType = currentType.enclosingType();
        }
        return (this.getPackage().getDefaultNullness() & location) != 0;
    }

    int getNullDefault() {
        return 0;
    }

    @Override
    public boolean acceptsNonNullDefault() {
        return true;
    }

    public final boolean hasRestrictedAccess() {
        return (this.modifiers & 0x40000) != 0;
    }

    public boolean hasNullBit(int mask) {
        return (this.typeBits & mask) != 0;
    }

    public boolean implementsInterface(ReferenceBinding anInterface, boolean searchHierarchy) {
        if (TypeBinding.equalsEquals(this, anInterface)) {
            return true;
        }
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        ReferenceBinding currentType = this;
        do {
            ReferenceBinding[] itsInterfaces;
            if ((itsInterfaces = currentType.superInterfaces()) == null || itsInterfaces == Binding.NO_SUPERINTERFACES) continue;
            if (interfacesToVisit == null) {
                interfacesToVisit = itsInterfaces;
                nextPosition = interfacesToVisit.length;
                continue;
            }
            int itsLength = itsInterfaces.length;
            if (nextPosition + itsLength >= interfacesToVisit.length) {
                ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
            }
            int a = 0;
            while (a < itsLength) {
                block14: {
                    ReferenceBinding next = itsInterfaces[a];
                    int b = 0;
                    while (b < nextPosition) {
                        if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                            ++b;
                            continue;
                        }
                        break block14;
                    }
                    interfacesToVisit[nextPosition++] = next;
                }
                ++a;
            }
        } while (searchHierarchy && (currentType = currentType.superclass()) != null);
        int i = 0;
        while (i < nextPosition) {
            currentType = interfacesToVisit[i];
            if (currentType.isEquivalentTo(anInterface)) {
                return true;
            }
            ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                int itsLength = itsInterfaces.length;
                if (nextPosition + itsLength >= interfacesToVisit.length) {
                    ReferenceBinding[] referenceBindingArray = interfacesToVisit;
                    interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5];
                    System.arraycopy(referenceBindingArray, 0, interfacesToVisit, 0, nextPosition);
                }
                int a = 0;
                while (a < itsLength) {
                    block15: {
                        ReferenceBinding next = itsInterfaces[a];
                        int b = 0;
                        while (b < nextPosition) {
                            if (!TypeBinding.equalsEquals(next, interfacesToVisit[b])) {
                                ++b;
                                continue;
                            }
                            break block15;
                        }
                        interfacesToVisit[nextPosition++] = next;
                    }
                    ++a;
                }
            }
            ++i;
        }
        return false;
    }

    boolean implementsMethod(MethodBinding method) {
        char[] selector = method.selector;
        ReferenceBinding type = this;
        while (type != null) {
            MethodBinding[] methods = type.methods();
            long range = ReferenceBinding.binarySearch(selector, methods);
            if (range >= 0L) {
                int start = (int)range;
                int end = (int)(range >> 32);
                int i = start;
                while (i <= end) {
                    if (methods[i].areParametersEqual(method)) {
                        return true;
                    }
                    ++i;
                }
            }
            type = type.superclass();
        }
        return false;
    }

    public final boolean isAbstract() {
        return (this.modifiers & 0x400) != 0;
    }

    @Override
    public boolean isAnnotationType() {
        return (this.modifiers & 0x2000) != 0;
    }

    public final boolean isBinaryBinding() {
        return (this.tagBits & 0x40L) != 0L;
    }

    @Override
    public boolean isClass() {
        return (this.modifiers & 0x6200) == 0;
    }

    private static SourceTypeBinding getSourceTypeBinding(ReferenceBinding ref) {
        if (ref instanceof SourceTypeBinding) {
            return (SourceTypeBinding)ref;
        }
        if (ref instanceof ParameterizedTypeBinding) {
            ParameterizedTypeBinding ptb = (ParameterizedTypeBinding)ref;
            return ptb.type instanceof SourceTypeBinding ? (SourceTypeBinding)ptb.type : null;
        }
        return null;
    }

    public boolean isNestmateOf(ReferenceBinding other) {
        SourceTypeBinding s1 = ReferenceBinding.getSourceTypeBinding(this);
        SourceTypeBinding s2 = ReferenceBinding.getSourceTypeBinding(other);
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.isNestmateOf(s2);
    }

    @Override
    public boolean isProperType(boolean admitCapture18) {
        ReferenceBinding outer = this.enclosingType();
        if (outer != null && !outer.isProperType(admitCapture18)) {
            return false;
        }
        return super.isProperType(admitCapture18);
    }

    @Override
    public boolean isCompatibleWith(TypeBinding otherType, Scope captureScope) {
        if (ReferenceBinding.equalsEquals(otherType, this)) {
            return true;
        }
        if (otherType.id == 1) {
            return true;
        }
        if (this.compatibleCache == null) {
            this.compatibleCache = new SimpleLookupTable(3);
            Object result = null;
        } else {
            Object result = this.compatibleCache.get(otherType);
            if (result != null) {
                return result == Boolean.TRUE;
            }
        }
        this.compatibleCache.put(otherType, Boolean.FALSE);
        if (this.isCompatibleWith0(otherType, captureScope)) {
            this.compatibleCache.put(otherType, Boolean.TRUE);
            return true;
        }
        if (captureScope == null && this instanceof TypeVariableBinding && ((TypeVariableBinding)this).firstBound instanceof ParameterizedTypeBinding) {
            this.compatibleCache.put(otherType, null);
        }
        return false;
    }

    private boolean isCompatibleWith0(TypeBinding otherType, Scope captureScope) {
        if (TypeBinding.equalsEquals(otherType, this)) {
            return true;
        }
        if (otherType.id == 1) {
            return true;
        }
        if (this.isEquivalentTo(otherType)) {
            return true;
        }
        switch (otherType.kind()) {
            case 516: 
            case 8196: {
                return false;
            }
            case 4100: {
                ReferenceContext referenceContext;
                MethodScope methodScope;
                if (otherType.isCapture()) {
                    CaptureBinding otherCapture = (CaptureBinding)otherType;
                    TypeBinding otherLowerBound = otherCapture.lowerBound;
                    if (otherLowerBound != null) {
                        if (otherLowerBound.isArrayType()) {
                            return false;
                        }
                        return this.isCompatibleWith(otherLowerBound);
                    }
                }
                if (otherType instanceof InferenceVariable && captureScope != null && (methodScope = captureScope.methodScope()) != null && (referenceContext = methodScope.referenceContext) instanceof LambdaExpression && ((LambdaExpression)referenceContext).inferenceContext != null) {
                    return true;
                }
            }
            case 4: 
            case 260: 
            case 1028: 
            case 2052: 
            case 32772: {
                switch (this.kind()) {
                    case 260: 
                    case 1028: 
                    case 2052: {
                        if (!TypeBinding.equalsEquals(this.erasure(), otherType.erasure())) break;
                        return false;
                    }
                }
                ReferenceBinding otherReferenceType = (ReferenceBinding)otherType;
                if (otherReferenceType.isIntersectionType18()) {
                    ReferenceBinding[] intersectingTypes;
                    ReferenceBinding[] referenceBindingArray = intersectingTypes = ((IntersectionTypeBinding18)otherReferenceType).intersectingTypes;
                    int n = intersectingTypes.length;
                    int n2 = 0;
                    while (n2 < n) {
                        ReferenceBinding binding = referenceBindingArray[n2];
                        if (!this.isCompatibleWith(binding)) {
                            return false;
                        }
                        ++n2;
                    }
                    return true;
                }
                if (otherReferenceType.isInterface()) {
                    if (this.implementsInterface(otherReferenceType, true)) {
                        return true;
                    }
                    if (this instanceof TypeVariableBinding && captureScope != null) {
                        TypeVariableBinding typeVariable = (TypeVariableBinding)this;
                        if (typeVariable.firstBound instanceof ParameterizedTypeBinding) {
                            TypeBinding bound = typeVariable.firstBound.capture(captureScope, -1, -1);
                            return bound.isCompatibleWith(otherReferenceType);
                        }
                    }
                }
                if (this.isInterface()) {
                    return false;
                }
                return otherReferenceType.isSuperclassOf(this);
            }
        }
        return false;
    }

    public final boolean isNonSealed() {
        return (this.modifiers & 0x4000000) != 0;
    }

    public boolean isSealed() {
        return (this.modifiers & 0x10000000) != 0;
    }

    @Override
    public boolean isSubtypeOf(TypeBinding other, boolean simulatingBugJDK8026527) {
        if (this.isSubTypeOfRTL(other)) {
            return true;
        }
        TypeBinding candidate = this.findSuperTypeOriginatingFrom(other);
        if (candidate == null) {
            return false;
        }
        if (TypeBinding.equalsEquals(candidate, other)) {
            return true;
        }
        if (other.isRawType() && TypeBinding.equalsEquals(candidate.erasure(), other.erasure())) {
            return true;
        }
        TypeBinding[] sis = other.typeArguments();
        TypeBinding[] tis = candidate.typeArguments();
        if (tis == null || sis == null) {
            return false;
        }
        if (sis.length != tis.length) {
            return false;
        }
        int i = 0;
        while (i < sis.length) {
            if (!tis[i].isTypeArgumentContainedBy(sis[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    protected boolean isSubTypeOfRTL(TypeBinding other) {
        ReferenceBinding[] intersecting;
        if (TypeBinding.equalsEquals(this, other)) {
            return true;
        }
        if (other instanceof CaptureBinding) {
            TypeBinding lower = ((CaptureBinding)other).lowerBound;
            return lower != null && this.isSubtypeOf(lower, false);
        }
        if (other instanceof ReferenceBinding && (intersecting = ((ReferenceBinding)other).getIntersectingTypes()) != null) {
            int i = 0;
            while (i < intersecting.length) {
                if (!this.isSubtypeOf(intersecting[i], false)) {
                    return false;
                }
                ++i;
            }
            return true;
        }
        return false;
    }

    public final boolean isDefault() {
        return (this.modifiers & 7) == 0;
    }

    public final boolean isDeprecated() {
        return (this.modifiers & 0x100000) != 0;
    }

    @Override
    public boolean isEnum() {
        return (this.modifiers & 0x4000) != 0;
    }

    public final boolean isFinal() {
        return (this.modifiers & 0x10) != 0;
    }

    public boolean isHierarchyBeingConnected() {
        return (this.tagBits & 0x200L) == 0L && (this.tagBits & 0x100L) != 0L;
    }

    public boolean isHierarchyBeingActivelyConnected() {
        return (this.tagBits & 0x200L) == 0L && (this.tagBits & 0x100L) != 0L && (this.tagBits & 0x80000L) == 0L;
    }

    public boolean isHierarchyConnected() {
        return true;
    }

    @Override
    public boolean isInterface() {
        return (this.modifiers & 0x200) != 0;
    }

    @Override
    public boolean isFunctionalInterface(Scope scope) {
        MethodBinding method;
        return this.isInterface() && (method = this.getSingleAbstractMethod(scope, true)) != null && method.isValidBinding();
    }

    public final boolean isPrivate() {
        return (this.modifiers & 2) != 0;
    }

    public final boolean isOrEnclosedByPrivateType() {
        if (this.isLocalType()) {
            return true;
        }
        ReferenceBinding type = this;
        while (type != null) {
            if ((type.modifiers & 2) != 0) {
                return true;
            }
            type = type.enclosingType();
        }
        return false;
    }

    public final boolean isProtected() {
        return (this.modifiers & 4) != 0;
    }

    public final boolean isPublic() {
        return (this.modifiers & 1) != 0;
    }

    @Override
    public final boolean isStatic() {
        return (this.modifiers & 0x208) != 0 || (this.tagBits & 4L) == 0L;
    }

    public final boolean isStrictfp() {
        return (this.modifiers & 0x800) != 0;
    }

    public boolean isSuperclassOf(ReferenceBinding otherType) {
        while ((otherType = otherType.superclass()) != null) {
            if (!otherType.isEquivalentTo(this)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isThrowable() {
        ReferenceBinding current = this;
        do {
            switch (current.id) {
                case 19: 
                case 21: 
                case 24: 
                case 25: {
                    return true;
                }
            }
        } while ((current = current.superclass()) != null);
        return false;
    }

    @Override
    public boolean isUncheckedException(boolean includeSupertype) {
        switch (this.id) {
            case 19: 
            case 24: {
                return true;
            }
            case 21: 
            case 25: {
                return includeSupertype;
            }
        }
        ReferenceBinding current = this;
        while ((current = current.superclass()) != null) {
            switch (current.id) {
                case 19: 
                case 24: {
                    return true;
                }
                case 21: 
                case 25: {
                    return false;
                }
            }
        }
        return false;
    }

    public final boolean isUsed() {
        return (this.modifiers & 0x8000000) != 0;
    }

    public final boolean isViewedAsDeprecated() {
        if ((this.modifiers & 0x300000) != 0) {
            return true;
        }
        if (this.getPackage().isViewedAsDeprecated()) {
            this.tagBits |= this.getPackage().tagBits & 0x4000000000000000L;
            return true;
        }
        return false;
    }

    public ReferenceBinding[] memberTypes() {
        return Binding.NO_MEMBER_TYPES;
    }

    public MethodBinding[] methods() {
        return Binding.NO_METHODS;
    }

    public final ReferenceBinding outermostEnclosingType() {
        ReferenceBinding last;
        ReferenceBinding current = this;
        do {
            last = current;
        } while ((current = current.enclosingType()) != null);
        return last;
    }

    @Override
    public char[] qualifiedSourceName() {
        if (this.isMemberType()) {
            return CharOperation.concat(this.enclosingType().qualifiedSourceName(), this.sourceName(), '.');
        }
        return this.sourceName();
    }

    @Override
    public char[] readableName() {
        return this.readableName(true);
    }

    public char[] readableName(boolean showGenerics) {
        TypeVariableBinding[] typeVars;
        char[] readableName = this.isMemberType() ? CharOperation.concat(this.enclosingType().readableName(showGenerics && this.hasEnclosingInstanceContext()), this.sourceName, '.') : CharOperation.concatWith(this.compoundName, '.');
        if (showGenerics && (typeVars = this.typeVariables()) != Binding.NO_TYPE_VARIABLES) {
            StringBuffer nameBuffer = new StringBuffer(10);
            nameBuffer.append(readableName).append('<');
            int i = 0;
            int length = typeVars.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(typeVars[i].readableName());
                ++i;
            }
            nameBuffer.append('>');
            int nameLength = nameBuffer.length();
            readableName = new char[nameLength];
            nameBuffer.getChars(0, nameLength, readableName, 0);
        }
        return readableName;
    }

    protected void appendNullAnnotation(StringBuffer nameBuffer, CompilerOptions options) {
        if (options.isAnnotationBasedNullAnalysisEnabled) {
            if (options.usesNullTypeAnnotations()) {
                AnnotationBinding[] annotationBindingArray = this.typeAnnotations;
                int n = this.typeAnnotations.length;
                int n2 = 0;
                while (n2 < n) {
                    AnnotationBinding annotation = annotationBindingArray[n2];
                    ReferenceBinding annotationType = annotation.getAnnotationType();
                    if (annotationType.hasNullBit(96)) {
                        nameBuffer.append('@').append(annotationType.shortReadableName()).append(' ');
                    }
                    ++n2;
                }
            } else {
                if ((this.tagBits & 0x100000000000000L) != 0L) {
                    char[][] nonNullAnnotationName = options.nonNullAnnotationName;
                    nameBuffer.append('@').append(nonNullAnnotationName[nonNullAnnotationName.length - 1]).append(' ');
                }
                if ((this.tagBits & 0x80000000000000L) != 0L) {
                    char[][] nullableAnnotationName = options.nullableAnnotationName;
                    nameBuffer.append('@').append(nullableAnnotationName[nullableAnnotationName.length - 1]).append(' ');
                }
            }
        }
    }

    public AnnotationHolder retrieveAnnotationHolder(Binding binding, boolean forceInitialization) {
        SimpleLookupTable store = this.storedAnnotations(forceInitialization, false);
        return store == null ? null : (AnnotationHolder)store.get(binding);
    }

    AnnotationBinding[] retrieveAnnotations(Binding binding) {
        AnnotationHolder holder = this.retrieveAnnotationHolder(binding, true);
        return holder == null ? Binding.NO_ANNOTATIONS : holder.getAnnotations();
    }

    @Override
    public void setAnnotations(AnnotationBinding[] annotations, boolean forceStore) {
        this.storeAnnotations(this, annotations, forceStore);
    }

    public void setContainerAnnotationType(ReferenceBinding value) {
    }

    public void tagAsHavingDefectiveContainerType() {
    }

    @Override
    public char[] nullAnnotatedReadableName(CompilerOptions options, boolean shortNames) {
        if (shortNames) {
            return this.nullAnnotatedShortReadableName(options);
        }
        return this.nullAnnotatedReadableName(options);
    }

    char[] nullAnnotatedReadableName(CompilerOptions options) {
        StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(this.enclosingType().nullAnnotatedReadableName(options, false));
            nameBuffer.append('.');
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.sourceName);
        } else if (this.compoundName != null) {
            int l = this.compoundName.length;
            int i = 0;
            while (i < l - 1) {
                nameBuffer.append(this.compoundName[i]);
                nameBuffer.append('.');
                ++i;
            }
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.compoundName[i]);
        } else {
            this.appendNullAnnotation(nameBuffer, options);
            if (this.sourceName != null) {
                nameBuffer.append(this.sourceName);
            } else {
                nameBuffer.append(this.readableName());
            }
        }
        TypeBinding[] arguments = this.typeArguments();
        if (arguments != null && arguments.length > 0) {
            nameBuffer.append('<');
            int i = 0;
            int length = arguments.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(arguments[i].nullAnnotatedReadableName(options, false));
                ++i;
            }
            nameBuffer.append('>');
        }
        int nameLength = nameBuffer.length();
        char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }

    char[] nullAnnotatedShortReadableName(CompilerOptions options) {
        StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(this.enclosingType().nullAnnotatedReadableName(options, true));
            nameBuffer.append('.');
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.sourceName);
        } else {
            this.appendNullAnnotation(nameBuffer, options);
            if (this.sourceName != null) {
                nameBuffer.append(this.sourceName);
            } else {
                nameBuffer.append(this.shortReadableName());
            }
        }
        TypeBinding[] arguments = this.typeArguments();
        if (arguments != null && arguments.length > 0) {
            nameBuffer.append('<');
            int i = 0;
            int length = arguments.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(arguments[i].nullAnnotatedReadableName(options, true));
                ++i;
            }
            nameBuffer.append('>');
        }
        int nameLength = nameBuffer.length();
        char[] shortReadableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        return shortReadableName;
    }

    @Override
    public char[] shortReadableName() {
        return this.shortReadableName(true);
    }

    public char[] shortReadableName(boolean showGenerics) {
        TypeVariableBinding[] typeVars;
        char[] shortReadableName = this.isMemberType() ? CharOperation.concat(this.enclosingType().shortReadableName(showGenerics && this.hasEnclosingInstanceContext()), this.sourceName, '.') : this.sourceName;
        if (showGenerics && (typeVars = this.typeVariables()) != Binding.NO_TYPE_VARIABLES) {
            StringBuffer nameBuffer = new StringBuffer(10);
            nameBuffer.append(shortReadableName).append('<');
            int i = 0;
            int length = typeVars.length;
            while (i < length) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(typeVars[i].shortReadableName());
                ++i;
            }
            nameBuffer.append('>');
            int nameLength = nameBuffer.length();
            shortReadableName = new char[nameLength];
            nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        }
        return shortReadableName;
    }

    @Override
    public char[] signature() {
        if (this.signature != null) {
            return this.signature;
        }
        this.signature = CharOperation.concat('L', this.constantPoolName(), ';');
        return this.signature;
    }

    @Override
    public char[] sourceName() {
        return this.sourceName;
    }

    @Override
    public ReferenceBinding upwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        return this;
    }

    @Override
    public ReferenceBinding downwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        return this;
    }

    void storeAnnotationHolder(Binding binding, AnnotationHolder holder) {
        if (holder == null) {
            SimpleLookupTable store = this.storedAnnotations(false, false);
            if (store != null) {
                store.removeKey(binding);
            }
        } else {
            SimpleLookupTable store = this.storedAnnotations(true, false);
            if (store != null) {
                store.put(binding, holder);
            }
        }
    }

    void storeAnnotations(Binding binding, AnnotationBinding[] annotations, boolean forceStore) {
        AnnotationHolder holder = null;
        if (annotations == null || annotations.length == 0) {
            SimpleLookupTable store = this.storedAnnotations(false, forceStore);
            if (store != null) {
                holder = (AnnotationHolder)store.get(binding);
            }
            if (holder == null) {
                return;
            }
        } else {
            SimpleLookupTable store = this.storedAnnotations(true, forceStore);
            if (store == null) {
                return;
            }
            holder = (AnnotationHolder)store.get(binding);
            if (holder == null) {
                holder = new AnnotationHolder();
            }
        }
        this.storeAnnotationHolder(binding, holder.setAnnotations(annotations));
    }

    SimpleLookupTable storedAnnotations(boolean forceInitialize, boolean forceStore) {
        return null;
    }

    @Override
    public ReferenceBinding superclass() {
        return null;
    }

    @Override
    public ReferenceBinding[] permittedTypes() {
        return Binding.NO_PERMITTEDTYPES;
    }

    @Override
    public ReferenceBinding[] superInterfaces() {
        return Binding.NO_SUPERINTERFACES;
    }

    public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
        if (this.isStatic()) {
            return null;
        }
        ReferenceBinding enclosingType = this.enclosingType();
        if (enclosingType == null) {
            return null;
        }
        return new ReferenceBinding[]{enclosingType};
    }

    MethodBinding[] unResolvedMethods() {
        return this.methods();
    }

    public FieldBinding[] unResolvedFields() {
        return Binding.NO_FIELDS;
    }

    protected int applyCloseableClassWhitelists(CompilerOptions options) {
        ReferenceBinding mySuper;
        switch (this.compoundName.length) {
            case 3: {
                if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0]) || !CharOperation.equals(TypeConstants.IO, this.compoundName[1])) break;
                char[] simpleName = this.compoundName[2];
                int l = TypeConstants.JAVA_IO_WRAPPER_CLOSEABLES.length;
                int i = 0;
                while (i < l) {
                    if (CharOperation.equals(simpleName, TypeConstants.JAVA_IO_WRAPPER_CLOSEABLES[i])) {
                        return 4;
                    }
                    ++i;
                }
                l = TypeConstants.JAVA_IO_RESOURCE_FREE_CLOSEABLES.length;
                i = 0;
                while (i < l) {
                    if (CharOperation.equals(simpleName, TypeConstants.JAVA_IO_RESOURCE_FREE_CLOSEABLES[i])) {
                        return 8;
                    }
                    ++i;
                }
                break;
            }
            case 4: {
                if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0]) || !CharOperation.equals(TypeConstants.UTIL, this.compoundName[1]) || !CharOperation.equals(TypeConstants.ZIP, this.compoundName[2])) break;
                char[] simpleName = this.compoundName[3];
                int l = TypeConstants.JAVA_UTIL_ZIP_WRAPPER_CLOSEABLES.length;
                int i = 0;
                while (i < l) {
                    if (CharOperation.equals(simpleName, TypeConstants.JAVA_UTIL_ZIP_WRAPPER_CLOSEABLES[i])) {
                        return 4;
                    }
                    ++i;
                }
                break;
            }
        }
        int l = TypeConstants.OTHER_WRAPPER_CLOSEABLES.length;
        int i = 0;
        while (i < l) {
            if (CharOperation.equals(this.compoundName, TypeConstants.OTHER_WRAPPER_CLOSEABLES[i])) {
                return 4;
            }
            ++i;
        }
        if (options.analyseResourceLeaks && (mySuper = this.superclass()) != null && mySuper.id != 1) {
            if (this.hasMethodWithNumArgs(TypeConstants.CLOSE, 0)) {
                return 0;
            }
            return mySuper.applyCloseableClassWhitelists(options);
        }
        return 0;
    }

    protected boolean hasMethodWithNumArgs(char[] selector, int numArgs) {
        MethodBinding[] methodBindingArray = this.unResolvedMethods();
        int n = methodBindingArray.length;
        int n2 = 0;
        while (n2 < n) {
            MethodBinding methodBinding = methodBindingArray[n2];
            if (CharOperation.equals(methodBinding.selector, selector) && methodBinding.parameters.length == numArgs) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    protected int applyCloseableInterfaceWhitelists() {
        switch (this.compoundName.length) {
            case 4: {
                if (CharOperation.equals(this.compoundName[0], TypeConstants.JAVA_UTIL_STREAM[0])) {
                    int i = 1;
                    while (i < 3) {
                        if (!CharOperation.equals(this.compoundName[i], TypeConstants.JAVA_UTIL_STREAM[i])) {
                            return 0;
                        }
                        ++i;
                    }
                    char[][] cArray = TypeConstants.RESOURCE_FREE_CLOSEABLE_J_U_STREAMS;
                    int n = TypeConstants.RESOURCE_FREE_CLOSEABLE_J_U_STREAMS.length;
                    int n2 = 0;
                    while (n2 < n) {
                        char[] streamName = cArray[n2];
                        if (CharOperation.equals(this.compoundName[3], streamName)) {
                            return 8;
                        }
                        ++n2;
                    }
                } else {
                    int i = 0;
                    while (i < 3) {
                        if (!CharOperation.equals(this.compoundName[i], TypeConstants.ONE_UTIL_STREAMEX[i])) {
                            return 0;
                        }
                        ++i;
                    }
                    char[][] cArray = TypeConstants.RESOURCE_FREE_CLOSEABLE_STREAMEX;
                    int n = TypeConstants.RESOURCE_FREE_CLOSEABLE_STREAMEX.length;
                    int n3 = 0;
                    while (n3 < n) {
                        char[] streamName = cArray[n3];
                        if (CharOperation.equals(this.compoundName[3], streamName)) {
                            return 8;
                        }
                        ++n3;
                    }
                }
                break;
            }
        }
        return 0;
    }

    protected MethodBinding[] getInterfaceAbstractContracts(Scope scope, boolean replaceWildcards, boolean filterDefaultMethods) throws InvalidInputException {
        if (!this.isInterface() || !this.isValidBinding()) {
            throw new InvalidInputException("Not a functional interface");
        }
        MethodBinding[] methods = this.methods();
        MethodBinding[] contracts = new MethodBinding[]{};
        int contractsCount = 0;
        int contractsLength = 0;
        ReferenceBinding[] superInterfaces = this.superInterfaces();
        int i = 0;
        int length = superInterfaces.length;
        while (i < length) {
            int superInterfaceContractsLength;
            MethodBinding[] superInterfaceContracts = superInterfaces[i].getInterfaceAbstractContracts(scope, replaceWildcards, false);
            int n = superInterfaceContractsLength = superInterfaceContracts == null ? 0 : superInterfaceContracts.length;
            if (superInterfaceContractsLength != 0) {
                if (contractsLength < contractsCount + superInterfaceContractsLength) {
                    MethodBinding[] methodBindingArray = contracts;
                    contractsLength = contractsCount + superInterfaceContractsLength;
                    contracts = new MethodBinding[contractsLength];
                    System.arraycopy(methodBindingArray, 0, contracts, 0, contractsCount);
                }
                System.arraycopy(superInterfaceContracts, 0, contracts, contractsCount, superInterfaceContractsLength);
                contractsCount += superInterfaceContractsLength;
            }
            ++i;
        }
        LookupEnvironment environment = scope.environment();
        int i2 = 0;
        int length2 = methods == null ? 0 : methods.length;
        while (i2 < length2) {
            MethodBinding method = methods[i2];
            if (!(method == null || method.isStatic() || method.redeclaresPublicObjectMethod(scope) || method.isPrivate())) {
                if (!method.isValidBinding()) {
                    throw new InvalidInputException("Not a functional interface");
                }
                int j = 0;
                while (j < contractsCount) {
                    if (contracts[j] != null && MethodVerifier.doesMethodOverride(method, contracts[j], environment) && j < --contractsCount) {
                        System.arraycopy(contracts, j + 1, contracts, j, contractsCount - j);
                        continue;
                    }
                    ++j;
                }
                if (!filterDefaultMethods || !method.isDefaultMethod()) {
                    if (contractsCount == contractsLength) {
                        MethodBinding[] methodBindingArray = contracts;
                        contracts = new MethodBinding[contractsLength += 16];
                        System.arraycopy(methodBindingArray, 0, contracts, 0, contractsCount);
                    }
                    if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                        ImplicitNullAnnotationVerifier.ensureNullnessIsKnown(method, scope);
                    }
                    contracts[contractsCount++] = method;
                }
            }
            ++i2;
        }
        i2 = 0;
        while (i2 < contractsCount) {
            MethodBinding contractI = contracts[i2];
            if (!TypeBinding.equalsEquals(contractI.declaringClass, this)) {
                int j = 0;
                while (j < contractsCount) {
                    MethodBinding contractJ = contracts[j];
                    if (i2 != j && !TypeBinding.equalsEquals(contractJ.declaringClass, this) && (contractI == contractJ || MethodVerifier.doesMethodOverride(contractI, contractJ, environment))) {
                        if (j < --contractsCount) {
                            System.arraycopy(contracts, j + 1, contracts, j, contractsCount - j);
                        }
                        if (--j < i2) {
                            --i2;
                        }
                    }
                    ++j;
                }
                if (filterDefaultMethods && contractI.isDefaultMethod()) {
                    if (i2 < --contractsCount) {
                        System.arraycopy(contracts, i2 + 1, contracts, i2, contractsCount - i2);
                    }
                    --i2;
                }
            }
            ++i2;
        }
        if (contractsCount < contractsLength) {
            MethodBinding[] methodBindingArray = contracts;
            contracts = new MethodBinding[contractsCount];
            System.arraycopy(methodBindingArray, 0, contracts, 0, contractsCount);
        }
        return contracts;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public MethodBinding getSingleAbstractMethod(Scope scope, boolean replaceWildcards) {
        v0 = index = replaceWildcards != false ? 0 : 1;
        if (this.singleAbstractMethod != null) {
            if (this.singleAbstractMethod[index] != null) {
                return this.singleAbstractMethod[index];
            }
        } else {
            this.singleAbstractMethod = new MethodBinding[2];
            options = scope.compilerOptions();
            if (options.complianceLevel == 0x3C0000L && options.enablePreviewFeatures && this.isSealed()) {
                this.singleAbstractMethod[index] = ReferenceBinding.samProblemBinding;
                return this.singleAbstractMethod[index];
            }
        }
        if (this.compoundName != null) {
            scope.compilationUnitScope().recordQualifiedReference(this.compoundName);
        }
        methods = null;
        try {
            methods = this.getInterfaceAbstractContracts(scope, replaceWildcards, true);
            if (methods == null || methods.length == 0) {
                this.singleAbstractMethod[index] = ReferenceBinding.samProblemBinding;
                return this.singleAbstractMethod[index];
            }
            contractParameterLength = 0;
            contractSelector = null;
            i = 0;
            length = methods.length;
            while (i < length) {
                method = methods[i];
                if (method != null) {
                    if (contractSelector == null) {
                        contractSelector = method.selector;
                        contractParameterLength = method.parameters == null ? 0 : method.parameters.length;
                    } else {
                        v1 = methodParameterLength = method.parameters == null ? 0 : method.parameters.length;
                        if (methodParameterLength != contractParameterLength || !CharOperation.equals(method.selector, contractSelector)) {
                            this.singleAbstractMethod[index] = ReferenceBinding.samProblemBinding;
                            return this.singleAbstractMethod[index];
                        }
                    }
                }
                ++i;
            }
        }
        catch (InvalidInputException v2) {
            this.singleAbstractMethod[index] = ReferenceBinding.samProblemBinding;
            return this.singleAbstractMethod[index];
        }
        if (methods.length == 1) {
            this.singleAbstractMethod[index] = methods[0];
            return this.singleAbstractMethod[index];
        }
        environment = scope.environment();
        genericMethodSeen = false;
        length = methods.length;
        analyseNullAnnotations = environment.globalOptions.isAnnotationBasedNullAnalysisEnabled;
        i = length - 1;
        while (i >= 0) {
            block37: {
                method = methods[i];
                otherMethod = null;
                if (method.typeVariables != Binding.NO_TYPE_VARIABLES) {
                    genericMethodSeen = true;
                }
                returnType = method.returnType;
                parameters = method.parameters;
                j = 0;
                while (j < length) {
                    if (i != j) {
                        otherMethod = methods[j];
                        if (otherMethod.typeVariables != Binding.NO_TYPE_VARIABLES) {
                            genericMethodSeen = true;
                        }
                        if (genericMethodSeen && (otherMethod = MethodVerifier.computeSubstituteMethod(otherMethod, method, environment)) == null || !MethodVerifier.isSubstituteParameterSubsignature(method, otherMethod, environment) || !MethodVerifier.areReturnTypesCompatible(method, otherMethod, environment)) break block37;
                        if (analyseNullAnnotations) {
                            returnType = NullAnnotationMatching.strongerType(returnType, otherMethod.returnType, environment);
                            parameters = NullAnnotationMatching.weakerTypes(parameters, otherMethod.parameters, environment);
                        }
                    }
                    ++j;
                }
                exceptions = new ReferenceBinding[]{};
                exceptionsCount = 0;
                exceptionsLength = 0;
                theAbstractMethod = method;
                shouldEraseThrows = theAbstractMethod.typeVariables == Binding.NO_TYPE_VARIABLES && genericMethodSeen != false;
                shouldAdaptThrows = theAbstractMethod.typeVariables != Binding.NO_TYPE_VARIABLES;
                typeVariableLength = theAbstractMethod.typeVariables.length;
                i = 0;
                block5: while (i < length) {
                    method = methods[i];
                    methodThrownExceptions = method.thrownExceptions;
                    v3 = methodExceptionsLength = methodThrownExceptions == null ? 0 : methodThrownExceptions.length;
                    if (methodExceptionsLength == 0) break;
                    if (shouldAdaptThrows && method != theAbstractMethod) {
                        v4 = methodThrownExceptions;
                        methodThrownExceptions = new ReferenceBinding[methodExceptionsLength];
                        System.arraycopy(v4, 0, methodThrownExceptions, 0, methodExceptionsLength);
                        tv = 0;
                        while (tv < typeVariableLength) {
                            if (methodThrownExceptions[tv] instanceof TypeVariableBinding) {
                                methodThrownExceptions[tv] = theAbstractMethod.typeVariables[tv];
                            }
                            ++tv;
                        }
                    }
                    j = 0;
                    while (j < methodExceptionsLength) {
                        block38: {
                            methodException = methodThrownExceptions[j];
                            if (shouldEraseThrows) {
                                methodException = (ReferenceBinding)methodException.erasure();
                            }
                            k = 0;
                            block8: while (k < length) {
                                if (i == k) ** GOTO lbl120
                                otherMethod = methods[k];
                                otherMethodThrownExceptions = otherMethod.thrownExceptions;
                                v5 = otherMethodExceptionsLength = otherMethodThrownExceptions == null ? 0 : otherMethodThrownExceptions.length;
                                if (otherMethodExceptionsLength == 0) break block5;
                                if (shouldAdaptThrows && otherMethod != theAbstractMethod) {
                                    v6 = otherMethodThrownExceptions;
                                    otherMethodThrownExceptions = new ReferenceBinding[otherMethodExceptionsLength];
                                    System.arraycopy(v6, 0, otherMethodThrownExceptions, 0, otherMethodExceptionsLength);
                                    tv = 0;
                                    while (tv < typeVariableLength) {
                                        if (otherMethodThrownExceptions[tv] instanceof TypeVariableBinding) {
                                            otherMethodThrownExceptions[tv] = theAbstractMethod.typeVariables[tv];
                                        }
                                        ++tv;
                                    }
                                }
                                l = 0;
                                while (l < otherMethodExceptionsLength) {
                                    otherException = otherMethodThrownExceptions[l];
                                    if (shouldEraseThrows) {
                                        otherException = (ReferenceBinding)otherException.erasure();
                                    }
                                    if (!methodException.isCompatibleWith(otherException)) {
                                        ++l;
                                        continue;
                                    }
lbl120:
                                    // 3 sources

                                    ++k;
                                    continue block8;
                                }
                                break block38;
                            }
                            if (exceptionsCount == exceptionsLength) {
                                v7 = exceptions;
                                exceptions = new ReferenceBinding[exceptionsLength += 16];
                                System.arraycopy(v7, 0, exceptions, 0, exceptionsCount);
                            }
                            exceptions[exceptionsCount++] = methodException;
                        }
                        ++j;
                    }
                    ++i;
                }
                if (exceptionsCount != exceptionsLength) {
                    v8 = exceptions;
                    exceptions = new ReferenceBinding[exceptionsCount];
                    System.arraycopy(v8, 0, exceptions, 0, exceptionsCount);
                }
                this.singleAbstractMethod[index] = new MethodBinding(theAbstractMethod.modifiers | 4096, theAbstractMethod.selector, returnType, parameters, exceptions, theAbstractMethod.declaringClass);
                this.singleAbstractMethod[index].typeVariables = theAbstractMethod.typeVariables;
                return this.singleAbstractMethod[index];
            }
            --i;
        }
        this.singleAbstractMethod[index] = ReferenceBinding.samProblemBinding;
        return this.singleAbstractMethod[index];
    }

    public static boolean isConsistentIntersection(TypeBinding[] intersectingTypes) {
        TypeBinding[] ci = new TypeBinding[intersectingTypes.length];
        int i = 0;
        while (i < ci.length) {
            TypeBinding current = intersectingTypes[i];
            ci[i] = current.isClass() || current.isArrayType() ? current : current.superclass();
            ++i;
        }
        TypeBinding mostSpecific = ci[0];
        int i2 = 1;
        while (i2 < ci.length) {
            TypeBinding current = ci[i2];
            if (!current.isTypeVariable() && !current.isWildcard() && current.isProperType(true) && !mostSpecific.isSubtypeOf(current, false)) {
                if (current.isSubtypeOf(mostSpecific, false)) {
                    mostSpecific = current;
                } else {
                    return false;
                }
            }
            ++i2;
        }
        return true;
    }

    public ModuleBinding module() {
        if (this.fPackage != null) {
            return this.fPackage.enclosingModule;
        }
        return null;
    }

    public boolean hasEnclosingInstanceContext() {
        if (this.isMemberType() && !this.isStatic()) {
            return true;
        }
        if (this.isLocalType() && this.isStatic()) {
            return false;
        }
        MethodBinding enclosingMethod = this.enclosingMethod();
        if (enclosingMethod != null) {
            return !enclosingMethod.isStatic();
        }
        return false;
    }
}

