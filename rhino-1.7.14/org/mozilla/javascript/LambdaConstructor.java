/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Constructable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.LambdaFunction;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;

public class LambdaConstructor
extends LambdaFunction {
    private static final long serialVersionUID = 2691205302914111400L;
    public static final int CONSTRUCTOR_FUNCTION = 1;
    public static final int CONSTRUCTOR_NEW = 2;
    public static final int CONSTRUCTOR_DEFAULT = 3;
    private final transient Constructable targetConstructor;
    private final int flags;

    public LambdaConstructor(Scriptable scope, String name, int length, Constructable target) {
        super(scope, name, length, null);
        this.targetConstructor = target;
        this.flags = 3;
    }

    public LambdaConstructor(Scriptable scope, String name, int length, int flags, Constructable target) {
        super(scope, name, length, null);
        this.targetConstructor = target;
        this.flags = flags;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if ((this.flags & 1) == 0) {
            throw ScriptRuntime.typeErrorById("msg.constructor.no.function", this.getFunctionName());
        }
        return this.targetConstructor.construct(cx, scope, args);
    }

    @Override
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        if ((this.flags & 2) == 0) {
            throw ScriptRuntime.typeErrorById("msg.no.new", this.getFunctionName());
        }
        Scriptable obj = this.targetConstructor.construct(cx, scope, args);
        obj.setPrototype(this.getClassPrototype());
        obj.setParentScope(scope);
        return obj;
    }

    public void definePrototypeMethod(Scriptable scope, String name, int length, Callable target) {
        LambdaFunction f = new LambdaFunction(scope, name, length, target);
        ScriptableObject proto = this.getPrototypeScriptable();
        proto.defineProperty(name, (Object)f, 0);
    }

    public void definePrototypeMethod(Scriptable scope, String name, int length, Callable target, int attributes, int propertyAttributes) {
        LambdaFunction f = new LambdaFunction(scope, name, length, target);
        f.setStandardPropertyAttributes(propertyAttributes);
        ScriptableObject proto = this.getPrototypeScriptable();
        proto.defineProperty(name, (Object)f, attributes);
    }

    public void definePrototypeProperty(String name, Object value, int attributes) {
        ScriptableObject proto = this.getPrototypeScriptable();
        proto.defineProperty(name, value, attributes);
    }

    public void definePrototypeProperty(Symbol key, Object value, int attributes) {
        ScriptableObject proto = this.getPrototypeScriptable();
        proto.defineProperty(key, value, attributes);
    }

    public void defineConstructorMethod(Scriptable scope, String name, int length, Callable target, int attributes) {
        LambdaFunction f = new LambdaFunction(scope, name, length, target);
        this.defineProperty(name, (Object)f, attributes);
    }

    public void defineConstructorMethod(Scriptable scope, Symbol key, String name, int length, Callable target, int attributes) {
        LambdaFunction f = new LambdaFunction(scope, name, length, target);
        this.defineProperty(key, (Object)f, attributes);
    }

    public void defineConstructorMethod(Scriptable scope, String name, int length, Callable target, int attributes, int propertyAttributes) {
        LambdaFunction f = new LambdaFunction(scope, name, length, target);
        f.setStandardPropertyAttributes(propertyAttributes);
        this.defineProperty(name, (Object)f, attributes);
    }

    public static <T> T convertThisObject(Scriptable thisObj, Class<T> targetClass) {
        if (!targetClass.isInstance(thisObj)) {
            throw ScriptRuntime.typeErrorById("msg.this.not.instance", new Object[0]);
        }
        return (T)thisObj;
    }

    private ScriptableObject getPrototypeScriptable() {
        Object prop = this.getPrototypeProperty();
        if (!(prop instanceof ScriptableObject)) {
            throw ScriptRuntime.typeError("Not properly a lambda constructor");
        }
        return (ScriptableObject)prop;
    }
}

