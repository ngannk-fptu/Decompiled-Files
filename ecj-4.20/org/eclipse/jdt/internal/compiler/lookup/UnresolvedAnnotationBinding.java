/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;

public class UnresolvedAnnotationBinding
extends AnnotationBinding {
    private LookupEnvironment env;
    private boolean typeUnresolved = true;

    UnresolvedAnnotationBinding(ReferenceBinding type, ElementValuePair[] pairs, LookupEnvironment env) {
        super(type, pairs);
        this.env = env;
    }

    @Override
    public void resolve() {
        if (this.typeUnresolved) {
            boolean wasToleratingMissingTypeProcessingAnnotations = this.env.mayTolerateMissingType;
            this.env.mayTolerateMissingType = true;
            try {
                this.type = (ReferenceBinding)BinaryTypeBinding.resolveType(this.type, this.env, false);
            }
            finally {
                this.env.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
            }
            this.typeUnresolved = false;
        }
    }

    @Override
    public ReferenceBinding getAnnotationType() {
        this.resolve();
        return this.type;
    }

    @Override
    public ElementValuePair[] getElementValuePairs() {
        if (this.env != null) {
            if (this.typeUnresolved) {
                this.resolve();
            }
            int i = this.pairs.length;
            while (--i >= 0) {
                ElementValuePair pair = this.pairs[i];
                MethodBinding[] methods = this.type.getMethods(pair.getName());
                if (methods != null && methods.length == 1) {
                    pair.setMethodBinding(methods[0]);
                }
                Object value = pair.getValue();
                boolean wasToleratingMissingTypeProcessingAnnotations = this.env.mayTolerateMissingType;
                this.env.mayTolerateMissingType = true;
                try {
                    if (value instanceof UnresolvedReferenceBinding) {
                        pair.setValue(((UnresolvedReferenceBinding)value).resolve(this.env, false));
                        continue;
                    }
                    if (!(value instanceof Object[])) continue;
                    Object[] values = (Object[])value;
                    int j = 0;
                    while (j < values.length) {
                        if (values[j] instanceof UnresolvedReferenceBinding) {
                            values[j] = ((UnresolvedReferenceBinding)values[j]).resolve(this.env, false);
                        }
                        ++j;
                    }
                }
                finally {
                    this.env.mayTolerateMissingType = wasToleratingMissingTypeProcessingAnnotations;
                }
            }
            this.env = null;
        }
        return this.pairs;
    }
}

