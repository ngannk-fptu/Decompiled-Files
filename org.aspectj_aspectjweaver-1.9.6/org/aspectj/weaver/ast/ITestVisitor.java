/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.ast;

import org.aspectj.weaver.ast.And;
import org.aspectj.weaver.ast.Call;
import org.aspectj.weaver.ast.FieldGetCall;
import org.aspectj.weaver.ast.HasAnnotation;
import org.aspectj.weaver.ast.Instanceof;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Not;
import org.aspectj.weaver.ast.Or;
import org.aspectj.weaver.internal.tools.MatchingContextBasedTest;

public interface ITestVisitor {
    public void visit(And var1);

    public void visit(Instanceof var1);

    public void visit(Not var1);

    public void visit(Or var1);

    public void visit(Literal var1);

    public void visit(Call var1);

    public void visit(FieldGetCall var1);

    public void visit(HasAnnotation var1);

    public void visit(MatchingContextBasedTest var1);
}

