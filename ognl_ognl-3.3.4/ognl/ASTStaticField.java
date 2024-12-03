/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import ognl.NodeType;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;

public class ASTStaticField
extends SimpleNode
implements NodeType {
    private String className;
    private String fieldName;
    private Class _getterClass;

    public ASTStaticField(int id) {
        super(id);
    }

    public ASTStaticField(OgnlParser p, int id) {
        super(p, id);
    }

    void init(String className, String fieldName) {
        this.className = className;
        this.fieldName = fieldName;
    }

    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        return OgnlRuntime.getStaticField(context, this.className, this.fieldName);
    }

    @Override
    public boolean isNodeConstant(OgnlContext context) throws OgnlException {
        boolean result = false;
        Exception reason = null;
        try {
            Class c = OgnlRuntime.classForName(context, this.className);
            if (this.fieldName.equals("class")) {
                result = true;
            } else if (c.isEnum()) {
                result = true;
            } else {
                Field f = OgnlRuntime.getField(c, this.fieldName);
                if (f == null) {
                    throw new NoSuchFieldException(this.fieldName);
                }
                if (!Modifier.isStatic(f.getModifiers())) {
                    throw new OgnlException("Field " + this.fieldName + " of class " + this.className + " is not static");
                }
                result = Modifier.isFinal(f.getModifiers());
            }
        }
        catch (ClassNotFoundException e) {
            reason = e;
        }
        catch (NoSuchFieldException e) {
            reason = e;
        }
        catch (SecurityException e) {
            reason = e;
        }
        if (reason != null) {
            throw new OgnlException("Could not get static field " + this.fieldName + " from class " + this.className, reason);
        }
        return result;
    }

    Class getFieldClass(OgnlContext context) throws OgnlException {
        Exception reason = null;
        try {
            Class c = OgnlRuntime.classForName(context, this.className);
            if (this.fieldName.equals("class")) {
                return c;
            }
            if (c.isEnum()) {
                return c;
            }
            Field f = c.getField(this.fieldName);
            return f.getType();
        }
        catch (ClassNotFoundException e) {
            reason = e;
        }
        catch (NoSuchFieldException e) {
            reason = e;
        }
        catch (SecurityException e) {
            reason = e;
        }
        if (reason != null) {
            throw new OgnlException("Could not get static field " + this.fieldName + " from class " + this.className, reason);
        }
        return null;
    }

    @Override
    public Class getGetterClass() {
        return this._getterClass;
    }

    @Override
    public Class getSetterClass() {
        return this._getterClass;
    }

    @Override
    public String toString() {
        return "@" + this.className + "@" + this.fieldName;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        try {
            Object obj = OgnlRuntime.getStaticField(context, this.className, this.fieldName);
            context.setCurrentObject(obj);
            this._getterClass = this.getFieldClass(context);
            context.setCurrentType(this._getterClass);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return this.className + "." + this.fieldName;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        try {
            Object obj = OgnlRuntime.getStaticField(context, this.className, this.fieldName);
            context.setCurrentObject(obj);
            this._getterClass = this.getFieldClass(context);
            context.setCurrentType(this._getterClass);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        return this.className + "." + this.fieldName;
    }
}

