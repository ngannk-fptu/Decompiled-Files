/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.List;
import ognl.ASTChain;
import ognl.ASTConst;
import ognl.ASTProperty;
import ognl.ASTRootVarRef;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlOps;
import ognl.OgnlParser;
import ognl.OgnlRuntime;
import ognl.SimpleNode;
import ognl.enhance.ExpressionCompiler;

public class ASTCtor
extends SimpleNode {
    private String className;
    private boolean isArray;

    public ASTCtor(int id) {
        super(id);
    }

    public ASTCtor(OgnlParser p, int id) {
        super(p, id);
    }

    void setClassName(String className) {
        this.className = className;
    }

    Class getCreatedClass(OgnlContext context) throws ClassNotFoundException {
        return OgnlRuntime.classForName(context, this.className);
    }

    void setArray(boolean value) {
        this.isArray = value;
    }

    public boolean isArray() {
        return this.isArray;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    @Override
    protected Object getValueBody(OgnlContext context, Object source) throws OgnlException {
        root = context.getRoot();
        count = this.jjtGetNumChildren();
        args = OgnlRuntime.getObjectArrayPool().create(count);
        try {
            for (i = 0; i < count; ++i) {
                args[i] = this._children[i].getValue(context, root);
            }
            if (!this.isArray) ** GOTO lbl33
            if (args.length == 1) {
                try {
                    componentClass = OgnlRuntime.classForName(context, this.className);
                    sourceList = null;
                    if (args[0] instanceof List) {
                        sourceList = (List)args[0];
                        size = sourceList.size();
                    } else {
                        size = (int)OgnlOps.longValue(args[0]);
                    }
                    result = Array.newInstance(componentClass, size);
                    if (sourceList == null) ** GOTO lbl34
                    converter = context.getTypeConverter();
                    icount = sourceList.size();
                    for (i = 0; i < icount; ++i) {
                        o = sourceList.get(i);
                        if (o == null || componentClass.isInstance(o)) {
                            Array.set(result, i, o);
                            continue;
                        }
                        Array.set(result, i, converter.convertValue(context, null, null, null, o, componentClass));
                    }
                }
                catch (ClassNotFoundException ex) {
                    throw new OgnlException("array component class '" + this.className + "' not found", ex);
                }
            } else {
                throw new OgnlException("only expect array size or fixed initializer list");
lbl33:
                // 1 sources

                result = OgnlRuntime.callConstructor(context, this.className, args);
            }
lbl34:
            // 3 sources

            var7_9 = result;
            return var7_9;
        }
        finally {
            OgnlRuntime.getObjectArrayPool().recycle(args);
        }
    }

    @Override
    public String toString() {
        String result = "new " + this.className;
        if (this.isArray) {
            result = this._children[0] instanceof ASTConst ? result + "[" + this._children[0] + "]" : result + "[] " + this._children[0];
        } else {
            result = result + "(";
            if (this._children != null && this._children.length > 0) {
                for (int i = 0; i < this._children.length; ++i) {
                    if (i > 0) {
                        result = result + ", ";
                    }
                    result = result + this._children[i];
                }
            }
            result = result + ")";
        }
        return result;
    }

    @Override
    public String toGetSourceString(OgnlContext context, Object target) {
        String result = "new " + this.className;
        Class<?> clazz = null;
        Object ctorValue = null;
        try {
            clazz = OgnlRuntime.classForName(context, this.className);
            ctorValue = this.getValueBody(context, target);
            context.setCurrentObject(ctorValue);
            if (clazz != null && ctorValue != null) {
                context.setCurrentType(ctorValue.getClass());
                context.setCurrentAccessor(ctorValue.getClass());
            }
            if (this.isArray) {
                context.put("_ctorClass", clazz);
            }
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        try {
            if (this.isArray) {
                result = this._children[0] instanceof ASTConst ? result + "[" + this._children[0].toGetSourceString(context, target) + "]" : (ASTProperty.class.isInstance(this._children[0]) ? result + "[" + ExpressionCompiler.getRootExpression(this._children[0], target, context) + this._children[0].toGetSourceString(context, target) + "]" : (ASTChain.class.isInstance(this._children[0]) ? result + "[" + this._children[0].toGetSourceString(context, target) + "]" : result + "[] " + this._children[0].toGetSourceString(context, target)));
            } else {
                result = result + "(";
                if (this._children != null && this._children.length > 0) {
                    int i;
                    Object[] values = new Object[this._children.length];
                    String[] expressions = new String[this._children.length];
                    Class[] types = new Class[this._children.length];
                    for (int i2 = 0; i2 < this._children.length; ++i2) {
                        Object objValue = this._children[i2].getValue(context, context.getRoot());
                        String value = this._children[i2].toGetSourceString(context, target);
                        if (!ASTRootVarRef.class.isInstance(this._children[i2])) {
                            value = ExpressionCompiler.getRootExpression(this._children[i2], target, context) + value;
                        }
                        String cast = "";
                        if (ExpressionCompiler.shouldCast(this._children[i2])) {
                            cast = (String)context.remove("_preCast");
                        }
                        if (cast == null) {
                            cast = "";
                        }
                        if (!ASTConst.class.isInstance(this._children[i2])) {
                            value = cast + value;
                        }
                        values[i2] = objValue;
                        expressions[i2] = value;
                        types[i2] = context.getCurrentType();
                    }
                    Constructor<?>[] cons = clazz.getConstructors();
                    Constructor<?> ctor = null;
                    Class[] ctorParamTypes = null;
                    for (i = 0; i < cons.length; ++i) {
                        Class[] ctorTypes = cons[i].getParameterTypes();
                        if (!OgnlRuntime.areArgsCompatible(values, ctorTypes) || ctor != null && !OgnlRuntime.isMoreSpecific(ctorTypes, ctorParamTypes)) continue;
                        ctor = cons[i];
                        ctorParamTypes = ctorTypes;
                    }
                    if (ctor == null) {
                        ctor = OgnlRuntime.getConvertedConstructorAndArgs(context, clazz, OgnlRuntime.getConstructors(clazz), values, new Object[values.length]);
                    }
                    if (ctor == null) {
                        throw new NoSuchMethodException("Unable to find constructor appropriate for arguments in class: " + clazz);
                    }
                    ctorParamTypes = ctor.getParameterTypes();
                    for (i = 0; i < this._children.length; ++i) {
                        String literal;
                        if (i > 0) {
                            result = result + ", ";
                        }
                        String value = expressions[i];
                        if (types[i].isPrimitive() && (literal = OgnlRuntime.getNumericLiteral(types[i])) != null) {
                            value = value + literal;
                        }
                        if (ctorParamTypes[i] != types[i]) {
                            if (!(values[i] == null || types[i].isPrimitive() || values[i].getClass().isArray() || ASTConst.class.isInstance(this._children[i]))) {
                                value = "(" + OgnlRuntime.getCompiler().getInterfaceClass(values[i].getClass()).getName() + ")" + value;
                            } else if (!ASTConst.class.isInstance(this._children[i]) || ASTConst.class.isInstance(this._children[i]) && !types[i].isPrimitive()) {
                                value = !types[i].isArray() && types[i].isPrimitive() && !ctorParamTypes[i].isPrimitive() ? "new " + ExpressionCompiler.getCastString(OgnlRuntime.getPrimitiveWrapperClass(types[i])) + "(" + value + ")" : " ($w) " + value;
                            }
                        }
                        result = result + value;
                    }
                }
                result = result + ")";
            }
            context.setCurrentType(ctorValue != null ? ctorValue.getClass() : clazz);
            context.setCurrentAccessor(clazz);
            context.setCurrentObject(ctorValue);
        }
        catch (Throwable t) {
            throw OgnlOps.castToRuntime(t);
        }
        context.remove("_ctorClass");
        return result;
    }

    @Override
    public String toSetSourceString(OgnlContext context, Object target) {
        return "";
    }
}

