/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.internal.tools;

import java.io.IOException;
import java.util.Map;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.internal.tools.MatchingContextBasedTest;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegate;
import org.aspectj.weaver.reflect.ReflectionFastMatchInfo;
import org.aspectj.weaver.reflect.ReflectionShadow;
import org.aspectj.weaver.reflect.ReflectionWorld;
import org.aspectj.weaver.tools.ContextBasedMatcher;
import org.aspectj.weaver.tools.MatchingContext;

public class PointcutDesignatorHandlerBasedPointcut
extends Pointcut {
    private final ContextBasedMatcher matcher;
    private final World world;

    public PointcutDesignatorHandlerBasedPointcut(ContextBasedMatcher expr, World world) {
        this.matcher = expr;
        this.world = world;
    }

    @Override
    public byte getPointcutKind() {
        return 22;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo info) {
        if (info instanceof ReflectionFastMatchInfo) {
            Class<?> clazz;
            block5: {
                if (!(this.world instanceof ReflectionWorld)) {
                    throw new IllegalStateException("Can only match user-extension pcds with a ReflectionWorld");
                }
                clazz = null;
                try {
                    clazz = Class.forName(info.getType().getName(), false, ((ReflectionWorld)this.world).getClassLoader());
                }
                catch (ClassNotFoundException cnfe) {
                    ReferenceTypeDelegate rtd;
                    if (!(info.getType() instanceof ReferenceType) || !((rtd = ((ReferenceType)info.getType()).getDelegate()) instanceof ReflectionBasedReferenceTypeDelegate)) break block5;
                    clazz = ((ReflectionBasedReferenceTypeDelegate)rtd).getClazz();
                }
            }
            if (clazz == null) {
                return FuzzyBoolean.MAYBE;
            }
            return FuzzyBoolean.fromBoolean(this.matcher.couldMatchJoinPointsInType(clazz, ((ReflectionFastMatchInfo)info).getMatchingContext()));
        }
        throw new IllegalStateException("Can only match user-extension pcds against Reflection FastMatchInfo objects");
    }

    @Override
    public int couldMatchKinds() {
        return Shadow.ALL_SHADOW_KINDS_BITS;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        if (shadow instanceof ReflectionShadow) {
            MatchingContext context = ((ReflectionShadow)shadow).getMatchingContext();
            org.aspectj.weaver.tools.FuzzyBoolean match = this.matcher.matchesStatically(context);
            if (match == org.aspectj.weaver.tools.FuzzyBoolean.MAYBE) {
                return FuzzyBoolean.MAYBE;
            }
            if (match == org.aspectj.weaver.tools.FuzzyBoolean.YES) {
                return FuzzyBoolean.YES;
            }
            if (match == org.aspectj.weaver.tools.FuzzyBoolean.NO) {
                return FuzzyBoolean.NO;
            }
        }
        throw new IllegalStateException("Can only match user-extension pcds against Reflection shadows (not BCEL)");
    }

    @Override
    protected void resolveBindings(IScope scope, Bindings bindings) {
    }

    @Override
    protected Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        return this;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        if (!this.matcher.mayNeedDynamicTest()) {
            return Literal.TRUE;
        }
        this.matchInternal(shadow);
        return new MatchingContextBasedTest(this.matcher);
    }

    public Pointcut parameterizeWith(Map typeVariableMap, World w) {
        return this;
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        throw new UnsupportedOperationException("can't write custom pointcut designator expressions to stream");
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return data;
    }
}

