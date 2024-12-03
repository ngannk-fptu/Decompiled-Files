/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.ELResolver
 *  javax.el.LambdaExpression
 *  javax.el.MethodInfo
 *  javax.el.PropertyNotFoundException
 *  javax.el.ValueReference
 */
package org.apache.el.parser;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.LambdaExpression;
import javax.el.MethodInfo;
import javax.el.PropertyNotFoundException;
import javax.el.ValueReference;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.AstLambdaExpression;
import org.apache.el.parser.AstMethodParameters;
import org.apache.el.parser.Node;
import org.apache.el.parser.SimpleNode;
import org.apache.el.stream.Optional;
import org.apache.el.util.MessageFactory;
import org.apache.el.util.ReflectionUtil;

public final class AstValue
extends SimpleNode {
    private static final Object[] EMPTY_ARRAY = new Object[0];

    public AstValue(int id) {
        super(id);
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        Target t = this.getTarget(ctx);
        ctx.setPropertyResolved(false);
        Class result = ctx.getELResolver().getType((ELContext)ctx, t.base, t.property);
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled", t.base, t.property));
        }
        return result;
    }

    private Target getTarget(EvaluationContext ctx) throws ELException {
        Object base = this.children[0].getValue(ctx);
        if (base == null) {
            throw new PropertyNotFoundException(MessageFactory.get("error.unreachable.base", this.children[0].getImage()));
        }
        Object property = null;
        int propCount = this.jjtGetNumChildren();
        int i = 1;
        ELResolver resolver = ctx.getELResolver();
        while (i < propCount) {
            if (i + 2 < propCount && this.children[i + 1] instanceof AstMethodParameters) {
                base = resolver.invoke((ELContext)ctx, base, this.children[i].getValue(ctx), null, ((AstMethodParameters)this.children[i + 1]).getParameters(ctx));
                i += 2;
            } else if (i + 2 == propCount && this.children[i + 1] instanceof AstMethodParameters) {
                ctx.setPropertyResolved(false);
                property = this.children[i].getValue(ctx);
                i += 2;
                if (property == null) {
                    throw new PropertyNotFoundException(MessageFactory.get("error.unreachable.property", property));
                }
            } else if (i + 1 < propCount) {
                property = this.children[i].getValue(ctx);
                ctx.setPropertyResolved(false);
                base = resolver.getValue((ELContext)ctx, base, property);
                ++i;
            } else {
                ctx.setPropertyResolved(false);
                property = this.children[i].getValue(ctx);
                ++i;
                if (property == null) {
                    throw new PropertyNotFoundException(MessageFactory.get("error.unreachable.property", property));
                }
            }
            if (base != null) continue;
            throw new PropertyNotFoundException(MessageFactory.get("error.unreachable.property", property));
        }
        Target t = new Target();
        t.base = base;
        t.property = property;
        return t;
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        Object base = this.children[0].getValue(ctx);
        int propCount = this.jjtGetNumChildren();
        int i = 1;
        Object suffix = null;
        ELResolver resolver = ctx.getELResolver();
        while (base != null && i < propCount) {
            suffix = this.children[i].getValue(ctx);
            if (i + 1 < propCount && this.children[i + 1] instanceof AstMethodParameters) {
                Node paramFoOptional;
                AstMethodParameters mps = (AstMethodParameters)this.children[i + 1];
                if (base instanceof Optional && "orElseGet".equals(suffix) && mps.jjtGetNumChildren() == 1 && !((paramFoOptional = mps.jjtGetChild(0)) instanceof AstLambdaExpression) && !(paramFoOptional instanceof LambdaExpression)) {
                    throw new ELException(MessageFactory.get("stream.optional.paramNotLambda", suffix));
                }
                Object[] paramValues = mps.getParameters(ctx);
                base = resolver.invoke((ELContext)ctx, base, suffix, (Class[])this.getTypesFromValues(paramValues), paramValues);
                i += 2;
                continue;
            }
            if (suffix == null) {
                return null;
            }
            ctx.setPropertyResolved(false);
            base = resolver.getValue((ELContext)ctx, base, suffix);
            ++i;
        }
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled", base, suffix));
        }
        return base;
    }

    @Override
    public boolean isReadOnly(EvaluationContext ctx) throws ELException {
        Target t = this.getTarget(ctx);
        ctx.setPropertyResolved(false);
        boolean result = ctx.getELResolver().isReadOnly((ELContext)ctx, t.base, t.property);
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled", t.base, t.property));
        }
        return result;
    }

    @Override
    public void setValue(EvaluationContext ctx, Object value) throws ELException {
        Target t = this.getTarget(ctx);
        ctx.setPropertyResolved(false);
        ELResolver resolver = ctx.getELResolver();
        Class targetClass = resolver.getType((ELContext)ctx, t.base, t.property);
        resolver.setValue((ELContext)ctx, t.base, t.property, ELSupport.coerceToType(ctx, value, targetClass));
        if (!ctx.isPropertyResolved()) {
            throw new PropertyNotFoundException(MessageFactory.get("error.resolver.unhandled", t.base, t.property));
        }
    }

    public MethodInfo getMethodInfo(EvaluationContext ctx, Class[] paramTypes) throws ELException {
        Target t = this.getTarget(ctx);
        Class[] types = null;
        if (this.isParametersProvided()) {
            Object[] values = ((AstMethodParameters)this.jjtGetChild(this.jjtGetNumChildren() - 1)).getParameters(ctx);
            types = this.getTypesFromValues(values);
        } else {
            types = paramTypes;
        }
        Method m = ReflectionUtil.getMethod(ctx, t.base, t.property, types, null);
        return new MethodInfo(m.getName(), m.getReturnType(), (Class[])m.getParameterTypes());
    }

    public Object invoke(EvaluationContext ctx, Class[] paramTypes, Object[] paramValues) throws ELException {
        Target t = this.getTarget(ctx);
        Method m = null;
        Object[] values = null;
        Class[] types = null;
        if (this.isParametersProvided()) {
            values = ((AstMethodParameters)this.jjtGetChild(this.jjtGetNumChildren() - 1)).getParameters(ctx);
            types = this.getTypesFromValues(values);
        } else {
            values = paramValues;
            types = paramTypes;
        }
        m = ReflectionUtil.getMethod(ctx, t.base, t.property, types, values);
        values = this.convertArgs(ctx, values, m);
        Object result = null;
        try {
            result = m.invoke(t.base, values);
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ELException((Throwable)e);
        }
        catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof ThreadDeath) {
                throw (ThreadDeath)cause;
            }
            if (cause instanceof VirtualMachineError) {
                throw (VirtualMachineError)cause;
            }
            throw new ELException(cause);
        }
        return result;
    }

    private Object[] convertArgs(EvaluationContext ctx, Object[] src, Method m) {
        Class<?>[] types = m.getParameterTypes();
        if (types.length == 0) {
            return EMPTY_ARRAY;
        }
        int paramCount = types.length;
        if (m.isVarArgs() && paramCount > 1 && (src == null || paramCount > src.length) || !m.isVarArgs() && (paramCount > 0 && src == null || src != null && src.length != paramCount)) {
            String srcCount = null;
            if (src != null) {
                srcCount = Integer.toString(src.length);
            }
            String msg = m.isVarArgs() ? MessageFactory.get("error.invoke.tooFewParams", m.getName(), srcCount, Integer.toString(paramCount)) : MessageFactory.get("error.invoke.wrongParams", m.getName(), srcCount, Integer.toString(paramCount));
            throw new IllegalArgumentException(msg);
        }
        if (src == null) {
            return new Object[1];
        }
        Object[] dest = new Object[paramCount];
        for (int i = 0; i < paramCount - 1; ++i) {
            dest[i] = ELSupport.coerceToType(ctx, src[i], types[i]);
        }
        if (m.isVarArgs()) {
            Class<?> varArgType = m.getParameterTypes()[paramCount - 1].getComponentType();
            Object[] varArgs = (Object[])Array.newInstance(varArgType, src.length - (paramCount - 1));
            for (int i = 0; i < src.length - (paramCount - 1); ++i) {
                varArgs[i] = ELSupport.coerceToType(ctx, src[paramCount - 1 + i], varArgType);
            }
            dest[paramCount - 1] = varArgs;
        } else {
            dest[paramCount - 1] = ELSupport.coerceToType(ctx, src[paramCount - 1], types[paramCount - 1]);
        }
        return dest;
    }

    private Class<?>[] getTypesFromValues(Object[] values) {
        if (values == null) {
            return null;
        }
        Class[] result = new Class[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = values[i] == null ? null : values[i].getClass();
        }
        return result;
    }

    @Override
    public ValueReference getValueReference(EvaluationContext ctx) {
        if (this.children.length > 2 && this.jjtGetChild(2) instanceof AstMethodParameters) {
            return null;
        }
        Target t = this.getTarget(ctx);
        return new ValueReference(t.base, t.property);
    }

    @Override
    public boolean isParametersProvided() {
        int len = this.children.length;
        return len > 2 && this.jjtGetChild(len - 1) instanceof AstMethodParameters;
    }

    protected static class Target {
        protected Object base;
        protected Object property;

        protected Target() {
        }
    }
}

