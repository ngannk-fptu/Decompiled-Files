/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package net.fortuna.ical4j.vcard.property;

import java.net.URISyntaxException;
import java.util.List;
import net.fortuna.ical4j.validate.ValidationException;
import net.fortuna.ical4j.vcard.Group;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.Property;
import net.fortuna.ical4j.vcard.PropertyFactory;
import net.fortuna.ical4j.vcard.parameter.Encoding;
import net.fortuna.ical4j.vcard.parameter.Type;
import net.fortuna.ical4j.vcard.parameter.Value;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class XML
extends Property {
    public static final PropertyFactory<XML> FACTORY = new Factory();
    private static final long serialVersionUID = -12345L;
    private String value;
    private byte[] binary;
    private final Log log = LogFactory.getLog(XML.class);

    public XML(String value) {
        super(Property.Id.XML);
        this.value = value;
        this.getParameters().add(Value.URI);
    }

    public XML(byte[] binary) {
        this(binary, null);
    }

    public XML(byte[] binary, Type contentType) {
        super(Property.Id.XML);
        this.binary = binary;
        this.getParameters().add(Encoding.B);
        if (contentType != null) {
            this.getParameters().add(contentType);
        }
    }

    public XML(List<Parameter> params, String value) throws DecoderException {
        this(null, params, value);
    }

    public XML(Group group, List<Parameter> params, String value) throws DecoderException {
        super(group, Property.Id.KEY, params);
        Parameter valueParameter = this.getParameter(Parameter.Id.VALUE);
        if (valueParameter == null || Value.TEXT.equals(valueParameter)) {
            this.value = value;
        } else {
            Base64 decoder = new Base64();
            this.binary = decoder.decode(value.getBytes());
        }
    }

    public byte[] getBinary() {
        return this.binary;
    }

    @Override
    public String getValue() {
        Parameter valueParameter = this.getParameter(Parameter.Id.VALUE);
        if (valueParameter == null || Value.TEXT.equals(valueParameter)) {
            return this.value;
        }
        if (this.binary != null) {
            try {
                Base64 encoder = new Base64();
                return new String(encoder.encode(this.binary));
            }
            catch (EncoderException ee) {
                this.log.error((Object)"Error encoding binary data", (Throwable)ee);
            }
        }
        return null;
    }

    @Override
    public void validate() throws ValidationException {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements PropertyFactory<XML> {
        private Factory() {
        }

        @Override
        public XML createProperty(List<Parameter> params, String value) throws DecoderException, URISyntaxException {
            return new XML(params, value);
        }

        @Override
        public XML createProperty(Group group, List<Parameter> params, String value) throws DecoderException, URISyntaxException {
            return new XML(group, params, value);
        }
    }
}

