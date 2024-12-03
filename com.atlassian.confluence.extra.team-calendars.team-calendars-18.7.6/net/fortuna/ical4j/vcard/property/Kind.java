/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Kind
extends Property {
    private static final long serialVersionUID = -3114221975393833838L;
    public static final Kind INDIVIDUAL = new Kind(Collections.unmodifiableList(new ArrayList()), "individual");
    public static final Kind GROUP = new Kind(Collections.unmodifiableList(new ArrayList()), "group");
    public static final Kind ORG = new Kind(Collections.unmodifiableList(new ArrayList()), "org");
    public static final Kind LOCATION = new Kind(Collections.unmodifiableList(new ArrayList()), "location");
    public static final Kind THING = new Kind(Collections.unmodifiableList(new ArrayList()), "thing");
    public static final PropertyFactory<Kind> FACTORY = new Factory();
    private final String value;

    public Kind(String value) {
        super(Property.Id.KIND);
        this.value = value;
    }

    public Kind(List<Parameter> params, String value) {
        super(Property.Id.KIND, params);
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
        this.assertParametersEmpty();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Kind> {
        private Factory() {
        }

        @Override
        public Kind createProperty(List<Parameter> params, String value) {
            return new Kind(params, value);
        }

        @Override
        public Kind createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

