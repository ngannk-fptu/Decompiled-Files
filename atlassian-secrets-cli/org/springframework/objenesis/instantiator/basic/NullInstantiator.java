/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.instantiator.ObjectInstantiator;
import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;

@Instantiator(value=Typology.NOT_COMPLIANT)
public class NullInstantiator<T>
implements ObjectInstantiator<T> {
    public NullInstantiator(Class<T> type) {
    }

    @Override
    public T newInstance() {
        return null;
    }
}

