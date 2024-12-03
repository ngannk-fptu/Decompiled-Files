/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.PropertyFactoryImpl;

@Deprecated
public final class PropertyFactoryRegistry
extends PropertyFactoryImpl {
    private static final long serialVersionUID = 3924903719847189199L;

    public void register(String name, PropertyFactory factory) {
        this.registerExtendedFactory(name, factory);
    }
}

