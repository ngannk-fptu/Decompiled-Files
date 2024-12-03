/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.model.TextList;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Categories
extends Property {
    public static final PropertyFactory<Categories> FACTORY = new Factory();
    private static final long serialVersionUID = -3233034210546002366L;
    private final TextList categories;

    public Categories(String ... categories) {
        super(Property.Id.CATEGORIES);
        if (categories.length == 0) {
            throw new IllegalArgumentException("Must specify at least category value");
        }
        this.categories = new TextList(categories);
    }

    public Categories(List<Parameter> params, String value) {
        super(Property.Id.CATEGORIES);
        this.categories = new TextList(value);
    }

    public TextList getCategories() {
        return this.categories;
    }

    @Override
    public String getValue() {
        return this.categories.toString();
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
    implements PropertyFactory<Categories> {
        private Factory() {
        }

        @Override
        public Categories createProperty(List<Parameter> params, String value) {
            return new Categories(params, value);
        }

        @Override
        public Categories createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

