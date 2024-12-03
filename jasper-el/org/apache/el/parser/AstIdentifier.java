/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELClass
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.MethodExpression
 *  javax.el.MethodInfo
 *  javax.el.MethodNotFoundException
 *  javax.el.PropertyNotFoundException
 *  javax.el.ValueExpression
 *  javax.el.ValueReference
 *  javax.el.VariableMapper
 */
package org.apache.el.parser;

import javax.el.ELClass;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.el.ValueReference;
import javax.el.VariableMapper;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.AstValue;
import org.apache.el.parser.SimpleNode;
import org.apache.el.util.MessageFactory;
import org.apache.el.util.Validation;

public final class AstIdentifier
extends SimpleNode {
    public AstIdentifier(int id) {
        super(id);
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        ValueExpression expr;
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null && (expr = varMapper.resolveVariable(this.image)) != null) {
            return expr.getType(ctx.getELContext());
        }
        ctx.setPropertyResolved(false);
        Class result = ctx.getELResolver().getType((ELContext)ctx, null, (Object)this.image);
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled.null", this.image));
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object result;
        ValueExpression expr;
        if (ctx.isLambdaArgument(this.image)) {
            return ctx.getLambdaArgument(this.image);
        }
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null && (expr = varMapper.resolveVariable(this.image)) != null) {
            return expr.getValue(ctx.getELContext());
        }
        ctx.setPropertyResolved(false);
        if (this.parent instanceof AstValue) {
            ctx.putContext(this.getClass(), Boolean.FALSE);
        } else {
            ctx.putContext(this.getClass(), Boolean.TRUE);
        }
        try {
            result = ctx.getELResolver().getValue((ELContext)ctx, null, (Object)this.image);
        }
        finally {
            ctx.putContext(this.getClass(), Boolean.FALSE);
        }
        if (ctx.isPropertyResolved()) {
            return result;
        }
        result = ctx.getImportHandler().resolveClass(this.image);
        if (result != null) {
            return new ELClass((Class)result);
        }
        result = ctx.getImportHandler().resolveStatic(this.image);
        if (result != null) {
            try {
                return ((Class)result).getField(this.image).get(null);
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                throw new ELException((Throwable)e);
            }
        }
        throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled.null", this.image));
    }

    @Override
    public boolean isReadOnly(EvaluationContext ctx) throws ELException {
        ValueExpression expr;
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null && (expr = varMapper.resolveVariable(this.image)) != null) {
            return expr.isReadOnly(ctx.getELContext());
        }
        ctx.setPropertyResolved(false);
        boolean result = ctx.getELResolver().isReadOnly((ELContext)ctx, null, (Object)this.image);
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled.null", this.image));
        }
        return result;
    }

    @Override
    public void setValue(EvaluationContext ctx, Object value) throws ELException {
        ValueExpression expr;
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper != null && (expr = varMapper.resolveVariable(this.image)) != null) {
            expr.setValue(ctx.getELContext(), value);
            return;
        }
        ctx.setPropertyResolved(false);
        ctx.getELResolver().setValue((ELContext)ctx, null, (Object)this.image, value);
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled.null", this.image));
        }
    }

    @Override
    public Object invoke(EvaluationContext ctx, Class<?>[] paramTypes, Object[] paramValues) throws ELException {
        return this.getMethodExpression(ctx).invoke(ctx.getELContext(), paramValues);
    }

    @Override
    public MethodInfo getMethodInfo(EvaluationContext ctx, Class<?>[] paramTypes) throws ELException {
        return this.getMethodExpression(ctx).getMethodInfo(ctx.getELContext());
    }

    @Override
    public void setImage(String image) {
        if (!Validation.isIdentifier(image)) {
            throw new ELException(MessageFactory.get("error.identifier.notjava", image));
        }
        this.image = image;
    }

    @Override
    public ValueReference getValueReference(EvaluationContext ctx) {
        VariableMapper varMapper = ctx.getVariableMapper();
        if (varMapper == null) {
            return null;
        }
        ValueExpression expr = varMapper.resolveVariable(this.image);
        if (expr == null) {
            return null;
        }
        return expr.getValueReference((ELContext)ctx);
    }

    private MethodExpression getMethodExpression(EvaluationContext ctx) throws ELException {
        Object obj = null;
        VariableMapper varMapper = ctx.getVariableMapper();
        ValueExpression ve = null;
        if (varMapper != null && (ve = varMapper.resolveVariable(this.image)) != null) {
            obj = ve.getValue((ELContext)ctx);
        }
        if (ve == null) {
            ctx.setPropertyResolved(false);
            obj = ctx.getELResolver().getValue((ELContext)ctx, null, (Object)this.image);
        }
        if (obj instanceof MethodExpression) {
            return (MethodExpression)obj;
        }
        if (obj == null) {
            throw new MethodNotFoundException(MessageFactory.get("error.identifier.noMethod", this.image));
        }
        throw new ELException(MessageFactory.get("error.identifier.notMethodExpression", this.image, obj.getClass().getName()));
    }
}

