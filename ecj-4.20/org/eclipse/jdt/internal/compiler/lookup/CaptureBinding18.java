/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public class CaptureBinding18
extends CaptureBinding {
    TypeBinding[] upperBounds;
    private char[] originalName;
    private CaptureBinding18 prototype;
    int recursionLevel = 0;

    public CaptureBinding18(ReferenceBinding contextType, char[] sourceName, char[] originalName, int start, int end, int captureID, LookupEnvironment environment) {
        super(contextType, sourceName, start, end, captureID, environment);
        this.originalName = originalName;
        this.prototype = this;
    }

    private CaptureBinding18(CaptureBinding18 prototype) {
        super(prototype);
        this.sourceName = CharOperation.append(prototype.sourceName, '\'');
        this.originalName = prototype.originalName;
        this.upperBounds = prototype.upperBounds;
        this.prototype = prototype.prototype;
    }

    public boolean setUpperBounds(TypeBinding[] upperBounds, ReferenceBinding javaLangObject) {
        this.upperBounds = upperBounds;
        if (upperBounds.length > 0) {
            this.firstBound = upperBounds[0];
        }
        int numReferenceInterfaces = 0;
        if (!CaptureBinding18.isConsistentIntersection(upperBounds)) {
            return false;
        }
        int i = 0;
        while (i < upperBounds.length) {
            TypeBinding aBound = upperBounds[i];
            if (aBound instanceof ReferenceBinding) {
                if (this.superclass == null && aBound.isClass()) {
                    this.superclass = (ReferenceBinding)aBound;
                } else if (aBound.isInterface()) {
                    ++numReferenceInterfaces;
                }
            } else if (TypeBinding.equalsEquals(aBound.leafComponentType(), this)) {
                return false;
            }
            ++i;
        }
        this.superInterfaces = new ReferenceBinding[numReferenceInterfaces];
        int idx = 0;
        int i2 = 0;
        while (i2 < upperBounds.length) {
            TypeBinding aBound = upperBounds[i2];
            if (aBound.isInterface()) {
                this.superInterfaces[idx++] = (ReferenceBinding)aBound;
            }
            ++i2;
        }
        if (this.superclass == null) {
            this.superclass = javaLangObject;
        }
        return true;
    }

    @Override
    public void initializeBounds(Scope scope, ParameterizedTypeBinding capturedParameterizedType) {
    }

    @Override
    public TypeBinding clone(TypeBinding enclosingType) {
        return new CaptureBinding18(this);
    }

    @Override
    public MethodBinding[] getMethods(char[] selector) {
        if (this.upperBounds.length == 1 && this.upperBounds[0] instanceof ReferenceBinding) {
            return ((ReferenceBinding)this.upperBounds[0]).getMethods(selector);
        }
        return super.getMethods(selector);
    }

    @Override
    public TypeBinding erasure() {
        if (this.upperBounds != null && this.upperBounds.length > 1) {
            ReferenceBinding[] erasures = new ReferenceBinding[this.upperBounds.length];
            boolean multipleErasures = false;
            int i = 0;
            while (i < this.upperBounds.length) {
                erasures[i] = (ReferenceBinding)this.upperBounds[i].erasure();
                if (i > 0 && TypeBinding.notEquals(erasures[0], erasures[i])) {
                    multipleErasures = true;
                }
                ++i;
            }
            if (!multipleErasures) {
                return erasures[0];
            }
            return this.environment.createIntersectionType18(erasures);
        }
        if (this.superclass == null) {
            return this.environment.getType(TypeConstants.JAVA_LANG_OBJECT);
        }
        return super.erasure();
    }

    @Override
    public boolean isEquivalentTo(TypeBinding otherType) {
        if (CaptureBinding18.equalsEquals(this, otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        if (this.upperBounds != null) {
            int i = 0;
            while (i < this.upperBounds.length) {
                TypeBinding aBound = this.upperBounds[i];
                if (aBound != null && aBound.isArrayType()) {
                    if (!aBound.isCompatibleWith(otherType)) {
                        return false;
                    }
                } else {
                    switch (otherType.kind()) {
                        case 516: 
                        case 8196: {
                            if (((WildcardBinding)otherType).boundCheck(aBound)) break;
                            return false;
                        }
                    }
                }
                ++i;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isCompatibleWith(TypeBinding otherType, Scope captureScope) {
        if (TypeBinding.equalsEquals(this, otherType)) {
            return true;
        }
        if (this.inRecursiveFunction) {
            return true;
        }
        this.inRecursiveFunction = true;
        try {
            if (this.upperBounds != null) {
                int length = this.upperBounds.length;
                int rightKind = otherType.kind();
                ReferenceBinding[] rightIntersectingTypes = null;
                if (rightKind == 8196 && otherType.boundKind() == 1) {
                    TypeBinding allRightBounds = ((WildcardBinding)otherType).allBounds();
                    if (allRightBounds instanceof IntersectionTypeBinding18) {
                        rightIntersectingTypes = ((IntersectionTypeBinding18)allRightBounds).intersectingTypes;
                    }
                } else if (rightKind == 32772) {
                    rightIntersectingTypes = ((IntersectionTypeBinding18)otherType).intersectingTypes;
                }
                if (rightIntersectingTypes != null) {
                    ReferenceBinding[] referenceBindingArray = rightIntersectingTypes;
                    int n = rightIntersectingTypes.length;
                    int n2 = 0;
                    while (n2 < n) {
                        block19: {
                            ReferenceBinding required = referenceBindingArray[n2];
                            TypeBinding[] typeBindingArray = this.upperBounds;
                            int n3 = this.upperBounds.length;
                            int n4 = 0;
                            while (n4 < n3) {
                                TypeBinding provided = typeBindingArray[n4];
                                if (!provided.isCompatibleWith(required, captureScope)) {
                                    ++n4;
                                    continue;
                                }
                                break block19;
                            }
                            return false;
                        }
                        ++n2;
                    }
                    return true;
                }
                int i = 0;
                while (i < length) {
                    if (this.upperBounds[i].isCompatibleWith(otherType, captureScope)) {
                        return true;
                    }
                    ++i;
                }
            }
            return false;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }

    @Override
    public TypeBinding findSuperTypeOriginatingFrom(TypeBinding otherType) {
        if (this.upperBounds != null && this.upperBounds.length > 1) {
            int i = 0;
            while (i < this.upperBounds.length) {
                TypeBinding candidate = this.upperBounds[i].findSuperTypeOriginatingFrom(otherType);
                if (candidate != null) {
                    return candidate;
                }
                ++i;
            }
        }
        return super.findSuperTypeOriginatingFrom(otherType);
    }

    @Override
    TypeBinding substituteInferenceVariable(InferenceVariable var, TypeBinding substituteType) {
        if (this.inRecursiveFunction) {
            return this;
        }
        this.inRecursiveFunction = true;
        try {
            boolean haveSubstitution = false;
            ReferenceBinding currentSuperclass = this.superclass;
            if (currentSuperclass != null) {
                currentSuperclass = (ReferenceBinding)currentSuperclass.substituteInferenceVariable(var, substituteType);
                haveSubstitution |= TypeBinding.notEquals(currentSuperclass, this.superclass);
            }
            ReferenceBinding[] currentSuperInterfaces = null;
            if (this.superInterfaces != null) {
                int length = this.superInterfaces.length;
                if (haveSubstitution) {
                    currentSuperInterfaces = new ReferenceBinding[length];
                    System.arraycopy(this.superInterfaces, 0, currentSuperInterfaces, 0, length);
                }
                int i = 0;
                while (i < length) {
                    ReferenceBinding currentSuperInterface = this.superInterfaces[i];
                    if (currentSuperInterface != null && TypeBinding.notEquals(currentSuperInterface = (ReferenceBinding)currentSuperInterface.substituteInferenceVariable(var, substituteType), this.superInterfaces[i])) {
                        if (currentSuperInterfaces == null) {
                            currentSuperInterfaces = new ReferenceBinding[length];
                            System.arraycopy(this.superInterfaces, 0, currentSuperInterfaces, 0, length);
                        }
                        currentSuperInterfaces[i] = currentSuperInterface;
                        haveSubstitution = true;
                    }
                    ++i;
                }
            }
            TypeBinding[] currentUpperBounds = null;
            if (this.upperBounds != null) {
                int length = this.upperBounds.length;
                if (haveSubstitution) {
                    currentUpperBounds = new TypeBinding[length];
                    System.arraycopy(this.upperBounds, 0, currentUpperBounds, 0, length);
                }
                int i = 0;
                while (i < length) {
                    TypeBinding currentBound = this.upperBounds[i];
                    if (currentBound != null && TypeBinding.notEquals(currentBound = currentBound.substituteInferenceVariable(var, substituteType), this.upperBounds[i])) {
                        if (currentUpperBounds == null) {
                            currentUpperBounds = new TypeBinding[length];
                            System.arraycopy(this.upperBounds, 0, currentUpperBounds, 0, length);
                        }
                        currentUpperBounds[i] = currentBound;
                        haveSubstitution = true;
                    }
                    ++i;
                }
            }
            TypeBinding currentFirstBound = null;
            if (this.firstBound != null) {
                currentFirstBound = this.firstBound.substituteInferenceVariable(var, substituteType);
                haveSubstitution |= TypeBinding.notEquals(this.firstBound, currentFirstBound);
            }
            if (haveSubstitution) {
                final CaptureBinding18 newCapture = (CaptureBinding18)this.clone(this.enclosingType());
                newCapture.tagBits = this.tagBits;
                Substitution substitution = new Substitution(){

                    @Override
                    public TypeBinding substitute(TypeVariableBinding typeVariable) {
                        return typeVariable == CaptureBinding18.this ? newCapture : typeVariable;
                    }

                    @Override
                    public boolean isRawSubstitution() {
                        return false;
                    }

                    @Override
                    public LookupEnvironment environment() {
                        return CaptureBinding18.this.environment;
                    }
                };
                if (currentFirstBound != null) {
                    newCapture.firstBound = Scope.substitute(substitution, currentFirstBound);
                }
                newCapture.superclass = (ReferenceBinding)Scope.substitute(substitution, currentSuperclass);
                newCapture.superInterfaces = Scope.substitute(substitution, currentSuperInterfaces);
                newCapture.upperBounds = Scope.substitute(substitution, currentUpperBounds);
                CaptureBinding18 captureBinding18 = newCapture;
                return captureBinding18;
            }
            CaptureBinding18 captureBinding18 = this;
            return captureBinding18;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }

    @Override
    public boolean isProperType(boolean admitCapture18) {
        if (!admitCapture18) {
            return false;
        }
        if (this.inRecursiveFunction) {
            return true;
        }
        this.inRecursiveFunction = true;
        try {
            if (this.lowerBound != null && !this.lowerBound.isProperType(admitCapture18)) {
                return false;
            }
            if (this.upperBounds != null) {
                int i = 0;
                while (i < this.upperBounds.length) {
                    if (!this.upperBounds[i].isProperType(admitCapture18)) {
                        return false;
                    }
                    ++i;
                }
            }
        }
        finally {
            this.inRecursiveFunction = false;
        }
        return true;
    }

    @Override
    public char[] readableName() {
        if (this.lowerBound == null && this.firstBound != null) {
            if (this.prototype.recursionLevel < 2) {
                try {
                    ++this.prototype.recursionLevel;
                    if (this.upperBounds != null && this.upperBounds.length > 1) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(this.upperBounds[0].readableName());
                        int i = 1;
                        while (i < this.upperBounds.length) {
                            sb.append('&').append(this.upperBounds[i].readableName());
                            ++i;
                        }
                        int len = sb.length();
                        char[] name = new char[len];
                        sb.getChars(0, len, name, 0);
                        char[] cArray = name;
                        return cArray;
                    }
                    char[] cArray = this.firstBound.readableName();
                    return cArray;
                }
                finally {
                    --this.prototype.recursionLevel;
                }
            }
            return this.originalName;
        }
        return super.readableName();
    }

    @Override
    public char[] shortReadableName() {
        if (this.lowerBound == null && this.firstBound != null) {
            if (this.prototype.recursionLevel < 2) {
                try {
                    ++this.prototype.recursionLevel;
                    if (this.upperBounds != null && this.upperBounds.length > 1) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(this.upperBounds[0].shortReadableName());
                        int i = 1;
                        while (i < this.upperBounds.length) {
                            sb.append('&').append(this.upperBounds[i].shortReadableName());
                            ++i;
                        }
                        int len = sb.length();
                        char[] name = new char[len];
                        sb.getChars(0, len, name, 0);
                        char[] cArray = name;
                        return cArray;
                    }
                    char[] cArray = this.firstBound.shortReadableName();
                    return cArray;
                }
                finally {
                    --this.prototype.recursionLevel;
                }
            }
            return this.originalName;
        }
        return super.shortReadableName();
    }

    @Override
    public TypeBinding uncapture(Scope scope) {
        return this;
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(TypeConstants.CAPTURE18);
        buffer.append('{').append(this.end).append('#').append(this.captureID).append('}');
        buffer.append(';');
        int length = buffer.length();
        char[] uniqueKey = new char[length];
        buffer.getChars(0, length, uniqueKey, 0);
        return uniqueKey;
    }
}

