/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.FunctionMapper
 */
package org.apache.el.lang;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.el.FunctionMapper;
import org.apache.el.util.MessageFactory;
import org.apache.el.util.ReflectionUtil;

public class FunctionMapperImpl
extends FunctionMapper
implements Externalizable {
    private static final long serialVersionUID = 1L;
    protected ConcurrentMap<String, Function> functions = new ConcurrentHashMap<String, Function>();

    public Method resolveFunction(String prefix, String localName) {
        Function f = (Function)this.functions.get(prefix + ":" + localName);
        if (f == null) {
            return null;
        }
        return f.getMethod();
    }

    public void mapFunction(String prefix, String localName, Method m) {
        String key = prefix + ":" + localName;
        if (m == null) {
            this.functions.remove(key);
        } else {
            Function f = new Function(prefix, localName, m);
            this.functions.put(key, f);
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.functions);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.functions = (ConcurrentMap)in.readObject();
    }

    public static class Function
    implements Externalizable {
        protected transient Method m;
        protected String owner;
        protected String name;
        protected String[] types;
        protected String prefix;
        protected String localName;

        public Function(String prefix, String localName, Method m) {
            if (localName == null) {
                throw new NullPointerException(MessageFactory.get("error.nullLocalName"));
            }
            if (m == null) {
                throw new NullPointerException(MessageFactory.get("error.nullMethod"));
            }
            this.prefix = prefix;
            this.localName = localName;
            this.m = m;
        }

        public Function() {
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeUTF(this.prefix != null ? this.prefix : "");
            out.writeUTF(this.localName);
            if (this.owner != null && this.name != null && this.types != null) {
                out.writeUTF(this.owner);
                out.writeUTF(this.name);
                out.writeObject(this.types);
            } else {
                out.writeUTF(this.m.getDeclaringClass().getName());
                out.writeUTF(this.m.getName());
                out.writeObject(ReflectionUtil.toTypeNameArray(this.m.getParameterTypes()));
            }
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.prefix = in.readUTF();
            if (this.prefix.isEmpty()) {
                this.prefix = null;
            }
            this.localName = in.readUTF();
            this.owner = in.readUTF();
            this.name = in.readUTF();
            this.types = (String[])in.readObject();
        }

        public Method getMethod() {
            if (this.m == null) {
                try {
                    Class<?> t = ReflectionUtil.forName(this.owner);
                    Class<?>[] p = ReflectionUtil.toTypeArray(this.types);
                    this.m = t.getMethod(this.name, p);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            return this.m;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Function) {
                return this.hashCode() == obj.hashCode();
            }
            return false;
        }

        public int hashCode() {
            return (this.prefix + this.localName).hashCode();
        }
    }
}

