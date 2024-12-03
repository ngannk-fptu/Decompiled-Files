/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;
import net.fortuna.ical4j.vcard.parameter.Value;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Agent
extends Property
implements Escapable {
    public static final PropertyFactory<Agent> FACTORY = new Factory();
    private static final long serialVersionUID = 2670466615841142934L;
    private URI uri;
    private String text;

    public Agent(URI uri) {
        super(Property.Id.AGENT);
        this.uri = uri;
    }

    public Agent(String text) {
        super(Property.Id.AGENT);
        this.text = text;
        this.getParameters().add(Value.TEXT);
    }

    public Agent(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.AGENT, params);
        if (Value.TEXT.equals(this.getParameter(Parameter.Id.VALUE))) {
            this.text = value;
        } else {
            this.uri = new URI(value);
        }
    }

    public URI getUri() {
        return this.uri;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String getValue() {
        if (Value.TEXT.equals(this.getParameter(Parameter.Id.VALUE))) {
            return this.text;
        }
        return Strings.valueOf(this.uri);
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            try {
                this.assertTextParameter(param);
            }
            catch (ValidationException ve) {
                this.assertPidParameter(param);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Agent> {
        private Factory() {
        }

        @Override
        public Agent createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new Agent(params, Strings.unescape(value));
        }

        @Override
        public Agent createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException {
            return null;
        }
    }
}

