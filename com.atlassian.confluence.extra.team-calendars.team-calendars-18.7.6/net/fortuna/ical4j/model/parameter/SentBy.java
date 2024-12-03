/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URI;
import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;

public class SentBy
extends Parameter {
    private static final long serialVersionUID = -1169413145174029391L;
    private URI address;

    public SentBy(String aValue) throws URISyntaxException {
        this(Uris.create(Strings.unquote(aValue)));
    }

    public SentBy(URI aUri) {
        super("SENT-BY", new Factory());
        this.address = aUri;
    }

    public final URI getAddress() {
        return this.address;
    }

    @Override
    public final String getValue() {
        return Uris.decode(Strings.valueOf(this.getAddress()));
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("SENT-BY");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            return new SentBy(value);
        }
    }
}

