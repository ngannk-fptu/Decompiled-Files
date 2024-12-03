/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.io.StringReader;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;
import ognl.AbstractMemberAccess;
import ognl.ClassResolver;
import ognl.Evaluation;
import ognl.ExpressionSyntaxException;
import ognl.MemberAccess;
import ognl.Node;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.ParseException;
import ognl.SimpleNode;
import ognl.TokenMgrError;
import ognl.TypeConverter;
import ognl.enhance.ExpressionAccessor;
import ognl.security.OgnlSecurityManager;

public abstract class Ognl {
    private static volatile Integer expressionMaxLength = null;
    private static volatile Boolean expressionMaxLengthFrozen = Boolean.FALSE;

    public static synchronized void applyExpressionMaxLength(Integer expressionMaxLength) {
        if (System.getSecurityManager() instanceof OgnlSecurityManager) {
            throw new SecurityException("the OGNL expressions maximum allowed length is not accessible inside expression itself!");
        }
        if (expressionMaxLengthFrozen.booleanValue()) {
            throw new IllegalStateException("The OGNL expression maximum allowed length has been frozen and cannot be changed.");
        }
        if (expressionMaxLength != null && expressionMaxLength < 0) {
            throw new IllegalArgumentException("The provided OGNL expression maximum allowed length, " + expressionMaxLength + ", is illegal.");
        }
        Ognl.expressionMaxLength = expressionMaxLength;
    }

    public static synchronized void freezeExpressionMaxLength() {
        if (System.getSecurityManager() instanceof OgnlSecurityManager) {
            throw new SecurityException("Freezing the OGNL expressions maximum allowed length is not accessible inside expression itself!");
        }
        expressionMaxLengthFrozen = Boolean.TRUE;
    }

    public static final synchronized void thawExpressionMaxLength() {
        if (System.getSecurityManager() instanceof OgnlSecurityManager) {
            throw new SecurityException("Thawing the OGNL expressions maximum allowed length is not accessible inside expression itself!");
        }
        expressionMaxLengthFrozen = Boolean.FALSE;
    }

    public static Object parseExpression(String expression) throws OgnlException {
        Integer currentExpressionMaxLength = expressionMaxLength;
        if (currentExpressionMaxLength != null && expression != null && expression.length() > currentExpressionMaxLength) {
            throw new OgnlException("Parsing blocked due to security reasons!", new SecurityException("This expression exceeded maximum allowed length: " + expression));
        }
        try {
            OgnlParser parser = new OgnlParser(new StringReader(expression));
            return parser.topLevelExpression();
        }
        catch (ParseException e) {
            throw new ExpressionSyntaxException(expression, e);
        }
        catch (TokenMgrError e) {
            throw new ExpressionSyntaxException(expression, e);
        }
    }

    public static Node compileExpression(OgnlContext context, Object root, String expression) throws Exception {
        Node expr = (Node)Ognl.parseExpression(expression);
        OgnlRuntime.compileExpression(context, expr, root);
        return expr;
    }

