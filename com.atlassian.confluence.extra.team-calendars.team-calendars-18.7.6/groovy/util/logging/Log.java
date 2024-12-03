/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.logging;

import groovy.lang.GroovyClassLoader;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;
import org.codehaus.groovy.transform.LogASTTransformation;

@Documented
@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.TYPE})
@GroovyASTTransformationClass(value={"org.codehaus.groovy.transform.LogASTTransformation"})
public @interface Log {
    public String value() default "log";

    public String category() default "##default-category-name##";

    public Class<? extends LogASTTransformation.LoggingStrategy> loggingStrategy() default JavaUtilLoggingStrategy.class;

    public static class JavaUtilLoggingStrategy
    extends LogASTTransformation.AbstractLoggingStrategy {
        private static final ClassNode LOGGER_CLASSNODE = ClassHelper.make(Logger.class);
        private static final ClassNode LEVEL_CLASSNODE = ClassHelper.make(Level.class);

        protected JavaUtilLoggingStrategy(GroovyClassLoader loader) {
            super(loader);
        }

        @Override
        public FieldNode addLoggerFieldToClass(ClassNode classNode, String logFieldName, String categoryName) {
            return classNode.addField(logFieldName, 154, LOGGER_CLASSNODE, new MethodCallExpression((Expression)new ClassExpression(LOGGER_CLASSNODE), "getLogger", (Expression)new ConstantExpression(this.getCategoryName(classNode, categoryName))));
        }

        @Override
        public boolean isLoggingMethod(String methodName) {
            return methodName.matches("severe|warning|info|fine|finer|finest");
        }

        @Override
        public Expression wrapLoggingMethodCall(Expression logVariable, String methodName, Expression originalExpression) {
            AttributeExpression logLevelExpression = new AttributeExpression((Expression)new ClassExpression(LEVEL_CLASSNODE), new ConstantExpression(methodName.toUpperCase(Locale.ENGLISH)));
            ArgumentListExpression args = new ArgumentListExpression();
            args.addExpression(logLevelExpression);
            MethodCallExpression condition = new MethodCallExpression(logVariable, "isLoggable", (Expression)args);
            condition.setImplicitThis(false);
            return new TernaryExpression(new BooleanExpression(condition), originalExpression, ConstantExpression.NULL);
        }
    }
}

