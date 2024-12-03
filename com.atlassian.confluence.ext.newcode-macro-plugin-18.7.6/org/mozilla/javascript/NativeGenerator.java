/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeIterator;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

public final class NativeGenerator
extends IdScriptableObject {
    private static final long serialVersionUID = 1645892441041347273L;
    private static final Object GENERATOR_TAG = "Generator";
    public static final int GENERATOR_SEND = 0;
    public static final int GENERATOR_THROW = 1;
    public static final int GENERATOR_CLOSE = 2;
    private static final int Id_close = 1;
    private static final int Id_next = 2;
    private static final int Id_send = 3;
    private static final int Id_throw = 4;
    private static final int Id___iterator__ = 5;
    private static final int MAX_PROTOTYPE_ID = 5;
    private NativeFunction function;
    private Object savedState;
    private String lineSource;
    private int lineNumber;
    private boolean firstTime = true;
    private boolean locked;

    static NativeGenerator init(ScriptableObject scope, boolean sealed) {
        NativeGenerator prototype = new NativeGenerator();
        if (scope != null) {
            prototype.setParentScope(scope);
            prototype.setPrototype(NativeGenerator.getObjectPrototype(scope));
        }
        prototype.activatePrototypeMap(5);
        if (sealed) {
            prototype.sealObject();
        }
        if (scope != null) {
            scope.associateValue(GENERATOR_TAG, prototype);
        }
        return prototype;
    }

    private NativeGenerator() {
    }

    public NativeGenerator(Scriptable scope, NativeFunction function, Object savedState) {
        this.function = function;
        this.savedState = savedState;
        Scriptable top = ScriptableObject.getTopLevelScope(scope);
        this.setParentScope(top);
        NativeGenerator prototype = (NativeGenerator)ScriptableObject.getTopScopeValue(top, GENERATOR_TAG);
        this.setPrototype(prototype);
    }

    @Override
    public String getClassName() {
        return "Generator";
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case 1: {
                arity = 1;
                s = "close";
                break;
            }
            case 2: {
                arity = 1;
                s = "next";
                break;
            }
            case 3: {
                arity = 0;
                s = "send";
                break;
            }
            case 4: {
                arity = 0;
                s = "throw";
                break;
            }
            case 5: {
                arity = 1;
                s = "__iterator__";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(GENERATOR_TAG, id, s, arity);
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(GENERATOR_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        NativeGenerator generator = NativeGenerator.ensureType(thisObj, NativeGenerator.class, f);
        switch (id) {
            case 1: {
                return generator.resume(cx, scope, 2, new GeneratorClosedException());
            }
            case 2: {
                generator.firstTime = false;
                return generator.resume(cx, scope, 0, Undefined.instance);
            }
            case 3: {
                Object arg;
                Object object = arg = args.length > 0 ? args[0] : Undefined.instance;
                if (generator.firstTime && !arg.equals(Undefined.instance)) {
                    throw ScriptRuntime.typeErrorById("msg.send.newborn", new Object[0]);
                }
                return generator.resume(cx, scope, 0, arg);
            }
            case 4: {
                return generator.resume(cx, scope, 1, args.length > 0 ? args[0] : Undefined.instance);
            }
            case 5: {
                return thisObj;
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object resume(Context cx, Scriptable scope, int operation, Object value) {
        if (this.savedState == null) {
            if (operation == 2) {
                return Undefined.instance;
            }
            Object thrown = operation == 1 ? value : NativeIterator.getStopIterationObject(scope);
            throw new JavaScriptException(thrown, this.lineSource, this.lineNumber);
        }
        try {
            Object thrown = this;
            synchronized (thrown) {
                if (this.locked) {
                    throw ScriptRuntime.typeErrorById("msg.already.exec.gen", new Object[0]);
                }
                this.locked = true;
            }
            thrown = this.function.resumeGenerator(cx, scope, operation, this.savedState, value);
            return thrown;
        }
        catch (GeneratorClosedException e) {
            Object object = Undefined.instance;
            return object;
        }
        catch (RhinoException e) {
            this.lineNumber = e.lineNumber();
            this.lineSource = e.lineSource();
            this.savedState = null;
            throw e;
        }
        finally {
            NativeGenerator nativeGenerator = this;
            synchronized (nativeGenerator) {
                this.locked = false;
            }
            if (operation == 2) {
                this.savedState = null;
            }
        }
    }

    @Override
    protected int findPrototypeId(String s) {
        int id;
        switch (s) {
            case "close": {
                id = 1;
                break;
            }
            case "next": {
                id = 2;
                break;
            }
            case "send": {
                id = 3;
                break;
            }
            case "throw": {
                id = 4;
                break;
            }
            case "__iterator__": {
                id = 5;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }

    public static class GeneratorClosedException
    extends RuntimeException {
        private static final long serialVersionUID = 2561315658662379681L;
    }
}

