/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.FunctionMapper
 *  javax.el.MethodExpression
 *  javax.el.ValueExpression
 *  javax.el.VariableMapper
 */
package org.apache.el.lang;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.security.AccessController;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.FunctionMapper;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import org.apache.el.MethodExpressionImpl;
import org.apache.el.MethodExpressionLiteral;
import org.apache.el.ValueExpressionImpl;
import org.apache.el.lang.FunctionMapperFactory;
import org.apache.el.lang.VariableMapperFactory;
import org.apache.el.parser.AstDeferredExpression;
import org.apache.el.parser.AstDynamicExpression;
import org.apache.el.parser.AstFunction;
import org.apache.el.parser.AstIdentifier;
import org.apache.el.parser.AstLiteralExpression;
import org.apache.el.parser.AstValue;
import org.apache.el.parser.ELParser;
import org.apache.el.parser.Node;
import org.apache.el.parser.NodeVisitor;
import org.apache.el.util.ConcurrentCache;
import org.apache.el.util.ExceptionUtils;
import org.apache.el.util.MessageFactory;

public final class ExpressionBuilder
implements NodeVisitor {
    private static final SynchronizedStack<ELParser> parserCache = new SynchronizedStack();
    private static final int CACHE_SIZE;
    private static final String CACHE_SIZE_PROP = "org.apache.el.ExpressionBuilder.CACHE_SIZE";
    private static final ConcurrentCache<String, Node> expressionCache;
    private FunctionMapper fnMapper;
    private VariableMapper varMapper;
    private final String expression;

    public ExpressionBuilder(String expression, ELContext ctx) throws ELException {
        this.expression = expression;
        FunctionMapper ctxFn = ctx.getFunctionMapper();
        VariableMapper ctxVar = ctx.getVariableMapper();
        if (ctxFn != null) {
            this.fnMapper = new FunctionMapperFactory(ctxFn);
        }
        if (ctxVar != null) {
            this.varMapper = new VariableMapperFactory(ctxVar);
        }
    }

    public static Node createNode(String expr) throws ELException {
        Node n = ExpressionBuilder.createNodeInternal(expr);
        return n;
    }

    private static Node createNodeInternal(String expr) throws ELException {
        if (expr == null) {
            throw new ELException(MessageFactory.get("error.null"));
        }
        Node n = expressionCache.get(expr);
        if (n == null) {
            ELParser parser = parserCache.pop();
            try {
                if (parser == null) {
                    parser = new ELParser(new StringReader(expr));
                } else {
                    parser.ReInit(new StringReader(expr));
                }
                n = parser.CompositeExpression();
                int numChildren = n.jjtGetNumChildren();
                if (numChildren == 1) {
                    n = n.jjtGetChild(0);
                } else {
                    Class<?> type = null;
                    Node child = null;
                    for (int i = 0; i < numChildren; ++i) {
                        child = n.jjtGetChild(i);
                        if (child instanceof AstLiteralExpression) continue;
                        if (type == null) {
                            type = child.getClass();
                            continue;
                        }
                        if (type.equals(child.getClass())) continue;
                        throw new ELException(MessageFactory.get("error.mixed", expr));
                    }
                }
                if (n instanceof AstDeferredExpression || n instanceof AstDynamicExpression) {
                    n = n.jjtGetChild(0);
                }
                expressionCache.put(expr, n);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                throw new ELException(MessageFactory.get("error.parseFail", expr), t);
            }
            finally {
                if (parser != null) {
                    parserCache.push(parser);
                }
            }
        }
        return n;
    }

    private void prepare(Node node) throws ELException {
        try {
            node.accept(this);
        }
        catch (Exception e) {
            if (e instanceof ELException) {
                throw (ELException)((Object)e);
            }
            throw new ELException((Throwable)e);
        }
        if (this.fnMapper instanceof FunctionMapperFactory) {
            this.fnMapper = ((FunctionMapperFactory)this.fnMapper).create();
        }
        if (this.varMapper instanceof VariableMapperFactory) {
            this.varMapper = ((VariableMapperFactory)this.varMapper).create();
        }
    }

    private Node build() throws ELException {
        Node n = ExpressionBuilder.createNodeInternal(this.expression);
        this.prepare(n);
        if (n instanceof AstDeferredExpression || n instanceof AstDynamicExpression) {
            n = n.jjtGetChild(0);
        }
        return n;
    }

    @Override
    public void visit(Node node) throws ELException {
        if (node instanceof AstFunction) {
            AstFunction funcNode = (AstFunction)node;
            Method m = null;
            if (this.fnMapper != null) {
                m = this.fnMapper.resolveFunction(funcNode.getPrefix(), funcNode.getLocalName());
            }
            if (m == null && this.varMapper != null && funcNode.getPrefix().length() == 0) {
                this.varMapper.resolveVariable(funcNode.getLocalName());
                return;
            }
            if (this.fnMapper == null) {
                throw new ELException(MessageFactory.get("error.fnMapper.null"));
            }
            if (m == null) {
                throw new ELException(MessageFactory.get("error.fnMapper.method", funcNode.getOutputName()));
            }
            int methodParameterCount = m.getParameterTypes().length;
            int inputParameterCount = node.jjtGetChild(0).jjtGetNumChildren();
            if (m.isVarArgs() && inputParameterCount < methodParameterCount - 1 || !m.isVarArgs() && inputParameterCount != methodParameterCount) {
                throw new ELException(MessageFactory.get("error.fnMapper.paramcount", funcNode.getOutputName(), "" + methodParameterCount, "" + node.jjtGetChild(0).jjtGetNumChildren()));
            }
        } else if (node instanceof AstIdentifier && this.varMapper != null) {
            String variable = node.getImage();
            this.varMapper.resolveVariable(variable);
        }
    }

    public ValueExpression createValueExpression(Class<?> expectedType) throws ELException {
        Node n = this.build();
        return new ValueExpressionImpl(this.expression, n, this.fnMapper, this.varMapper, expectedType);
    }

    public MethodExpression createMethodExpression(Class<?> expectedReturnType, Class<?>[] expectedParamTypes) throws ELException {
        Node n = this.build();
        if (!n.isParametersProvided() && expectedParamTypes == null) {
            throw new NullPointerException(MessageFactory.get("error.method.nullParms"));
        }
        if (n instanceof AstValue || n instanceof AstIdentifier) {
            return new MethodExpressionImpl(this.expression, n, this.fnMapper, this.varMapper, expectedReturnType, expectedParamTypes);
        }
        if (n instanceof AstLiteralExpression) {
            return new MethodExpressionLiteral(this.expression, expectedReturnType, expectedParamTypes);
        }
        throw new ELException(MessageFactory.get("error.invalidMethodExpression", this.expression));
    }

    static {
        String cacheSizeStr = System.getSecurityManager() == null ? System.getProperty(CACHE_SIZE_PROP, "5000") : AccessController.doPrivileged(() -> System.getProperty(CACHE_SIZE_PROP, "5000"));
        CACHE_SIZE = Integer.parseInt(cacheSizeStr);
        expressionCache = new ConcurrentCache(CACHE_SIZE);
    }

    private static class SynchronizedStack<T> {
        public static final int DEFAULT_SIZE = 128;
        private static final int DEFAULT_LIMIT = -1;
        private int size;
        private final int limit;
        private int index = -1;
        private Object[] stack;

        SynchronizedStack() {
            this(128, -1);
        }

        SynchronizedStack(int size, int limit) {
            this.size = size;
            this.limit = limit;
            this.stack = new Object[size];
        }

        public synchronized boolean push(T obj) {
            ++this.index;
            if (this.index == this.size) {
                if (this.limit == -1 || this.size < this.limit) {
                    this.expand();
                } else {
                    --this.index;
                    return false;
                }
            }
            this.stack[this.index] = obj;
            return true;
        }

        public synchronized T pop() {
            if (this.index == -1) {
                return null;
            }
            Object result = this.stack[this.index];
            this.stack[this.index--] = null;
            return (T)result;
        }

        private void expand() {
            int newSize = this.size * 2;
            if (this.limit != -1 && newSize > this.limit) {
                newSize = this.limit;
            }
            Object[] newStack = new Object[newSize];
            System.arraycopy(this.stack, 0, newStack, 0, this.size);
            this.stack = newStack;
            this.size = newSize;
        }
    }
}

