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

public class Dir
extends Parameter {
    private static final long serialVersionUID = -8581904779721020689L;
    private URI uri;

    public Dir(String aValue) throws URISyntaxException {
        this(Uris.create(Strings.unquote(aValue)));
    }

    public Dir(URI aUri) {
        super("DIR", new Factory());
        this.uri = aUri;
    }

    public final URI getUri() {
        return this.uri;
    }

    @Override
    public final String getValue() {
        return Uris.decode(Strings.valueOf(this.getUri()));
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("DIR");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            return new Dir(value);
        }
    }
}

