/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class TzId
extends Parameter
implements Escapable {
    private static final long serialVersionUID = 2366516258055857879L;
    public static final String PREFIX = "/";
    private String value;

    public TzId(String aValue) {
        super("TZID", new Factory());
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
            super("TZID");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            return new TzId(Strings.unescape(value));
        }
    }
}

