/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.NativeArrayIterator;
import org.mozilla.javascript.NativeCall;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.UniqueTag;

final class Arguments
extends IdScriptableObject {
    private static final long serialVersionUID = 4275508002492040609L;
    private static final String FTAG = "Arguments";
    private static final int Id_callee = 1;
    private static final int Id_length = 2;
    private static final int Id_caller = 3;
    private static final int MAX_INSTANCE_ID = 3;
    private static BaseFunction iteratorMethod = new BaseFunction(){
        private static final long serialVersionUID = 4239122318596177391L;

        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            return new NativeArrayIterator(scope, thisObj, NativeArrayIterator.ARRAY_ITERATOR_TYPE.VALUES);
        }
    };
    private Object callerObj;
    private Object calleeObj;
    private Object lengthObj;
    private int callerAttr = 2;
    private int calleeAttr = 2;
    private int lengthAttr = 2;
    private NativeCall activation;
    private Object[] args;

    public Arguments(NativeCall activation) {
        this.activation = activation;
        Scriptable parent = activation.getParentScope();
        this.setParentScope(parent);
        this.setPrototype(ScriptableObject.getObjectPrototype(parent));
        this.args = activation.originalArgs;
        this.lengthObj = this.args.length;
        NativeFunction f = activation.function;
        this.calleeObj = f;
        int version = f.getLanguageVersion();
        this.callerObj = version <= 130 && version != 0 ? null : NOT_FOUND;
        this.defineProperty(SymbolKey.ITERATOR, (Object)iteratorMethod, 2);
    }

    @Override
    public String getClassName() {
        return FTAG;
    }

    private Object arg(int index) {
        if (index < 0 || this.args.length <= index) {
            return NOT_FOUND;
        }
        return this.args[index];
    }

    private void putIntoActivation(int index, Object value) {
        String argName = this.activation.function.getParamOrVarName(index);
        this.activation.put(argName, (Scriptable)this.activation, value);
    }

    private Object getFromActivation(int index) {
        String argName = this.activation.function.getParamOrVarName(index);
        return this.activation.get(argName, (Scriptable)this.activation);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void replaceArg(int index, Object value) {
        if (this.sharedWithActivation(index)) {
            this.putIntoActivation(index, value);
        }
        Arguments arguments = this;
        synchronized (arguments) {
            if (this.args == this.activation.originalArgs) {
                this.args = (Object[])this.args.clone();
            }
            this.args[index] = value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeArg(int index) {
        Arguments arguments = this;
        synchronized (arguments) {
            if (this.args[index] != NOT_FOUND) {
                if (this.args == this.activation.originalArgs) {
                    this.args = (Object[])this.args.clone();
                }
                this.args[index] = NOT_FOUND;
            }
        }
    }

    @Override
    public boolean has(int index, Scriptable start) {
        if (this.arg(index) != NOT_FOUND) {
            return true;
        }
        return super.has(index, start);
    }

    @Override
    public Object get(int index, Scriptable start) {
        Object value = this.arg(index);
        if (value == NOT_FOUND) {
            return super.get(index, start);
        }
        if (this.sharedWithActivation(index)) {
            return this.getFromActivation(index);
        }
        return value;
    }

    private boolean sharedWithActivation(int index) {
        Context cx = Context.getContext();
        if (cx.isStrictMode()) {
            return false;
        }
        NativeFunction f = this.activation.function;
        int definedCount = f.getParamCount();
        if (index < definedCount) {
            if (index < definedCount - 1) {
                String argName = f.getParamOrVarName(index);
                for (int i = index + 1; i < definedCount; ++i) {
                    if (!argName.equals(f.getParamOrVarName(i))) continue;
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        if (this.arg(index) == NOT_FOUND) {
            super.put(index, start, value);
        } else {
            this.replaceArg(index, value);
        }
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        super.put(name, start, value);
    }

    @Override
    public void delete(int index) {
        if (0 <= index && index < this.args.length) {
            this.removeArg(index);
        }
        super.delete(index);
    }

    @Override
    protected int getMaxInstanceId() {
        return 3;
    }

    @Override
    protected int findInstanceIdInfo(String s) {
        int attr;
        int id;
        switch (s) {
            case "callee": {
                id = 1;
                break;
            }
            case "length": {
                id = 2;
                break;
            }
            case "caller": {
                id = 3;
                break;
            }
            default: {
                id = 0;
            }
        }
        Context cx = Context.getContext();
        if (cx.isStrictMode() && (id == 1 || id == 3)) {
            return super.findInstanceIdInfo(s);
        }
        if (id == 0) {
            return super.findInstanceIdInfo(s);
        }
        switch (id) {
            case 1: {
                attr = this.calleeAttr;
                break;
            }
            case 3: {
                attr = this.callerAttr;
                break;
            }
            case 2: {
                attr = this.lengthAttr;
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return Arguments.instanceIdInfo(attr, id);
    }

    @Override
    protected String getInstanceIdName(int id) {
        switch (id) {
            case 1: {
                return "callee";
            }
            case 2: {
                return "length";
            }
            case 3: {
                return "caller";
            }
        }
        return null;
    }

    @Override
    protected Object getInstanceIdValue(int id) {
        switch (id) {
            case 1: {
                return this.calleeObj;
            }
            case 2: {
                return this.lengthObj;
            }
            case 3: {
                NativeCall caller;
                Object value = this.callerObj;
                if (value == UniqueTag.NULL_VALUE) {
                    value = null;
                } else if (value == null && (caller = this.activation.parentActivationCall) != null) {
                    value = caller.get("arguments", (Scriptable)caller);
                }
                return value;
            }
        }
        return super.getInstanceIdValue(id);
    }

    @Override
    protected void setInstanceIdValue(int id, Object value) {
        switch (id) {
            case 1: {
                this.calleeObj = value;
                return;
            }
            case 2: {
                this.lengthObj = value;
                return;
            }
            case 3: {
                this.callerObj = value != null ? value : UniqueTag.NULL_VALUE;
                return;
            }
        }
        super.setInstanceIdValue(id, value);
    }

    @Override
    protected void setInstanceIdAttributes(int id, int attr) {
        switch (id) {
            case 1: {
                this.calleeAttr = attr;
                return;
            }
            case 2: {
                this.lengthAttr = attr;
                return;
            }
            case 3: {
                this.callerAttr = attr;
                return;
            }
        }
        super.setInstanceIdAttributes(id, attr);
    }

    @Override
    Object[] getIds(boolean getNonEnumerable, boolean getSymbols) {
        Object[] ids = super.getIds(getNonEnumerable, getSymbols);
        if (this.args.length != 0) {
            int i;
            boolean[] present = new boolean[this.args.length];
            int extraCount = this.args.length;
            for (i = 0; i != ids.length; ++i) {
                int index;
                Object id = ids[i];
                if (!(id instanceof Integer) || 0 > (index = ((Integer)id).intValue()) || index >= this.args.length || present[index]) continue;
                present[index] = true;
                --extraCount;
            }
            if (!getNonEnumerable) {
                for (i = 0; i < present.length; ++i) {
                    if (present[i] || !super.has(i, (Scriptable)this)) continue;
                    present[i] = true;
                    --extraCount;
                }
            }
            if (extraCount != 0) {
                Object[] tmp = new Object[extraCount + ids.length];
                System.arraycopy(ids, 0, tmp, extraCount, ids.length);
                ids = tmp;
                int offset = 0;
                for (int i2 = 0; i2 != this.args.length; ++i2) {
                    if (present[i2]) continue;
                    ids[offset] = i2;
                    ++offset;
                }
                if (offset != extraCount) {
                    Kit.codeBug();
                }
            }
        }
        return ids;
    }

    @Override
    protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
        int index;
        if (ScriptRuntime.isSymbol(id) || id instanceof Scriptable) {
            return super.getOwnPropertyDescriptor(cx, id);
        }
        double d = ScriptRuntime.toNumber(id);
        if (d != (double)(index = (int)d)) {
            return super.getOwnPropertyDescriptor(cx, id);
        }
        Object value = this.arg(index);
        if (value == NOT_FOUND) {
            return super.getOwnPropertyDescriptor(cx, id);
        }
        if (this.sharedWithActivation(index)) {
            value = this.getFromActivation(index);
        }
        if (super.has(index, (Scriptable)this)) {
            ScriptableObject desc = super.getOwnPropertyDescriptor(cx, id);
            desc.put("value", (Scriptable)desc, value);
            return desc;
        }
        Scriptable scope = this.getParentScope();
        if (scope == null) {
            scope = this;
        }
        return Arguments.buildDataDescriptor(scope, value, 0);
    }

    @Override
    protected void defineOwnProperty(Context cx, Object id, ScriptableObject desc, boolean checkValid) {
        int index;
        super.defineOwnProperty(cx, id, desc, checkValid);
        if (ScriptRuntime.isSymbol(id)) {
            return;
        }
        double d = ScriptRuntime.toNumber(id);
        if (d != (double)(index = (int)d)) {
            return;
        }
        Object value = this.arg(index);
        if (value == NOT_FOUND) {
            return;
        }
        if (this.isAccessorDescriptor(desc)) {
            this.removeArg(index);
            return;
        }
        Object newValue = Arguments.getProperty((Scriptable)desc, "value");
        if (newValue == NOT_FOUND) {
            return;
        }
        this.replaceArg(index, newValue);
        if (Arguments.isFalse(Arguments.getProperty((Scriptable)desc, "writable"))) {
            this.removeArg(index);
        }
    }

    void defineAttributesForStrictMode() {
        Context cx = Context.getContext();
        if (!cx.isStrictMode()) {
            return;
        }
        this.setGetterOrSetter("caller", 0, new ThrowTypeError("caller"), true);
        this.setGetterOrSetter("caller", 0, new ThrowTypeError("caller"), false);
        this.setGetterOrSetter("callee", 0, new ThrowTypeError("callee"), true);
        this.setGetterOrSetter("callee", 0, new ThrowTypeError("callee"), false);
        this.setAttributes("caller", 6);
        this.setAttributes("callee", 6);
        this.callerObj = null;
        this.calleeObj = null;
    }

    private static class ThrowTypeError
    extends BaseFunction {
        private static final long serialVersionUID = -744615873947395749L;
        private String propertyName;

        ThrowTypeError(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            throw ScriptRuntime.typeErrorById("msg.arguments.not.access.strict", this.propertyName);
        }
    }
}

