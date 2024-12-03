/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.util.List;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.patterns.BindingPattern;
import org.aspectj.weaver.patterns.BindingTypePattern;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePattern;

public abstract class NameBindingPointcut
extends Pointcut {
    protected Test exposeStateForVar(Var var, TypePattern type, ExposedState state, World world) {
        ResolvedType myType;
        if (type instanceof BindingTypePattern) {
            BindingTypePattern b = (BindingTypePattern)type;
            state.set(b.getFormalIndex(), var);
        }
        if ((myType = type.getExactType().resolve(world)).isParameterizedType()) {
            myType = myType.getRawType();
        }
        return Test.makeInstanceof(var, myType.resolve(world));
    }

    public abstract List<BindingTypePattern> getBindingTypePatterns();

    public abstract List<BindingPattern> getBindingAnnotationTypePatterns();
}

