/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class Encoding
extends Parameter {
    private static final long serialVersionUID = 7536336461076399077L;
    private static final String VALUE_SEVEN_BIT = "7BIT";
    private static final String VALUE_EIGHT_BIT = "8BIT";
    private static final String VALUE_BINARY = "BINARY";
    private static final String VALUE_QUOTED_PRINTABLE = "QUOTED-PRINTABLE";
    private static final String VALUE_BASE64 = "BASE64";
    public static final Encoding SEVEN_BIT = new Encoding("7BIT");
    public static final Encoding EIGHT_BIT = new Encoding("8BIT");
    public static final Encoding BINARY = new Encoding("BINARY");
    public static final Encoding QUOTED_PRINTABLE = new Encoding("QUOTED-PRINTABLE");
    public static final Encoding BASE64 = new Encoding("BASE64");
    private String value;

    public Encoding(String aValue) {
        super("ENCODING", new Factory());
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
            super("ENCODING");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            Encoding parameter = new Encoding(value);
            if (EIGHT_BIT.equals(parameter)) {
                parameter = EIGHT_BIT;
            } else if (BASE64.equals(parameter)) {
                parameter = BASE64;
            }
            return parameter;
        }
    }
}

