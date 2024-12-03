/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class XParameter
extends Parameter {
    private static final long serialVersionUID = -3372153616695145903L;
    private String value;

    public XParameter(String aName, String aValue) {
        super(aName, new Factory(aName));
        this.value = Strings.unquote(aValue);
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;
        private final String name;

        public Factory(String name) {
            super(name);
            this.name = name;
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            return new XParameter(this.name, value);
        }
    }
}

