/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionCall;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdFunctionObjectES6;
import org.mozilla.javascript.NativeSymbol;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolScriptable;
import org.mozilla.javascript.UniqueTag;

public abstract class IdScriptableObject
extends ScriptableObject
implements IdFunctionCall {
    private static final long serialVersionUID = -3744239272168621609L;
    private transient PrototypeValues prototypeValues;

    public IdScriptableObject() {
    }

    public IdScriptableObject(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
    }

    protected final boolean defaultHas(String name) {
        return super.has(name, (Scriptable)this);
    }

    protected final Object defaultGet(String name) {
        return super.get(name, (Scriptable)this);
    }

    protected final void defaultPut(String name, Object value) {
        super.put(name, (Scriptable)this, value);
    }

    @Override
    public boolean has(String name, Scriptable start) {
        int id;
        int info = this.findInstanceIdInfo(name);
        if (info != 0) {
            int attr = info >>> 16;
            if ((attr & 4) != 0) {
                return true;
            }
            int id2 = info & 0xFFFF;
            return NOT_FOUND != this.getInstanceIdValue(id2);
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            return this.prototypeValues.has(id);
        }
        return super.has(name, start);
    }

    @Override
    public boolean has(Symbol key, Scriptable start) {
        int id;
        int info = this.findInstanceIdInfo(key);
        if (info != 0) {
            int attr = info >>> 16;
            if ((attr & 4) != 0) {
                return true;
            }
            int id2 = info & 0xFFFF;
            return NOT_FOUND != this.getInstanceIdValue(id2);
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0) {
            return this.prototypeValues.has(id);
        }
        return super.has(key, start);
    }

    @Override
    public Object get(String name, Scriptable start) {
        int id;
        Object value = super.get(name, start);
        if (value != NOT_FOUND) {
            return value;
        }
        int info = this.findInstanceIdInfo(name);
        if (info != 0 && (value = this.getInstanceIdValue(id = info & 0xFFFF)) != NOT_FOUND) {
            return value;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0 && (value = this.prototypeValues.get(id)) != NOT_FOUND) {
            return value;
        }
        return NOT_FOUND;
    }

    @Override
    public Object get(Symbol key, Scriptable start) {
        int id;
        Object value = super.get(key, start);
        if (value != NOT_FOUND) {
            return value;
        }
        int info = this.findInstanceIdInfo(key);
        if (info != 0 && (value = this.getInstanceIdValue(id = info & 0xFFFF)) != NOT_FOUND) {
            return value;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0 && (value = this.prototypeValues.get(id)) != NOT_FOUND) {
            return value;
        }
        return NOT_FOUND;
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        int id;
        int info = this.findInstanceIdInfo(name);
        if (info != 0) {
            if (start == this && this.isSealed()) {
                throw Context.reportRuntimeErrorById("msg.modify.sealed", name);
            }
            int attr = info >>> 16;
            if ((attr & 1) == 0) {
                if (start == this) {
                    int id2 = info & 0xFFFF;
                    this.setInstanceIdValue(id2, value);
                } else {
                    start.put(name, start, value);
                }
            }
            return;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            if (start == this && this.isSealed()) {
                throw Context.reportRuntimeErrorById("msg.modify.sealed", name);
            }
            this.prototypeValues.set(id, start, value);
            return;
        }
        super.put(name, start, value);
    }

    @Override
    public void put(Symbol key, Scriptable start, Object value) {
        int id;
        int info = this.findInstanceIdInfo(key);
        if (info != 0) {
            if (start == this && this.isSealed()) {
                throw Context.reportRuntimeErrorById("msg.modify.sealed", new Object[0]);
            }
            int attr = info >>> 16;
            if ((attr & 1) == 0) {
                if (start == this) {
                    int id2 = info & 0xFFFF;
                    this.setInstanceIdValue(id2, value);
                } else {
                    IdScriptableObject.ensureSymbolScriptable(start).put(key, start, value);
                }
            }
            return;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0) {
            if (start == this && this.isSealed()) {
                throw Context.reportRuntimeErrorById("msg.modify.sealed", new Object[0]);
            }
            this.prototypeValues.set(id, start, value);
            return;
        }
        super.put(key, start, value);
    }

    @Override
    public void delete(String name) {
        int id;
        int info = this.findInstanceIdInfo(name);
        if (info != 0 && !this.isSealed()) {
            int attr = info >>> 16;
            if ((attr & 4) != 0) {
                Context cx = Context.getContext();
                if (cx.isStrictMode()) {
                    throw ScriptRuntime.typeErrorById("msg.delete.property.with.configurable.false", name);
                }
            } else {
                int id2 = info & 0xFFFF;
                this.setInstanceIdValue(id2, NOT_FOUND);
            }
            return;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            if (!this.isSealed()) {
                this.prototypeValues.delete(id);
            }
            return;
        }
        super.delete(name);
    }

    @Override
    public void delete(Symbol key) {
        int id;
        int info = this.findInstanceIdInfo(key);
        if (info != 0 && !this.isSealed()) {
            int attr = info >>> 16;
            if ((attr & 4) != 0) {
                Context cx = Context.getContext();
                if (cx.isStrictMode()) {
                    throw ScriptRuntime.typeErrorById("msg.delete.property.with.configurable.false", new Object[0]);
                }
            } else {
                int id2 = info & 0xFFFF;
                this.setInstanceIdValue(id2, NOT_FOUND);
            }
            return;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0) {
            if (!this.isSealed()) {
                this.prototypeValues.delete(id);
            }
            return;
        }
        super.delete(key);
    }

    @Override
    public int getAttributes(String name) {
        int id;
        int info = this.findInstanceIdInfo(name);
        if (info != 0) {
            int attr = info >>> 16;
            return attr;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            return this.prototypeValues.getAttributes(id);
        }
        return super.getAttributes(name);
    }

    @Override
    public int getAttributes(Symbol key) {
        int id;
        int info = this.findInstanceIdInfo(key);
        if (info != 0) {
            int attr = info >>> 16;
            return attr;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0) {
            return this.prototypeValues.getAttributes(id);
        }
        return super.getAttributes(key);
    }

    @Override
    public void setAttributes(String name, int attributes) {
        int id;
        ScriptableObject.checkValidAttributes(attributes);
        int info = this.findInstanceIdInfo(name);
        if (info != 0) {
            int id2 = info & 0xFFFF;
            int currentAttributes = info >>> 16;
            if (attributes != currentAttributes) {
                this.setInstanceIdAttributes(id2, attributes);
            }
            return;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            this.prototypeValues.setAttributes(id, attributes);
            return;
        }
        super.setAttributes(name, attributes);
    }

    @Override
    Object[] getIds(boolean getNonEnumerable, boolean getSymbols) {
        int maxInstanceId;
        Object[] result = super.getIds(getNonEnumerable, getSymbols);
        if (this.prototypeValues != null) {
            result = this.prototypeValues.getNames(getNonEnumerable, getSymbols, result);
        }
        if ((maxInstanceId = this.getMaxInstanceId()) != 0) {
            Object[] ids = null;
            int count = 0;
            for (int id = maxInstanceId; id != 0; --id) {
                int attr;
                String name = this.getInstanceIdName(id);
                int info = this.findInstanceIdInfo(name);
                if (info == 0 || ((attr = info >>> 16) & 4) == 0 && NOT_FOUND == this.getInstanceIdValue(id) || !getNonEnumerable && (attr & 2) != 0) continue;
                if (count == 0) {
                    ids = new Object[id];
                }
                ids[count++] = name;
            }
            if (count != 0) {
                if (result.length == 0 && ids.length == count) {
                    result = ids;
                } else {
                    Object[] tmp = new Object[result.length + count];
                    System.arraycopy(result, 0, tmp, 0, result.length);
                    System.arraycopy(ids, 0, tmp, result.length, count);
                    result = tmp;
                }
            }
        }
        return result;
    }

    protected int getMaxInstanceId() {
        return 0;
    }

    protected static int instanceIdInfo(int attributes, int id) {
        return attributes << 16 | id;
    }

    protected int findInstanceIdInfo(String name) {
        return 0;
    }

    protected int findInstanceIdInfo(Symbol key) {
        return 0;
    }

    protected String getInstanceIdName(int id) {
        throw new IllegalArgumentException(String.valueOf(id));
    }

    protected Object getInstanceIdValue(int id) {
        throw new IllegalStateException(String.valueOf(id));
    }

    protected void setInstanceIdValue(int id, Object value) {
        throw new IllegalStateException(String.valueOf(id));
    }

    protected void setInstanceIdAttributes(int id, int attr) {
        throw ScriptRuntime.constructError("InternalError", "Changing attributes not supported for " + this.getClassName() + " " + this.getInstanceIdName(id) + " property");
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        throw f.unknown();
    }

    public final IdFunctionObject exportAsJSClass(int maxPrototypeId, Scriptable scope, boolean sealed) {
        if (scope != this && scope != null) {
            this.setParentScope(scope);
            this.setPrototype(IdScriptableObject.getObjectPrototype(scope));
        }
        this.activatePrototypeMap(maxPrototypeId);
        IdFunctionObject ctor = this.prototypeValues.createPrecachedConstructor();
        if (sealed) {
            this.sealObject();
        }
        this.fillConstructorProperties(ctor);
        if (sealed) {
            ctor.sealObject();
        }
        ctor.exportAsScopeProperty();
        return ctor;
    }

    public final boolean hasPrototypeMap() {
        return this.prototypeValues != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void activatePrototypeMap(int maxPrototypeId) {
        PrototypeValues values = new PrototypeValues(this, maxPrototypeId);
        IdScriptableObject idScriptableObject = this;
        synchronized (idScriptableObject) {
            if (this.prototypeValues != null) {
                throw new IllegalStateException();
            }
            this.prototypeValues = values;
        }
    }

    public final IdFunctionObject initPrototypeMethod(Object tag, int id, String name, int arity) {
        return this.initPrototypeMethod(tag, id, name, name, arity);
    }

    public final IdFunctionObject initPrototypeMethod(Object tag, int id, String propertyName, String functionName, int arity) {
        Scriptable scope = ScriptableObject.getTopLevelScope(this);
        IdFunctionObject function = this.newIdFunction(tag, id, functionName != null ? functionName : propertyName, arity, scope);
        this.prototypeValues.initValue(id, propertyName, (Object)function, 2);
        return function;
    }

    public final IdFunctionObject initPrototypeMethod(Object tag, int id, Symbol key, String functionName, int arity) {
        Scriptable scope = ScriptableObject.getTopLevelScope(this);
        IdFunctionObject function = this.newIdFunction(tag, id, functionName, arity, scope);
        this.prototypeValues.initValue(id, key, (Object)function, 2);
        return function;
    }

    public final void initPrototypeConstructor(IdFunctionObject f) {
        int id = this.prototypeValues.constructorId;
        if (id == 0) {
            throw new IllegalStateException();
        }
        if (f.methodId() != id) {
            throw new IllegalArgumentException();
        }
        if (this.isSealed()) {
            f.sealObject();
        }
        this.prototypeValues.initValue(id, "constructor", (Object)f, 2);
    }

    public final void initPrototypeValue(int id, String name, Object value, int attributes) {
        this.prototypeValues.initValue(id, name, value, attributes);
    }

    public final void initPrototypeValue(int id, Symbol key, Object value, int attributes) {
        this.prototypeValues.initValue(id, key, value, attributes);
    }

    protected void initPrototypeId(int id) {
        throw new IllegalStateException(String.valueOf(id));
    }

    protected int findPrototypeId(String name) {
        throw new IllegalStateException(name);
    }

    protected int findPrototypeId(Symbol key) {
        return 0;
    }

    protected void fillConstructorProperties(IdFunctionObject ctor) {
    }

    protected void addIdFunctionProperty(Scriptable obj, Object tag, int id, String name, int arity) {
        Scriptable scope = ScriptableObject.getTopLevelScope(obj);
        IdFunctionObject f = this.newIdFunction(tag, id, name, arity, scope);
        f.addAsProperty(obj);
    }

    protected static <T> T ensureType(Object obj, Class<T> clazz, IdFunctionObject f) {
        if (clazz.isInstance(obj)) {
            return (T)obj;
        }
        if (obj == null) {
            throw ScriptRuntime.typeErrorById("msg.incompat.call.details", f.getFunctionName(), "null", clazz.getName());
        }
        throw ScriptRuntime.typeErrorById("msg.incompat.call.details", f.getFunctionName(), obj.getClass().getName(), clazz.getName());
    }

    private IdFunctionObject newIdFunction(Object tag, int id, String name, int arity, Scriptable scope) {
        IdFunctionObject function = null;
        function = Context.getContext().getLanguageVersion() < 200 ? new IdFunctionObject(this, tag, id, name, arity, scope) : new IdFunctionObjectES6(this, tag, id, name, arity, scope);
        if (this.isSealed()) {
            function.sealObject();
        }
        return function;
    }

    @Override
    protected void defineOwnProperty(Context cx, Object key, ScriptableObject desc, boolean checkValid) {
        if (key instanceof String) {
            int id;
            String name = (String)key;
            int info = this.findInstanceIdInfo(name);
            if (info != 0) {
                id = info & 0xFFFF;
                if (this.isAccessorDescriptor(desc)) {
                    this.delete(id);
                } else {
                    Object currentValue;
                    this.checkPropertyDefinition(desc);
                    ScriptableObject current = this.getOwnPropertyDescriptor(cx, key);
                    this.checkPropertyChange(name, current, desc);
                    int attr = info >>> 16;
                    Object value = IdScriptableObject.getProperty((Scriptable)desc, "value");
                    if (value != NOT_FOUND && (attr & 1) == 0 && !this.sameValue(value, currentValue = this.getInstanceIdValue(id))) {
                        this.setInstanceIdValue(id, value);
                    }
                    this.setAttributes(name, this.applyDescriptorToAttributeBitset(attr, desc));
                    return;
                }
            }
            if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
                if (this.isAccessorDescriptor(desc)) {
                    this.prototypeValues.delete(id);
                } else {
                    Object currentValue;
                    this.checkPropertyDefinition(desc);
                    ScriptableObject current = this.getOwnPropertyDescriptor(cx, key);
                    this.checkPropertyChange(name, current, desc);
                    int attr = this.prototypeValues.getAttributes(id);
                    Object value = IdScriptableObject.getProperty((Scriptable)desc, "value");
                    if (value != NOT_FOUND && (attr & 1) == 0 && !this.sameValue(value, currentValue = this.prototypeValues.get(id))) {
                        this.prototypeValues.set(id, this, value);
                    }
                    this.prototypeValues.setAttributes(id, this.applyDescriptorToAttributeBitset(attr, desc));
                    if (super.has(name, (Scriptable)this)) {
                        super.delete(name);
                    }
                    return;
                }
            }
        }
        super.defineOwnProperty(cx, key, desc, checkValid);
    }

    @Override
    protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
        ScriptableObject desc = super.getOwnPropertyDescriptor(cx, id);
        if (desc == null) {
            if (id instanceof String) {
                desc = this.getBuiltInDescriptor((String)id);
            } else if (ScriptRuntime.isSymbol(id)) {
                desc = this.getBuiltInDescriptor(((NativeSymbol)id).getKey());
            }
        }
        return desc;
    }

    private ScriptableObject getBuiltInDescriptor(String name) {
        int id;
        int info;
        Object value = null;
        int attr = 0;
        Scriptable scope = this.getParentScope();
        if (scope == null) {
            scope = this;
        }
        if ((info = this.findInstanceIdInfo(name)) != 0) {
            int id2 = info & 0xFFFF;
            value = this.getInstanceIdValue(id2);
            attr = info >>> 16;
            return IdScriptableObject.buildDataDescriptor(scope, value, attr);
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(name)) != 0) {
            value = this.prototypeValues.get(id);
            attr = this.prototypeValues.getAttributes(id);
            return IdScriptableObject.buildDataDescriptor(scope, value, attr);
        }
        return null;
    }

    private ScriptableObject getBuiltInDescriptor(Symbol key) {
        int id;
        Object value = null;
        int attr = 0;
        Scriptable scope = this.getParentScope();
        if (scope == null) {
            scope = this;
        }
        if (this.prototypeValues != null && (id = this.prototypeValues.findId(key)) != 0) {
            value = this.prototypeValues.get(id);
            attr = this.prototypeValues.getAttributes(id);
            return IdScriptableObject.buildDataDescriptor(scope, value, attr);
        }
        return null;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int maxPrototypeId = stream.readInt();
        if (maxPrototypeId != 0) {
            this.activatePrototypeMap(maxPrototypeId);
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int maxPrototypeId = 0;
        if (this.prototypeValues != null) {
            maxPrototypeId = this.prototypeValues.getMaxId();
        }
        stream.writeInt(maxPrototypeId);
    }

    private static final class PrototypeValues
    implements Serializable {
        private static final long serialVersionUID = 3038645279153854371L;
        private static final int NAME_SLOT = 1;
        private static final int SLOT_SPAN = 2;
        private IdScriptableObject obj;
        private int maxId;
        private Object[] valueArray;
        private short[] attributeArray;
        int constructorId;
        private IdFunctionObject constructor;
        private short constructorAttrs;

        PrototypeValues(IdScriptableObject obj, int maxId) {
            if (obj == null) {
                throw new IllegalArgumentException();
            }
            if (maxId < 1) {
                throw new IllegalArgumentException();
            }
            this.obj = obj;
            this.maxId = maxId;
        }

        final int getMaxId() {
            return this.maxId;
        }

        final void initValue(int id, String name, Object value, int attributes) {
            if (1 > id || id > this.maxId) {
                throw new IllegalArgumentException();
            }
            if (name == null) {
                throw new IllegalArgumentException();
            }
            if (value == Scriptable.NOT_FOUND) {
                throw new IllegalArgumentException();
            }
            ScriptableObject.checkValidAttributes(attributes);
            if (this.obj.findPrototypeId(name) != id) {
                throw new IllegalArgumentException(name);
            }
            if (id == this.constructorId) {
                if (!(value instanceof IdFunctionObject)) {
                    throw new IllegalArgumentException("consructor should be initialized with IdFunctionObject");
                }
                this.constructor = (IdFunctionObject)value;
                this.constructorAttrs = (short)attributes;
                return;
            }
            this.initSlot(id, name, value, attributes);
        }

        final void initValue(int id, Symbol key, Object value, int attributes) {
            if (1 > id || id > this.maxId) {
                throw new IllegalArgumentException();
            }
            if (key == null) {
                throw new IllegalArgumentException();
            }
            if (value == Scriptable.NOT_FOUND) {
                throw new IllegalArgumentException();
            }
            ScriptableObject.checkValidAttributes(attributes);
            if (this.obj.findPrototypeId(key) != id) {
                throw new IllegalArgumentException(key.toString());
            }
            if (id == this.constructorId) {
                if (!(value instanceof IdFunctionObject)) {
                    throw new IllegalArgumentException("consructor should be initialized with IdFunctionObject");
                }
                this.constructor = (IdFunctionObject)value;
                this.constructorAttrs = (short)attributes;
                return;
            }
            this.initSlot(id, key, value, attributes);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void initSlot(int id, Object name, Object value, int attributes) {
            Object[] array = this.valueArray;
            if (array == null) {
                throw new IllegalStateException();
            }
            if (value == null) {
                value = UniqueTag.NULL_VALUE;
            }
            int index = (id - 1) * 2;
            PrototypeValues prototypeValues = this;
            synchronized (prototypeValues) {
                Object value2 = array[index];
                if (value2 == null) {
                    array[index] = value;
                    array[index + 1] = name;
                    this.attributeArray[id - 1] = (short)attributes;
                } else if (!name.equals(array[index + 1])) {
                    throw new IllegalStateException();
                }
            }
        }

        final IdFunctionObject createPrecachedConstructor() {
            if (this.constructorId != 0) {
                throw new IllegalStateException();
            }
            this.constructorId = this.obj.findPrototypeId("constructor");
            if (this.constructorId == 0) {
                throw new IllegalStateException("No id for constructor property");
            }
            this.obj.initPrototypeId(this.constructorId);
            if (this.constructor == null) {
                throw new IllegalStateException(this.obj.getClass().getName() + ".initPrototypeId() did not initialize id=" + this.constructorId);
            }
            this.constructor.initFunction(this.obj.getClassName(), ScriptableObject.getTopLevelScope(this.obj));
            this.constructor.markAsConstructor(this.obj);
            return this.constructor;
        }

        final int findId(String name) {
            return this.obj.findPrototypeId(name);
        }

        final int findId(Symbol key) {
            return this.obj.findPrototypeId(key);
        }

        final boolean has(int id) {
            Object[] array = this.valueArray;
            if (array == null) {
                return true;
            }
            int valueSlot = (id - 1) * 2;
            Object value = array[valueSlot];
            if (value == null) {
                return true;
            }
            return value != Scriptable.NOT_FOUND;
        }

        final Object get(int id) {
            Object value = this.ensureId(id);
            if (value == UniqueTag.NULL_VALUE) {
                value = null;
            }
            return value;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        final void set(int id, Scriptable start, Object value) {
            if (value == Scriptable.NOT_FOUND) {
                throw new IllegalArgumentException();
            }
            this.ensureId(id);
            short attr = this.attributeArray[id - 1];
            if ((attr & 1) == 0) {
                if (start == this.obj) {
                    if (value == null) {
                        value = UniqueTag.NULL_VALUE;
                    }
                    int valueSlot = (id - 1) * 2;
                    PrototypeValues prototypeValues = this;
                    synchronized (prototypeValues) {
                        this.valueArray[valueSlot] = value;
                    }
                } else {
                    int nameSlot = (id - 1) * 2 + 1;
                    Object name = this.valueArray[nameSlot];
                    if (name instanceof Symbol) {
                        if (start instanceof SymbolScriptable) {
                            ((SymbolScriptable)((Object)start)).put((Symbol)name, start, value);
                        }
                    } else {
                        start.put((String)name, start, value);
                    }
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        final void delete(int id) {
            this.ensureId(id);
            short attr = this.attributeArray[id - 1];
            if ((attr & 4) != 0) {
                Context cx = Context.getContext();
                if (cx.isStrictMode()) {
                    int nameSlot = (id - 1) * 2 + 1;
                    String name = (String)this.valueArray[nameSlot];
                    throw ScriptRuntime.typeErrorById("msg.delete.property.with.configurable.false", name);
                }
            } else {
                int valueSlot = (id - 1) * 2;
                PrototypeValues prototypeValues = this;
                synchronized (prototypeValues) {
                    this.valueArray[valueSlot] = Scriptable.NOT_FOUND;
                    this.attributeArray[id - 1] = 0;
                }
            }
        }

        final int getAttributes(int id) {
            this.ensureId(id);
            return this.attributeArray[id - 1];
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        final void setAttributes(int id, int attributes) {
            ScriptableObject.checkValidAttributes(attributes);
            this.ensureId(id);
            PrototypeValues prototypeValues = this;
            synchronized (prototypeValues) {
                this.attributeArray[id - 1] = (short)attributes;
            }
        }

        final Object[] getNames(boolean getAll, boolean getSymbols, Object[] extraEntries) {
            Object[] names = null;
            int count = 0;
            for (int id = 1; id <= this.maxId; ++id) {
                Object value = this.ensureId(id);
                if (!getAll && (this.attributeArray[id - 1] & 2) != 0 || value == Scriptable.NOT_FOUND) continue;
                int nameSlot = (id - 1) * 2 + 1;
                Object name = this.valueArray[nameSlot];
                if (name instanceof String) {
                    if (names == null) {
                        names = new Object[this.maxId];
                    }
                    names[count++] = name;
                    continue;
                }
                if (!getSymbols || !(name instanceof Symbol)) continue;
                if (names == null) {
                    names = new Object[this.maxId];
                }
                names[count++] = name.toString();
            }
            if (count == 0) {
                return extraEntries;
            }
            if (extraEntries == null || extraEntries.length == 0) {
                if (count != names.length) {
                    Object[] tmp = new Object[count];
                    System.arraycopy(names, 0, tmp, 0, count);
                    names = tmp;
                }
                return names;
            }
            int extra = extraEntries.length;
            Object[] tmp = new Object[extra + count];
            System.arraycopy(extraEntries, 0, tmp, 0, extra);
            System.arraycopy(names, 0, tmp, extra, count);
            return tmp;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Object ensureId(int id) {
            int valueSlot;
            Object value;
            Object[] array = this.valueArray;
            if (array == null) {
                PrototypeValues prototypeValues = this;
                synchronized (prototypeValues) {
                    array = this.valueArray;
                    if (array == null) {
                        this.valueArray = array = new Object[this.maxId * 2];
                        this.attributeArray = new short[this.maxId];
                    }
                }
            }
            if ((value = array[valueSlot = (id - 1) * 2]) == null) {
                if (id == this.constructorId) {
                    this.initSlot(this.constructorId, "constructor", this.constructor, this.constructorAttrs);
                    this.constructor = null;
                } else {
                    this.obj.initPrototypeId(id);
                }
                value = array[valueSlot];
                if (value == null) {
                    throw new IllegalStateException(this.obj.getClass().getName() + ".initPrototypeId(int id) did not initialize id=" + id);
                }
            }
            return value;
        }
    }
}

