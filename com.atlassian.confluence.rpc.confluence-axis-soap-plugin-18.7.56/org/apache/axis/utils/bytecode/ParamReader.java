/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils.bytecode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.bytecode.ClassReader;

public class ParamReader
extends ClassReader {
    private String methodName;
    private Map methods = new HashMap();
    private Class[] paramTypes;
    static /* synthetic */ Class class$org$apache$axis$utils$bytecode$ParamReader;

    public ParamReader(Class c) throws IOException {
        this(ParamReader.getBytes(c));
    }

    public ParamReader(byte[] b) throws IOException {
        super(b, ParamReader.findAttributeReaders(class$org$apache$axis$utils$bytecode$ParamReader == null ? (class$org$apache$axis$utils$bytecode$ParamReader = ParamReader.class$("org.apache.axis.utils.bytecode.ParamReader")) : class$org$apache$axis$utils$bytecode$ParamReader));
        int i;
        if (this.readInt() != -889275714) {
            throw new IOException(Messages.getMessage("badClassFile00"));
        }
        this.readShort();
        this.readShort();
        this.readCpool();
        this.readShort();
        this.readShort();
        this.readShort();
        int count = this.readShort();
        for (i = 0; i < count; ++i) {
            this.readShort();
        }
        count = this.readShort();
        for (i = 0; i < count; ++i) {
            this.readShort();
            this.readShort();
            this.readShort();
            this.skipAttributes();
        }
        count = this.readShort();
        for (i = 0; i < count; ++i) {
            this.readShort();
            int m = this.readShort();
            String name = this.resolveUtf8(m);
            int d = this.readShort();
            this.methodName = name + this.resolveUtf8(d);
            this.readAttributes();
        }
    }

    public void readCode() throws IOException {
        this.readShort();
        int maxLocals = this.readShort();
        MethodInfo info = new MethodInfo(maxLocals);
        if (this.methods != null && this.methodName != null) {
            this.methods.put(this.methodName, info);
        }
        this.skipFully(this.readInt());
        this.skipFully(8 * this.readShort());
        this.readAttributes();
    }

    public String[] getParameterNames(Constructor ctor) {
        this.paramTypes = ctor.getParameterTypes();
        return this.getParameterNames(ctor, this.paramTypes);
    }

    public String[] getParameterNames(Method method) {
        this.paramTypes = method.getParameterTypes();
        return this.getParameterNames(method, this.paramTypes);
    }

    protected String[] getParameterNames(Member member, Class[] paramTypes) {
        MethodInfo info = (MethodInfo)this.methods.get(ParamReader.getSignature(member, paramTypes));
        if (info != null) {
            String[] paramNames = new String[paramTypes.length];
            int j = Modifier.isStatic(member.getModifiers()) ? 0 : 1;
            boolean found = false;
            for (int i = 0; i < paramNames.length; ++i) {
                if (info.names[j] != null) {
                    found = true;
                    paramNames[i] = info.names[j];
                }
                ++j;
                if (paramTypes[i] != Double.TYPE && paramTypes[i] != Long.TYPE) continue;
                ++j;
            }
            if (found) {
                return paramNames;
            }
            return null;
        }
        return null;
    }

    private MethodInfo getMethodInfo() {
        MethodInfo info = null;
        if (this.methods != null && this.methodName != null) {
            info = (MethodInfo)this.methods.get(this.methodName);
        }
        return info;
    }

    public void readLocalVariableTable() throws IOException {
        int len = this.readShort();
        MethodInfo info = this.getMethodInfo();
        for (int j = 0; j < len; ++j) {
            this.readShort();
            this.readShort();
            int nameIndex = this.readShort();
            this.readShort();
            int index = this.readShort();
            if (info == null) continue;
            info.names[index] = this.resolveUtf8(nameIndex);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private static class MethodInfo {
        String[] names;
        int maxLocals;

        public MethodInfo(int maxLocals) {
            this.maxLocals = maxLocals;
            this.names = new String[maxLocals];
        }
    }
}

