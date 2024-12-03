/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Set;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

public class IntersectionTypeBinding18
extends ReferenceBinding {
    public ReferenceBinding[] intersectingTypes;
    private ReferenceBinding javaLangObject;
    int length;

    public IntersectionTypeBinding18(ReferenceBinding[] intersectingTypes, LookupEnvironment environment) {
        this.intersectingTypes = intersectingTypes;
        this.length = intersectingTypes.length;
        if (!intersectingTypes[0].isClass()) {
            this.javaLangObject = environment.getResolvedJavaBaseType(TypeConstants.JAVA_LANG_OBJECT, null);
            this.modifiers |= 0x200;
        }
    }

    private IntersectionTypeBinding18(IntersectionTypeBinding18 prototype) {
        this.intersectingTypes = prototype.intersectingTypes;
        this.length = prototype.length;
        if (!this.intersectingTypes[0].isClass()) {
            this.javaLangObject = prototype.javaLangObject;
            this.modifiers |= 0x200;
        }
    }

    @Override
    public TypeBinding clone(TypeBinding enclosingType) {
        return new IntersectionTypeBinding18(this);
    }

    @Override
    protected MethodBinding[] getInterfaceAbstractContracts(Scope scope, boolean replaceWildcards, boolean filterDefaultMethods) throws InvalidInputException {
        int typesLength = this.intersectingTypes.length;
        MethodBinding[][] methods = new MethodBinding[typesLength][];
        int contractsLength = 0;
        int i = 0;
        while (i < typesLength) {
            methods[i] = this.intersectingTypes[i].getInterfaceAbstractContracts(scope, replaceWildcards, true);
            contractsLength += methods[i].length;
            ++i;
        }
        MethodBinding[] contracts = new MethodBinding[contractsLength];
        int idx = 0;
        int i2 = 0;
        while (i2 < typesLength) {
            int len = methods[i2].length;
            System.arraycopy(methods[i2], 0, contracts, idx, len);
            idx += len;
            ++i2;
        }
        return contracts;
    }

    @Override
    public boolean hasTypeBit(int bit) {
        int i = 0;
        while (i < this.length) {
            if (this.intersectingTypes[i].hasTypeBit(bit)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public boolean canBeInstantiated() {
        return false;
    }

    @Override
    public boolean canBeSeenBy(PackageBinding invocationPackage) {
        int i = 0;
        while (i < this.length) {
            if (!this.intersectingTypes[i].canBeSeenBy(invocationPackage)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean canBeSeenBy(Scope scope) {
        int i = 0;
        while (i < this.length) {
            if (!this.intersectingTypes[i].canBeSeenBy(scope)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public boolean canBeSeenBy(ReferenceBinding receiverType, ReferenceBinding invocationType) {
        int i = 0;
        while (i < this.length) {
            if (!this.intersectingTypes[i].canBeSeenBy(receiverType, invocationType)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    @Override
    public char[] constantPoolName() {
        TypeBinding erasure = this.erasure();
        if (erasure != this) {
            return erasure.constantPoolName();
        }
        if (this.intersectingTypes[0].id == 1 && this.intersectingTypes.length > 1) {
            return this.intersectingTypes[1].constantPoolName();
        }
        return this.intersectingTypes[0].constantPoolName();
    }

    @Override
    public PackageBinding getPackage() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReferenceBinding[] getIntersectingTypes() {
        return this.intersectingTypes;
    }

    @Override
    public ReferenceBinding superclass() {
        return this.intersectingTypes[0].isClass() ? this.intersectingTypes[0] : this.javaLangObject;
    }

    @Override
    public ReferenceBinding[] superInterfaces() {
        if (this.intersectingTypes[0].isClass()) {
            ReferenceBinding[] superInterfaces = new ReferenceBinding[this.length - 1];
            System.arraycopy(this.intersectingTypes, 1, superInterfaces, 0, this.length - 1);
            return superInterfaces;
        }
        return this.intersectingTypes;
    }

    @Override
    public boolean isBoxedPrimitiveType() {
        return this.intersectingTypes[0].isBoxedPrimitiveType();
    }

    @Override
    public boolean isCompatibleWith(TypeBinding right, Scope scope) {
        if (TypeBinding.equalsEquals(this, right)) {
            return true;
        }
        int rightKind = right.kind();
        ReferenceBinding[] rightIntersectingTypes = null;
        if (rightKind == 8196 && right.boundKind() == 1) {
            TypeBinding allRightBounds = ((WildcardBinding)right).allBounds();
            if (allRightBounds instanceof IntersectionTypeBinding18) {
                rightIntersectingTypes = ((IntersectionTypeBinding18)allRightBounds).intersectingTypes;
            }
        } else if (rightKind == 32772) {
            rightIntersectingTypes = ((IntersectionTypeBinding18)right).intersectingTypes;
        }
        if (rightIntersectingTypes != null) {
            ReferenceBinding[] referenceBindingArray = rightIntersectingTypes;
            int n = rightIntersectingTypes.length;
            int n2 = 0;
            while (n2 < n) {
                block11: {
                    ReferenceBinding required = referenceBindingArray[n2];
                    ReferenceBinding[] referenceBindingArray2 = this.intersectingTypes;
                    int n3 = this.intersectingTypes.length;
                    int n4 = 0;
                    while (n4 < n3) {
                        ReferenceBinding provided = referenceBindingArray2[n4];
                        if (!((TypeBinding)provided).isCompatibleWith(required, scope)) {
                            ++n4;
                            continue;
                        }
                        break block11;
                    }
                    return false;
                }
                ++n2;
            }
            return true;
        }
        int i = 0;
        while (i < this.length) {
            if (this.intersectingTypes[i].isCompatibleWith(right, scope)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public boolean isSubtypeOf(TypeBinding other, boolean simulatingBugJDK8026527) {
        ReferenceBinding[] rightIntersectingTypes;
        if (TypeBinding.equalsEquals(this, other)) {
            return true;
        }
        if (other instanceof ReferenceBinding && (rightIntersectingTypes = ((ReferenceBinding)other).getIntersectingTypes()) != null && rightIntersectingTypes.length > 1) {
            int numRequired = rightIntersectingTypes.length;
            TypeBinding[] required = new TypeBinding[numRequired];
            System.arraycopy(rightIntersectingTypes, 0, required, 0, numRequired);
            int i = 0;
            while (i < this.length) {
                ReferenceBinding provided = this.intersectingTypes[i];
                int j = 0;
                while (j < required.length) {
                    if (required[j] != null && ((TypeBinding)provided).isSubtypeOf(required[j], simulatingBugJDK8026527)) {
                        required[j] = null;
                        if (--numRequired != 0) break;
                        return true;
                    }
                    ++j;
                }
                ++i;
            }
            return false;
        }
        int i = 0;
        while (i < this.intersectingTypes.length) {
            if (this.intersectingTypes[i].isSubtypeOf(other, false)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public TypeBinding erasure() {
        int classIdx = -1;
        int i = 0;
        while (i < this.intersectingTypes.length) {
            if (this.intersectingTypes[i].isClass() && this.intersectingTypes[i].id != 1) {
                if (classIdx == -1) {
                    classIdx = i;
                } else {
                    classIdx = Integer.MAX_VALUE;
                    break;
                }
            }
            ++i;
        }
        if (classIdx > -1 && classIdx < Integer.MAX_VALUE) {
            return this.intersectingTypes[classIdx].erasure();
        }
        return this;
    }

    @Override
    public char[] qualifiedSourceName() {
        StringBuffer qualifiedSourceName = new StringBuffer(16);
        int i = 0;
        while (i < this.length) {
            qualifiedSourceName.append(this.intersectingTypes[i].qualifiedSourceName());
            if (i != this.length - 1) {
                qualifiedSourceName.append(" & ");
            }
            ++i;
        }
        return qualifiedSourceName.toString().toCharArray();
    }

    @Override
    public char[] sourceName() {
        StringBuffer srcName = new StringBuffer(16);
        int i = 0;
        while (i < this.length) {
            srcName.append(this.intersectingTypes[i].sourceName());
            if (i != this.length - 1) {
                srcName.append(" & ");
            }
            ++i;
        }
        return srcName.toString().toCharArray();
    }

    @Override
    public char[] readableName() {
        StringBuffer readableName = new StringBuffer(16);
        int i = 0;
        while (i < this.length) {
            readableName.append(this.intersectingTypes[i].readableName());
            if (i != this.length - 1) {
                readableName.append(" & ");
            }
            ++i;
        }
        return readableName.toString().toCharArray();
    }

    @Override
    public char[] shortReadableName() {
        StringBuffer shortReadableName = new StringBuffer(16);
        int i = 0;
        while (i < this.length) {
            shortReadableName.append(this.intersectingTypes[i].shortReadableName());
            if (i != this.length - 1) {
                shortReadableName.append(" & ");
            }
            ++i;
        }
        return shortReadableName.toString().toCharArray();
    }

    @Override
    public boolean isIntersectionType18() {
        return true;
    }

    @Override
    public int kind() {
        return 32772;
    }

    @Override
    public String debugName() {
        StringBuffer debugName = new StringBuffer(16);
        int i = 0;
        while (i < this.length) {
            debugName.append(this.intersectingTypes[i].debugName());
            if (i != this.length - 1) {
                debugName.append(" & ");
            }
            ++i;
        }
        return debugName.toString();
    }

    public String toString() {
        return this.debugName();
    }

    public TypeBinding getSAMType(Scope scope) {
        int i = 0;
        int max = this.intersectingTypes.length;
        while (i < max) {
            ReferenceBinding typeBinding = this.intersectingTypes[i];
            MethodBinding methodBinding = ((TypeBinding)typeBinding).getSingleAbstractMethod(scope, true);
            if (methodBinding != null && methodBinding.problemId() != 17) {
                return typeBinding;
            }
            ++i;
        }
        return null;
    }

    @Override
    void collectInferenceVariables(Set<InferenceVariable> variables) {
        int i = 0;
        while (i < this.intersectingTypes.length) {
            this.intersectingTypes[i].collectInferenceVariables(variables);
            ++i;
        }
    }

    @Override
    public ReferenceBinding upwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        ReferenceBinding[] projectedTypes = new ReferenceBinding[this.intersectingTypes.length];
        int i = 0;
        while (i < this.intersectingTypes.length) {
            projectedTypes[i] = this.intersectingTypes[i].upwardsProjection(scope, mentionedTypeVariables);
            ++i;
        }
        return (ReferenceBinding)scope.environment().createIntersectionType18(projectedTypes);
    }

    @Override
    public ReferenceBinding downwardsProjection(Scope scope, TypeBinding[] mentionedTypeVariables) {
        ReferenceBinding[] projectedTypes = new ReferenceBinding[this.intersectingTypes.length];
        int i = 0;
        while (i < this.intersectingTypes.length) {
            projectedTypes[i] = this.intersectingTypes[i].downwardsProjection(scope, mentionedTypeVariables);
            ++i;
        }
        return (ReferenceBinding)scope.environment().createIntersectionType18(projectedTypes);
    }

    @Override
    public boolean mentionsAny(TypeBinding[] parameters, int idx) {
        if (super.mentionsAny(parameters, idx)) {
            return true;
        }
        int i = 0;
        while (i < this.intersectingTypes.length) {
            if (this.intersectingTypes[i].mentionsAny(parameters, -1)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    @Override
    public long updateTagBits() {
        ReferenceBinding[] referenceBindingArray = this.intersectingTypes;
        int n = this.intersectingTypes.length;
        int n2 = 0;
        while (n2 < n) {
            ReferenceBinding intersectingType = referenceBindingArray[n2];
            this.tagBits |= intersectingType.updateTagBits();
            ++n2;
        }
        return super.updateTagBits();
    }
}

