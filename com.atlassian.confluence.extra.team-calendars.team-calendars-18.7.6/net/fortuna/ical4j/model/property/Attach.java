/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.model.property;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.util.DecoderFactory;
import net.fortuna.ical4j.util.EncoderFactory;
import net.fortuna.ical4j.util.Strings;
import net.fortuna.ical4j.util.Uris;
import net.fortuna.ical4j.validate.ParameterValidator;
import net.fortuna.ical4j.validate.ValidationException;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Attach
extends Property {
    private static final long serialVersionUID = 4439949507756383452L;
    private URI uri;
    private byte[] binary;

    public Attach() {
        super("ATTACH", new Factory());
    }

    public Attach(ParameterList aList, String aValue) throws URISyntaxException {
        super("ATTACH", aList, new Factory());
        this.setValue(aValue);
    }

    public Attach(byte[] data) {
        super("ATTACH", new Factory());
        this.getParameters().add(Encoding.BASE64);
        this.getParameters().add(Value.BINARY);
        this.binary = data;
    }

    public Attach(ParameterList aList, byte[] data) {
        super("ATTACH", aList, new Factory());
        this.binary = data;
    }

    public Attach(URI aUri) {
        super("ATTACH", new Factory());
        this.uri = aUri;
    }

    public Attach(ParameterList aList, URI aUri) {
        super("ATTACH", aList, new Factory());
        this.uri = aUri;
    }

    @Override
    public final void validate() throws ValidationException {
        ParameterValidator.assertOneOrLess("FMTTYPE", this.getParameters());
        if (Value.BINARY.equals(this.getParameter("VALUE"))) {
            ParameterValidator.assertOne("ENCODING", this.getParameters());
            if (!Encoding.BASE64.equals(this.getParameter("ENCODING"))) {
                throw new ValidationException("If the value type parameter is [BINARY], the inlineencoding parameter MUST be specified with the value [BASE64]");
            }
        }
    }

    public final byte[] getBinary() {
        return this.binary;
    }

    public final URI getUri() {
        return this.uri;
    }

    @Override
    public final void setValue(String aValue) throws URISyntaxException {
        if (this.getParameter("ENCODING") != null) {
            try {
                BinaryDecoder decoder = DecoderFactory.getInstance().createBinaryDecoder((Encoding)this.getParameter("ENCODING"));
                this.binary = decoder.decode(aValue.getBytes());
            }
            catch (UnsupportedEncodingException uee) {
                Logger log = LoggerFactory.getLogger(Attach.class);
                log.error("Error encoding binary data", (Throwable)uee);
            }
            catch (DecoderException de) {
                Logger log = LoggerFactory.getLogger(Attach.class);
                log.error("Error decoding binary data", (Throwable)de);
            }
        } else {
            this.uri = Uris.create(aValue);
        }
    }

    @Override
    public final String getValue() {
        if (this.getUri() != null) {
            return Uris.decode(Strings.valueOf(this.getUri()));
        }
        if (this.getBinary() != null) {
            try {
                BinaryEncoder encoder = EncoderFactory.getInstance().createBinaryEncoder((Encoding)this.getParameter("ENCODING"));
                return new String(encoder.encode(this.getBinary()));
            }
            catch (UnsupportedEncodingException | EncoderException uee) {
                Logger log = LoggerFactory.getLogger(Attach.class);
                log.error("Error encoding binary data", (Throwable)uee);
            }
        }
        return null;
    }

    public final void setBinary(byte[] binary) {
        this.binary = binary;
        this.uri = null;
    }

    public final void setUri(URI uri) {
        this.uri = uri;
        this.binary = null;
    }

    public static class Factory
    extends Content.Factory
    implements PropertyFactory<Property> {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("ATTACH");
        }

        @Override
        public Property createProperty(ParameterList parameters, String value) throws IOException, URISyntaxException, ParseException {
            return new Attach(parameters, value);
        }

        @Override
        public Property createProperty() {
            return new Attach();
        }
    }
}

