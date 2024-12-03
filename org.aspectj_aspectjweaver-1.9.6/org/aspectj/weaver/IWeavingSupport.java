/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;

public interface IWeavingSupport {
    public Advice createAdviceMunger(AjAttribute.AdviceAttribute var1, Pointcut var2, Member var3, ResolvedType var4);

    public ConcreteTypeMunger makeCflowStackFieldAdder(ResolvedMember var1);

    public ConcreteTypeMunger makeCflowCounterFieldAdder(ResolvedMember var1);

    public ConcreteTypeMunger makePerClauseAspect(ResolvedType var1, PerClause.Kind var2);

    public ConcreteTypeMunger concreteTypeMunger(ResolvedTypeMunger var1, ResolvedType var2);

    public ConcreteTypeMunger createAccessForInlineMunger(ResolvedType var1);

    public Var makeCflowAccessVar(ResolvedType var1, Member var2, int var3);
}

