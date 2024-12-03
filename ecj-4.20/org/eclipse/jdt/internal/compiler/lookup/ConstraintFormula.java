/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.BoundSet;
import org.eclipse.jdt.internal.compiler.lookup.CaptureBinding18;
import org.eclipse.jdt.internal.compiler.lookup.ConstraintTypeFormula;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.InferenceFailureException;
import org.eclipse.jdt.internal.compiler.lookup.InferenceVariable;
import org.eclipse.jdt.internal.compiler.lookup.ReductionResult;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

abstract class ConstraintFormula
extends ReductionResult {
    static final List<InferenceVariable> EMPTY_VARIABLE_LIST = Collections.emptyList();
    static final ConstraintFormula[] NO_CONSTRAINTS = new ConstraintTypeFormula[0];
    static final char LEFT_ANGLE_BRACKET = '\u27e8';
    static final char RIGHT_ANGLE_BRACKET = '\u27e9';

    ConstraintFormula() {
    }

    public abstract Object reduce(InferenceContext18 var1) throws InferenceFailureException;

    Collection<InferenceVariable> inputVariables(InferenceContext18 context) {
        return EMPTY_VARIABLE_LIST;
    }

    Collection<InferenceVariable> outputVariables(InferenceContext18 context) {
        HashSet<InferenceVariable> variables = new HashSet<InferenceVariable>();
        this.right.collectInferenceVariables(variables);
        if (!variables.isEmpty()) {
            variables.removeAll(this.inputVariables(context));
        }
        return variables;
    }

    public boolean applySubstitution(BoundSet solutionSet, InferenceVariable[] variables) {
        int i = 0;
        while (i < variables.length) {
            InferenceVariable variable = variables[i];
            TypeBinding instantiation = solutionSet.getInstantiation(variables[i], null);
            if (instantiation == null) {
                return false;
            }
            this.right = this.right.substituteInferenceVariable(variable, instantiation);
            ++i;
        }
        return true;
    }

    protected void appendTypeName(StringBuffer buf, TypeBinding type) {
        if (type instanceof CaptureBinding18) {
            buf.append(type.toString());
        } else {
            buf.append(type.readableName());
        }
    }
}

