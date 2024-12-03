/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.parameter;

import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;

public class Filename
extends Parameter {
    private static final long serialVersionUID = 1L;
    private static final String PARAMETER_NAME = "FILENAME";
    private final String value;

    public Filename(String value) {
        super(PARAMETER_NAME, new Factory());
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super(Filename.PARAMETER_NAME);
        }

        public Parameter createParameter(String value) {
            return new Filename(value);
        }
    }
}

