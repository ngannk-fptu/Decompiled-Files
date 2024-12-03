/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.patterns;

import org.aspectj.weaver.patterns.AndAnnotationTypePattern;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.AndTypePattern;
import org.aspectj.weaver.patterns.AnnotationPatternList;
import org.aspectj.weaver.patterns.AnnotationPointcut;
import org.aspectj.weaver.patterns.AnyAnnotationTypePattern;
import org.aspectj.weaver.patterns.AnyTypePattern;
import org.aspectj.weaver.patterns.AnyWithAnnotationTypePattern;
import org.aspectj.weaver.patterns.ArgsAnnotationPointcut;
import org.aspectj.weaver.patterns.ArgsPointcut;
import org.aspectj.weaver.patterns.BindingAnnotationTypePattern;
import org.aspectj.weaver.patterns.BindingTypePattern;
import org.aspectj.weaver.patterns.CflowPointcut;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.DeclareErrorOrWarning;
import org.aspectj.weaver.patterns.DeclareParents;
import org.aspectj.weaver.patterns.DeclarePrecedence;
import org.aspectj.weaver.patterns.DeclareSoft;
import org.aspectj.weaver.patterns.EllipsisAnnotationTypePattern;
import org.aspectj.weaver.patterns.EllipsisTypePattern;
import org.aspectj.weaver.patterns.ExactAnnotationTypePattern;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.aspectj.weaver.patterns.HandlerPointcut;
import org.aspectj.weaver.patterns.HasMemberTypePattern;
import org.aspectj.weaver.patterns.IfPointcut;
import org.aspectj.weaver.patterns.KindedPointcut;
import org.aspectj.weaver.patterns.ModifiersPattern;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.patterns.NoTypePattern;
import org.aspectj.weaver.patterns.NotAnnotationTypePattern;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.NotTypePattern;
import org.aspectj.weaver.patterns.OrAnnotationTypePattern;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.OrTypePattern;
import org.aspectj.weaver.patterns.PatternNode;
import org.aspectj.weaver.patterns.PatternNodeVisitor;
import org.aspectj.weaver.patterns.PerCflow;
import org.aspectj.weaver.patterns.PerFromSuper;
import org.aspectj.weaver.patterns.PerObject;
import org.aspectj.weaver.patterns.PerSingleton;
import org.aspectj.weaver.patterns.PerTypeWithin;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.ReferencePointcut;
import org.aspectj.weaver.patterns.SignaturePattern;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.aspectj.weaver.patterns.ThisOrTargetPointcut;
import org.aspectj.weaver.patterns.ThrowsPattern;
import org.aspectj.weaver.patterns.TypeCategoryTypePattern;
import org.aspectj.weaver.patterns.TypePatternList;
import org.aspectj.weaver.patterns.TypeVariablePattern;
import org.aspectj.weaver.patterns.TypeVariablePatternList;
import org.aspectj.weaver.patterns.WildAnnotationTypePattern;
import org.aspectj.weaver.patterns.WildTypePattern;
import org.aspectj.weaver.patterns.WithinAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinCodeAnnotationPointcut;
import org.aspectj.weaver.patterns.WithinPointcut;
import org.aspectj.weaver.patterns.WithincodePointcut;

public abstract class AbstractPatternNodeVisitor
implements PatternNodeVisitor {
    @Override
    public Object visit(AnyTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(NoTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(EllipsisTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(AnyWithAnnotationTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(AnyAnnotationTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(EllipsisAnnotationTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(AndAnnotationTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(AndPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(AndTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(AnnotationPatternList node, Object data) {
        return node;
    }

    @Override
    public Object visit(AnnotationPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(ArgsAnnotationPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(ArgsPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(BindingAnnotationTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(BindingTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(CflowPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(ConcreteCflowPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(DeclareAnnotation node, Object data) {
        return node;
    }

    @Override
    public Object visit(DeclareErrorOrWarning node, Object data) {
        return node;
    }

    @Override
    public Object visit(DeclareParents node, Object data) {
        return node;
    }

    @Override
    public Object visit(DeclarePrecedence node, Object data) {
        return node;
    }

    @Override
    public Object visit(DeclareSoft node, Object data) {
        return node;
    }

    @Override
    public Object visit(ExactAnnotationTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(ExactTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(HandlerPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(IfPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(KindedPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(ModifiersPattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(NamePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(NotAnnotationTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(NotPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(NotTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(OrAnnotationTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(OrPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(OrTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(PerCflow node, Object data) {
        return node;
    }

    @Override
    public Object visit(PerFromSuper node, Object data) {
        return node;
    }

    @Override
    public Object visit(PerObject node, Object data) {
        return node;
    }

    @Override
    public Object visit(PerSingleton node, Object data) {
        return node;
    }

    @Override
    public Object visit(PerTypeWithin node, Object data) {
        return node;
    }

    @Override
    public Object visit(PatternNode node, Object data) {
        return node;
    }

    @Override
    public Object visit(ReferencePointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(SignaturePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(ThisOrTargetAnnotationPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(ThisOrTargetPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(ThrowsPattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(TypePatternList node, Object data) {
        return node;
    }

    @Override
    public Object visit(WildAnnotationTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(WildTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(WithinAnnotationPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(WithinCodeAnnotationPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(WithinPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(WithincodePointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(Pointcut.MatchesNothingPointcut node, Object data) {
        return node;
    }

    @Override
    public Object visit(TypeVariablePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(TypeVariablePatternList node, Object data) {
        return node;
    }

    @Override
    public Object visit(HasMemberTypePattern node, Object data) {
        return node;
    }

    @Override
    public Object visit(TypeCategoryTypePattern node, Object data) {
        return node;
    }
}

