/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.instantiator.sun;

import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;
import org.springframework.objenesis.instantiator.basic.DelegatingToExoticInstantiator;

@Instantiator(value=Typology.STANDARD)
public class MagicInstantiator<T>
extends DelegatingToExoticInstantiator<T> {
    public MagicInstantiator(Class<T> type) {
        super("org.springframework.objenesis.instantiator.exotic.MagicInstantiator", type);
    }
}

