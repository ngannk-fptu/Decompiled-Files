/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

class SpecialRef
extends Ref {
    private static final long serialVersionUID = -7521596632456797847L;
    private static final int SPECIAL_NONE = 0;
    private static final int SPECIAL_PROTO = 1;
    private static final int SPECIAL_PARENT = 2;
    private Scriptable target;
    private int type;
    private String name;

    private SpecialRef(Scriptable target, int type, String name) {
        this.target = target;
        this.type = type;
        this.name = name;
    }

    static Ref createSpecial(Context cx, Scriptable scope, Object object, String name) {
        int type;
        Scriptable target = ScriptRuntime.toObjectOrNull(cx, object, scope);
        if (target == null) {
            throw ScriptRuntime.undefReadError(object, name);
        }
        if (name.equals("__proto__")) {
            type = 1;
        } else if (name.equals("__parent__")) {
            type = 2;
        } else {
            throw new IllegalArgumentException(name);
        }
        if (!cx.hasFeature(5)) {
            type = 0;
        }
        return new SpecialRef(target, type, name);
    }

    @Override
    public Object get(Context cx) {
        switch (this.type) {
            case 0: {
                return ScriptRuntime.getObjectProp(this.target, this.name, cx);
            }
            case 1: {
                return this.target.getPrototype();
            }
            case 2: {
                return this.target.getParentScope();
            }
        }
        throw Kit.codeBug();
    }

    @Override
    @Deprecated
    public Object set(Context cx, Object value) {
        throw new IllegalStateException();
    }

    @Override
    public Object set(Context cx, Scriptable scope, Object value) {
        switch (this.type) {
            case 0: {
                return ScriptRuntime.setObjectProp(this.target, this.name, value, cx);
            }
            case 1: 
            case 2: {
                Scriptable obj = ScriptRuntime.toObjectOrNull(cx, value, scope);
                if (obj != null) {
                    Scriptable search = obj;
                    do {
                        if (search != this.target) continue;
                        throw Context.reportRuntimeErrorById("msg.cyclic.value", this.name);
                    } while ((search = this.type == 1 ? search.getPrototype() : search.getParentScope()) != null);
                }
                if (this.type == 1) {
                    if (this.target instanceof ScriptableObject && !((ScriptableObject)this.target).isExtensible() && cx.getLanguageVersion() >= 180) {
                        throw ScriptRuntime.typeErrorById("msg.not.extensible", new Object[0]);
                    }
                    if (cx.getLanguageVersion() >= 200) {
                        String typeOfTarget = ScriptRuntime.typeof(this.target);
                        if ("function".equals(typeOfTarget)) {
                            if (value == null) {
                                this.target.setPrototype(Undefined.SCRIPTABLE_UNDEFINED);
                                return value;
                            }
                            String typeOfValue = ScriptRuntime.typeof(value);
                            if ("object".equals(typeOfValue) || "function".equals(typeOfValue)) {
                                this.target.setPrototype(obj);
                            }
                            return value;
                        }
                        String typeOfValue = ScriptRuntime.typeof(value);
                        if (value != null && !"object".equals(typeOfValue) || !"object".equals(typeOfTarget)) {
                            return Undefined.instance;
                        }
                        this.target.setPrototype(obj);
                    } else {
                        this.target.setPrototype(obj);
                    }
                } else {
                    this.target.setParentScope(obj);
                }
                return obj;
            }
        }
        throw Kit.codeBug();
    }

    @Override
    public boolean has(Context cx) {
        if (this.type == 0) {
            return ScriptRuntime.hasObjectElem(this.target, this.name, cx);
        }
        return true;
    }

    @Override
    public boolean delete(Context cx) {
        if (this.type == 0) {
            return ScriptRuntime.deleteObjectElem(this.target, this.name, cx);
        }
        return false;
    }
}

