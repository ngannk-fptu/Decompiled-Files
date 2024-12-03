/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.ReferenceType;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.MemberImpl;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.ast.And;
import org.aspectj.weaver.ast.Call;
import org.aspectj.weaver.ast.CallExpr;
import org.aspectj.weaver.ast.Expr;
import org.aspectj.weaver.ast.FieldGet;
import org.aspectj.weaver.ast.FieldGetCall;
import org.aspectj.weaver.ast.HasAnnotation;
import org.aspectj.weaver.ast.IExprVisitor;
import org.aspectj.weaver.ast.ITestVisitor;
import org.aspectj.weaver.ast.Instanceof;
import org.aspectj.weaver.ast.Literal;
import org.aspectj.weaver.ast.Not;
import org.aspectj.weaver.ast.Or;
import org.aspectj.weaver.ast.Test;
import org.aspectj.weaver.ast.Var;
import org.aspectj.weaver.bcel.BcelVar;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.internal.tools.MatchingContextBasedTest;

public final class BcelRenderer
implements ITestVisitor,
IExprVisitor {
    private InstructionList instructions;
    private InstructionFactory fact;
    private BcelWorld world;
    InstructionHandle sk;
    InstructionHandle fk;
    InstructionHandle next = null;

    private BcelRenderer(InstructionFactory fact, BcelWorld world) {
        this.fact = fact;
        this.world = world;
        this.instructions = new InstructionList();
    }

    public static InstructionList renderExpr(InstructionFactory fact, BcelWorld world, Expr e) {
        BcelRenderer renderer = new BcelRenderer(fact, world);
        e.accept(renderer);
        return renderer.instructions;
    }

    public static InstructionList renderExpr(InstructionFactory fact, BcelWorld world, Expr e, Type desiredType) {
        BcelRenderer renderer = new BcelRenderer(fact, world);
        e.accept(renderer);
        InstructionList il = renderer.instructions;
        il.append(Utility.createConversion(fact, BcelWorld.makeBcelType(e.getType()), desiredType));
        return il;
    }

    public static InstructionList renderExprs(InstructionFactory fact, BcelWorld world, Expr[] es) {
        BcelRenderer renderer = new BcelRenderer(fact, world);
        for (int i = es.length - 1; i >= 0; --i) {
            es[i].accept(renderer);
        }
        return renderer.instructions;
    }

    public static InstructionList renderTest(InstructionFactory fact, BcelWorld world, Test e, InstructionHandle sk, InstructionHandle fk, InstructionHandle next) {
        BcelRenderer renderer = new BcelRenderer(fact, world);
        renderer.recur(e, sk, fk, next);
        return renderer.instructions;
    }

    private void recur(Test e, InstructionHandle sk, InstructionHandle fk, InstructionHandle next) {
        this.sk = sk;
        this.fk = fk;
        this.next = next;
        e.accept(this);
    }

    @Override
    public void visit(And e) {
        InstructionHandle savedFk = this.fk;
        this.recur(e.getRight(), this.sk, this.fk, this.next);
        InstructionHandle ning = this.instructions.getStart();
        this.recur(e.getLeft(), ning, savedFk, ning);
    }

    @Override
    public void visit(Or e) {
        InstructionHandle savedSk = this.sk;
        this.recur(e.getRight(), this.sk, this.fk, this.next);
        this.recur(e.getLeft(), savedSk, this.instructions.getStart(), this.instructions.getStart());
    }

    @Override
    public void visit(Not e) {
        this.recur(e.getBody(), this.fk, this.sk, this.next);
    }

    @Override
    public void visit(Instanceof i) {
        this.instructions.insert(this.createJumpBasedOnBooleanOnStack());
        this.instructions.insert(Utility.createInstanceof(this.fact, (ReferenceType)BcelWorld.makeBcelType(i.getType())));
        i.getVar().accept(this);
    }

    @Override
    public void visit(HasAnnotation hasAnnotation) {
        InstructionList il = new InstructionList();
        il.append(InstructionFactory.createBranchInstruction((short)198, this.fk));
        il.append(((BcelVar)hasAnnotation.getVar()).createLoad(this.fact));
        MemberImpl getClass = MemberImpl.method(UnresolvedType.OBJECT, 0, UnresolvedType.JL_CLASS, "getClass", UnresolvedType.NONE);
        il.append(Utility.createInvoke(this.fact, this.world, (Member)getClass));
        il.append(this.fact.createConstant(new ObjectType(hasAnnotation.getAnnotationType().getName())));
        MemberImpl isAnnotationPresent = MemberImpl.method(UnresolvedType.JL_CLASS, 0, UnresolvedType.BOOLEAN, "isAnnotationPresent", new UnresolvedType[]{UnresolvedType.JL_CLASS});
        il.append(Utility.createInvoke(this.fact, this.world, (Member)isAnnotationPresent));
        il.append(this.createJumpBasedOnBooleanOnStack());
        this.instructions.insert(il);
        hasAnnotation.getVar().accept(this);
    }

    @Override
    public void visit(MatchingContextBasedTest matchingContextTest) {
        throw new UnsupportedOperationException("matching context extension not supported in bytecode weaving");
    }

    private InstructionList createJumpBasedOnBooleanOnStack() {
        InstructionList il = new InstructionList();
        if (this.sk == this.fk) {
            if (this.sk != this.next) {
                il.insert(InstructionFactory.createBranchInstruction((short)167, this.sk));
            }
            return il;
        }
        if (this.fk == this.next) {
            il.insert(InstructionFactory.createBranchInstruction((short)154, this.sk));
        } else if (this.sk == this.next) {
            il.insert(InstructionFactory.createBranchInstruction((short)153, this.fk));
        } else {
            il.insert(InstructionFactory.createBranchInstruction((short)167, this.sk));
            il.insert(InstructionFactory.createBranchInstruction((short)153, this.fk));
        }
        return il;
    }

    @Override
    public void visit(Literal literal) {
        if (literal == Literal.FALSE) {
            throw new BCException("visiting a false expression");
        }
    }

    @Override
    public void visit(Call call) {
        Member method = call.getMethod();
        Expr[] args = call.getArgs();
        InstructionList callIl = new InstructionList();
        int len = args.length;
        for (int i = 0; i < len; ++i) {
            Type desiredType = BcelWorld.makeBcelType(method.getParameterTypes()[i]);
            Expr arg = args[i];
            if (arg == null) {
                InstructionList iList = new InstructionList();
                iList.append(InstructionFactory.createNull(desiredType));
                callIl.append(iList);
                continue;
            }
            callIl.append(BcelRenderer.renderExpr(this.fact, this.world, arg, desiredType));
        }
        callIl.append(Utility.createInvoke(this.fact, this.world, method));
        callIl.append(this.createJumpBasedOnBooleanOnStack());
        this.instructions.insert(callIl);
    }

    @Override
    public void visit(FieldGetCall fieldGetCall) {
        Member field = fieldGetCall.getField();
        Member method = fieldGetCall.getMethod();
        InstructionList il = new InstructionList();
        il.append(Utility.createGet(this.fact, field));
        Expr[] args = fieldGetCall.getArgs();
        il.append(BcelRenderer.renderExprs(this.fact, this.world, args));
        il.append(Utility.createInvoke(this.fact, this.world, method));
        il.append(this.createJumpBasedOnBooleanOnStack());
        this.instructions.insert(il);
    }

    @Override
    public void visit(Var var) {
        BcelVar bvar = (BcelVar)var;
        bvar.insertLoad(this.instructions, this.fact);
    }

    @Override
    public void visit(FieldGet fieldGet) {
        Member field = fieldGet.getField();
        this.instructions.insert(Utility.createGet(this.fact, field));
    }

    @Override
    public void visit(CallExpr call) {
        Member method = call.getMethod();
        Expr[] args = call.getArgs();
        InstructionList callIl = BcelRenderer.renderExprs(this.fact, this.world, args);
        callIl.append(Utility.createInvoke(this.fact, this.world, method));
        this.instructions.insert(callIl);
    }
}

