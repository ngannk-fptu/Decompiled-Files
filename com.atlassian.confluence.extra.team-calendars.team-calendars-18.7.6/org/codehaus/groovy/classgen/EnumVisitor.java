/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.EnumConstantClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;

public class EnumVisitor
extends ClassCodeVisitorSupport {
    private static final int FS = 24;
    private static final int PS = 9;
    private static final int PUBLIC_FS = 25;
    private static final int PRIVATE_FS = 26;
    private final SourceUnit sourceUnit;

    public EnumVisitor(CompilationUnit cu, SourceUnit su) {
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
        boolean isAic = EnumVisitor.isAnonymousInnerClass(enumClass);
        FieldNode minValue = null;
        FieldNode maxValue = null;
        FieldNode values = null;
        if (!isAic) {
            ClassNode enumRef = enumClass.getPlainNodeReference();
            values = new FieldNode("$VALUES", 4122, enumRef.makeArray(), enumClass, null);
            values.setSynthetic(true);
            EnumVisitor.addMethods(enumClass, values);
            EnumVisitor.checkForAbstractMethods(enumClass);
            minValue = new FieldNode("MIN_VALUE", 25, enumRef, enumClass, null);
            maxValue = new FieldNode("MAX_VALUE", 25, enumRef, enumClass, null);
        }
        this.addInit(enumClass, minValue, maxValue, values, isAic);
    }

    private static void checkForAbstractMethods(ClassNode enumClass) {
        List<MethodNode> methods = enumClass.getMethods();
        for (MethodNode m : methods) {
            if (!m.isAbstract()) continue;
            enumClass.setModifiers(enumClass.getModifiers() | 0x400);
            break;
        }
    }

    private static void addMethods(ClassNode enumClass, FieldNode values) {
        BlockStatement ifStatement;
        BlockStatement code;
        MethodNode nextMethod;
        Token assign;
        List<MethodNode> methods = enumClass.getMethods();
        boolean hasNext = false;
        boolean hasPrevious = false;
        for (MethodNode m : methods) {
            if (m.getName().equals("next") && m.getParameters().length == 0) {
                hasNext = true;
            }
            if (m.getName().equals("previous") && m.getParameters().length == 0) {
                hasPrevious = true;
            }
            if (!hasNext || !hasPrevious) continue;
            break;
        }
        ClassNode enumRef = enumClass.getPlainNodeReference();
        MethodNode valuesMethod = new MethodNode("values", 25, enumRef.makeArray(), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, null);
        valuesMethod.setSynthetic(true);
        BlockStatement code2 = new BlockStatement();
        MethodCallExpression cloneCall = new MethodCallExpression((Expression)new FieldExpression(values), "clone", MethodCallExpression.NO_ARGUMENTS);
        cloneCall.setMethodTarget(values.getType().getMethod("clone", Parameter.EMPTY_ARRAY));
        code2.addStatement(new ReturnStatement(cloneCall));
        valuesMethod.setCode(code2);
        enumClass.addMethod(valuesMethod);
        if (!hasNext) {
            assign = Token.newSymbol(100, -1, -1);
            Token ge = Token.newSymbol(127, -1, -1);
            nextMethod = new MethodNode("next", 4097, enumRef, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, null);
            nextMethod.setSynthetic(true);
            code = new BlockStatement();
            ifStatement = new BlockStatement();
            ifStatement.addStatement(new ExpressionStatement(new BinaryExpression(new VariableExpression("ordinal"), assign, new ConstantExpression(0))));
            code.addStatement(new ExpressionStatement(new DeclarationExpression(new VariableExpression("ordinal"), assign, (Expression)new MethodCallExpression((Expression)new MethodCallExpression((Expression)VariableExpression.THIS_EXPRESSION, "ordinal", MethodCallExpression.NO_ARGUMENTS), "next", MethodCallExpression.NO_ARGUMENTS))));
            code.addStatement(new IfStatement(new BooleanExpression(new BinaryExpression(new VariableExpression("ordinal"), ge, new MethodCallExpression((Expression)new FieldExpression(values), "size", MethodCallExpression.NO_ARGUMENTS))), ifStatement, EmptyStatement.INSTANCE));
            code.addStatement(new ReturnStatement(new MethodCallExpression((Expression)new FieldExpression(values), "getAt", (Expression)new VariableExpression("ordinal"))));
            nextMethod.setCode(code);
            enumClass.addMethod(nextMethod);
        }
        if (!hasPrevious) {
            assign = Token.newSymbol(100, -1, -1);
            Token lt = Token.newSymbol(124, -1, -1);
            nextMethod = new MethodNode("previous", 4097, enumRef, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, null);
            nextMethod.setSynthetic(true);
            code = new BlockStatement();
            ifStatement = new BlockStatement();
            ifStatement.addStatement(new ExpressionStatement(new BinaryExpression(new VariableExpression("ordinal"), assign, new MethodCallExpression((Expression)new MethodCallExpression((Expression)new FieldExpression(values), "size", MethodCallExpression.NO_ARGUMENTS), "minus", (Expression)new ConstantExpression(1)))));
            code.addStatement(new ExpressionStatement(new DeclarationExpression(new VariableExpression("ordinal"), assign, (Expression)new MethodCallExpression((Expression)new MethodCallExpression((Expression)VariableExpression.THIS_EXPRESSION, "ordinal", MethodCallExpression.NO_ARGUMENTS), "previous", MethodCallExpression.NO_ARGUMENTS))));
            code.addStatement(new IfStatement(new BooleanExpression(new BinaryExpression(new VariableExpression("ordinal"), lt, new ConstantExpression(0))), ifStatement, EmptyStatement.INSTANCE));
            code.addStatement(new ReturnStatement(new MethodCallExpression((Expression)new FieldExpression(values), "getAt", (Expression)new VariableExpression("ordinal"))));
            nextMethod.setCode(code);
            enumClass.addMethod(nextMethod);
        }
        Parameter stringParameter = new Parameter(ClassHelper.STRING_TYPE, "name");
        MethodNode valueOfMethod = new MethodNode("valueOf", 9, enumRef, new Parameter[]{stringParameter}, ClassNode.EMPTY_ARRAY, null);
        ArgumentListExpression callArguments = new ArgumentListExpression();
        callArguments.addExpression(new ClassExpression(enumClass));
        callArguments.addExpression(new VariableExpression("name"));
        code = new BlockStatement();
        code.addStatement(new ReturnStatement(new MethodCallExpression((Expression)new ClassExpression(ClassHelper.Enum_Type), "valueOf", (Expression)callArguments)));
        valueOfMethod.setCode(code);
        valueOfMethod.setSynthetic(true);
        enumClass.addMethod(valueOfMethod);
    }

    private void addInit(ClassNode enumClass, FieldNode minValue, FieldNode maxValue, FieldNode values, boolean isAic) {
        ClassNode enumRef = enumClass.getPlainNodeReference();
        Parameter[] parameter = new Parameter[]{new Parameter(ClassHelper.OBJECT_TYPE.makeArray(), "para")};
        MethodNode initMethod = new MethodNode("$INIT", 4121, enumRef, parameter, ClassNode.EMPTY_ARRAY, null);
        initMethod.setSynthetic(true);
        ConstructorCallExpression cce = new ConstructorCallExpression(ClassNode.THIS, new ArgumentListExpression(new SpreadExpression(new VariableExpression("para"))));
        BlockStatement code = new BlockStatement();
        code.addStatement(new ReturnStatement(cce));
        initMethod.setCode(code);
        enumClass.addMethod(initMethod);
        List<FieldNode> fields = enumClass.getFields();
        ArrayList<Expression> arrayInit = new ArrayList<Expression>();
        int value = -1;
        Token assign = Token.newSymbol(100, -1, -1);
        ArrayList<Statement> block = new ArrayList<Statement>();
        FieldNode tempMin = null;
        FieldNode tempMax = null;
        for (FieldNode field : fields) {
            if ((field.getModifiers() & 0x4000) == 0) continue;
            ++value;
            if (tempMin == null) {
                tempMin = field;
            }
            tempMax = field;
            ClassNode enumBase = enumClass;
            ArgumentListExpression args = new ArgumentListExpression();
            args.addExpression(new ConstantExpression(field.getName()));
            args.addExpression(new ConstantExpression(value));
            if (field.getInitialExpression() == null) {
                if ((enumClass.getModifiers() & 0x400) != 0) {
                    this.addError(field, "The enum constant " + field.getName() + " must override abstract methods from " + enumBase.getName() + ".");
                    continue;
                }
            } else {
                ListExpression oldArgs = (ListExpression)field.getInitialExpression();
                ArrayList<MapEntryExpression> savedMapEntries = new ArrayList<MapEntryExpression>();
                for (Expression exp : oldArgs.getExpressions()) {
                    ClassExpression clazzExp;
                    ClassNode ref;
                    if (exp instanceof MapEntryExpression) {
                        savedMapEntries.add((MapEntryExpression)exp);
                        continue;
                    }
                    InnerClassNode inner = null;
                    if (exp instanceof ClassExpression && (ref = (clazzExp = (ClassExpression)exp).getType()) instanceof EnumConstantClassNode) {
                        inner = (InnerClassNode)ref;
                    }
                    if (inner != null) {
                        List<MethodNode> baseMethods = enumBase.getMethods();
                        for (MethodNode methodNode : baseMethods) {
                            MethodNode enumConstMethod;
                            if (!methodNode.isAbstract() || (enumConstMethod = inner.getMethod(methodNode.getName(), methodNode.getParameters())) != null && (enumConstMethod.getModifiers() & 0x400) == 0) continue;
                            this.addError(field, "Can't have an abstract method in enum constant " + field.getName() + ". Implement method '" + methodNode.getTypeDescriptor() + "'.");
                        }
                        if (inner.getVariableScope() == null) {
                            enumBase = inner;
                            initMethod.setModifiers(initMethod.getModifiers() & 0xFFFFFFEF);
                            continue;
                        }
                    }
                    args.addExpression(exp);
                }
                if (!savedMapEntries.isEmpty()) {
                    args.getExpressions().add(2, new MapExpression(savedMapEntries));
                }
            }
            field.setInitialValueExpression(null);
            block.add(new ExpressionStatement(new BinaryExpression(new FieldExpression(field), assign, new StaticMethodCallExpression(enumBase, "$INIT", args))));
            arrayInit.add(new FieldExpression(field));
        }
        if (!isAic) {
            if (tempMin != null) {
                block.add(new ExpressionStatement(new BinaryExpression(new FieldExpression(minValue), assign, new FieldExpression(tempMin))));
                block.add(new ExpressionStatement(new BinaryExpression(new FieldExpression(maxValue), assign, new FieldExpression(tempMax))));
                enumClass.addField(minValue);
                enumClass.addField(maxValue);
            }
            block.add(new ExpressionStatement(new BinaryExpression(new FieldExpression(values), assign, new ArrayExpression(enumClass, arrayInit))));
            enumClass.addField(values);
        }
        enumClass.addStaticInitializerStatements(block, true);
    }

    private void addError(AnnotatedNode exp, String msg) {
        this.sourceUnit.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException(msg + '\n', exp.getLineNumber(), exp.getColumnNumber(), exp.getLastLineNumber(), exp.getLastColumnNumber()), this.sourceUnit));
    }

    private static boolean isAnonymousInnerClass(ClassNode enumClass) {
        if (!(enumClass instanceof EnumConstantClassNode)) {
            return false;
        }
        InnerClassNode ic = (InnerClassNode)enumClass;
        return ic.getVariableScope() == null;
    }
}

