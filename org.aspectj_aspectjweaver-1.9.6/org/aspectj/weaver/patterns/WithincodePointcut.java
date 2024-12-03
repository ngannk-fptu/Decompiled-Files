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
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.SignaturePattern;

public class WithincodePointcut
extends Pointcut {
    private SignaturePattern signature;
    private static final int matchedShadowKinds;

    public WithincodePointcut(SignaturePattern signature) {
        this.signature = signature;
        this.pointcutKind = (byte)12;
    }

    public SignaturePattern getSignature() {
        return this.signature;
    }

    @Override
    public int couldMatchKinds() {
        return matchedShadowKinds;
    }

    public Pointcut parameterizeWith(Map typeVariableMap, World w) {
        WithincodePointcut ret = new WithincodePointcut((SignaturePattern)this.signature.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo type) {
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        return FuzzyBoolean.fromBoolean(this.signature.matches(shadow.getEnclosingCodeSignature(), shadow.getIWorld(), false));
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(12);
        this.signature.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        WithincodePointcut ret = new WithincodePointcut(SignaturePattern.read(s, context));
        ret.readLocation(context, s);
        return ret;
    }

    @Override
    public void resolveBindings(IScope scope, Bindings bindings) {
        this.signature = this.signature.resolveBindings(scope, bindings);
        HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
        this.signature.getDeclaringType().traverse(visitor, null);
        if (visitor.wellHasItThen()) {
            scope.message(MessageUtil.error(WeaverMessages.format("noParameterizedDeclaringTypesWithinCode"), this.getSourceLocation()));
        }
        visitor = new HasThisTypePatternTriedToSneakInSomeGenericOrParameterizedTypePatternMatchingStuffAnywhereVisitor();
        this.signature.getThrowsPattern().traverse(visitor, null);
        if (visitor.wellHasItThen()) {
            scope.message(MessageUtil.error(WeaverMessages.format("noGenericThrowables"), this.getSourceLocation()));
        }
    }

    @Override
    public void postRead(ResolvedType enclosingType) {
        this.signature.postRead(enclosingType);
    }

    public boolean equals(Object other) {
        if (!(other instanceof WithincodePointcut)) {
            return false;
        }
        WithincodePointcut o = (WithincodePointcut)other;
        return o.signature.equals(this.signature);
    }

    public int hashCode() {
        int result = 43;
        result = 37 * result + this.signature.hashCode();
        return result;
    }

    public String toString() {
        return "withincode(" + this.signature + ")";
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        return this.match(shadow).alwaysTrue() ? Literal.TRUE : Literal.FALSE;
    }

    @Override
    public Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        WithincodePointcut ret = new WithincodePointcut(this.signature);
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public Object accept(PatternNodeVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    static {
        int flags = Shadow.ALL_SHADOW_KINDS_BITS;
        for (int i = 0; i < Shadow.SHADOW_KINDS.length; ++i) {
            if (!Shadow.SHADOW_KINDS[i].isEnclosingKind()) continue;
            flags -= Shadow.SHADOW_KINDS[i].bit;
        }
        flags |= Shadow.ConstructorExecution.bit;
        matchedShadowKinds = flags |= Shadow.Initialization.bit;
    }
}

