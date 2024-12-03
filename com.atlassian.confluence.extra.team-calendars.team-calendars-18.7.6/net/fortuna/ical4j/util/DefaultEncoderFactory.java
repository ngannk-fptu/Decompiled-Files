/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.util.EncoderFactory;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.QuotedPrintableCodec;

public class DefaultEncoderFactory
extends EncoderFactory {
    private static final String UNSUPPORTED_ENCODING_MESSAGE = "Encoder not available for encoding [{0}]";

    @Override
    public BinaryEncoder createBinaryEncoder(Encoding encoding) throws UnsupportedEncodingException {
        if (Encoding.QUOTED_PRINTABLE.equals(encoding)) {
            return new QuotedPrintableCodec();
        }
        if (Encoding.BASE64.equals(encoding)) {
            return new Base64();
        }
        throw new UnsupportedEncodingException(MessageFormat.format(UNSUPPORTED_ENCODING_MESSAGE, encoding));
    }

    @Override
    public StringEncoder createStringEncoder(Encoding encoding) throws UnsupportedEncodingException {
        if (Encoding.QUOTED_PRINTABLE.equals(encoding)) {
            return new QuotedPrintableCodec();
        }
        throw new UnsupportedEncodingException(MessageFormat.format(UNSUPPORTED_ENCODING_MESSAGE, encoding));
    }
}

