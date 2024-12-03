/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.DefaultDecoderFactory;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.StringDecoder;

public abstract class DecoderFactory {
    public static final String KEY_FACTORY_CLASS = "net.fortuna.ical4j.factory.decoder";
    private static DecoderFactory instance;

    public static DecoderFactory getInstance() {
        return instance;
    }

    public abstract BinaryDecoder createBinaryDecoder(Encoding var1) throws UnsupportedEncodingException;

    public abstract StringDecoder createStringDecoder(Encoding var1) throws UnsupportedEncodingException;

    static {
        Optional<DefaultDecoderFactory> property = Configurator.getObjectProperty(KEY_FACTORY_CLASS);
        instance = property.orElse(new DefaultDecoderFactory());
    }
}

