/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.MemberBox;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Slot;
import org.mozilla.javascript.Undefined;

public class AccessorSlot
extends Slot {
    private static final long serialVersionUID = 1677840254177335827L;
    transient Getter getter;
    transient Setter setter;

    AccessorSlot(Slot oldSlot) {
        super(oldSlot);
    }

    @Override
    boolean isValueSlot() {
        return false;
    }

    @Override
    boolean isSetterSlot() {
        return true;
    }

    @Override
    ScriptableObject getPropertyDescriptor(Context cx, Scriptable scope) {
        Function f;
        String fName;
        ScriptableObject desc = (ScriptableObject)cx.newObject(scope);
        desc.setCommonDescriptorProperties(this.getAttributes(), this.getter == null && this.setter == null);
        String string = fName = this.name == null ? "f" : this.name.toString();
        if (this.getter != null) {
            f = this.getter.asGetterFunction(fName, scope);
            desc.defineProperty("get", f == null ? Undefined.instance : f, 0);
        }
        if (this.setter != null) {
            f = this.setter.asSetterFunction(fName, scope);
            desc.defineProperty("set", f == null ? Undefined.instance : f, 0);
        }
        return desc;
    }

    @Override
    public boolean setValue(Object value, Scriptable owner, Scriptable start) {
        if (this.setter == null) {
            if (this.getter != null) {
                this.throwNoSetterException(start, value);
                return true;
            }
        } else {
            return this.setter.setValue(value, owner, start);
        }
        return super.setValue(value, owner, start);
    }

    @Override
    public Object getValue(Scriptable start) {
        if (this.getter != null) {
            return this.getter.getValue(start);
        }
        return super.getValue(start);
    }

    @Override
    Function getSetterFunction(String name, Scriptable scope) {
        if (this.setter == null) {
            return null;
        }
        return this.setter.asSetterFunction(name, scope);
    }

    @Override
    Function getGetterFunction(String name, Scriptable scope) {
        if (this.getter == null) {
            return null;
        }
        return this.getter.asGetterFunction(name, scope);
    }

    static final class FunctionSetter
    implements Setter {
        final Object target;

        FunctionSetter(Object target) {
            this.target = target;
        }

        @Override
        public boolean setValue(Object value, Scriptable owner, Scriptable start) {
            if (this.target instanceof Function) {
                Function t = (Function)this.target;
                Context cx = Context.getContext();
                t.call(cx, t.getParentScope(), start, new Object[]{value});
            }
            return true;
        }

        @Override
        public Function asSetterFunction(String name, Scriptable scope) {
            return this.target instanceof Function ? (Function)this.target : null;
        }
    }

    static final class MemberBoxSetter
    implements Setter {
        final MemberBox member;

        MemberBoxSetter(MemberBox member) {
            this.member = member;
        }

        @Override
        public boolean setValue(Object value, Scriptable owner, Scriptable start) {
            Context cx = Context.getContext();
            Class<?>[] pTypes = this.member.argTypes;
            Class<?> valueType = pTypes[pTypes.length - 1];
            int tag = FunctionObject.getTypeTag(valueType);
            Object actualArg = FunctionObject.convertArg(cx, start, value, tag);
            if (this.member.delegateTo == null) {
                this.member.invoke(start, new Object[]{actualArg});
            } else {
                this.member.invoke(this.member.delegateTo, new Object[]{start, actualArg});
            }
            return true;
        }

        @Override
        public Function asSetterFunction(String name, Scriptable scope) {
            return this.member.asSetterFunction(name, scope);
        }
    }

    static interface Setter {
        public boolean setValue(Object var1, Scriptable var2, Scriptable var3);

        public Function asSetterFunction(String var1, Scriptable var2);
    }

    static final class FunctionGetter
    implements Getter {
        final Object target;

        FunctionGetter(Object target) {
            this.target = target;
        }

        @Override
        public Object getValue(Scriptable start) {
            if (this.target instanceof Function) {
                Function t = (Function)this.target;
                Context cx = Context.getContext();
                return t.call(cx, t.getParentScope(), start, ScriptRuntime.emptyArgs);
            }
            return Undefined.instance;
        }

        @Override
        public Function asGetterFunction(String name, Scriptable scope) {
            return this.target instanceof Function ? (Function)this.target : null;
        }
    }

    static final class MemberBoxGetter
    implements Getter {
        final MemberBox member;

        MemberBoxGetter(MemberBox member) {
            this.member = member;
        }

        @Override
        public Object getValue(Scriptable start) {
            if (this.member.delegateTo == null) {
                return this.member.invoke(start, ScriptRuntime.emptyArgs);
            }
            return this.member.invoke(this.member.delegateTo, new Object[]{start});
        }

        @Override
        public Function asGetterFunction(String name, Scriptable scope) {
            return this.member.asGetterFunction(name, scope);
        }
    }

    static interface Getter {
        public Object getValue(Scriptable var1);

        public Function asGetterFunction(String var1, Scriptable var2);
    }
}

