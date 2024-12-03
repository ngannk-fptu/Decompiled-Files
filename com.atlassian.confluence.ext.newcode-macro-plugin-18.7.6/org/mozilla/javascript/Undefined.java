/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.Serializable;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public class Undefined
implements Serializable {
    private static final long serialVersionUID = 9195680630202616767L;
    public static final Object instance = new Undefined();
    private static final int instanceHash = System.identityHashCode(instance);
    public static final Scriptable SCRIPTABLE_UNDEFINED = new Scriptable(){

        @Override
        public String getClassName() {
            return "undefined";
        }

        @Override
        public Object get(String name, Scriptable start) {
            return NOT_FOUND;
        }

        @Override
        public Object get(int index, Scriptable start) {
            return NOT_FOUND;
        }

        @Override
        public boolean has(String name, Scriptable start) {
            return false;
        }

        @Override
        public boolean has(int index, Scriptable start) {
            return false;
        }

        @Override
        public void put(String name, Scriptable start, Object value) {
        }

        @Override
        public void put(int index, Scriptable start, Object value) {
        }

        @Override
        public void delete(String name) {
        }

        @Override
        public void delete(int index) {
        }

        @Override
        public Scriptable getPrototype() {
            return null;
        }

        @Override
        public void setPrototype(Scriptable prototype) {
        }

        @Override
        public Scriptable getParentScope() {
            return null;
        }

        @Override
        public void setParentScope(Scriptable parent) {
        }

        @Override
        public Object[] getIds() {
            return ScriptRuntime.emptyArgs;
        }

        @Override
        public Object getDefaultValue(Class<?> hint) {
            if (hint == null || hint == ScriptRuntime.StringClass) {
                return this.toString();
            }
            return null;
        }

        @Override
        public boolean hasInstance(Scriptable instance) {
            return false;
        }

        public String toString() {
            return "undefined";
        }

        public boolean equals(Object obj) {
            return Undefined.isUndefined(obj) || super.equals(obj);
        }

        public int hashCode() {
            return instanceHash;
        }
    };

    private Undefined() {
    }

    public Object readResolve() {
        return instance;
    }

    public boolean equals(Object obj) {
        return Undefined.isUndefined(obj) || super.equals(obj);
    }

    public int hashCode() {
        return instanceHash;
    }

    public static boolean isUndefined(Object obj) {
        return instance == obj || SCRIPTABLE_UNDEFINED == obj;
    }
}

