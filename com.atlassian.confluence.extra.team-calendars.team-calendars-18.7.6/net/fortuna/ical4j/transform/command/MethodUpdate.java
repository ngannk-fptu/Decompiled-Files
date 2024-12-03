/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.command;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.transform.Transformer;

public class MethodUpdate
implements Transformer<Calendar> {
    private final Method newMethod;

    public MethodUpdate(Method method) {
        this.newMethod = method;
    }

    @Override
    public Calendar transform(Calendar object) {
        PropertyList<Property> calProps = object.getProperties();
        Property oldMethod = (Property)calProps.getProperty("METHOD");
        if (oldMethod != null) {
            calProps.remove(oldMethod);
        }
        calProps.add(this.newMethod);
        return object;
    }
}

