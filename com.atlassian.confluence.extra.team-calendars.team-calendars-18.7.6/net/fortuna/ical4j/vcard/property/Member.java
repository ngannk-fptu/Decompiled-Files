/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Member
extends Property {
    public static final PropertyFactory<Member> FACTORY = new Factory();
    private static final long serialVersionUID = 6622845049765958916L;
    private final URI uri;

    public Member(URI uri) {
        super(Property.Id.MEMBER);
        this.uri = uri;
    }

    public Member(List<Parameter> params, String value) throws URISyntaxException {
        super(Property.Id.MEMBER, params);
        this.uri = new URI(value);
    }

    public URI getUri() {
        return this.uri;
    }

    @Override
    public String getValue() {
        return Strings.valueOf(this.uri);
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            this.assertPidParameter(param);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Member> {
        private Factory() {
        }

        @Override
        public Member createProperty(List<Parameter> params, String value) throws URISyntaxException {
            return new Member(params, value);
        }

        @Override
        public Member createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

