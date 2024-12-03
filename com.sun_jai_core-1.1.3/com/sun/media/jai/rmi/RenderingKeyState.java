/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import com.sun.media.jai.rmi.RenderingHintsState;
import com.sun.media.jai.rmi.SerializableStateImpl;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

public class RenderingKeyState
extends SerializableStateImpl {
    private transient RenderingHintsState.HintElement predefinedKey;
    static /* synthetic */ Class class$java$awt$RenderingHints$Key;

    public static Class[] getSupportedClasses() {
        return new Class[]{class$java$awt$RenderingHints$Key == null ? (class$java$awt$RenderingHints$Key = RenderingKeyState.class$("java.awt.RenderingHints$Key")) : class$java$awt$RenderingHints$Key};
    }

    public static boolean permitsSubclasses() {
        return true;
    }

    public RenderingKeyState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
        Hashtable predefinedObjects = RenderingHintsState.getHintTable();
        this.predefinedKey = (RenderingHintsState.HintElement)predefinedObjects.get(o);
        if (this.predefinedKey == null) {
            throw new RuntimeException(JaiI18N.getString("RenderingKeyState0"));
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.predefinedKey);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.predefinedKey = (RenderingHintsState.HintElement)in.readObject();
        this.theObject = this.predefinedKey.getObject();
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

