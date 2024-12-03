/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.parser.node;

import java.lang.reflect.InvocationTargetException;
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
import org.apache.velocity.util.introspection.VelPropertyGet;

public class ASTIdentifier
extends SimpleNode {
    private String identifier = "";
    protected Info uberInfo;
    protected boolean strictRef = false;

    public ASTIdentifier(int id) {
        super(id);
    }

    public ASTIdentifier(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        super.init(context, data);
        this.identifier = this.getFirstToken().image;
        this.uberInfo = new Info(this.getTemplateName(), this.getLine(), this.getColumn());
        this.strictRef = this.rsvc.getBoolean("runtime.references.strict", false);
        return data;
    }

    @Override
    public Object execute(Object o, InternalContextAdapter context) throws MethodInvocationException {
        VelPropertyGet vg = null;
        try {
            IntrospectionCacheData icd = context.icacheGet(this);
            if (icd != null && o != null && icd.contextData == o.getClass()) {
                vg = (VelPropertyGet)icd.thingy;
            } else {
                vg = this.rsvc.getUberspect().getPropertyGet(o, this.identifier, this.uberInfo);
                if (vg != null && vg.isCacheable() && o != null) {
                    icd = new IntrospectionCacheData();
                    icd.contextData = o.getClass();
                    icd.thingy = vg;
                    context.icachePut(this, icd);
                }
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "ASTIdentifier.execute() : identifier = " + this.identifier;
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
        if (vg == null) {
            if (this.strictRef) {
                throw new MethodInvocationException("Object '" + o.getClass().getName() + "' does not contain property '" + this.identifier + "'", null, this.identifier, this.uberInfo.getTemplateName(), this.uberInfo.getLine(), this.uberInfo.getColumn());
            }
            return null;
        }
        try {
            return vg.invoke(o);
        }
        catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t instanceof Exception) {
                try {
                    return EventHandlerUtil.methodException(this.rsvc, context, o.getClass(), vg.getMethodName(), (Exception)t);
                }
                catch (Exception e) {
                    throw new MethodInvocationException("Invocation of method '" + vg.getMethodName() + "' in  " + o.getClass() + " threw exception " + ite.getTargetException().toString(), ite.getTargetException(), vg.getMethodName(), this.getTemplateName(), this.getLine(), this.getColumn());
                }
            }
            throw new MethodInvocationException("Invocation of method '" + vg.getMethodName() + "' in  " + o.getClass() + " threw exception " + ite.getTargetException().toString(), ite.getTargetException(), vg.getMethodName(), this.getTemplateName(), this.getLine(), this.getColumn());
        }
        catch (IllegalArgumentException iae) {
            return null;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "ASTIdentifier() : exception invoking method for identifier '" + this.identifier + "' in " + o.getClass();
            this.log.error(msg, e);
            throw new VelocityException(msg, e);
        }
    }
}

