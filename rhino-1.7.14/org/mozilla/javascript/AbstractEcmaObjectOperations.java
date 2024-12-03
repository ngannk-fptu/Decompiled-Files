/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Constructable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

class AbstractEcmaObjectOperations {
    AbstractEcmaObjectOperations() {
    }

    static boolean testIntegrityLevel(Context cx, Object o, INTEGRITY_LEVEL level) {
        ScriptableObject obj = ScriptableObject.ensureScriptableObject(o);
        if (obj.isExtensible()) {
            return false;
        }
        for (Object name : obj.getIds(true, true)) {
            ScriptableObject desc = obj.getOwnPropertyDescriptor(cx, name);
            if (Boolean.TRUE.equals(desc.get("configurable"))) {
                return false;
            }
            if (level != INTEGRITY_LEVEL.FROZEN || !desc.isDataDescriptor(desc) || !Boolean.TRUE.equals(desc.get("writable"))) continue;
            return false;
        }
        return true;
    }

    static boolean setIntegrityLevel(Context cx, Object o, INTEGRITY_LEVEL level) {
        ScriptableObject obj = ScriptableObject.ensureScriptableObject(o);
        obj.preventExtensions();
        for (Object key : obj.getIds(true, true)) {
            ScriptableObject desc = obj.getOwnPropertyDescriptor(cx, key);
            if (level == INTEGRITY_LEVEL.SEALED) {
                if (!Boolean.TRUE.equals(desc.get("configurable"))) continue;
                desc.put("configurable", (Scriptable)desc, (Object)false);
                obj.defineOwnProperty(cx, key, desc, false);
                continue;
            }
            if (obj.isDataDescriptor(desc) && Boolean.TRUE.equals(desc.get("writable"))) {
                desc.put("writable", (Scriptable)desc, (Object)false);
            }
            if (Boolean.TRUE.equals(desc.get("configurable"))) {
                desc.put("configurable", (Scriptable)desc, (Object)false);
            }
            obj.defineOwnProperty(cx, key, desc, false);
        }
        return true;
    }

    public static Constructable speciesConstructor(Context cx, Scriptable s, Constructable defaultConstructor) {
        Object constructor = ScriptableObject.getProperty(s, "constructor");
        if (constructor == Scriptable.NOT_FOUND || Undefined.isUndefined(constructor)) {
            return defaultConstructor;
        }
        if (!ScriptRuntime.isObject(constructor)) {
            throw ScriptRuntime.typeErrorById("msg.arg.not.object", ScriptRuntime.typeof(constructor));
        }
        Object species = ScriptableObject.getProperty((Scriptable)constructor, SymbolKey.SPECIES);
        if (species == Scriptable.NOT_FOUND || species == null || Undefined.isUndefined(species)) {
            return defaultConstructor;
        }
        if (!(species instanceof Constructable)) {
            throw ScriptRuntime.typeErrorById("msg.not.ctor", ScriptRuntime.typeof(species));
        }
        return (Constructable)species;
    }

    static enum INTEGRITY_LEVEL {
        FROZEN,
        SEALED;

    }
}

