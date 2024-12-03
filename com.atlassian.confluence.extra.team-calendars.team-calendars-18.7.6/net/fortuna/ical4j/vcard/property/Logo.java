/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;
import net.fortuna.ical4j.vcard.parameter.Encoding;
import net.fortuna.ical4j.vcard.parameter.Type;
import net.fortuna.ical4j.vcard.parameter.Value;
import net.fortuna.ical4j.vcard.property.Photo;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Logo
extends Property {
    public static final PropertyFactory<Logo> FACTORY = new Factory();
    private static final long serialVersionUID = 7255763733402012595L;
    private final Log log = LogFactory.getLog(Photo.class);
    private URI uri;
    private byte[] binary;

    public Logo(URI uri) {
        super(Property.Id.LOGO);
        this.uri = uri;
        this.getParameters().add(Value.URI);
    }

    public Logo(byte[] binary) {
        this(binary, null);
    }

    public Logo(byte[] binary, Type contentType) {
        super(Property.Id.LOGO);
        this.binary = binary;
        this.getParameters().add(Encoding.B);
        if (contentType != null) {
            this.getParameters().add(contentType);
        }
    }

    public Logo(List<Parameter> params, String value) throws URISyntaxException, DecoderException {
        super(Property.Id.LOGO, params);
        Parameter valueParameter = this.getParameter(Parameter.Id.VALUE);
        if (valueParameter != null && Value.URI.equals(valueParameter) || valueParameter != null && CompatibilityHints.isHintEnabled("ical4j.parsing.relaxed") && "URL".equalsIgnoreCase(valueParameter.getValue())) {
            this.uri = new URI(value);
        } else {
            Base64 decoder = new Base64();
            this.binary = decoder.decode(value.getBytes());
        }
    }

    public URI getUri() {
        return this.uri;
    }

    public byte[] getBinary() {
        return this.binary;
    }

    @Override
    public String getValue() {
        String stringValue = null;
        if (Value.URI.equals(this.getParameter(Parameter.Id.VALUE))) {
            stringValue = Strings.valueOf(this.uri);
        } else if (this.binary != null) {
            try {
                Base64 encoder = new Base64();
                stringValue = new String(encoder.encode(this.binary));
            }
            catch (EncoderException ee) {
                this.log.error((Object)"Error encoding binary data", (Throwable)ee);
            }
        }
        return stringValue;
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
    implements PropertyFactory<Logo> {
        private Factory() {
        }

        @Override
        public Logo createProperty(List<Parameter> params, String value) throws URISyntaxException, DecoderException {
            return new Logo(params, value);
        }

        @Override
        public Logo createProperty(Group group, List<Parameter> params, String value) throws URISyntaxException, ParseException {
            return null;
        }
    }
}

