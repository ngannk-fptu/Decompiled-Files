/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.Map;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePattern;

public class HandlerPointcut
extends Pointcut {
    TypePattern exceptionType;
    private static final int MATCH_KINDS = Shadow.ExceptionHandler.bit;

    public HandlerPointcut(TypePattern exceptionType) {
        this.exceptionType = exceptionType;
        this.pointcutKind = (byte)13;
    }

    @Override
    public int couldMatchKinds() {
        return MATCH_KINDS;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo type) {
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        if (shadow.getKind() != Shadow.ExceptionHandler) {
            return FuzzyBoolean.NO;
        }
        this.exceptionType.resolve(shadow.getIWorld());
        return this.exceptionType.matches(shadow.getSignature().getParameterTypes()[0].resolve(shadow.getIWorld()), TypePattern.STATIC);
    }

    public Pointcut parameterizeWith(Map typeVariableMap, World w) {
        HandlerPointcut ret = new HandlerPointcut(this.exceptionType.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    public boolean equals(Object other) {
        if (!(other instanceof HandlerPointcut)) {
            return false;
        }
        HandlerPointcut o = (HandlerPointcut)other;
        return o.exceptionType.equals(this.exceptionType);
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.exceptionType.hashCode();
        return result;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("handler(");
        buf.append(this.exceptionType.toString());
        buf.append(")");
        return buf.toString();
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(13);
        this.exceptionType.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        HandlerPointcut ret = new HandlerPointcut(TypePattern.read(s, context));
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        UnresolvedType exactType;
        this.exceptionType = this.exceptionType.resolveBindings(scope, bindings, false, false);
        boolean invalidParameterization = false;
        if (this.exceptionType.getTypeParameters().size() > 0) {
            invalidParameterization = true;
        }
        if ((exactType = this.exceptionType.getExactType()) != null && exactType.isParameterizedType()) {
            invalidParameterization = true;
        }
        if (invalidParameterization) {
            scope.message(MessageUtil.error(WeaverMessages.format("noParameterizedTypePatternInHandler"), this.getSourceLocation()));
        }
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        return this.match(shadow).alwaysTrue() ? Literal.TRUE : Literal.FALSE;
    }

    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        HandlerPointcut ret = new HandlerPointcut(this.exceptionType);
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

