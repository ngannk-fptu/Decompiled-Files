/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SplitPackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class UnresolvedReferenceBinding
extends ReferenceBinding {
    ReferenceBinding resolvedType;
    TypeBinding[] wrappers;
    UnresolvedReferenceBinding prototype;

    UnresolvedReferenceBinding(char[][] compoundName, PackageBinding packageBinding) {
        this.compoundName = compoundName;
        this.sourceName = compoundName[compoundName.length - 1];
        this.fPackage = packageBinding;
        this.wrappers = null;
        this.prototype = this;
        this.computeId();
    }

    public UnresolvedReferenceBinding(UnresolvedReferenceBinding prototype) {
        super(prototype);
        this.resolvedType = prototype.resolvedType;
        this.wrappers = null;
        this.prototype = prototype.prototype;
    }

    @Override
    public TypeBinding clone(TypeBinding outerType) {
        if (this.resolvedType != null) {
            return this.resolvedType.clone(outerType);
        }
        UnresolvedReferenceBinding copy = new UnresolvedReferenceBinding(this);
        this.addWrapper(copy, null);
        return copy;
    }

    void addWrapper(TypeBinding wrapper, LookupEnvironment environment) {
        if (this.resolvedType != null) {
            wrapper.swapUnresolved(this, this.resolvedType, environment);
            return;
        }
        if (this.wrappers == null) {
            this.wrappers = new TypeBinding[]{wrapper};
        } else {
            int length = this.wrappers.length;
            this.wrappers = new TypeBinding[length + 1];
            System.arraycopy(this.wrappers, 0, this.wrappers, 0, length);
            this.wrappers[length] = wrapper;
        }
    }

    @Override
    public boolean isUnresolvedType() {
        return true;
    }

    @Override
    public String debugName() {
        return this.toString();
    }

    @Override
    public int depth() {
        int last = this.compoundName.length - 1;
        return CharOperation.occurencesOf('$', this.compoundName[last], 1);
    }

    @Override
    public boolean hasTypeBit(int bit) {
        return false;
    }

    @Override
    public TypeBinding prototype() {
        return this.prototype;
    }

    ReferenceBinding resolve(LookupEnvironment environment, boolean convertGenericToRawType) {
        if (this != this.prototype) {
            ReferenceBinding targetType = this.prototype.resolve(environment, convertGenericToRawType);
            targetType = convertGenericToRawType && targetType != null && targetType.isRawType() ? (ReferenceBinding)environment.createAnnotatedType((TypeBinding)targetType, this.typeAnnotations) : this.resolvedType;
            return targetType;
        }
        ReferenceBinding targetType = this.resolvedType;
        if (targetType == null) {
            char[] typeName = this.compoundName[this.compoundName.length - 1];
            targetType = this.fPackage.getType0(typeName);
            if (targetType == this || targetType == null) {
                if (this.fPackage instanceof SplitPackageBinding) {
                    targetType = environment.askForType(this.fPackage, typeName, this.fPackage.enclosingModule);
                } else if (targetType == this) {
                    targetType = environment.askForType(this.compoundName, this.fPackage.enclosingModule);
                }
            }
            if ((targetType == null || targetType == this) && CharOperation.contains('.', typeName)) {
                targetType = environment.askForType(this.fPackage, CharOperation.replaceOnCopy(typeName, '.', '$'), this.fPackage.enclosingModule);
            }
            if (targetType == null || targetType == this) {
                if ((this.tagBits & 0x80L) == 0L && !environment.mayTolerateMissingType) {
                    environment.problemReporter.isClassPathCorrect(this.compoundName, environment.root.unitBeingCompleted, environment.missingClassFileLocation, false);
                }
                targetType = environment.createMissingType(null, this.compoundName);
            }
            this.setResolvedType(targetType, environment);
        }
        if (convertGenericToRawType) {
            targetType = (ReferenceBinding)environment.convertUnresolvedBinaryToRawType(targetType);
        }
        return targetType;
    }

    void setResolvedType(ReferenceBinding targetType, LookupEnvironment environment) {
        if (this.resolvedType == targetType) {
            return;
        }
        this.resolvedType = targetType;
        environment.updateCaches(this, targetType);
        if (this.wrappers != null) {
            int i = 0;
            int l = this.wrappers.length;
            while (i < l) {
                this.wrappers[i].swapUnresolved(this, targetType, environment);
                ++i;
            }
        }
    }

    @Override
    public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding unannotatedType, LookupEnvironment environment) {
        ReferenceBinding annotatedType;
        if (this.resolvedType != null) {
            return;
        }
        this.resolvedType = annotatedType = (ReferenceBinding)unannotatedType.clone(null);
        annotatedType.setTypeAnnotations(this.getTypeAnnotations(), environment.globalOptions.isAnnotationBasedNullAnalysisEnabled);
        environment.updateCaches(this, annotatedType);
        if (this.wrappers != null) {
            int i = 0;
            int l = this.wrappers.length;
            while (i < l) {
                this.wrappers[i].swapUnresolved(this, annotatedType, environment);
                ++i;
            }
        }
    }

    public String toString() {
        if (this.hasTypeAnnotations()) {
            return String.valueOf(super.annotatedDebugName()) + "(unresolved)";
        }
        return "Unresolved type " + (this.compoundName != null ? CharOperation.toString(this.compoundName) : "UNNAMED");
    }
}

