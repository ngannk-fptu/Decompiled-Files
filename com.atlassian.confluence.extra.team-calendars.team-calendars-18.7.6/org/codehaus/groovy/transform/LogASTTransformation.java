/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import groovy.transform.CompilationUnitAware;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class LogASTTransformation
extends AbstractASTTransformation
implements CompilationUnitAware {
    public static final String DEFAULT_CATEGORY_NAME = "##default-category-name##";
    private CompilationUnit compilationUnit;

    @Override
    public void visit(ASTNode[] nodes, final SourceUnit source) {
        this.init(nodes, source);
        AnnotatedNode targetClass = (AnnotatedNode)nodes[1];
        AnnotationNode logAnnotation = (AnnotationNode)nodes[0];
        GroovyClassLoader classLoader = this.compilationUnit != null ? this.compilationUnit.getTransformLoader() : source.getClassLoader();
        final LoggingStrategy loggingStrategy = this.createLoggingStrategy(logAnnotation, classLoader);
        if (loggingStrategy == null) {
            return;
        }
        final String logFieldName = this.lookupLogFieldName(logAnnotation);
        final String categoryName = this.lookupCategoryName(logAnnotation);
        if (!(targetClass instanceof ClassNode)) {
            throw new GroovyBugError("Class annotation " + logAnnotation.getClassNode().getName() + " annotated no Class, this must not happen.");
        }
        ClassNode classNode = (ClassNode)targetClass;
        ClassCodeExpressionTransformer transformer = new ClassCodeExpressionTransformer(){
            private FieldNode logNode;

            @Override
            protected SourceUnit getSourceUnit() {
                return source;
            }

            @Override
            public Expression transform(Expression exp) {
                if (exp == null) {
                    return null;
                }
                if (exp instanceof MethodCallExpression) {
                    return this.transformMethodCallExpression(exp);
                }
                if (exp instanceof ClosureExpression) {
                    return this.transformClosureExpression((ClosureExpression)exp);
                }
                return super.transform(exp);
            }

            @Override
            public void visitClass(ClassNode node) {
                FieldNode logField = node.getField(logFieldName);
                if (logField != null && logField.getOwner().equals(node)) {
                    this.addError("Class annotated with Log annotation cannot have log field declared", logField);
                } else if (logField != null && !Modifier.isPrivate(logField.getModifiers())) {
                    this.addError("Class annotated with Log annotation cannot have log field declared because the field exists in the parent class: " + logField.getOwner().getName(), logField);
                } else {
                    this.logNode = loggingStrategy.addLoggerFieldToClass(node, logFieldName, categoryName);
                }
                super.visitClass(node);
            }

            private Expression transformClosureExpression(ClosureExpression exp) {
                if (exp.getCode() instanceof BlockStatement) {
                    BlockStatement code = (BlockStatement)exp.getCode();
                    super.visitBlockStatement(code);
                }
                return exp;
            }

            private Expression transformMethodCallExpression(Expression exp) {
                Expression modifiedCall = this.addGuard((MethodCallExpression)exp);
                return modifiedCall == null ? super.transform(exp) : modifiedCall;
            }

            private Expression addGuard(MethodCallExpression mce) {
                if (!(mce.getObjectExpression() instanceof VariableExpression)) {
                    return null;
                }
                VariableExpression variableExpression = (VariableExpression)mce.getObjectExpression();
                if (!variableExpression.getName().equals(logFieldName) || !(variableExpression.getAccessedVariable() instanceof DynamicVariable)) {
                    return null;
                }
                String methodName = mce.getMethodAsString();
                if (methodName == null) {
                    return null;
                }
                if (!loggingStrategy.isLoggingMethod(methodName)) {
                    return null;
                }
                if (this.usesSimpleMethodArgumentsOnly(mce)) {
                    return null;
                }
                variableExpression.setAccessedVariable(this.logNode);
                return loggingStrategy.wrapLoggingMethodCall(variableExpression, methodName, mce);
            }

            private boolean usesSimpleMethodArgumentsOnly(MethodCallExpression mce) {
                Expression arguments = mce.getArguments();
                if (arguments instanceof TupleExpression) {
                    TupleExpression tuple = (TupleExpression)arguments;
                    for (Expression exp : tuple.getExpressions()) {
                        if (this.isSimpleExpression(exp)) continue;
                        return false;
                    }
                    return true;
                }
                return !this.isSimpleExpression(arguments);
            }

            private boolean isSimpleExpression(Expression exp) {
                if (exp instanceof ConstantExpression) {
                    return true;
                }
                return exp instanceof VariableExpression;
            }
        };
        transformer.visitClass(classNode);
        new VariableScopeVisitor(this.sourceUnit, true).visitClass(classNode);
    }

    private String lookupLogFieldName(AnnotationNode logAnnotation) {
        Expression member = logAnnotation.getMember("value");
        if (member != null && member.getText() != null) {
            return member.getText();
        }
        return "log";
    }

    private String lookupCategoryName(AnnotationNode logAnnotation) {
        Expression member = logAnnotation.getMember("category");
        if (member != null && member.getText() != null) {
            return member.getText();
        }
        return DEFAULT_CATEGORY_NAME;
    }

    private LoggingStrategy createLoggingStrategy(AnnotationNode logAnnotation, GroovyClassLoader loader) {
        Object defaultValue;
        Method annotationMethod;
        Class<?> annotationClass;
        String annotationName = logAnnotation.getClassNode().getName();
        try {
            annotationClass = Class.forName(annotationName, false, loader);
        }
        catch (Throwable e) {
            throw new RuntimeException("Could not resolve class named " + annotationName);
        }
        try {
            annotationMethod = annotationClass.getDeclaredMethod("loggingStrategy", null);
        }
        catch (Throwable e) {
            throw new RuntimeException("Could not find method named loggingStrategy on class named " + annotationName);
        }
        try {
            defaultValue = annotationMethod.getDefaultValue();
        }
        catch (Throwable e) {
            throw new RuntimeException("Could not find default value of method named loggingStrategy on class named " + annotationName);
        }
        if (!LoggingStrategy.class.isAssignableFrom((Class)defaultValue)) {
            throw new RuntimeException("Default loggingStrategy value on class named " + annotationName + " is not a LoggingStrategy");
        }
        try {
            Class strategyClass = (Class)defaultValue;
            if (AbstractLoggingStrategy.class.isAssignableFrom(strategyClass)) {
                return (LoggingStrategy)DefaultGroovyMethods.newInstance(strategyClass, new Object[]{loader});
            }
            return (LoggingStrategy)strategyClass.newInstance();
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setCompilationUnit(CompilationUnit unit) {
        this.compilationUnit = unit;
    }

    public static abstract class AbstractLoggingStrategy
    implements LoggingStrategy {
        protected final GroovyClassLoader loader;

        protected AbstractLoggingStrategy(GroovyClassLoader loader) {
            this.loader = loader;
        }

        protected AbstractLoggingStrategy() {
            this(null);
        }

        @Override
        public String getCategoryName(ClassNode classNode, String categoryName) {
            if (categoryName.equals(LogASTTransformation.DEFAULT_CATEGORY_NAME)) {
                return classNode.getName();
            }
            return categoryName;
        }

        protected ClassNode classNode(String name) {
            ClassLoader cl = this.loader == null ? this.getClass().getClassLoader() : this.loader;
            try {
                return ClassHelper.make(Class.forName(name, false, cl));
            }
            catch (ClassNotFoundException e) {
                throw new GroovyRuntimeException("Unable to load logging class", e);
            }
        }
    }

    public static interface LoggingStrategy {
        public FieldNode addLoggerFieldToClass(ClassNode var1, String var2, String var3);

        public boolean isLoggingMethod(String var1);

        public String getCategoryName(ClassNode var1, String var2);

        public Expression wrapLoggingMethodCall(Expression var1, String var2, Expression var3);
    }
}

