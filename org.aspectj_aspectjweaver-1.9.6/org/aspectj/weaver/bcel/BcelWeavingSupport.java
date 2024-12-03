/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.IWeavingSupport;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.bcel.BcelAccessForInlineMunger;
import org.aspectj.weaver.bcel.BcelAdvice;
import org.aspectj.weaver.bcel.BcelCflowAccessVar;
import org.aspectj.weaver.bcel.BcelCflowCounterFieldAdder;
import org.aspectj.weaver.bcel.BcelCflowStackFieldAdder;
import org.aspectj.weaver.bcel.BcelPerClauseAspectAdder;
import org.aspectj.weaver.bcel.BcelTypeMunger;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;

public class BcelWeavingSupport
implements IWeavingSupport {
    @Override
    public Advice createAdviceMunger(AjAttribute.AdviceAttribute attribute, Pointcut pointcut, Member signature, ResolvedType concreteAspect) {
        return new BcelAdvice(attribute, pointcut, signature, concreteAspect);
    }

    @Override
    public ConcreteTypeMunger makeCflowStackFieldAdder(ResolvedMember cflowField) {
        return new BcelCflowStackFieldAdder(cflowField);
    }

    @Override
    public ConcreteTypeMunger makeCflowCounterFieldAdder(ResolvedMember cflowField) {
        return new BcelCflowCounterFieldAdder(cflowField);
    }

    @Override
    public ConcreteTypeMunger makePerClauseAspect(ResolvedType aspect, PerClause.Kind kind) {
        return new BcelPerClauseAspectAdder(aspect, kind);
    }

    @Override
    public Var makeCflowAccessVar(ResolvedType formalType, Member cflowField, int arrayIndex) {
        return new BcelCflowAccessVar(formalType, cflowField, arrayIndex);
    }

    @Override
    public ConcreteTypeMunger concreteTypeMunger(ResolvedTypeMunger munger, ResolvedType aspectType) {
        return new BcelTypeMunger(munger, aspectType);
    }

    @Override
    public ConcreteTypeMunger createAccessForInlineMunger(ResolvedType aspect) {
        return new BcelAccessForInlineMunger(aspect);
    }
}

