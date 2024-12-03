/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.velocity.runtime.parser.node;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.velocity.app.event.EventHandlerUtil;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.IntrospectionCacheData;
import org.apache.velocity.util.introspection.VelMethod;

public class ASTMethod
extends SimpleNode {
    private String methodName = "";
    private int paramCount = 0;
    protected Info uberInfo;
    protected boolean strictRef = false;

    public ASTMethod(int id) {
        super(id);
    }

    public ASTMethod(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        super.init(context, data);
        this.uberInfo = new Info(this.getTemplateName(), this.getLine(), this.getColumn());
        this.methodName = this.getFirstToken().image;
        this.paramCount = this.jjtGetNumChildren() - 1;
        this.strictRef = this.rsvc.getBoolean("runtime.references.strict", false);
        return data;
    }

    @Override
    public Object execute(Object o, InternalContextAdapter context) throws MethodInvocationException {
        VelMethod method = null;
        Object[] params = new Object[this.paramCount];
        try {
            Class[] paramClasses = this.paramCount > 0 ? new Class[this.paramCount] : ArrayUtils.EMPTY_CLASS_ARRAY;
            for (int j = 0; j < this.paramCount; ++j) {
                params[j] = this.jjtGetChild(j + 1).value(context);
                if (params[j] == null) continue;
                paramClasses[j] = params[j].getClass();
            }
            MethodCacheKey mck = new MethodCacheKey(this.methodName, paramClasses);
            IntrospectionCacheData icd = context.icacheGet(mck);
            if (icd != null && o != null && icd.contextData == o.getClass()) {
                method = (VelMethod)icd.thingy;
            } else {
                method = this.rsvc.getUberspect().getMethod(o, this.methodName, params, new Info(this.getTemplateName(), this.getLine(), this.getColumn()));
                if (method != null && o != null) {
                    icd = new IntrospectionCacheData();
                    icd.contextData = o.getClass();
                    icd.thingy = method;
                    context.icachePut(mck, icd);
                }
            }
            if (method == null) {
                if (this.strictRef) {
                    StringBuffer plist = new StringBuffer();
                    for (int i = 0; i < params.length; ++i) {
                        Class param = paramClasses[i];
                        plist.append(param == null ? "null" : param.getName());
                        if (i >= params.length - 1) continue;
                        plist.append(", ");
                    }
                    throw new MethodInvocationException("Object '" + o.getClass().getName() + "' does not contain method " + this.methodName + "(" + plist + ")", null, this.methodName, this.uberInfo.getTemplateName(), this.uberInfo.getLine(), this.uberInfo.getColumn());
                }
                return null;
            }
        }
        catch (MethodInvocationException mie) {
            throw mie;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "ASTMethod.execute() : exception from introspection";
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
        try {
            Object obj = method.invoke(o, params);
            if (obj == null && method.getReturnType() == Void.TYPE) {
                return "";
            }
            return obj;
        }
        catch (InvocationTargetException ite) {
            return this.handleInvocationException(o, context, ite.getTargetException());
        }
        catch (IllegalArgumentException t) {
            return this.handleInvocationException(o, context, t);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "ASTMethod.execute() : exception invoking method '" + this.methodName + "' in " + o.getClass();
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
    }

    private Object handleInvocationException(Object o, InternalContextAdapter context, Throwable t) {
        if (t instanceof Exception) {
            try {
                return EventHandlerUtil.methodException(this.rsvc, context, o.getClass(), this.methodName, (Exception)t);
            }
            catch (Exception e) {
                throw new MethodInvocationException("Invocation of method '" + this.methodName + "' in  " + o.getClass() + " threw exception " + e.toString(), e, this.methodName, this.getTemplateName(), this.getLine(), this.getColumn());
            }
        }
        throw new MethodInvocationException("Invocation of method '" + this.methodName + "' in  " + o.getClass() + " threw exception " + t.toString(), t, this.methodName, this.getTemplateName(), this.getLine(), this.getColumn());
    }

    public String getMethodName() {
        return this.methodName;
    }

    public static class MethodCacheKey {
        private final String methodName;
        private final Class[] params;

        public MethodCacheKey(String methodName, Class[] params) {
            this.methodName = methodName != null ? methodName : "";
            this.params = params != null ? params : ArrayUtils.EMPTY_CLASS_ARRAY;
        }

        public boolean equals(Object o) {
            if (o instanceof MethodCacheKey) {
                MethodCacheKey other = (MethodCacheKey)o;
                if (this.params.length == other.params.length && this.methodName.equals(other.methodName)) {
                    for (int i = 0; i < this.params.length; ++i) {
                        if (!(this.params[i] == null ? this.params[i] != other.params[i] : !this.params[i].equals(other.params[i]))) continue;
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            int result = 17;
            for (int i = 0; i < this.params.length; ++i) {
                Class param = this.params[i];
                if (param == null) continue;
                result = result * 37 + param.hashCode();
            }
            result = result * 37 + this.methodName.hashCode();
            return result;
        }
    }
}

