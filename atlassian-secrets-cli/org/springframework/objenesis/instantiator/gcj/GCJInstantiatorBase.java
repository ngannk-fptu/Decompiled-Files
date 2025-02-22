/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.instantiator.gcj;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public abstract class GCJInstantiatorBase<T>
implements ObjectInstantiator<T> {
    static Method newObjectMethod = null;
    static ObjectInputStream dummyStream;
    protected final Class<T> type;

    private static void initialize() {
        if (newObjectMethod == null) {
            try {
                newObjectMethod = ObjectInputStream.class.getDeclaredMethod("newObject", Class.class, Class.class);
                newObjectMethod.setAccessible(true);
                dummyStream = new DummyStream();
            }
            catch (RuntimeException e) {
                throw new ObjenesisException(e);
            }
            catch (NoSuchMethodException e) {
                throw new ObjenesisException(e);
            }
            catch (IOException e) {
                throw new ObjenesisException(e);
            }
        }
    }

    public GCJInstantiatorBase(Class<T> type) {
        this.type = type;
        GCJInstantiatorBase.initialize();
    }

    @Override
    public abstract T newInstance();

    private static class DummyStream
    extends ObjectInputStream {
    }
}

