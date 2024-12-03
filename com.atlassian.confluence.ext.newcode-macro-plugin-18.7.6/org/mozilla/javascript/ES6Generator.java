/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ES6Iterator;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeGenerator;
import org.mozilla.javascript.NativeIterator;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

public final class ES6Generator
extends IdScriptableObject {
    private static final long serialVersionUID = 1645892441041347273L;
    private static final Object GENERATOR_TAG = "Generator";
    private static final int Id_next = 1;
    private static final int Id_return = 2;
    private static final int Id_throw = 3;
    private static final int SymbolId_iterator = 4;
    private static final int MAX_PROTOTYPE_ID = 4;
    private NativeFunction function;
    private Object savedState;
    private String lineSource;
    private int lineNumber;
    private State state = State.SUSPENDED_START;
    private Object delegee;

    static ES6Generator init(ScriptableObject scope, boolean sealed) {
        ES6Generator prototype = new ES6Generator();
        if (scope != null) {
            prototype.setParentScope(scope);
            prototype.setPrototype(ES6Generator.getObjectPrototype(scope));
        }
        prototype.activatePrototypeMap(4);
        if (sealed) {
            prototype.sealObject();
        }
        if (scope != null) {
            scope.associateValue(GENERATOR_TAG, prototype);
        }
        return prototype;
    }

    private ES6Generator() {
    }

    public ES6Generator(Scriptable scope, NativeFunction function, Object savedState) {
        this.function = function;
        this.savedState = savedState;
        Scriptable top = ScriptableObject.getTopLevelScope(scope);
        this.setParentScope(top);
        ES6Generator prototype = (ES6Generator)ScriptableObject.getTopScopeValue(top, GENERATOR_TAG);
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
        if (id == 4) {
            this.initPrototypeMethod(GENERATOR_TAG, id, SymbolKey.ITERATOR, "[Symbol.iterator]", 0);
            return;
        }
        switch (id) {
            case 1: {
                arity = 1;
                s = "next";
                break;
            }
            case 2: {
                arity = 1;
                s = "return";
                break;
            }
            case 3: {
                arity = 1;
                s = "throw";
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
        ES6Generator generator = ES6Generator.ensureType(thisObj, ES6Generator.class, f);
        Object value = args.length >= 1 ? args[0] : Undefined.instance;
        switch (id) {
            case 2: {
                if (generator.delegee == null) {
                    return generator.resumeAbruptLocal(cx, scope, 2, value);
                }
                return generator.resumeDelegeeReturn(cx, scope, value);
            }
            case 1: {
                if (generator.delegee == null) {
                    return generator.resumeLocal(cx, scope, value);
                }
                return generator.resumeDelegee(cx, scope, value);
            }
            case 3: {
                if (generator.delegee == null) {
                    return generator.resumeAbruptLocal(cx, scope, 1, value);
                }
                return generator.resumeDelegeeThrow(cx, scope, value);
            }
            case 4: {
                return thisObj;
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    private Scriptable resumeDelegee(Context cx, Scriptable scope, Object value) {
        try {
            Object[] objectArray;
            if (Undefined.instance.equals(value)) {
                objectArray = ScriptRuntime.emptyArgs;
            } else {
                Object[] objectArray2 = new Object[1];
                objectArray = objectArray2;
                objectArray2[0] = value;
            }
            Object[] nextArgs = objectArray;
            Callable nextFn = ScriptRuntime.getPropFunctionAndThis(this.delegee, "next", cx, scope);
            Scriptable nextThis = ScriptRuntime.lastStoredScriptable(cx);
            Object nr = nextFn.call(cx, scope, nextThis, nextArgs);
            Scriptable nextResult = ScriptableObject.ensureScriptable(nr);
            if (ScriptRuntime.isIteratorDone(cx, nextResult)) {
                this.delegee = null;
                return this.resumeLocal(cx, scope, ScriptableObject.getProperty(nextResult, "value"));
            }
            return nextResult;
        }
        catch (RhinoException re) {
            this.delegee = null;
            return this.resumeAbruptLocal(cx, scope, 1, re);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Scriptable resumeDelegeeThrow(Context cx, Scriptable scope, Object value) {
        boolean returnCalled = false;
        try {
            Callable throwFn = ScriptRuntime.getPropFunctionAndThis(this.delegee, "throw", cx, scope);
            Scriptable nextThis = ScriptRuntime.lastStoredScriptable(cx);
            Object throwResult = throwFn.call(cx, scope, nextThis, new Object[]{value});
            if (ScriptRuntime.isIteratorDone(cx, throwResult)) {
                try {
                    returnCalled = true;
                    this.callReturnOptionally(cx, scope, Undefined.instance);
                }
                finally {
                    this.delegee = null;
                }
                return this.resumeLocal(cx, scope, ScriptRuntime.getObjectProp(throwResult, "value", cx, scope));
            }
            return ES6Generator.ensureScriptable(throwResult);
        }
        catch (RhinoException re) {
            block12: {
                try {
                    if (returnCalled) break block12;
                    try {
                        this.callReturnOptionally(cx, scope, Undefined.instance);
                    }
                    catch (RhinoException re2) {
                        Scriptable scriptable = this.resumeAbruptLocal(cx, scope, 1, re2);
                        this.delegee = null;
                        return scriptable;
                    }
                }
                finally {
                    this.delegee = null;
                }
            }
            return this.resumeAbruptLocal(cx, scope, 1, re);
        }
    }

    private Scriptable resumeDelegeeReturn(Context cx, Scriptable scope, Object value) {
        try {
            Object retResult = this.callReturnOptionally(cx, scope, value);
            if (retResult != null) {
                if (ScriptRuntime.isIteratorDone(cx, retResult)) {
                    this.delegee = null;
                    return this.resumeAbruptLocal(cx, scope, 2, ScriptRuntime.getObjectPropNoWarn(retResult, "value", cx, scope));
                }
                return ES6Generator.ensureScriptable(retResult);
            }
            this.delegee = null;
            return this.resumeAbruptLocal(cx, scope, 2, value);
        }
        catch (RhinoException re) {
            this.delegee = null;
            return this.resumeAbruptLocal(cx, scope, 1, re);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Scriptable resumeLocal(Context cx, Scriptable scope, Object value) {
        Scriptable result;
        block27: {
            if (this.state == State.COMPLETED) {
                return ES6Iterator.makeIteratorResult(cx, scope, Boolean.TRUE);
            }
            if (this.state == State.EXECUTING) {
                throw ScriptRuntime.typeErrorById("msg.generator.executing", new Object[0]);
            }
            result = ES6Iterator.makeIteratorResult(cx, scope, Boolean.FALSE);
            this.state = State.EXECUTING;
            try {
                Object r = this.function.resumeGenerator(cx, scope, 0, this.savedState, value);
                if (r instanceof YieldStarResult) {
                    Scriptable delResult;
                    this.state = State.SUSPENDED_YIELD;
                    YieldStarResult ysResult = (YieldStarResult)r;
                    try {
                        this.delegee = ScriptRuntime.callIterator(ysResult.getResult(), cx, scope);
                    }
                    catch (RhinoException re) {
                        Scriptable scriptable = this.resumeAbruptLocal(cx, scope, 1, re);
                        if (this.state == State.COMPLETED) {
                            ScriptableObject.putProperty(result, "done", (Object)Boolean.TRUE);
                        } else {
                            this.state = State.SUSPENDED_YIELD;
                        }
                        return scriptable;
                    }
                    try {
                        delResult = this.resumeDelegee(cx, scope, Undefined.instance);
                    }
                    finally {
                        this.state = State.EXECUTING;
                    }
                    if (ScriptRuntime.isIteratorDone(cx, delResult)) {
                        this.state = State.COMPLETED;
                    }
                    Scriptable scriptable = delResult;
                    return scriptable;
                }
                ScriptableObject.putProperty(result, "value", r);
            }
            catch (NativeGenerator.GeneratorClosedException gce) {
                this.state = State.COMPLETED;
            }
            catch (JavaScriptException jse) {
                this.state = State.COMPLETED;
                if (jse.getValue() instanceof NativeIterator.StopIteration) {
                    ScriptableObject.putProperty(result, "value", ((NativeIterator.StopIteration)jse.getValue()).getValue());
                    break block27;
                }
                this.lineNumber = jse.lineNumber();
                this.lineSource = jse.lineSource();
                if (jse.getValue() instanceof RhinoException) {
                    throw (RhinoException)jse.getValue();
                }
                throw jse;
            }
            catch (RhinoException re) {
                this.lineNumber = re.lineNumber();
                this.lineSource = re.lineSource();
                throw re;
            }
            finally {
                if (this.state == State.COMPLETED) {
                    ScriptableObject.putProperty(result, "done", (Object)Boolean.TRUE);
                } else {
                    this.state = State.SUSPENDED_YIELD;
                }
            }
        }
        return result;
    }

    private Scriptable resumeAbruptLocal(Context cx, Scriptable scope, int op, Object value) {
        Scriptable result;
        block22: {
            if (this.state == State.EXECUTING) {
                throw ScriptRuntime.typeErrorById("msg.generator.executing", new Object[0]);
            }
            if (this.state == State.SUSPENDED_START) {
                this.state = State.COMPLETED;
            }
            result = ES6Iterator.makeIteratorResult(cx, scope, Boolean.FALSE);
            if (this.state == State.COMPLETED) {
                if (op == 1) {
                    throw new JavaScriptException(value, this.lineSource, this.lineNumber);
                }
                ScriptableObject.putProperty(result, "done", (Object)Boolean.TRUE);
                return result;
            }
            this.state = State.EXECUTING;
            Object throwValue = value;
            if (op == 2) {
                if (!(value instanceof NativeGenerator.GeneratorClosedException)) {
                    throwValue = new NativeGenerator.GeneratorClosedException();
                }
            } else if (value instanceof JavaScriptException) {
                throwValue = ((JavaScriptException)value).getValue();
            } else if (value instanceof RhinoException) {
                throwValue = ScriptRuntime.wrapException((Throwable)value, scope, cx);
            }
            try {
                Object r = this.function.resumeGenerator(cx, scope, op, this.savedState, throwValue);
                ScriptableObject.putProperty(result, "value", r);
                this.state = State.SUSPENDED_YIELD;
            }
            catch (NativeGenerator.GeneratorClosedException gce) {
                this.state = State.COMPLETED;
            }
            catch (JavaScriptException jse) {
                this.state = State.COMPLETED;
                if (jse.getValue() instanceof NativeIterator.StopIteration) {
                    ScriptableObject.putProperty(result, "value", ((NativeIterator.StopIteration)jse.getValue()).getValue());
                    break block22;
                }
                this.lineNumber = jse.lineNumber();
                this.lineSource = jse.lineSource();
                if (jse.getValue() instanceof RhinoException) {
                    throw (RhinoException)jse.getValue();
                }
                throw jse;
            }
            catch (RhinoException re) {
                this.state = State.COMPLETED;
                this.lineNumber = re.lineNumber();
                this.lineSource = re.lineSource();
                throw re;
            }
            finally {
                if (this.state == State.COMPLETED) {
                    this.delegee = null;
                    ScriptableObject.putProperty(result, "done", (Object)Boolean.TRUE);
                }
            }
        }
        return result;
    }

    private Object callReturnOptionally(Context cx, Scriptable scope, Object value) {
        Object[] objectArray;
        if (Undefined.instance.equals(value)) {
            objectArray = ScriptRuntime.emptyArgs;
        } else {
            Object[] objectArray2 = new Object[1];
            objectArray = objectArray2;
            objectArray2[0] = value;
        }
        Object[] retArgs = objectArray;
        Object retFnObj = ScriptRuntime.getObjectPropNoWarn(this.delegee, "return", cx, scope);
        if (!Undefined.instance.equals(retFnObj)) {
            if (!(retFnObj instanceof Callable)) {
                throw ScriptRuntime.typeErrorById("msg.isnt.function", "return", ScriptRuntime.typeof(retFnObj));
            }
            return ((Callable)retFnObj).call(cx, scope, ES6Generator.ensureScriptable(this.delegee), retArgs);
        }
        return null;
    }

    @Override
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.ITERATOR.equals(k)) {
            return 4;
        }
        return 0;
    }

    @Override
    protected int findPrototypeId(String s) {
        int id;
        block5: {
            id = 0;
            String X = null;
            int s_length = s.length();
            if (s_length == 4) {
                X = "next";
                id = 1;
            } else if (s_length == 5) {
                X = "throw";
                id = 3;
            } else if (s_length == 6) {
                X = "return";
                id = 2;
            }
            if (X == null || X == s || X.equals(s)) break block5;
            id = 0;
        }
        return id;
    }

    public static final class YieldStarResult {
        private Object result;

        public YieldStarResult(Object result) {
            this.result = result;
        }

        Object getResult() {
            return this.result;
        }
    }

    static enum State {
        SUSPENDED_START,
        SUSPENDED_YIELD,
        EXECUTING,
        COMPLETED;

    }
}

