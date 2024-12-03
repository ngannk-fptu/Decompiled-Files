/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class Related
extends Parameter {
    private static final long serialVersionUID = 1570525804115869565L;
    private static final String VALUE_START = "START";
    private static final String VALUE_END = "END";
    public static final Related START = new Related("START");
    public static final Related END = new Related("END");
    private String value;

    public Related(String aValue) {
        super("RELATED", new Factory());
        this.value = Strings.unquote(aValue);
        if (!VALUE_START.equals(this.value) && !VALUE_END.equals(this.value)) {
            throw new IllegalArgumentException("Invalid value [" + this.value + "]");
        }
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
            super("RELATED");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            Related parameter = new Related(value);
            if (START.equals(parameter)) {
                parameter = START;
            } else if (END.equals(parameter)) {
                parameter = END;
            }
            return parameter;
        }
    }
}

