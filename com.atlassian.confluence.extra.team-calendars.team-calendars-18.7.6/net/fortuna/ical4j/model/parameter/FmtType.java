/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class FmtType
extends Parameter {
    private static final long serialVersionUID = -8764966004966855480L;
    private String value;

    public FmtType(String aValue) {
        super("FMTTYPE", new Factory());
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

        public Factory() {
            super("FMTTYPE");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            return new FmtType(value);
        }
    }
}

