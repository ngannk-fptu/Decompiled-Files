/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public final class MemberTypeBinding
extends NestedTypeBinding {
    public MemberTypeBinding(char[][] compoundName, ClassScope scope, SourceTypeBinding enclosingType) {
        super(compoundName, scope, enclosingType);
        this.tagBits |= 0x80CL;
    }

    public MemberTypeBinding(MemberTypeBinding prototype) {
        super(prototype);
    }

    void checkSyntheticArgsAndFields() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.isStatic()) {
            return;
        }
        if (this.isInterface()) {
            return;
        }
        if (!this.isPrototype()) {
            ((MemberTypeBinding)this.prototype).checkSyntheticArgsAndFields();
            return;
        }
        this.addSyntheticArgumentAndField(this.enclosingType);
    }

    @Override
    public char[] constantPoolName() {
        if (this.constantPoolName != null) {
            return this.constantPoolName;
        }
        if (!this.isPrototype()) {
            return this.prototype.constantPoolName();
        }
        this.constantPoolName = CharOperation.concat(this.enclosingType().constantPoolName(), this.sourceName, '$');
        return this.constantPoolName;
    }

    @Override
    public TypeBinding clone(TypeBinding outerType) {
        MemberTypeBinding copy = new MemberTypeBinding(this);
        copy.enclosingType = (SourceTypeBinding)outerType;
        return copy;
    }

    @Override
    public void initializeDeprecatedAnnotationTagBits() {
        if (!this.isPrototype()) {
            this.prototype.initializeDeprecatedAnnotationTagBits();
            return;
        }
        if ((this.tagBits & 0x400000000L) == 0L) {
            super.initializeDeprecatedAnnotationTagBits();
            if ((this.tagBits & 0x400000000000L) == 0L) {
                ReferenceBinding enclosing = this.enclosingType();
                if ((enclosing.tagBits & 0x400000000L) == 0L) {
                    enclosing.initializeDeprecatedAnnotationTagBits();
                }
                if (enclosing.isViewedAsDeprecated()) {
                    this.modifiers |= 0x200000;
                    this.tagBits |= enclosing.tagBits & 0x4000000000000000L;
                }
            }
        }
    }

    @Override
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        return "Member type : " + new String(this.sourceName()) + " " + super.toString();
    }

    @Override
    public ModuleBinding module() {
        return this.enclosingType.module();
    }
}

