/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import net.fortuna.ical4j.model.ComponentFactory;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.Observance;

public class Daylight
extends Observance {
    private static final long serialVersionUID = -2494710612002978763L;

    public Daylight() {
        super("DAYLIGHT");
    }

    public Daylight(PropertyList properties) {
        super("DAYLIGHT", properties);
    }

    public static class Factory
    extends Content.Factory
    implements ComponentFactory<Daylight> {
        public Factory() {
            super("DAYLIGHT");
        }

        @Override
        public Daylight createComponent() {
            return new Daylight();
        }

        @Override
        public Daylight createComponent(PropertyList properties) {
            return new Daylight(properties);
        }

        @Override
        public Daylight createComponent(PropertyList properties, ComponentList subComponents) {
            throw new UnsupportedOperationException(String.format("%s does not support sub-components", "DAYLIGHT"));
        }
    }
}

