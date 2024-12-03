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
public final class ProdId
extends Property {
    public static final PropertyFactory<ProdId> FACTORY = new Factory();
    private static final long serialVersionUID = 8104072716649404803L;
    private final String value;

    public ProdId(String value) {
        super(Property.Id.PRODID);
        this.value = value;
    }

    public ProdId(List<Parameter> params, String value) {
        super(Property.Id.PRODID, params);
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
    implements PropertyFactory<ProdId> {
        private Factory() {
        }

        @Override
        public ProdId createProperty(List<Parameter> params, String value) {
            return new ProdId(params, value);
        }

        @Override
        public ProdId createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

