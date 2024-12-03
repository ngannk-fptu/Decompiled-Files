/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.EnumConstantClassNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.TupleConstructorASTTransformation;

public class EnumCompletionVisitor
extends ClassCodeVisitorSupport {
    private final SourceUnit sourceUnit;

    public EnumCompletionVisitor(CompilationUnit cu, SourceUnit su) {
        this.sourceUnit = su;
    }

    @Override
    public void visitClass(ClassNode node) {
        if (!node.isEnum()) {
            return;
        }
        this.completeEnum(node);
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.sourceUnit;
    }

    private void completeEnum(ClassNode enumClass) {
        boolean isAic = EnumCompletionVisitor.isAnonymousInnerClass(enumClass);
        if (enumClass.getDeclaredConstructors().isEmpty()) {
            EnumCompletionVisitor.addImplicitConstructors(enumClass, isAic);
        }
        for (ConstructorNode ctor : enumClass.getDeclaredConstructors()) {
            this.transformConstructor(ctor, isAic);
        }
    }

    private static void addImplicitConstructors(ClassNode enumClass, boolean aic) {
        if (aic) {
            ClassNode sn = enumClass.getSuperClass();
            ArrayList<ConstructorNode> sctors = new ArrayList<ConstructorNode>(sn.getDeclaredConstructors());
            if (sctors.isEmpty()) {
                EnumCompletionVisitor.addMapConstructors(enumClass, false);
            } else {
                for (ConstructorNode constructorNode : sctors) {
                    ConstructorNode init = new ConstructorNode(1, constructorNode.getParameters(), ClassNode.EMPTY_ARRAY, new BlockStatement());
                    enumClass.addConstructor(init);
                }
            }
        } else {
            EnumCompletionVisitor.addMapConstructors(enumClass, false);
        }
    }

    private void transformConstructor(ConstructorNode ctor, boolean isAic) {
        boolean chainedThisConstructorCall = false;
        ConstructorCallExpression cce = null;
        if (ctor.firstStatementIsSpecialConstructorCall()) {
            Statement code = ctor.getFirstStatement();
            cce = (ConstructorCallExpression)((ExpressionStatement)code).getExpression();
            if (cce.isSuperCall()) {
                return;
            }
            chainedThisConstructorCall = true;
        }
        Parameter[] oldP = ctor.getParameters();
        Parameter[] newP = new Parameter[oldP.length + 2];
        String stringParameterName = this.getUniqueVariableName("__str", ctor.getCode());
        newP[0] = new Parameter(ClassHelper.STRING_TYPE, stringParameterName);
        String intParameterName = this.getUniqueVariableName("__int", ctor.getCode());
        newP[1] = new Parameter(ClassHelper.int_TYPE, intParameterName);
        System.arraycopy(oldP, 0, newP, 2, oldP.length);
        ctor.setParameters(newP);
        VariableExpression stringVariable = new VariableExpression(newP[0]);
        VariableExpression intVariable = new VariableExpression(newP[1]);
        if (chainedThisConstructorCall) {
            TupleExpression args = (TupleExpression)cce.getArguments();
            List<Expression> argsExprs = args.getExpressions();
            argsExprs.add(0, stringVariable);
            argsExprs.add(1, intVariable);
        } else {
            ArrayList<Expression> args = new ArrayList<Expression>();
            args.add(stringVariable);
            args.add(intVariable);
            if (isAic) {
                for (Parameter parameter : oldP) {
                    args.add(new VariableExpression(parameter.getName()));
                }
            }
            cce = new ConstructorCallExpression(ClassNode.SUPER, new ArgumentListExpression(args));
            BlockStatement code = new BlockStatement();
            code.addStatement(new ExpressionStatement(cce));
            Statement oldCode = ctor.getCode();
            if (oldCode != null) {
                code.addStatement(oldCode);
            }
            ctor.setCode(code);
        }
    }

    public static void addMapConstructors(ClassNode enumClass, boolean hasNoArg) {
        TupleConstructorASTTransformation.addMapConstructors(enumClass, hasNoArg, "One of the enum constants for enum " + enumClass.getName() + " was initialized with null. Please use a non-null value or define your own constructor.");
    }

    private String getUniqueVariableName(final String name, Statement code) {
        if (code == null) {
            return name;
        }
        final Object[] found = new Object[1];
        CodeVisitorSupport cv = new CodeVisitorSupport(){

            @Override
            public void visitVariableExpression(VariableExpression expression) {
                if (expression.getName().equals(name)) {
                    found[0] = Boolean.TRUE;
                }
            }
        };
        code.visit(cv);
        if (found[0] != null) {
            return this.getUniqueVariableName("_" + name, code);
        }
        return name;
    }

    private static boolean isAnonymousInnerClass(ClassNode enumClass) {
        if (!(enumClass instanceof EnumConstantClassNode)) {
            return false;
        }
        InnerClassNode ic = (InnerClassNode)enumClass;
        return ic.getVariableScope() == null;
    }
}