    public static Map createDefaultContext(Object root) {
        AbstractMemberAccess memberAccess = new AbstractMemberAccess(){

            @Override
            public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
                int modifiers = member.getModifiers();
                return Modifier.isPublic(modifiers);
            }
        };
        return Ognl.addDefaultContext(root, memberAccess, null, null, new OgnlContext(null, null, memberAccess));
    }

    public static Map createDefaultContext(Object root, ClassResolver classResolver) {
        AbstractMemberAccess memberAccess = new AbstractMemberAccess(){

            @Override
            public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
                int modifiers = member.getModifiers();
                return Modifier.isPublic(modifiers);
            }
        };
        return Ognl.addDefaultContext(root, memberAccess, classResolver, null, new OgnlContext(classResolver, null, memberAccess));
    }

    public static Map createDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter) {
        AbstractMemberAccess memberAccess = new AbstractMemberAccess(){

            @Override
            public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
                int modifiers = member.getModifiers();
                return Modifier.isPublic(modifiers);
            }
        };
        return Ognl.addDefaultContext(root, memberAccess, classResolver, converter, new OgnlContext(classResolver, converter, memberAccess));
    }

    public static Map createDefaultContext(Object root, MemberAccess memberAccess, ClassResolver classResolver, TypeConverter converter) {
        return Ognl.addDefaultContext(root, memberAccess, classResolver, converter, new OgnlContext(classResolver, converter, memberAccess));
    }

    public static Map createDefaultContext(Object root, MemberAccess memberAccess) {
        return Ognl.addDefaultContext(root, memberAccess, null, null, new OgnlContext(null, null, memberAccess));
    }

    public static Map addDefaultContext(Object root, Map context) {
        AbstractMemberAccess memberAccess = new AbstractMemberAccess(){

            @Override
            public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
                int modifiers = member.getModifiers();
                return Modifier.isPublic(modifiers);
            }
        };
        return Ognl.addDefaultContext(root, memberAccess, null, null, context);
    }

    public static Map addDefaultContext(Object root, ClassResolver classResolver, Map context) {
        AbstractMemberAccess memberAccess = new AbstractMemberAccess(){

            @Override
            public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
                int modifiers = member.getModifiers();
                return Modifier.isPublic(modifiers);
            }
        };
        return Ognl.addDefaultContext(root, memberAccess, classResolver, null, context);
    }

    public static Map addDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter, Map context) {
        return Ognl.addDefaultContext(root, null, classResolver, converter, context);
    }

    public static Map addDefaultContext(Object root, MemberAccess memberAccess, ClassResolver classResolver, TypeConverter converter, Map context) {
        OgnlContext result = context instanceof OgnlContext ? (OgnlContext)context : new OgnlContext(memberAccess, classResolver, converter, context);
        result.setRoot(root);
        return result;
    }

    public static void setClassResolver(Map context, ClassResolver classResolver) {
    }

    public static ClassResolver getClassResolver(Map context) {
        return null;
    }

    @Deprecated
    public static void setTypeConverter(Map context, TypeConverter converter) {
    }

    public static TypeConverter getTypeConverter(Map context) {
        if (context instanceof OgnlContext) {
            return ((OgnlContext)context).getTypeConverter();
        }
        return null;
    }

    public static void setRoot(Map context, Object root) {
        context.put("root", root);
    }

    public static Object getRoot(Map context) {
        return context.get("root");
    }

    public static Evaluation getLastEvaluation(Map context) {
        return (Evaluation)context.get("_lastEvaluation");
    }

    public static Object getValue(Object tree, Map context, Object root) throws OgnlException {
        return Ognl.getValue(tree, context, root, null);
    }

    public static Object getValue(Object tree, Map context, Object root, Class resultType) throws OgnlException {
        OgnlContext ognlContext = (OgnlContext)Ognl.addDefaultContext(root, context);
        Node node = (Node)tree;
        Object result = node.getAccessor() != null ? node.getAccessor().get(ognlContext, root) : node.getValue(ognlContext, root);
        if (resultType != null) {
            result = Ognl.getTypeConverter(context).convertValue(context, root, null, null, result, resultType);
        }
        return result;
    }

    public static Object getValue(ExpressionAccessor expression, OgnlContext context, Object root) {
        return expression.get(context, root);
    }

    public static Object getValue(ExpressionAccessor expression, OgnlContext context, Object root, Class resultType) {
        return Ognl.getTypeConverter(context).convertValue(context, root, null, null, expression.get(context, root), resultType);
    }

    public static Object getValue(String expression, Map context, Object root) throws OgnlException {
        return Ognl.getValue(expression, context, root, null);
    }

    public static Object getValue(String expression, Map context, Object root, Class resultType) throws OgnlException {
        return Ognl.getValue(Ognl.parseExpression(expression), context, root, resultType);
    }

    public static Object getValue(Object tree, Object root) throws OgnlException {
        return Ognl.getValue(tree, root, null);
    }

    public static Object getValue(Object tree, Object root, Class resultType) throws OgnlException {
        return Ognl.getValue(tree, Ognl.createDefaultContext(root), root, resultType);
    }

    public static Object getValue(String expression, Object root) throws OgnlException {
        return Ognl.getValue(expression, root, null);
    }

    public static Object getValue(String expression, Object root, Class resultType) throws OgnlException {
        return Ognl.getValue(Ognl.parseExpression(expression), root, resultType);
    }

    public static void setValue(Object tree, Map context, Object root, Object value) throws OgnlException {
        OgnlContext ognlContext = (OgnlContext)Ognl.addDefaultContext(root, context);
        Node n = (Node)tree;
        if (n.getAccessor() != null) {
            n.getAccessor().set(ognlContext, root, value);
            return;
        }
        n.setValue(ognlContext, root, value);
    }

    public static void setValue(ExpressionAccessor expression, OgnlContext context, Object root, Object value) {
        expression.set(context, root, value);
    }

    public static void setValue(String expression, Map context, Object root, Object value) throws OgnlException {
        Ognl.setValue(Ognl.parseExpression(expression), context, root, value);
    }

    public static void setValue(Object tree, Object root, Object value) throws OgnlException {
        Ognl.setValue(tree, Ognl.createDefaultContext(root), root, value);
    }

    public static void setValue(String expression, Object root, Object value) throws OgnlException {
        Ognl.setValue(Ognl.parseExpression(expression), root, value);
    }

    public static boolean isConstant(Object tree, Map context) throws OgnlException {
        return ((SimpleNode)tree).isConstant((OgnlContext)Ognl.addDefaultContext(null, context));
    }

    public static boolean isConstant(String expression, Map context) throws OgnlException {
        return Ognl.isConstant(Ognl.parseExpression(expression), context);
    }

    public static boolean isConstant(Object tree) throws OgnlException {
        return Ognl.isConstant(tree, Ognl.createDefaultContext(null));
    }

    public static boolean isConstant(String expression) throws OgnlException {
        return Ognl.isConstant(Ognl.parseExpression(expression), Ognl.createDefaultContext(null));
    }

    public static boolean isSimpleProperty(Object tree, Map context) throws OgnlException {
        return ((SimpleNode)tree).isSimpleProperty((OgnlContext)Ognl.addDefaultContext(null, context));
    }

    public static boolean isSimpleProperty(String expression, Map context) throws OgnlException {
        return Ognl.isSimpleProperty(Ognl.parseExpression(expression), context);
    }

    public static boolean isSimpleProperty(Object tree) throws OgnlException {
        return Ognl.isSimpleProperty(tree, Ognl.createDefaultContext(null));
    }

    public static boolean isSimpleProperty(String expression) throws OgnlException {
        return Ognl.isSimpleProperty(Ognl.parseExpression(expression), Ognl.createDefaultContext(null));
    }

    public static boolean isSimpleNavigationChain(Object tree, Map context) throws OgnlException {
        return ((SimpleNode)tree).isSimpleNavigationChain((OgnlContext)Ognl.addDefaultContext(null, context));
    }

    public static boolean isSimpleNavigationChain(String expression, Map context) throws OgnlException {
        return Ognl.isSimpleNavigationChain(Ognl.parseExpression(expression), context);
    }

    public static boolean isSimpleNavigationChain(Object tree) throws OgnlException {
        return Ognl.isSimpleNavigationChain(tree, Ognl.createDefaultContext(null));
    }

    public static boolean isSimpleNavigationChain(String expression) throws OgnlException {
        return Ognl.isSimpleNavigationChain(Ognl.parseExpression(expression), Ognl.createDefaultContext(null));
    }

    private Ognl() {
    }
}

