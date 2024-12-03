/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.UnionTypeReference;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class MultiCatchExceptionLabel
extends ExceptionLabel {
    ExceptionLabel[] exceptionLabels;

    public MultiCatchExceptionLabel(CodeStream codeStream, TypeBinding exceptionType) {
        super(codeStream, exceptionType);
    }

    public void initialize(UnionTypeReference typeReference, Annotation[] annotations) {
        TypeReference[] typeReferences = typeReference.typeReferences;
        int length = typeReferences.length;
        this.exceptionLabels = new ExceptionLabel[length];
        int i = 0;
        while (i < length) {
            this.exceptionLabels[i] = new ExceptionLabel(this.codeStream, typeReferences[i].resolvedType, typeReferences[i], (Annotation[])(i == 0 ? annotations : null));
            ++i;
        }
    }

    @Override
    public void place() {
        int i = 0;
        int max = this.exceptionLabels.length;
        while (i < max) {
            this.exceptionLabels[i].place();
            ++i;
        }
    }

    @Override
    public void placeEnd() {
        int i = 0;
        int max = this.exceptionLabels.length;
        while (i < max) {
            this.exceptionLabels[i].placeEnd();
            ++i;
        }
    }

    @Override
    public void placeStart() {
        int i = 0;
        int max = this.exceptionLabels.length;
        while (i < max) {
            this.exceptionLabels[i].placeStart();
            ++i;
        }
    }

    @Override
    public int getCount() {
        int temp = 0;
        int i = 0;
        int max = this.exceptionLabels.length;
        while (i < max) {
            temp += this.exceptionLabels[i].getCount();
            ++i;
        }
        return temp;
    }
}

