/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.model.ParameterFactoryImpl;

@Deprecated
public final class ParameterFactoryRegistry
extends ParameterFactoryImpl {
    private static final long serialVersionUID = -3372324894953715583L;

    public void register(String name, ParameterFactory factory) {
        this.registerExtendedFactory(name, factory);
    }
}

