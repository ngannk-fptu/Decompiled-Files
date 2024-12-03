/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.objenesis.instantiator.basic;

import org.springframework.objenesis.instantiator.annotations.Instantiator;
import org.springframework.objenesis.instantiator.annotations.Typology;
import org.springframework.objenesis.instantiator.basic.DelegatingToExoticInstantiator;

@Instantiator(value=Typology.STANDARD)
public class ProxyingInstantiator<T>
extends DelegatingToExoticInstantiator<T> {
    public ProxyingInstantiator(Class<T> type) {
        super("org.springframework.objenesis.instantiator.exotic.ProxyingInstantiator", type);
    }
}

