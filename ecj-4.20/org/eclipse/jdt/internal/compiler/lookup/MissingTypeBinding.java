/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class MissingTypeBinding
extends BinaryTypeBinding {
    public MissingTypeBinding(PackageBinding packageBinding, char[][] compoundName, LookupEnvironment environment) {
        this.compoundName = compoundName;
        this.computeId();
        this.tagBits |= 0x200C0L;
        this.environment = environment;
        this.fPackage = packageBinding;
        this.fileName = CharOperation.concatWith(compoundName, '/');
        this.sourceName = compoundName[compoundName.length - 1];
        this.modifiers = 1;
        this.superclass = null;
        this.superInterfaces = Binding.NO_SUPERINTERFACES;
        this.permittedSubtypes = Binding.NO_PERMITTEDTYPES;
        this.typeVariables = Binding.NO_TYPE_VARIABLES;
        this.memberTypes = Binding.NO_MEMBER_TYPES;
        this.fields = Binding.NO_FIELDS;
        this.methods = Binding.NO_METHODS;
    }

    @Override
    public TypeBinding clone(TypeBinding outerType) {
        return this;
    }

    @Override
    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if (missingTypes == null) {
            missingTypes = new ArrayList<TypeBinding>(5);
        } else if (missingTypes.contains(this)) {
            return missingTypes;
        }
        missingTypes.add(this);
        return missingTypes;
    }

    @Override
    public int problemId() {
        return 1;
    }

    void setMissingSuperclass(ReferenceBinding missingSuperclass) {
        this.superclass = missingSuperclass;
    }

    @Override
    public void setTypeAnnotations(AnnotationBinding[] annotations, boolean evalNullAnnotations) {
    }

    @Override
    public String toString() {
        return "[MISSING:" + new String(CharOperation.concatWith(this.compoundName, '.')) + "]";
    }
}

