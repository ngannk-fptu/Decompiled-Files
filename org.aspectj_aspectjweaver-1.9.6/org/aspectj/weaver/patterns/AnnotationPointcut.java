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
import org.aspectj.weaver.AjcMemberMaker;
import org.aspectj.weaver.AnnotatedElement;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.ISourceContext;
import org.aspectj.weaver.IntMap;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.NewFieldTypeMunger;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.TypeVariableDeclaringElement;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.VersionedDataInputStream;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.World;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.patterns.AnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingAnnotationFieldTypePattern;
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

public class AnnotationPointcut
extends NameBindingPointcut {
    private ExactAnnotationTypePattern annotationTypePattern;
    private String declarationText;

    public AnnotationPointcut(ExactAnnotationTypePattern type) {
        this.annotationTypePattern = type;
        this.pointcutKind = (byte)16;
        this.buildDeclarationText();
    }

    public AnnotationPointcut(ExactAnnotationTypePattern type, ShadowMunger munger) {
        this(type);
        this.buildDeclarationText();
    }

    public ExactAnnotationTypePattern getAnnotationTypePattern() {
        return this.annotationTypePattern;
    }

    @Override
    public int couldMatchKinds() {
        return Shadow.ALL_SHADOW_KINDS_BITS;
    }

    @Override
    public Pointcut parameterizeWith(Map<String, UnresolvedType> typeVariableMap, World w) {
        AnnotationPointcut ret = new AnnotationPointcut((ExactAnnotationTypePattern)this.annotationTypePattern.parameterizeWith(typeVariableMap, w));
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    public FuzzyBoolean fastMatch(FastMatchInfo info) {
        if (info.getKind() == Shadow.StaticInitialization) {
            return this.annotationTypePattern.fastMatches(info.getType());
        }
        return FuzzyBoolean.MAYBE;
    }

    @Override
    protected FuzzyBoolean matchInternal(Shadow shadow) {
        TypeVariableDeclaringElement toMatchAgainst = null;
        Member member = shadow.getSignature();
        ResolvedMember rMember = member.resolve(shadow.getIWorld());
        if (rMember == null) {
            if (member.getName().startsWith("ajc$")) {
                return FuzzyBoolean.NO;
            }
            shadow.getIWorld().getLint().unresolvableMember.signal(member.toString(), this.getSourceLocation());
            return FuzzyBoolean.NO;
        }
        Shadow.Kind kind = shadow.getKind();
        if (kind == Shadow.StaticInitialization) {
            toMatchAgainst = rMember.getDeclaringType().resolve(shadow.getIWorld());
        } else if (kind == Shadow.ExceptionHandler) {
            toMatchAgainst = rMember.getParameterTypes()[0].resolve(shadow.getIWorld());
        } else {
            toMatchAgainst = rMember;
            if (rMember.isAnnotatedElsewhere() && (kind == Shadow.FieldGet || kind == Shadow.FieldSet)) {
                List<ConcreteTypeMunger> mungers = rMember.getDeclaringType().resolve(shadow.getIWorld()).getInterTypeMungers();
                for (ConcreteTypeMunger typeMunger : mungers) {
                    ResolvedMember rmm;
                    ResolvedMember fakerm;
                    if (!(typeMunger.getMunger() instanceof NewFieldTypeMunger) || !(fakerm = typeMunger.getSignature()).equals(member)) continue;
                    ResolvedMember ajcMethod = AjcMemberMaker.interFieldInitializer(fakerm, typeMunger.getAspectType());
                    toMatchAgainst = rmm = this.findMethod(typeMunger.getAspectType(), ajcMethod);
                }
            }
        }
        this.annotationTypePattern.resolve(shadow.getIWorld());
        return this.annotationTypePattern.matches((AnnotatedElement)((Object)toMatchAgainst));
    }

    private ResolvedMember findMethod(ResolvedType aspectType, ResolvedMember ajcMethod) {
        ResolvedMember[] decMethods = aspectType.getDeclaredMethods();
        for (int i = 0; i < decMethods.length; ++i) {
            ResolvedMember member = decMethods[i];
            if (!member.equals(ajcMethod)) continue;
            return member;
        }
        return null;
    }

    @Override
    protected void resolveBindings(IScope scope, Bindings bindings) {
        if (!scope.getWorld().isInJava5Mode()) {
            scope.message(MessageUtil.error(WeaverMessages.format("atannotationNeedsJava5"), this.getSourceLocation()));
            return;
        }
        this.annotationTypePattern = (ExactAnnotationTypePattern)this.annotationTypePattern.resolveBindings(scope, bindings, true);
    }

    @Override
    protected Pointcut concretize1(ResolvedType inAspect, ResolvedType declaringType, IntMap bindings) {
        ExactAnnotationTypePattern newType = (ExactAnnotationTypePattern)this.annotationTypePattern.remapAdviceFormals(bindings);
        AnnotationPointcut ret = new AnnotationPointcut(newType, bindings.getEnclosingAdvice());
        ret.copyLocationFrom(this);
        return ret;
    }

    @Override
    protected Test findResidueInternal(Shadow shadow, ExposedState state) {
        if (this.annotationTypePattern instanceof BindingAnnotationFieldTypePattern) {
            if (shadow.getKind() != Shadow.MethodExecution) {
                shadow.getIWorld().getMessageHandler().handleMessage(MessageUtil.error("Annotation field binding is only supported at method-execution join points (compiler limitation)", this.getSourceLocation()));
                return Literal.TRUE;
            }
            BindingAnnotationFieldTypePattern btp = (BindingAnnotationFieldTypePattern)this.annotationTypePattern;
            ResolvedType formalType = btp.getFormalType().resolve(shadow.getIWorld());
            UnresolvedType annoType = btp.getAnnotationType();
            Var var = shadow.getKindedAnnotationVar(annoType);
            if (var == null) {
                throw new BCException("Unexpected problem locating annotation at join point '" + shadow + "'");
            }
            state.set(btp.getFormalIndex(), var.getAccessorForValue(formalType, btp.formalName));
        } else if (this.annotationTypePattern instanceof BindingAnnotationTypePattern) {
            BindingAnnotationTypePattern btp = (BindingAnnotationTypePattern)this.annotationTypePattern;
            UnresolvedType annotationType = btp.getAnnotationType();
            Var var = shadow.getKindedAnnotationVar(annotationType);
            if (var == null) {
                if (this.matchInternal(shadow).alwaysTrue()) {
                    return Literal.TRUE;
                }
                return Literal.FALSE;
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
        if (this.annotationTypePattern instanceof BindingPattern) {
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
        s.writeByte(16);
        this.annotationTypePattern.write(s);
        this.writeLocation(s);
    }

    public static Pointcut read(VersionedDataInputStream s, ISourceContext context) throws IOException {
        AnnotationTypePattern type = AnnotationTypePattern.read(s, context);
        AnnotationPointcut ret = new AnnotationPointcut((ExactAnnotationTypePattern)type);
        ret.readLocation(context, s);
        return ret;
    }

    public boolean equals(Object other) {
        if (!(other instanceof AnnotationPointcut)) {
            return false;
        }
        AnnotationPointcut o = (AnnotationPointcut)other;
        return o.annotationTypePattern.equals(this.annotationTypePattern);
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.annotationTypePattern.hashCode();
        return result;
    }

    public void buildDeclarationText() {
        StringBuffer buf = new StringBuffer();
        buf.append("@annotation(");
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

