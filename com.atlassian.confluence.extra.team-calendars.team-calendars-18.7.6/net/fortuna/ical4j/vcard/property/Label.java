/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.model.Escapable;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;
import net.fortuna.ical4j.vcard.parameter.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Label
extends Property
implements Escapable {
    public static final PropertyFactory<Label> FACTORY = new Factory();
    private static final long serialVersionUID = -3634101566227652040L;
    private final String value;

    public Label(String value, Type ... types) {
        super(Property.Id.LABEL);
        this.value = value;
        for (Type type : types) {
            this.getParameters().add(type);
        }
    }

    public Label(List<Parameter> params, String value) {
        super(Property.Id.LABEL, params);
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void validate() throws ValidationException {
        for (Parameter param : this.getParameters()) {
            try {
                this.assertTypeParameter(param);
            }
            catch (ValidationException ve) {
                try {
                    this.assertTextParameter(param);
                }
                catch (ValidationException ve2) {
                    this.assertPidParameter(param);
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<Label> {
        private Factory() {
        }

        @Override
        public Label createProperty(List<Parameter> params, String value) {
            return new Label(params, Strings.unescape(value));
        }

        @Override
        public Label createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

