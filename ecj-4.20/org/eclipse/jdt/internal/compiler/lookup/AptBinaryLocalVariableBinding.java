/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Arrays;
import java.util.Objects;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class AptBinaryLocalVariableBinding
extends LocalVariableBinding {
    AnnotationBinding[] annotationBindings;
    public MethodBinding methodBinding;

    public AptBinaryLocalVariableBinding(char[] name, TypeBinding type, int modifiers, AnnotationBinding[] annotationBindings, MethodBinding methodBinding) {
        super(name, type, modifiers, true);
        this.annotationBindings = annotationBindings == null ? Binding.NO_ANNOTATIONS : annotationBindings;
        this.methodBinding = methodBinding;
    }

    @Override
    public AnnotationBinding[] getAnnotations() {
        return this.annotationBindings;
    }

    public int hashCode() {
        int result = 17;
        int c = CharOperation.hashCode(this.name);
        result = 31 * result + c;
        c = this.type.hashCode();
        result = 31 * result + c;
        c = this.modifiers;
        result = 31 * result + c;
        c = Arrays.hashCode(this.annotationBindings);
        result = 31 * result + c;
        c = this.methodBinding.hashCode();
        result = 31 * result + c;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AptBinaryLocalVariableBinding other = (AptBinaryLocalVariableBinding)obj;
        return CharOperation.equals(this.name, other.name) && Objects.equals(this.type, other.type) && this.modifiers == other.modifiers && Arrays.equals(this.annotationBindings, other.annotationBindings) && Objects.equals(this.methodBinding, other.methodBinding);
    }
}

