/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.InterfaceHandler;
import com.sun.media.jai.rmi.JaiI18N;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.Serializer;

public class InterfaceState
implements SerializableState {
    private transient Object theObject;
    private transient Serializer[] theSerializers;
    private transient RenderingHints hints;
    static /* synthetic */ Class class$javax$media$jai$JAI;

    public InterfaceState(Object o, Serializer[] serializers, RenderingHints h) {
        if (o == null || serializers == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.theObject = o;
        this.theSerializers = serializers;
        this.hints = h == null ? null : (RenderingHints)h.clone();
    }

    public Object getObject() {
        return this.theObject;
    }

    public Class getObjectClass() {
        return this.theObject.getClass();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        int numSerializers = this.theSerializers.length;
        out.writeInt(numSerializers);
        for (int i = 0; i < numSerializers; ++i) {
            Serializer s = this.theSerializers[i];
            out.writeObject(s.getSupportedClass());
            out.writeObject(s.getState(this.theObject, this.hints));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int numInterfaces = in.readInt();
        Class[] interfaces = new Class[numInterfaces];
        SerializableState[] implementations = new SerializableState[numInterfaces];
        for (int i = 0; i < numInterfaces; ++i) {
            interfaces[i] = (Class)in.readObject();
            implementations[i] = (SerializableState)in.readObject();
        }
        InterfaceHandler handler = new InterfaceHandler(interfaces, implementations);
        this.theObject = Proxy.newProxyInstance((class$javax$media$jai$JAI == null ? (class$javax$media$jai$JAI = InterfaceState.class$("javax.media.jai.JAI")) : class$javax$media$jai$JAI).getClassLoader(), interfaces, (InvocationHandler)handler);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

