/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.PropertyList;

public interface ComponentFactory<T extends Component> {
    public T createComponent();

    public T createComponent(PropertyList var1);

    public T createComponent(PropertyList var1, ComponentList var2);

    public boolean supports(String var1);
}

