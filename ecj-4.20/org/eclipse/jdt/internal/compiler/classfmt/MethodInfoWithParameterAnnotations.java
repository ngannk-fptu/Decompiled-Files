/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.classfmt.AnnotationInfo;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfo;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithAnnotations;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;

class MethodInfoWithParameterAnnotations
extends MethodInfoWithAnnotations {
    private AnnotationInfo[][] parameterAnnotations;

    MethodInfoWithParameterAnnotations(MethodInfo methodInfo, AnnotationInfo[] annotations, AnnotationInfo[][] parameterAnnotations) {
        super(methodInfo, annotations);
        this.parameterAnnotations = parameterAnnotations;
    }

    @Override
    public IBinaryAnnotation[] getParameterAnnotations(int index, char[] classFileName) {
        try {
            return this.parameterAnnotations == null ? null : this.parameterAnnotations[index];
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            StringBuffer message = new StringBuffer("Mismatching number of parameter annotations, ");
            message.append(index);
            message.append('>');
            message.append(this.parameterAnnotations.length - 1);
            message.append(" in ");
            message.append(this.getSelector());
            char[] desc = this.getGenericSignature();
            if (desc != null) {
                message.append(desc);
            } else {
                message.append(this.getMethodDescriptor());
            }
            if (classFileName != null) {
                message.append(" in ").append(classFileName);
            }
            throw new IllegalStateException(message.toString(), aioobe);
        }
    }

    @Override
    public int getAnnotatedParametersCount() {
        return this.parameterAnnotations == null ? 0 : this.parameterAnnotations.length;
    }

    @Override
    protected void initialize() {
        int i = 0;
        int l = this.parameterAnnotations == null ? 0 : this.parameterAnnotations.length;
        while (i < l) {
            AnnotationInfo[] infos = this.parameterAnnotations[i];
            int j = 0;
            int k = infos == null ? 0 : infos.length;
            while (j < k) {
                infos[j].initialize();
                ++j;
            }
            ++i;
        }
        super.initialize();
    }

    @Override
    protected void reset() {
        int i = 0;
        int l = this.parameterAnnotations == null ? 0 : this.parameterAnnotations.length;
        while (i < l) {
            AnnotationInfo[] infos = this.parameterAnnotations[i];
            int j = 0;
            int k = infos == null ? 0 : infos.length;
            while (j < k) {
                infos[j].reset();
                ++j;
            }
            ++i;
        }
        super.reset();
    }
}

