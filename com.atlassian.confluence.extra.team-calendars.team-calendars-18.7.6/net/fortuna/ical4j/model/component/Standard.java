/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.Observance;

public class Standard
extends Observance {
    private static final long serialVersionUID = -4750910013406451159L;

    public Standard() {
        super("STANDARD");
    }

    public Standard(PropertyList properties) {
        super("STANDARD", properties);
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<Standard> {
        public Factory() {
            super("STANDARD");
        }

        @Override
        public Standard createComponent() {
            return new Standard();
        }

        @Override
        public Standard createComponent(PropertyList properties) {
            return new Standard(properties);
        }

        @Override
        public Standard createComponent(PropertyList properties, ComponentList subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", "STANDARD"));
        }
    }
}

