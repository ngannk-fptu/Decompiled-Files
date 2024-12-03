/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ClientPidMap
extends Property {
    public static final PropertyFactory<ClientPidMap> FACTORY = new Factory();
    private static final long serialVersionUID = 1533383111522264554L;
    private static final String DELIMITER = ";";
    private int pid;
    private String urn;

    public ClientPidMap(int pid, String urn) {
        super(Property.Id.CLIENTPIDMAP);
        this.pid = pid;
        this.urn = urn;
    }

    public ClientPidMap(List<Parameter> params, String value) {
        this(null, params, value);
    }

    public ClientPidMap(Group group, List<Parameter> params, String value) {
        super(group, Property.Id.CLIENTPIDMAP, params);
        String[] components = value.split(DELIMITER);
        this.pid = new Integer(components[0]);
        this.urn = components[1];
    }

    @Override
    public String getValue() {
        return String.valueOf(this.pid) + DELIMITER + this.urn;
    }

    public int getPid() {
        return this.pid;
    }

    public String getUrn() {
        return this.urn;
    }

    @Override
    public void validate() throws ValidationException {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<ClientPidMap> {
        private Factory() {
        }

        @Override
        public ClientPidMap createProperty(List<Parameter> params, String value) {
            return new ClientPidMap(params, value);
        }

        @Override
        public ClientPidMap createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return new ClientPidMap(group, params, value);
        }
    }
}

