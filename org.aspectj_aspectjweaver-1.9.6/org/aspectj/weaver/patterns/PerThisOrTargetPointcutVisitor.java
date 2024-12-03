/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.weaver.BCException;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.patterns.AbstractPatternNodeVisitor;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.AndTypePattern;
import org.aspectj.weaver.patterns.AnnotationPointcut;
import org.aspectj.weaver.patterns.AnyTypePattern;
import org.aspectj.weaver.patterns.AnyWithAnnotationTypePattern;
import org.aspectj.weaver.patterns.ArgsAnnotationPointcut;
import org.aspectj.weaver.patterns.ArgsPointcut;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.aspectj.weaver.patterns.HandlerPointcut;
import org.aspectj.weaver.patterns.HasMemberTypePatternForPerThisMatching;
import org.aspectj.weaver.patterns.IfPointcut;
import org.aspectj.weaver.patterns.KindedPointcut;
import org.aspectj.weaver.patterns.NoTypePattern;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.OrTypePattern;
import org.aspectj.weaver.patterns.ParserException;
import org.aspectj.weaver.patterns.PatternParser;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.aspectj.weaver.patterns.SignaturePattern;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.aspectj.weaver.patterns.TypePattern;
import org.aspectj.weaver.patterns.WithinAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinCodeAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinPointcut;
import org.aspectj.weaver.patterns.WithincodePointcut;

public class PerThisOrTargetPointcutVisitor
extends AbstractPatternNodeVisitor {
    private static final TypePattern MAYBE = new TypePatternMayBe();
    private final boolean m_isTarget;
    private final ResolvedType m_fromAspectType;

    public PerThisOrTargetPointcutVisitor(boolean isTarget, ResolvedType fromAspectType) {
        this.m_isTarget = isTarget;
        this.m_fromAspectType = fromAspectType;
    }

    public TypePattern getPerTypePointcut(Pointcut perClausePointcut) {
        Object o = perClausePointcut.accept(this, perClausePointcut);
        if (o instanceof TypePattern) {
            return (TypePattern)o;
        }
        throw new BCException("perClausePointcut visitor did not return a typepattern, it returned " + o + (o == null ? "" : " of type " + o.getClass()));
    }

    @Override
    public Object visit(WithinPointcut node, Object data) {
        if (this.m_isTarget) {
            return MAYBE;
        }
        return node.getTypePattern();
    }

    @Override
    public Object visit(WithincodePointcut node, Object data) {
        if (this.m_isTarget) {
            return MAYBE;
        }
        return node.getSignature().getDeclaringType();
    }

    @Override
    public Object visit(WithinAnnotationPointcut node, Object data) {
        if (this.m_isTarget) {
            return MAYBE;
        }
        return new AnyWithAnnotationTypePattern(node.getAnnotationTypePattern());
    }

    @Override
    public Object visit(WithinCodeAnnotationPointcut node, Object data) {
        if (this.m_isTarget) {
            return MAYBE;
        }
        return MAYBE;
    }

    @Override
    public Object visit(KindedPointcut node, Object data) {
        if (node.getKind().equals(Shadow.AdviceExecution)) {
            return MAYBE;
        }
        if (node.getKind().equals(Shadow.ConstructorExecution) || node.getKind().equals(Shadow.Initialization) || node.getKind().equals(Shadow.MethodExecution) || node.getKind().equals(Shadow.PreInitialization) || node.getKind().equals(Shadow.StaticInitialization)) {
            SignaturePattern signaturePattern = node.getSignature();
            boolean isStarAnnotation = signaturePattern.isStarAnnotation();
            if (!this.m_isTarget && node.getKind().equals(Shadow.MethodExecution) && !isStarAnnotation) {
                return new HasMemberTypePatternForPerThisMatching(signaturePattern);
            }
            return signaturePattern.getDeclaringType();
        }
        if (node.getKind().equals(Shadow.ConstructorCall) || node.getKind().equals(Shadow.FieldGet) || node.getKind().equals(Shadow.FieldSet) || node.getKind().equals(Shadow.MethodCall)) {
            if (this.m_isTarget) {
                return node.getSignature().getDeclaringType();
            }
            return MAYBE;
        }
        if (node.getKind().equals(Shadow.ExceptionHandler)) {
            return MAYBE;
        }
        throw new ParserException("Undetermined - should not happen: " + node.getKind().getSimpleName(), null);
    }

    @Override
    public Object visit(AndPointcut node, Object data) {
        return new AndTypePattern(this.getPerTypePointcut(node.left), this.getPerTypePointcut(node.right));
    }

    @Override
    public Object visit(OrPointcut node, Object data) {
        return new OrTypePattern(this.getPerTypePointcut(node.left), this.getPerTypePointcut(node.right));
    }

    @Override
    public Object visit(NotPointcut node, Object data) {
        return MAYBE;
    }

    @Override
    public Object visit(ThisOrTargetAnnotationPointcut node, Object data) {
        if (this.m_isTarget && !node.isThis()) {
            return new AnyWithAnnotationTypePattern(node.getAnnotationTypePattern());
        }
        if (!this.m_isTarget && node.isThis()) {
            return new AnyWithAnnotationTypePattern(node.getAnnotationTypePattern());
        }
        return MAYBE;
    }

    @Override
    public Object visit(ThisOrTargetPointcut node, Object data) {
        if (this.m_isTarget && !node.isThis() || !this.m_isTarget && node.isThis()) {
            String pointcutString = node.getType().toString();
            if (pointcutString.equals("<nothing>")) {
                return new NoTypePattern();
            }
            TypePattern copy = new PatternParser(pointcutString.replace('$', '.')).parseTypePattern();
            copy.includeSubtypes = true;
            return copy;
        }
        return MAYBE;
    }

    @Override
    public Object visit(ReferencePointcut node, Object data) {
        ResolvedType searchStart = this.m_fromAspectType;
        if (node.onType != null && (searchStart = node.onType.resolve(this.m_fromAspectType.getWorld())).isMissing()) {
            return MAYBE;
        }
        ResolvedPointcutDefinition pointcutDec = searchStart.findPointcut(node.name);
        return this.getPerTypePointcut(pointcutDec.getPointcut());
    }

    @Override
    public Object visit(IfPointcut node, Object data) {
        return TypePattern.ANY;
    }

    @Override
    public Object visit(HandlerPointcut node, Object data) {
        return MAYBE;
    }

    @Override
    public Object visit(CflowPointcut node, Object data) {
        return MAYBE;
    }

    @Override
    public Object visit(ConcreteCflowPointcut node, Object data) {
        return MAYBE;
    }

    @Override
    public Object visit(ArgsPointcut node, Object data) {
        return MAYBE;
    }

    @Override
    public Object visit(ArgsAnnotationPointcut node, Object data) {
        return MAYBE;
    }

    @Override
    public Object visit(AnnotationPointcut node, Object data) {
        return MAYBE;
    }

    @Override
    public Object visit(Pointcut.MatchesNothingPointcut node, Object data) {
        return new NoTypePattern(){

            @Override
            public String toString() {
                return "false";
            }
        };
    }

    private static class TypePatternMayBe
    extends AnyTypePattern {
        private TypePatternMayBe() {
        }
    }
}

