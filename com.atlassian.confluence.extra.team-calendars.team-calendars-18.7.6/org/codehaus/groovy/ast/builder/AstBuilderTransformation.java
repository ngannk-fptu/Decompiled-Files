/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.builder;

import groovy.lang.MissingPropertyException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.ClosureUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.io.ReaderSource;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class AstBuilderTransformation
implements ASTTransformation {
    @Override
    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        AstBuilderInvocationTrap transformer = new AstBuilderInvocationTrap(sourceUnit.getAST().getImports(), sourceUnit.getAST().getStarImports(), sourceUnit.getSource(), sourceUnit);
        if (nodes != null) {
            for (ASTNode aSTNode : nodes) {
                if (aSTNode instanceof AnnotationNode || aSTNode instanceof ClassNode) continue;
                aSTNode.visit(transformer);
            }
        }
        if (sourceUnit.getAST() != null) {
            sourceUnit.getAST().visit(transformer);
            if (sourceUnit.getAST().getStatementBlock() != null) {
                sourceUnit.getAST().getStatementBlock().visit(transformer);
            }
            if (sourceUnit.getAST().getClasses() != null) {
                for (ClassNode classNode : sourceUnit.getAST().getClasses()) {
                    if (classNode.getMethods() != null) {
                        for (MethodNode methodNode : classNode.getMethods()) {
                            if (methodNode == null || methodNode.getCode() == null) continue;
                            methodNode.getCode().visit(transformer);
                        }
                    }
                    try {
                        if (classNode.getDeclaredConstructors() != null) {
                            for (MethodNode methodNode : classNode.getDeclaredConstructors()) {
                                if (methodNode == null || methodNode.getCode() == null) continue;
                                methodNode.getCode().visit(transformer);
                            }
                        }
                    }
                    catch (MissingPropertyException missingPropertyException) {
                        // empty catch block
                    }
                    if (classNode.getFields() != null) {
                        for (FieldNode fieldNode : classNode.getFields()) {
                            if (fieldNode.getInitialValueExpression() == null) continue;
                            fieldNode.getInitialValueExpression().visit(transformer);
                        }
                    }
                    try {
                        if (classNode.getObjectInitializerStatements() == null) continue;
                        for (Statement statement : classNode.getObjectInitializerStatements()) {
                            if (statement == null) continue;
                            statement.visit(transformer);
                        }
                    }
                    catch (MissingPropertyException missingPropertyException) {
                    }
                }
            }
            if (sourceUnit.getAST().getMethods() != null) {
                for (MethodNode node : sourceUnit.getAST().getMethods()) {
                    if (node == null) continue;
                    if (node.getParameters() != null) {
                        for (Parameter parameter : node.getParameters()) {
                            if (parameter == null || parameter.getInitialExpression() == null) continue;
                            parameter.getInitialExpression().visit(transformer);
                        }
                    }
                    if (node.getCode() == null) continue;
                    node.getCode().visit(transformer);
                }
            }
        }
    }

    private static class AstBuilderInvocationTrap
    extends CodeVisitorSupport {
        private final List<String> factoryTargets = new ArrayList<String>();
        private final ReaderSource source;
        private final SourceUnit sourceUnit;

        AstBuilderInvocationTrap(List<ImportNode> imports, List<ImportNode> importPackages, ReaderSource source, SourceUnit sourceUnit) {
            if (source == null) {
                throw new IllegalArgumentException("Null: source");
            }
            if (sourceUnit == null) {
                throw new IllegalArgumentException("Null: sourceUnit");
            }
            this.source = source;
            this.sourceUnit = sourceUnit;
            this.factoryTargets.add("org.codehaus.groovy.ast.builder.AstBuilder");
            if (imports != null) {
                for (ImportNode importStatement : imports) {
                    if (!"org.codehaus.groovy.ast.builder.AstBuilder".equals(importStatement.getType().getName())) continue;
                    this.factoryTargets.add(importStatement.getAlias());
                }
            }
            if (importPackages != null) {
                for (ImportNode importPackage : importPackages) {
                    if (!"org.codehaus.groovy.ast.builder.".equals(importPackage.getPackageName())) continue;
                    this.factoryTargets.add("AstBuilder");
                    break;
                }
            }
        }

        private void addError(String msg, ASTNode expr) {
            this.sourceUnit.getErrorCollector().addErrorAndContinue(new SyntaxErrorMessage(new SyntaxException(msg + '\n', expr.getLineNumber(), expr.getColumnNumber(), expr.getLastLineNumber(), expr.getLastColumnNumber()), this.sourceUnit));
        }

        @Override
        public void visitMethodCallExpression(MethodCallExpression call) {
            if (this.isBuildInvocation(call)) {
                ClosureExpression closureExpression = AstBuilderInvocationTrap.getClosureArgument(call);
                List<Expression> otherArgs = AstBuilderInvocationTrap.getNonClosureArguments(call);
                String source = this.convertClosureToSource(closureExpression);
                otherArgs.add(new ConstantExpression(source));
                call.setArguments(new ArgumentListExpression(otherArgs));
                call.setMethod(new ConstantExpression("buildFromBlock"));
                call.setSpreadSafe(false);
                call.setSafe(false);
                call.setImplicitThis(false);
            } else {
                call.getObjectExpression().visit(this);
                call.getMethod().visit(this);
                call.getArguments().visit(this);
            }
        }

        private static List<Expression> getNonClosureArguments(MethodCallExpression call) {
            ArrayList<Expression> result = new ArrayList<Expression>();
            if (call.getArguments() instanceof TupleExpression) {
                for (ASTNode aSTNode : ((TupleExpression)call.getArguments()).getExpressions()) {
                    if (aSTNode instanceof ClosureExpression) continue;
                    result.add((Expression)aSTNode);
                }
            }
            return result;
        }

        private static ClosureExpression getClosureArgument(MethodCallExpression call) {
            if (call.getArguments() instanceof TupleExpression) {
                for (ASTNode aSTNode : ((TupleExpression)call.getArguments()).getExpressions()) {
                    if (!(aSTNode instanceof ClosureExpression)) continue;
                    return (ClosureExpression)aSTNode;
                }
            }
            return null;
        }

        private boolean isBuildInvocation(MethodCallExpression call) {
            String name;
            if (call == null) {
                throw new IllegalArgumentException("Null: call");
            }
            if (call.getMethod() instanceof ConstantExpression && "buildFromCode".equals(((ConstantExpression)call.getMethod()).getValue()) && call.getObjectExpression() != null && call.getObjectExpression().getType() != null && (name = call.getObjectExpression().getType().getName()) != null && !"".equals(name) && this.factoryTargets.contains(name) && call.getArguments() != null && call.getArguments() instanceof TupleExpression && ((TupleExpression)call.getArguments()).getExpressions() != null) {
                for (ASTNode aSTNode : ((TupleExpression)call.getArguments()).getExpressions()) {
                    if (!(aSTNode instanceof ClosureExpression)) continue;
                    return true;
                }
            }
            return false;
        }

        private String convertClosureToSource(ClosureExpression expression) {
            try {
                return ClosureUtils.convertClosureToSource(this.source, expression);
            }
            catch (Exception e) {
                this.addError(e.getMessage(), expression);
                return null;
            }
        }
    }
}

