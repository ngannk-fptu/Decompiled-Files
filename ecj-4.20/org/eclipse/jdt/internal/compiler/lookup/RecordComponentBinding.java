/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.RecordComponent;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

public class RecordComponentBinding
extends VariableBinding {
    public ReferenceBinding declaringRecord;
    public BlockScope declaringScope;

    public RecordComponentBinding(ReferenceBinding declaringRecord, RecordComponent declaration, TypeBinding type, int modifiers) {
        super(declaration.name, type, modifiers, null);
        this.declaringRecord = declaringRecord;
        declaration.binding = this;
    }

    public RecordComponentBinding(char[] name, TypeBinding type, int modifiers, ReferenceBinding declaringClass) {
        super(name, type, modifiers, null);
        this.declaringRecord = declaringClass;
    }

    @Override
    public final int kind() {
        return 131072;
    }

    @Override
    public char[] computeUniqueKey(boolean isLeaf) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.declaringRecord.computeUniqueKey(false));
        buffer.append('#');
        buffer.append(this.name);
        int length = buffer.length();
        char[] uniqueKey = new char[length];
        buffer.getChars(0, length, uniqueKey, 0);
        return uniqueKey;
    }

    public char[] genericSignature() {
        if ((this.modifiers & 0x40000000) == 0) {
            return null;
        }
        return this.type.genericTypeSignature();
    }

    @Override
    public AnnotationBinding[] getAnnotations() {
        RecordComponentBinding originalRecordComponentBinding = this.original();
        ReferenceBinding declaringRecordBinding = originalRecordComponentBinding.declaringRecord;
        if (declaringRecordBinding == null) {
            return Binding.NO_ANNOTATIONS;
        }
        return declaringRecordBinding.retrieveAnnotations(originalRecordComponentBinding);
    }

    @Override
    public long getAnnotationTagBits() {
        RecordComponentBinding originalRecordComponentBinding = this.original();
        if ((originalRecordComponentBinding.tagBits & 0x200000000L) == 0L && originalRecordComponentBinding.declaringRecord instanceof SourceTypeBinding) {
            ClassScope scope = ((SourceTypeBinding)originalRecordComponentBinding.declaringRecord).scope;
            if (scope == null) {
                this.tagBits |= 0x600000000L;
                return 0L;
            }
            TypeDeclaration typeDecl = scope.referenceContext;
            RecordComponent recordComponent = typeDecl.declarationOf(originalRecordComponentBinding);
            if (recordComponent != null) {
                ASTNode.resolveAnnotations((BlockScope)typeDecl.initializerScope, recordComponent.annotations, originalRecordComponentBinding);
            }
        }
        return originalRecordComponentBinding.tagBits;
    }

    public final boolean isDeprecated() {
        return (this.modifiers & 0x100000) != 0;
    }

    public final boolean isPublic() {
        return (this.modifiers & 1) != 0;
    }

    public RecordComponentBinding original() {
        return this;
    }

    @Override
    public void setAnnotations(AnnotationBinding[] annotations, boolean forceStore) {
        this.declaringRecord.storeAnnotations(this, annotations, forceStore);
    }

    public RecordComponent sourceRecordComponent() {
        if (!(this.declaringRecord instanceof SourceTypeBinding)) {
            return null;
        }
        SourceTypeBinding sourceType = (SourceTypeBinding)this.declaringRecord;
        RecordComponent[] recordComponents = sourceType.scope.referenceContext.recordComponents;
        if (recordComponents != null) {
            int i = recordComponents.length;
            while (--i >= 0) {
                if (this != recordComponents[i].binding) continue;
                return recordComponents[i];
            }
        }
        return null;
    }
}

