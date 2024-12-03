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

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Note
extends Property
implements Escapable {
    public static final PropertyFactory<Note> FACTORY = new Factory();
    private static final long serialVersionUID = -1435219426295284759L;
    private final String value;

    public Note(String value) {
        super(Property.Id.NOTE);
        this.value = value;
    }

    public Note(List<Parameter> params, String value) {
        super(Property.Id.NOTE, params);
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
    implements PropertyFactory<Note> {
        private Factory() {
        }

        @Override
        public Note createProperty(List<Parameter> params, String value) {
            return new Note(params, Strings.unescape(value));
        }

        @Override
        public Note createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

