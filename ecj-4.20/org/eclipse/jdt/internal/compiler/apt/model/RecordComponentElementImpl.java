/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.RecordComponentElement;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.model.ExecutableElementImpl;
import org.eclipse.jdt.internal.compiler.apt.model.VariableElementImpl;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.RecordComponentBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;

public class RecordComponentElementImpl
extends VariableElementImpl
implements RecordComponentElement {
    protected RecordComponentElementImpl(BaseProcessingEnvImpl env, RecordComponentBinding binding) {
        super(env, binding);
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.RECORD_COMPONENT;
    }

    @Override
    public ExecutableElement getAccessor() {
        RecordComponentBinding comp = (RecordComponentBinding)this._binding;
        ReferenceBinding binding = comp.declaringRecord;
        if (binding instanceof SourceTypeBinding) {
            MethodBinding accessor = ((SourceTypeBinding)binding).getRecordComponentAccessor(comp.name);
            return new ExecutableElementImpl(this._env, accessor);
        }
        return null;
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> visitor, P param) {
        return visitor.visitRecordComponent(this, param);
    }
}

