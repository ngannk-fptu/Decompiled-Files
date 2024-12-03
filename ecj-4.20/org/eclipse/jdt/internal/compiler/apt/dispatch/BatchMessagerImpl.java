/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import org.eclipse.jdt.internal.compiler.apt.dispatch.AptProblem;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseMessagerImpl;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.batch.Main;

public class BatchMessagerImpl
extends BaseMessagerImpl
implements Messager {
    private final Main _compiler;
    private final BaseProcessingEnvImpl _processingEnv;

    public BatchMessagerImpl(BaseProcessingEnvImpl processingEnv, Main compiler) {
        this._compiler = compiler;
        this._processingEnv = processingEnv;
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg) {
        this.printMessage(kind, msg, null, null, null);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e) {
        this.printMessage(kind, msg, e, null, null);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a) {
        this.printMessage(kind, msg, e, a, null);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e, AnnotationMirror a, AnnotationValue v) {
        AptProblem problem;
        if (kind == Diagnostic.Kind.ERROR) {
            this._processingEnv.setErrorRaised(true);
        }
        if ((problem = BatchMessagerImpl.createProblem(kind, msg, e, a, v)) != null) {
            this._compiler.addExtraProblems(problem);
        }
    }
}

