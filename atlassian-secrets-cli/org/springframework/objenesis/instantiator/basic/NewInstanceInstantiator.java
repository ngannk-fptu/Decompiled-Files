/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.ObjenesisException;
import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(value=Typology.NOT_COMPLIANT)
public class NewInstanceInstantiator<T>
implements ObjectInstantiator<T> {
    private final Class<T> type;

    public NewInstanceInstantiator(Class<T> type) {
        this.type = type;
    }

    @Override
    public T newInstance() {
        try {
            return this.type.newInstance();
        }
        catch (Exception e) {
            throw new ObjenesisException(e);
        }
    }
}

