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

public class AltRep
extends Parameter {
    private static final long serialVersionUID = -2445932592596993470L;
    private URI uri;

    public AltRep(String aValue) throws URISyntaxException {
        this(Uris.create(Strings.unquote(aValue)));
    }

    public AltRep(URI aUri) {
        super("ALTREP", new Factory());
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
            super("ALTREP");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            return new AltRep(value);
        }
    }
}

