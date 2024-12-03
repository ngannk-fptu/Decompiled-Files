/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingAnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingPattern;
import org.aspectj.weaver.patterns.BindingTypePattern;
import org.aspectj.weaver.patterns.Bindings;
import org.aspectj.weaver.patterns.ExactAnnotationTypePattern;
import org.aspectj.weaver.patterns.ExposedState;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IScope;
import org.aspectj.weaver.patterns.NameBindingPointcut;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.Pointcut;

public class WithinCodeAnnotationPointcut
extends NameBindingPointcut {
    private ExactAnnotationTypePattern annotationTypePattern;
    private String declarationText;
    private static final int matchedShadowKinds;

    public WithinCodeAnnotationPointcut(ExactAnnotationTypePattern type) {
        this.annotationTypePattern = type;
        this.pointcutKind = (byte)18;
        this.buildDeclarationText();
    }

    public WithinCodeAnnotationPointcut(ExactAnnotationTypePattern type, ShadowMunger munger) {
        this(type);
        this.pointcutKind = (byte)18;
    }

    public ExactAnnotationTypePattern getAnnotationTypePattern() {
        return this.annotationTypePattern;
    }

    @Override
    public int couldMatchKinds() {
        return matchedShadowKinds;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        WithinCodeAnnotationPointcut ret = new WithinCodeAnnotationPointcut((ExactAnnotationTypePattern)this.annotationTypePattern.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo info) {
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        Member member = shadow.getEnclosingCodeSignature();
        ResolvedMember rMember = member.resolve(shadow.getIWorld());
        if (rMember == null) {
            if (member.getName().startsWith("ajc$")) {
                return FuzzyBoolean.NO;
            }
            shadow.getIWorld().getLint().unresolvableMember.signal(member.toString(), this.getSourceLocation());
            return FuzzyBoolean.NO;
        }
        this.annotationTypePattern.resolve(shadow.getIWorld());
        return this.annotationTypePattern.matches(rMember);
    }

    @Override
    protected void resolveBindings(IScope scope, Bindings bindings) {
        if (!scope.getWorld().isInJava5Mode()) {
            scope.message(MessageUtil.error(WeaverMessages.format("atwithincodeNeedsJava5"), this.getSourceLocation()));
            return;
        }
        this.annotationTypePattern = (ExactAnnotationTypePattern)this.annotationTypePattern.resolveBindings(scope, bindings, true);
    }

    @Override
    protected Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        ExactAnnotationTypePattern newType = (ExactAnnotationTypePattern)this.annotationTypePattern.remapAdviceFormals(bindings);
        WithinCodeAnnotationPointcut ret = new WithinCodeAnnotationPointcut(newType, bindings.getEnclosingAdvice());
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        if (this.annotationTypePattern instanceof BindingAnnotationTypePattern) {
            BindingAnnotationTypePattern btp = (BindingAnnotationTypePattern)this.annotationTypePattern;
            UnresolvedType annotationType = btp.annotationType;
            Var var = shadow.getWithinCodeAnnotationVar(annotationType);
            if (var == null) {
                throw new BCException("Impossible! annotation=[" + annotationType + "]  shadow=[" + shadow + " at " + shadow.getSourceLocation() + "]    pointcut is at [" + this.getSourceLocation() + "]");
            }
            state.set(btp.getFormalIndex(), var);
        }
        if (this.matchInternal(shadow).alwaysTrue()) {
            return Literal.TRUE;
        }
        return Literal.FALSE;
    }

    @Override
    public List<BindingPattern> getBindingAnnotationTypePatterns() {
        if (this.annotationTypePattern instanceof BindingAnnotationTypePattern) {
            ArrayList<BindingPattern> l = new ArrayList<BindingPattern>();
            l.add((BindingPattern)((Object)this.annotationTypePattern));
            return l;
        }
        return Collections.emptyList();
    }

    @Override
    public List<BindingTypePattern> getBindingTypePatterns() {
        return Collections.emptyList();
    }

    @Override
    public void write(CompressingDataOutputStream s) throws IOException {
        s.writeByte(18);
        this.annotationTypePattern.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        AnnotationTypePattern type = AnnotationTypePattern.read(s, context);
        WithinCodeAnnotationPointcut ret = new WithinCodeAnnotationPointcut((ExactAnnotationTypePattern)type);
        ret.readLocation(context, s);
        return ret;
    }

    public boolean equals(Object other) {
        if (!(other instanceof WithinCodeAnnotationPointcut)) {
            return false;
        }
        WithinCodeAnnotationPointcut o = (WithinCodeAnnotationPointcut)other;
        return o.annotationTypePattern.equals(this.annotationTypePattern);
    }

    public int hashCode() {
        int result = 17;
        result = 23 * result + this.annotationTypePattern.hashCode();
        return result;
    }

    private void buildDeclarationText() {
        StringBuffer buf = new StringBuffer();
        buf.append("@withincode(");
        String annPatt = this.annotationTypePattern.toString();
        buf.append(annPatt.startsWith("@") ? annPatt.substring(1) : annPatt);
        buf.append(")");
        this.declarationText = buf.toString();
    }

    public String toString() {
        return this.declarationText;
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
        matchedShadowKinds = flags;
    }
}

