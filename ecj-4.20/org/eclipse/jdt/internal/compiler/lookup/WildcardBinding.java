/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.List;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;

public class WildcardBinding
extends ReferenceBinding {
    public ReferenceBinding genericType;
    public int rank;
    public TypeBinding bound;
    public TypeBinding[] otherBounds;
    char[] genericSignature;
    public int boundKind;
    ReferenceBinding superclass;
    ReferenceBinding[] superInterfaces;
    TypeVariableBinding typeVariable;
    LookupEnvironment environment;
    boolean inRecursiveFunction = false;

    public WildcardBinding(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind, LookupEnvironment environment) {
        this.rank = rank;
        this.boundKind = boundKind;
        this.modifiers = 0x40000001;
        this.environment = environment;
        this.initialize(genericType, bound, otherBounds);
        if (genericType instanceof UnresolvedReferenceBinding) {
            ((UnresolvedReferenceBinding)genericType).addWrapper(this, environment);
        }
        if (bound instanceof UnresolvedReferenceBinding) {
            ((UnresolvedReferenceBinding)bound).addWrapper(this, environment);
        }
        this.tagBits |= 0x1000000L;
        this.typeBits = 0x8000000;
    }

    @Override
    TypeBinding bound() {
        return this.bound;
    }

    @Override
    int boundKind() {
        return this.boundKind;
    }

    public TypeBinding allBounds() {
        if (this.otherBounds == null || this.otherBounds.length == 0) {
            return this.bound;
        }
        ReferenceBinding[] allBounds = new ReferenceBinding[this.otherBounds.length + 1];
        try {
            allBounds[0] = (ReferenceBinding)this.bound;
            System.arraycopy(this.otherBounds, 0, allBounds, 1, this.otherBounds.length);
        }
        catch (ArrayStoreException | ClassCastException runtimeException) {
            return this.bound;
        }
        return this.environment.createIntersectionType18(allBounds);
    }

    @Override
    public void setTypeAnnotations(AnnotationBinding[] annotations, boolean evalNullAnnotations) {
        this.tagBits |= 0x200000L;
        if (annotations != null && annotations.length != 0) {
            this.typeAnnotations = annotations;
        }
        if (evalNullAnnotations) {
            this.evaluateNullAnnotations(null, null);
        }
    }

    public void evaluateNullAnnotations(Scope scope, Wildcard wildcard) {
        long typeVariableNullTagBits;
        TypeVariableBinding typeVariable2;
        long nullTagBits = this.determineNullBitsFromDeclaration(scope, wildcard);
        if (nullTagBits == 0L && (typeVariable2 = this.typeVariable()) != null && (typeVariableNullTagBits = typeVariable2.tagBits & 0x180000000000000L) != 0L) {
            nullTagBits = typeVariableNullTagBits;
        }
        if (nullTagBits != 0L) {
            this.tagBits = this.tagBits & 0xFE7FFFFFFFFFFFFFL | nullTagBits | 0x100000L;
        }
    }

    public long determineNullBitsFromDeclaration(Scope scope, Wildcard wildcard) {
        long nullTagBits;
        block23: {
            TypeBinding newBound;
            long boundNullTagBits;
            Object annotation;
            block24: {
                block25: {
                    nullTagBits = 0L;
                    AnnotationBinding[] annotations = this.typeAnnotations;
                    if (annotations != null) {
                        int i = 0;
                        int length = annotations.length;
                        while (i < length) {
                            annotation = annotations[i];
                            if (annotation != null) {
                                Annotation annotation1;
                                if (((AnnotationBinding)annotation).type.hasNullBit(64)) {
                                    if ((nullTagBits & 0x100000000000000L) == 0L) {
                                        nullTagBits |= 0x80000000000000L;
                                    } else if (wildcard != null && (annotation1 = wildcard.findAnnotation(0x80000000000000L)) != null) {
                                        scope.problemReporter().contradictoryNullAnnotations(annotation1);
                                    }
                                } else if (((AnnotationBinding)annotation).type.hasNullBit(32)) {
                                    if ((nullTagBits & 0x80000000000000L) == 0L) {
                                        nullTagBits |= 0x100000000000000L;
                                    } else if (wildcard != null && (annotation1 = wildcard.findAnnotation(0x100000000000000L)) != null) {
                                        scope.problemReporter().contradictoryNullAnnotations(annotation1);
                                    }
                                }
                            }
                            ++i;
                        }
                    }
                    if (this.bound == null || !this.bound.isValidBinding() || (boundNullTagBits = this.bound.tagBits & 0x180000000000000L) == 0L) break block23;
                    if (this.boundKind != 2) break block24;
                    if ((boundNullTagBits & 0x80000000000000L) == 0L) break block23;
                    if (nullTagBits != 0L) break block25;
                    nullTagBits = 0x80000000000000L;
                    break block23;
                }
                if (wildcard == null || (nullTagBits & 0x100000000000000L) == 0L) break block23;
                annotation = wildcard.bound.findAnnotation(boundNullTagBits);
                if (annotation == null) {
                    this.bound = newBound = this.bound.withoutToplevelNullAnnotation();
                    wildcard.bound.resolvedType = newBound;
                } else {
                    scope.problemReporter().contradictoryNullAnnotationsOnBounds((Annotation)annotation, nullTagBits);
                }
                break block23;
            }
            if ((boundNullTagBits & 0x100000000000000L) != 0L) {
                if (nullTagBits == 0L) {
                    nullTagBits = 0x100000000000000L;
                } else if (wildcard != null && (nullTagBits & 0x80000000000000L) != 0L) {
                    annotation = wildcard.bound.findAnnotation(boundNullTagBits);
                    if (annotation == null) {
                        this.bound = newBound = this.bound.withoutToplevelNullAnnotation();
                        wildcard.bound.resolvedType = newBound;
                    } else {
                        scope.problemReporter().contradictoryNullAnnotationsOnBounds((Annotation)annotation, nullTagBits);
                    }
                }
            }
            if (nullTagBits == 0L && this.otherBounds != null) {
                int i = 0;
                int length = this.otherBounds.length;
                while (i < length) {
                    if ((this.otherBounds[i].tagBits & 0x100000000000000L) != 0L) {
                        nullTagBits = 0x100000000000000L;
                        break;
                    }
                    ++i;
                }
            }
        }
        return nullTagBits;
    }

    @Override
    public ReferenceBinding actualType() {
        return this.genericType;
    }

    @Override
    TypeBinding[] additionalBounds() {
        return this.otherBounds;
    }

    @Override
    public int kind() {
        return this.otherBounds == null ? 516 : 8196;
    }

    public boolean boundCheck(TypeBinding argumentType) {
        switch (this.boundKind) {
            case 0: {
                return true;
            }
            case 1: {
                if (!argumentType.isCompatibleWith(this.bound)) {
                    return false;
                }
                int i = 0;
                int length = this.otherBounds == null ? 0 : this.otherBounds.length;
                while (i < length) {
                    if (!argumentType.isCompatibleWith(this.otherBounds[i])) {
                        return false;
                    }
                    ++i;
                }
                return true;
            }
        }
        return argumentType.isCompatibleWith(this.bound);
    }

    @Override
    public boolean canBeInstantiated() {
        return false;
    }

    @Override
    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if ((this.tagBits & 0x80L) != 0L) {
            missingTypes = this.bound.collectMissingTypes(missingTypes);
        }
        return missingTypes;
    }

    @Override
    public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint) {
        block82: {
            if ((this.tagBits & 0x20000000L) == 0L) {
                return;
            }
            if (actualType == TypeBinding.NULL || actualType.kind() == 65540) {
                return;
            }
            if (actualType.isCapture()) {
                CaptureBinding capture = (CaptureBinding)actualType;
                actualType = capture.wildcard;
            }
            block0 : switch (constraint) {
                case 1: {
                    WildcardBinding actualWildcard;
                    block5 : switch (this.boundKind) {
                        case 0: {
                            break;
                        }
                        case 1: {
                            block10 : switch (actualType.kind()) {
                                case 516: {
                                    actualWildcard = (WildcardBinding)actualType;
                                    switch (actualWildcard.boundKind) {
                                        case 0: {
                                            break block10;
                                        }
                                        case 1: {
                                            this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 1);
                                            break block10;
                                        }
                                    }
                                    break;
                                }
                                case 8196: {
                                    WildcardBinding actualIntersection = (WildcardBinding)actualType;
                                    this.bound.collectSubstitutes(scope, actualIntersection.bound, inferenceContext, 1);
                                    int i = 0;
                                    int length = actualIntersection.otherBounds.length;
                                    while (i < length) {
                                        this.bound.collectSubstitutes(scope, actualIntersection.otherBounds[i], inferenceContext, 1);
                                        ++i;
                                    }
                                    break block0;
                                }
                                default: {
                                    this.bound.collectSubstitutes(scope, actualType, inferenceContext, 1);
                                    break;
                                }
                            }
                            break block0;
                        }
                        case 2: {
                            switch (actualType.kind()) {
                                case 516: {
                                    actualWildcard = (WildcardBinding)actualType;
                                    switch (actualWildcard.boundKind) {
                                        case 0: {
                                            break block5;
                                        }
                                        case 1: {
                                            break block5;
                                        }
                                        case 2: {
                                            this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 2);
                                            int i = 0;
                                            int length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length;
                                            while (i < length) {
                                                this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, 2);
                                                ++i;
                                            }
                                            break block0;
                                        }
                                    }
                                    break block5;
                                }
                                case 8196: {
                                    break block5;
                                }
                            }
                            this.bound.collectSubstitutes(scope, actualType, inferenceContext, 2);
                        }
                    }
                    break;
                }
                case 0: {
                    WildcardBinding actualWildcard;
                    block27 : switch (this.boundKind) {
                        case 0: {
                            break;
                        }
                        case 1: {
                            switch (actualType.kind()) {
                                case 516: {
                                    actualWildcard = (WildcardBinding)actualType;
                                    switch (actualWildcard.boundKind) {
                                        case 0: {
                                            break;
                                        }
                                        case 1: {
                                            this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 0);
                                            int i = 0;
                                            int length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length;
                                            while (i < length) {
                                                this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, 0);
                                                ++i;
                                            }
                                            break block0;
                                        }
                                    }
                                    break block0;
                                }
                                case 8196: {
                                    WildcardBinding actuaIntersection = (WildcardBinding)actualType;
                                    this.bound.collectSubstitutes(scope, actuaIntersection.bound, inferenceContext, 0);
                                    int i = 0;
                                    int length = actuaIntersection.otherBounds == null ? 0 : actuaIntersection.otherBounds.length;
                                    while (i < length) {
                                        this.bound.collectSubstitutes(scope, actuaIntersection.otherBounds[i], inferenceContext, 0);
                                        ++i;
                                    }
                                    break block0;
                                }
                            }
                            break block0;
                        }
                        case 2: {
                            switch (actualType.kind()) {
                                case 516: {
                                    actualWildcard = (WildcardBinding)actualType;
                                    switch (actualWildcard.boundKind) {
                                        case 0: {
                                            break block27;
                                        }
                                        case 1: {
                                            break block27;
                                        }
                                        case 2: {
                                            this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 0);
                                            int i = 0;
                                            int length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length;
                                            while (i < length) {
                                                this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, 0);
                                                ++i;
                                            }
                                            break block0;
                                        }
                                    }
                                    break block27;
                                }
                                case 8196: {
                                    break block27;
                                }
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    WildcardBinding actualWildcard;
                    switch (this.boundKind) {
                        case 0: {
                            break block0;
                        }
                        case 1: {
                            switch (actualType.kind()) {
                                case 516: {
                                    actualWildcard = (WildcardBinding)actualType;
                                    switch (actualWildcard.boundKind) {
                                        case 0: {
                                            break;
                                        }
                                        case 1: {
                                            this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 2);
                                            int i = 0;
                                            int length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length;
                                            while (i < length) {
                                                this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, 2);
                                                ++i;
                                            }
                                            break block82;
                                        }
                                    }
                                    break block82;
                                }
                                case 8196: {
                                    WildcardBinding actualIntersection = (WildcardBinding)actualType;
                                    this.bound.collectSubstitutes(scope, actualIntersection.bound, inferenceContext, 2);
                                    int i = 0;
                                    int length = actualIntersection.otherBounds == null ? 0 : actualIntersection.otherBounds.length;
                                    while (i < length) {
                                        this.bound.collectSubstitutes(scope, actualIntersection.otherBounds[i], inferenceContext, 2);
                                        ++i;
                                    }
                                    break block82;
                                }
                            }
                            break block82;
                        }
                        case 2: {
                            switch (actualType.kind()) {
                                case 516: {
                                    actualWildcard = (WildcardBinding)actualType;
                                    switch (actualWildcard.boundKind) {
                                        case 0: {
                                            break block0;
                                        }
                                        case 1: {
                                            break block0;
                                        }
                                        case 2: {
                                            this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 2);
                                            int i = 0;
                                            int length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length;
                                            while (i < length) {
                                                this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, 2);
                                                ++i;
                                            }
                                            break block0;
                                        }
                                    }
                                    break block0;
                                }
                                case 8196: {
                                    break block0;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        char[] wildCardKey;
        char[] genericTypeKey = this.genericType.computeUniqueKey(false);
        char[] rankComponent = (String.valueOf('{') + String.valueOf(this.rank) + '}').toCharArray();
        switch (this.boundKind) {
            case 0: {
                wildCardKey = TypeConstants.WILDCARD_STAR;
                break;
            }
            case 1: {
                wildCardKey = CharOperation.concat(TypeConstants.WILDCARD_PLUS, this.bound.computeUniqueKey(false));
                break;
            }
            default: {
                wildCardKey = CharOperation.concat(TypeConstants.WILDCARD_MINUS, this.bound.computeUniqueKey(false));
            }
        }
        return CharOperation.concat(genericTypeKey, rankComponent, wildCardKey);
    }

    @Override
    public char[] constantPoolName() {
        return this.erasure().constantPoolName();
    }

    @Override
    public TypeBinding clone(TypeBinding immaterial) {
        return new WildcardBinding(this.genericType, this.rank, this.bound, this.otherBounds, this.boundKind, this.environment);
    }

    @Override
    public String annotatedDebugName() {
        StringBuffer buffer = new StringBuffer(16);
        AnnotationBinding[] annotations = this.getTypeAnnotations();
        int i = 0;
        int length = annotations == null ? 0 : annotations.length;
        while (i < length) {
            buffer.append(annotations[i]);
            buffer.append(' ');
            ++i;
        }
        switch (this.boundKind) {
            case 0: {
                return buffer.append(TypeConstants.WILDCARD_NAME).toString();
            }
            case 1: {
                if (this.otherBounds == null) {
                    return buffer.append(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.annotatedDebugName().toCharArray())).toString();
                }
                buffer.append(this.bound.annotatedDebugName());
                i = 0;
                length = this.otherBounds.length;
                while (i < length) {
                    buffer.append(" & ").append(this.otherBounds[i].annotatedDebugName());
                    ++i;
                }
                return buffer.toString();
            }
        }
        return buffer.append(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.annotatedDebugName().toCharArray())).toString();
    }

    @Override
    public String debugName() {
        return this.toString();
    }

    @Override
    public TypeBinding erasure() {
        if (this.otherBounds == null) {
            if (this.boundKind == 1) {
                return this.bound.erasure();
            }
            TypeVariableBinding var = this.typeVariable();
            if (var != null) {
                return var.erasure();
            }
            return this.genericType;
        }
        return this.bound.id == 1 ? this.otherBounds[0].erasure() : this.bound.erasure();
    }

    @Override
    public char[] genericTypeSignature() {
        if (this.genericSignature == null) {
            switch (this.boundKind) {
                case 0: {
                    this.genericSignature = TypeConstants.WILDCARD_STAR;
                    break;
                }
                case 1: {
                    this.genericSignature = CharOperation.concat(TypeConstants.WILDCARD_PLUS, this.bound.genericTypeSignature());
                    break;
                }
                default: {
                    this.genericSignature = CharOperation.concat(TypeConstants.WILDCARD_MINUS, this.bound.genericTypeSignature());
                }
            }
        }
        return this.genericSignature;
    }

    @Override
    public int hashCode() {
        return this.genericType.hashCode();
    }

    @Override
    public boolean hasTypeBit(int bit) {
        if (this.typeBits == 0x8000000) {
            this.typeBits = 0;
            if (this.superclass != null && this.superclass.hasTypeBit(-134217729)) {
                this.typeBits |= this.superclass.typeBits & 0x713;
            }
            if (this.superInterfaces != null) {
                int i = 0;
                int l = this.superInterfaces.length;
                while (i < l) {
                    if (this.superInterfaces[i].hasTypeBit(-134217729)) {
                        this.typeBits |= this.superInterfaces[i].typeBits & 0x713;
                    }
                    ++i;
                }
            }
        }
        return (this.typeBits & bit) != 0;
    }

    void initialize(ReferenceBinding someGenericType, TypeBinding someBound, TypeBinding[] someOtherBounds) {
        this.genericType = someGenericType;
        this.bound = someBound;
        this.otherBounds = someOtherBounds;
        if (someGenericType != null) {
            this.fPackage = someGenericType.getPackage();
        }
        if (someBound != null) {
            this.tagBits |= someBound.tagBits & 0x2000000020100880L;
        }
        if (someOtherBounds != null) {
            int i = 0;
            int max = someOtherBounds.length;
            while (i < max) {
                TypeBinding someOtherBound = someOtherBounds[i];
                this.tagBits |= someOtherBound.tagBits & 0x2000000000100800L;
                ++i;
            }
        }
    }

    @Override
    public boolean isSuperclassOf(ReferenceBinding otherType) {
        if (this.boundKind == 2) {
            if (this.bound instanceof ReferenceBinding) {
                return ((ReferenceBinding)this.bound).isSuperclassOf(otherType);
            }
            return otherType.id == 1;
        }
        return false;
    }

    @Override
    public boolean isIntersectionType() {
        return this.otherBounds != null;
    }

    @Override
    public ReferenceBinding[] getIntersectingTypes() {
        if (this.isIntersectionType()) {
            ReferenceBinding[] allBounds = new ReferenceBinding[this.otherBounds.length + 1];
            try {
                allBounds[0] = (ReferenceBinding)this.bound;
                System.arraycopy(this.otherBounds, 0, allBounds, 1, this.otherBounds.length);
            }
            catch (ArrayStoreException | ClassCastException runtimeException) {
                return null;
            }
            return allBounds;
        }
        return null;
    }

    @Override
    public boolean isHierarchyConnected() {
        return this.superclass != null && this.superInterfaces != null;
    }

    @Override
    public boolean enterRecursiveFunction() {
        if (this.inRecursiveFunction) {
            return false;
        }
        this.inRecursiveFunction = true;
        return true;
    }

    @Override
    public void exitRecursiveFunction() {
        this.inRecursiveFunction = false;
    }

    @Override
    public boolean isProperType(boolean admitCapture18) {
        if (this.inRecursiveFunction) {
            return true;
        }
        this.inRecursiveFunction = true;
        try {
            if (this.bound != null && !this.bound.isProperType(admitCapture18)) {
                return false;
            }
            if (this.superclass != null && !this.superclass.isProperType(admitCapture18)) {
                return false;
            }
            if (this.superInterfaces != null) {
                int i = 0;
                int l = this.superInterfaces.length;
                while (i < l) {
                    if (!this.superInterfaces[i].isProperType(admitCapture18)) {
                        return false;
                    }
                    ++i;
                }
            }
            return true;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }

    @Override
    TypeBinding substituteInferenceVariable(InferenceVariable var, TypeBinding substituteType) {
        boolean haveSubstitution = false;
        TypeBinding currentBound = this.bound;
        if (currentBound != null) {
            currentBound = currentBound.substituteInferenceVariable(var, substituteType);
            haveSubstitution |= TypeBinding.notEquals(currentBound, this.bound);
        }
        TypeBinding[] currentOtherBounds = null;
        if (this.otherBounds != null) {
            int length = this.otherBounds.length;
            if (haveSubstitution) {
                currentOtherBounds = new ReferenceBinding[length];
                System.arraycopy(this.otherBounds, 0, currentOtherBounds, 0, length);
            }
            int i = 0;
            while (i < length) {
                TypeBinding currentOtherBound = this.otherBounds[i];
                if (currentOtherBound != null && TypeBinding.notEquals(currentOtherBound = currentOtherBound.substituteInferenceVariable(var, substituteType), this.otherBounds[i])) {
                    if (currentOtherBounds == null) {
                        currentOtherBounds = new ReferenceBinding[length];
                        System.arraycopy(this.otherBounds, 0, currentOtherBounds, 0, length);
                    }
                    currentOtherBounds[i] = currentOtherBound;
                }
                ++i;
            }
        }
        if (haveSubstitution |= currentOtherBounds != null) {
            return this.environment.createWildcard(this.genericType, this.rank, currentBound, currentOtherBounds, this.boundKind);
        }
        return this;
    }

    @Override
    public boolean isUnboundWildcard() {
        return this.boundKind == 0;
    }

    @Override
    public boolean isWildcard() {
        return true;
    }

    @Override
    int rank() {
        return this.rank;
    }

    @Override
    public char[] readableName() {
        switch (this.boundKind) {
            case 0: {
                return TypeConstants.WILDCARD_NAME;
            }
            case 1: {
                if (this.otherBounds == null) {
                    return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.readableName());
                }
                StringBuffer buffer = new StringBuffer(10);
                buffer.append(this.bound.readableName());
                int i = 0;
                int length = this.otherBounds.length;
                while (i < length) {
                    buffer.append('&').append(this.otherBounds[i].readableName());
                    ++i;
                }
                int length2 = buffer.length();
                char[] result = new char[length2];
                buffer.getChars(0, length2, result, 0);
                return result;
            }
        }
        return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.readableName());
    }

    @Override
    public char[] nullAnnotatedReadableName(CompilerOptions options, boolean shortNames) {
        StringBuffer buffer = new StringBuffer(10);
        this.appendNullAnnotation(buffer, options);
        switch (this.boundKind) {
            case 0: {
                buffer.append(TypeConstants.WILDCARD_NAME);
                break;
            }
            case 1: {
                if (this.otherBounds == null) {
                    buffer.append(TypeConstants.WILDCARD_NAME).append(TypeConstants.WILDCARD_EXTENDS);
                    buffer.append(this.bound.nullAnnotatedReadableName(options, shortNames));
                    break;
                }
                buffer.append(this.bound.nullAnnotatedReadableName(options, shortNames));
                int i = 0;
                int length = this.otherBounds.length;
                while (i < length) {
                    buffer.append('&').append(this.otherBounds[i].nullAnnotatedReadableName(options, shortNames));
                    ++i;
                }
                break;
            }
            default: {
                buffer.append(TypeConstants.WILDCARD_NAME).append(TypeConstants.WILDCARD_SUPER).append(this.bound.nullAnnotatedReadableName(options, shortNames));
            }
        }
        int length = buffer.length();
        char[] result = new char[length];
        buffer.getChars(0, length, result, 0);
        return result;
    }

    ReferenceBinding resolve() {
        if ((this.tagBits & 0x1000000L) == 0L) {
            return this;
        }
        this.tagBits &= 0xFFFFFFFFFEFFFFFFL;
        BinaryTypeBinding.resolveType(this.genericType, this.environment, false);
        switch (this.boundKind) {
            case 1: {
                TypeBinding resolveType;
                this.bound = resolveType = BinaryTypeBinding.resolveType(this.bound, this.environment, true);
                this.tagBits |= resolveType.tagBits & 0x800L | 0x2000000000000000L;
                int i = 0;
                int length = this.otherBounds == null ? 0 : this.otherBounds.length;
                while (i < length) {
                    this.otherBounds[i] = resolveType = BinaryTypeBinding.resolveType(this.otherBounds[i], this.environment, true);
                    this.tagBits |= resolveType.tagBits & 0x800L | 0x2000000000000000L;
                    ++i;
                }
                break;
            }
            case 2: {
                TypeBinding resolveType;
                this.bound = resolveType = BinaryTypeBinding.resolveType(this.bound, this.environment, true);
                this.tagBits |= resolveType.tagBits & 0x800L | 0x2000000000000000L;
            }
        }
        if (this.environment.usesNullTypeAnnotations()) {
            this.evaluateNullAnnotations(null, null);
        }
        return this;
    }

    @Override
    public char[] shortReadableName() {
        switch (this.boundKind) {
            case 0: {
                return TypeConstants.WILDCARD_NAME;
            }
            case 1: {
                if (this.otherBounds == null) {
                    return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.shortReadableName());
                }
                StringBuffer buffer = new StringBuffer(10);
                buffer.append(this.bound.shortReadableName());
                int i = 0;
                int length = this.otherBounds.length;
                while (i < length) {
                    buffer.append('&').append(this.otherBounds[i].shortReadableName());
                    ++i;
                }
                int length2 = buffer.length();
                char[] result = new char[length2];
                buffer.getChars(0, length2, result, 0);
                return result;
            }
        }
        return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.shortReadableName());
    }

    @Override
    public char[] signature() {
        if (this.signature == null) {
            switch (this.boundKind) {
                case 1: {
                    return this.bound.signature();
                }
            }
            return this.typeVariable().signature();
        }
        return this.signature;
    }

    @Override
    public char[] sourceName() {
        switch (this.boundKind) {
            case 0: {
                return TypeConstants.WILDCARD_NAME;
            }
            case 1: {
                return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.sourceName());
            }
        }
        return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.sourceName());
    }

    @Override
    public ReferenceBinding superclass() {
        if (this.superclass == null) {
            TypeBinding superType = null;
            if (this.boundKind == 1 && !this.bound.isInterface()) {
                superType = this.bound;
            } else {
                TypeVariableBinding variable = this.typeVariable();
                if (variable != null) {
                    superType = variable.firstBound;
                }
            }
            this.superclass = superType instanceof ReferenceBinding && !superType.isInterface() ? (ReferenceBinding)superType : this.environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_OBJECT, null);
        }
        return this.superclass;
    }

    @Override
    public ReferenceBinding[] superInterfaces() {
        if (this.superInterfaces == null) {
            this.superInterfaces = this.typeVariable() != null ? this.typeVariable.superInterfaces() : Binding.NO_SUPERINTERFACES;
            if (this.boundKind == 1) {
                int length;
                if (this.bound.isInterface()) {
                    length = this.superInterfaces.length;
                    this.superInterfaces = new ReferenceBinding[length + 1];
                    System.arraycopy(this.superInterfaces, 0, this.superInterfaces, 1, length);
                    this.superInterfaces[0] = (ReferenceBinding)this.bound;
                }
                if (this.otherBounds != null) {
                    length = this.superInterfaces.length;
                    int otherLength = this.otherBounds.length;
                    this.superInterfaces = new ReferenceBinding[length + otherLength];
                    System.arraycopy(this.superInterfaces, 0, this.superInterfaces, 0, length);
                    int i = 0;
                    while (i < otherLength) {
                        this.superInterfaces[length + i] = (ReferenceBinding)this.otherBounds[i];
                        ++i;
                    }
                }
            }
        }
        return this.superInterfaces;
    }

    @Override
    public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType, LookupEnvironment env) {
        boolean affected = false;
        if (this.genericType == unresolvedType) {
            this.genericType = resolvedType;
            affected = true;
        }
        if (this.bound == unresolvedType) {
            this.bound = env.convertUnresolvedBinaryToRawType(resolvedType);
            affected = true;
        }
        if (this.otherBounds != null) {
            int i = 0;
            int length = this.otherBounds.length;
            while (i < length) {
                if (this.otherBounds[i] == unresolvedType) {
                    this.otherBounds[i] = env.convertUnresolvedBinaryToRawType(resolvedType);
                    affected = true;
                }
                ++i;
            }
        }
        if (affected) {
            this.initialize(this.genericType, this.bound, this.otherBounds);
        }
    }

    public String toString() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        switch (this.boundKind) {
            case 0: {
                return new String(TypeConstants.WILDCARD_NAME);
            }
            case 1: {
                if (this.otherBounds == null) {
                    return new String(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.debugName().toCharArray()));
                }
                StringBuffer buffer = new StringBuffer(this.bound.debugName());
                int i = 0;
                int length = this.otherBounds.length;
                while (i < length) {
                    buffer.append('&').append(this.otherBounds[i].debugName());
                    ++i;
                }
                return buffer.toString();
            }
        }
        return new String(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.debugName().toCharArray()));
    }

    public TypeVariableBinding typeVariable() {
        TypeVariableBinding[] typeVariables;
        if (this.typeVariable == null && this.rank < (typeVariables = this.genericType.typeVariables()).length) {
            this.typeVariable = typeVariables[this.rank];
        }
        return this.typeVariable;
    }

    @Override
    public TypeBinding unannotated() {
        return this.hasTypeAnnotations() ? this.environment.getUnannotatedType(this) : this;
    }

    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.getTypeAnnotations());
        return this.environment.createWildcard(this.genericType, this.rank, this.bound, this.otherBounds, this.boundKind, newAnnotations);
    }

    @Override
    public TypeBinding uncapture(Scope scope) {
        TypeBinding[] typeBindingArray;
        if ((this.tagBits & 0x2000000000000000L) == 0L) {
            return this;
        }
        TypeBinding freeBound = this.bound != null ? this.bound.uncapture(scope) : null;
        int length = 0;
        if (this.otherBounds == null) {
            typeBindingArray = null;
        } else {
            length = this.otherBounds.length;
            typeBindingArray = new TypeBinding[length];
        }
        TypeBinding[] freeOtherBounds = typeBindingArray;
        int i = 0;
        while (i < length) {
            freeOtherBounds[i] = this.otherBounds[i] == null ? null : this.otherBounds[i].uncapture(scope);
            ++i;
        }
        return scope.environment().createWildcard(this.genericType, this.rank, freeBound, freeOtherBounds, this.boundKind, this.getTypeAnnotations());
    }

    @Override
    void collectInferenceVariables(Set<InferenceVariable> variables) {
        if (this.bound != null) {
            this.bound.collectInferenceVariables(variables);
        }
        if (this.otherBounds != null) {
            int i = 0;
            int length = this.otherBounds.length;
            while (i < length) {
                this.otherBounds[i].collectInferenceVariables(variables);
                ++i;
            }
        }
    }

    @Override
    public boolean mentionsAny(TypeBinding[] parameters, int idx) {
        if (this.inRecursiveFunction) {
            return false;
        }
        this.inRecursiveFunction = true;
        try {
            if (super.mentionsAny(parameters, idx)) {
                return true;
            }
            if (this.bound != null && this.bound.mentionsAny(parameters, -1)) {
                return true;
            }
            if (this.otherBounds != null) {
                int i = 0;
                int length = this.otherBounds.length;
                while (i < length) {
                    if (this.otherBounds[i].mentionsAny(parameters, -1)) {
                        return true;
                    }
                    ++i;
                }
            }
        }
        finally {
            this.inRecursiveFunction = false;
        }
        return false;
    }

    @Override
    public boolean acceptsNonNullDefault() {
        return false;
    }

    @Override
    public long updateTagBits() {
        if (!this.inRecursiveFunction) {
            this.inRecursiveFunction = true;
            try {
                if (this.bound != null) {
                    this.tagBits |= this.bound.updateTagBits();
                }
                if (this.otherBounds != null) {
                    int i = 0;
                    int length = this.otherBounds.length;
                    while (i < length) {
                        this.tagBits |= this.otherBounds[i].updateTagBits();
                        ++i;
                    }
                }
            }
            finally {
                this.inRecursiveFunction = false;
            }
        }
        return super.updateTagBits();
    }
}

