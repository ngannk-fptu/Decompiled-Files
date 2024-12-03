/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.ClassUtils;
import org.apache.velocity.util.introspection.VelMethod;

public class ASTIndex
extends SimpleNode {
    private final String methodName = "get";
    protected boolean strictRef = false;
    private static final Object[] noParams = new Object[0];
    private static final Class[] noTypes = new Class[0];

    public ASTIndex(int i) {
        super(i);
    }

    public ASTIndex(Parser p, int i) {
        super(p, i);
    }

    public Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        super.init(context, data);
        this.strictRef = this.rsvc.getBoolean("runtime.references.strict", false);
        return data;
    }

    public static Object adjMinusIndexArg(Object argument, Object o, InternalContextAdapter context, SimpleNode node) {
        if (argument instanceof Integer && (Integer)argument < 0) {
            VelMethod method = ClassUtils.getMethod("size", noParams, noTypes, o, context, node, false);
            if (method == null) {
                throw new VelocityException("A 'size()' method required for negative value " + (Integer)argument + " does not exist for class '" + o.getClass().getName() + "' at " + Log.formatFileString(node));
            }
            Object size = null;
            try {
                size = method.invoke(o, noParams);
            }
            catch (Exception e) {
                throw new VelocityException("Error trying to calls the 'size()' method on '" + o.getClass().getName() + "' at " + Log.formatFileString(node), e);
            }
            int sizeint = 0;
            try {
                sizeint = (Integer)size;
            }
            catch (ClassCastException e) {
                throw new VelocityException("Method 'size()' on class '" + o.getClass().getName() + "' returned '" + size.getClass().getName() + "' when Integer was expected at " + Log.formatFileString(node));
            }
            argument = new Integer(sizeint + (Integer)argument);
        }
        return argument;
    }

    public Object execute(Object o, InternalContextAdapter context) throws MethodInvocationException {
        Object argument = this.jjtGetChild(0).value(context);
        argument = ASTIndex.adjMinusIndexArg(argument, o, context, this);
        Object[] params = new Object[]{argument};
        Class[] paramClasses = new Class[]{argument == null ? null : argument.getClass()};
        VelMethod method = ClassUtils.getMethod("get", params, paramClasses, o, context, this, this.strictRef);
        if (method == null) {
            return null;
        }
        try {
            Object obj = method.invoke(o, params);
            if (obj == null && method.getReturnType() == Void.TYPE) {
                return "";
            }
            return obj;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "Error invoking method 'get(" + (argument == null ? "null" : argument.getClass().getName()) + ")' in " + o.getClass().getName() + " at " + Log.formatFileString(this);
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
    }
}

