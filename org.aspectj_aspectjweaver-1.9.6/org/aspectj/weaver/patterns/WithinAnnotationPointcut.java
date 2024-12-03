/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
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

public class WithinAnnotationPointcut
extends NameBindingPointcut {
    private AnnotationTypePattern annotationTypePattern;
    private String declarationText;

    public WithinAnnotationPointcut(AnnotationTypePattern type) {
        this.annotationTypePattern = type;
        this.pointcutKind = (byte)17;
        this.buildDeclarationText();
    }

    public WithinAnnotationPointcut(AnnotationTypePattern type, ShadowMunger munger) {
        this(type);
        this.pointcutKind = (byte)17;
    }

    public AnnotationTypePattern getAnnotationTypePattern() {
        return this.annotationTypePattern;
    }

    @Override
    public int couldMatchKinds() {
        return Shadow.ALL_SHADOW_KINDS_BITS;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        WithinAnnotationPointcut ret = new WithinAnnotationPointcut(this.annotationTypePattern.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo info) {
        return this.annotationTypePattern.fastMatches(info.getType());
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        ResolvedType enclosingType = shadow.getIWorld().resolve(shadow.getEnclosingType(), true);
        if (enclosingType.isMissing()) {
            shadow.getIWorld().getLint().cantFindType.signal(new String[]{WeaverMessages.format("cantFindTypeWithinpcd", shadow.getEnclosingType().getName())}, shadow.getSourceLocation(), new ISourceLocation[]{this.getSourceLocation()});
        }
        this.annotationTypePattern.resolve(shadow.getIWorld());
        return this.annotationTypePattern.matches(enclosingType);
    }

    @Override
    protected void resolveBindings(IScope scope, Bindings bindings) {
        if (!scope.getWorld().isInJava5Mode()) {
            scope.message(MessageUtil.error(WeaverMessages.format("atwithinNeedsJava5"), this.getSourceLocation()));
            return;
        }
        this.annotationTypePattern = this.annotationTypePattern.resolveBindings(scope, bindings, true);
    }

    @Override
    protected Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        ExactAnnotationTypePattern newType = (ExactAnnotationTypePattern)this.annotationTypePattern.remapAdviceFormals(bindings);
        WithinAnnotationPointcut ret = new WithinAnnotationPointcut(newType, bindings.getEnclosingAdvice());
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        if (this.annotationTypePattern instanceof BindingAnnotationTypePattern) {
            BindingAnnotationTypePattern btp = (BindingAnnotationTypePattern)this.annotationTypePattern;
            UnresolvedType annotationType = btp.annotationType;
            Var var = shadow.getWithinAnnotationVar(annotationType);
            if (var == null) {
                throw new BCException("Impossible! annotation=[" + annotationType + "]  shadow=[" + shadow + " at " + shadow.getSourceLocation() + "]    pointcut is at [" + this.getSourceLocation() + "]");
            }
            state.set(btp.getFormalIndex(), var);
        }
        return this.match(shadow).alwaysTrue() ? Literal.TRUE : Literal.FALSE;
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
        s.writeByte(17);
        this.annotationTypePattern.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        AnnotationTypePattern type = AnnotationTypePattern.read(s, context);
        WithinAnnotationPointcut ret = new WithinAnnotationPointcut(type);
        ret.readLocation(context, s);
        return ret;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WithinAnnotationPointcut)) {
            return false;
        }
        WithinAnnotationPointcut other = (WithinAnnotationPointcut)obj;
        return other.annotationTypePattern.equals(this.annotationTypePattern);
    }

    public int hashCode() {
        return 17 + 19 * this.annotationTypePattern.hashCode();
    }

    private void buildDeclarationText() {
        StringBuffer buf = new StringBuffer();
        buf.append("@within(");
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
}

