/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.instantiator.gcj;

import java.lang.reflect.InvocationTargetException;
import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;
import org.springframework.objenesis.instantiator.gcj.GCJInstantiatorBase;

@Instantiator(value=Typology.STANDARD)
public class GCJInstantiator<T>
extends GCJInstantiatorBase<T> {
    public GCJInstantiator(Class<T> type) {
        super(type);
    }

    @Override
    public T newInstance() {
        try {
            return this.type.cast(newObjectMethod.invoke((Object)dummyStream, this.type, Object.class));
        }
        catch (RuntimeException e) {
            throw new ObjenesisException(e);
        }
        catch (IllegalAccessException e) {
            throw new ObjenesisException(e);
        }
        catch (InvocationTargetException e) {
            throw new ObjenesisException(e);
        }
    }
}

