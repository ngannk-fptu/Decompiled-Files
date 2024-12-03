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
public @interface Log4j {
    public String value() default "log";

    public String category() default "##default-category-name##";

    public Class<? extends LogASTTransformation.LoggingStrategy> loggingStrategy() default Log4jLoggingStrategy.class;

    public static class Log4jLoggingStrategy
    extends LogASTTransformation.AbstractLoggingStrategy {
        private static final String LOGGER_NAME = "org.apache.log4j.Logger";
        private static final String PRIORITY_NAME = "org.apache.log4j.Priority";

        protected Log4jLoggingStrategy(GroovyClassLoader loader) {
            super(loader);
        }

        @Override
        public FieldNode addLoggerFieldToClass(ClassNode classNode, String logFieldName, String categoryName) {
            return classNode.addField(logFieldName, 154, this.classNode(LOGGER_NAME), new MethodCallExpression((Expression)new ClassExpression(this.classNode(LOGGER_NAME)), "getLogger", (Expression)new ConstantExpression(this.getCategoryName(classNode, categoryName))));
        }

        @Override
        public boolean isLoggingMethod(String methodName) {
            return methodName.matches("fatal|error|warn|info|debug|trace");
        }

        @Override
        public Expression wrapLoggingMethodCall(Expression logVariable, String methodName, Expression originalExpression) {
            MethodCallExpression condition;
            if (!"trace".equals(methodName)) {
                AttributeExpression logLevelExpression = new AttributeExpression((Expression)new ClassExpression(this.classNode(PRIORITY_NAME)), new ConstantExpression(methodName.toUpperCase(Locale.ENGLISH)));
                ArgumentListExpression args = new ArgumentListExpression();
                args.addExpression(logLevelExpression);
                condition = new MethodCallExpression(logVariable, "isEnabledFor", (Expression)args);
            } else {
                condition = new MethodCallExpression(logVariable, "is" + methodName.substring(0, 1).toUpperCase(Locale.ENGLISH) + methodName.substring(1, methodName.length()) + "Enabled", (Expression)ArgumentListExpression.EMPTY_ARGUMENTS);
            }
            condition.setImplicitThis(false);
            return new TernaryExpression(new BooleanExpression(condition), originalExpression, ConstantExpression.NULL);
        }
    }
}

