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

public interface PatternNodeVisitor {
    public Object visit(AndAnnotationTypePattern var1, Object var2);

    public Object visit(AnyAnnotationTypePattern var1, Object var2);

    public Object visit(EllipsisAnnotationTypePattern var1, Object var2);

    public Object visit(ExactAnnotationTypePattern var1, Object var2);

    public Object visit(BindingAnnotationTypePattern var1, Object var2);

    public Object visit(NotAnnotationTypePattern var1, Object var2);

    public Object visit(OrAnnotationTypePattern var1, Object var2);

    public Object visit(WildAnnotationTypePattern var1, Object var2);

    public Object visit(AnnotationPatternList var1, Object var2);

    public Object visit(AndTypePattern var1, Object var2);

    public Object visit(AnyTypePattern var1, Object var2);

    public Object visit(AnyWithAnnotationTypePattern var1, Object var2);

    public Object visit(EllipsisTypePattern var1, Object var2);

    public Object visit(ExactTypePattern var1, Object var2);

    public Object visit(BindingTypePattern var1, Object var2);

    public Object visit(NotTypePattern var1, Object var2);

    public Object visit(NoTypePattern var1, Object var2);

    public Object visit(OrTypePattern var1, Object var2);

    public Object visit(WildTypePattern var1, Object var2);

    public Object visit(TypePatternList var1, Object var2);

    public Object visit(HasMemberTypePattern var1, Object var2);

    public Object visit(TypeCategoryTypePattern var1, Object var2);

    public Object visit(AndPointcut var1, Object var2);

    public Object visit(CflowPointcut var1, Object var2);

    public Object visit(ConcreteCflowPointcut var1, Object var2);

    public Object visit(HandlerPointcut var1, Object var2);

    public Object visit(IfPointcut var1, Object var2);

    public Object visit(KindedPointcut var1, Object var2);

    public Object visit(Pointcut.MatchesNothingPointcut var1, Object var2);

    public Object visit(AnnotationPointcut var1, Object var2);

    public Object visit(ArgsAnnotationPointcut var1, Object var2);

    public Object visit(ArgsPointcut var1, Object var2);

    public Object visit(ThisOrTargetAnnotationPointcut var1, Object var2);

    public Object visit(ThisOrTargetPointcut var1, Object var2);

    public Object visit(WithinAnnotationPointcut var1, Object var2);

    public Object visit(WithinCodeAnnotationPointcut var1, Object var2);

    public Object visit(NotPointcut var1, Object var2);

    public Object visit(OrPointcut var1, Object var2);

    public Object visit(ReferencePointcut var1, Object var2);

    public Object visit(WithinPointcut var1, Object var2);

    public Object visit(WithincodePointcut var1, Object var2);

    public Object visit(PerCflow var1, Object var2);

    public Object visit(PerFromSuper var1, Object var2);

    public Object visit(PerObject var1, Object var2);

    public Object visit(PerSingleton var1, Object var2);

    public Object visit(PerTypeWithin var1, Object var2);

    public Object visit(DeclareAnnotation var1, Object var2);

    public Object visit(DeclareErrorOrWarning var1, Object var2);

    public Object visit(DeclareParents var1, Object var2);

    public Object visit(DeclarePrecedence var1, Object var2);

    public Object visit(DeclareSoft var1, Object var2);

    public Object visit(ModifiersPattern var1, Object var2);

    public Object visit(NamePattern var1, Object var2);

    public Object visit(SignaturePattern var1, Object var2);

    public Object visit(ThrowsPattern var1, Object var2);

    public Object visit(TypeVariablePattern var1, Object var2);

    public Object visit(TypeVariablePatternList var1, Object var2);

    public Object visit(PatternNode var1, Object var2);
}

