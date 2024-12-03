/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.util;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.DefaultEncoderFactory;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.StringEncoder;

public abstract class EncoderFactory {
    public static final String KEY_FACTORY_CLASS = "net.fortuna.ical4j.factory.encoder";
    private static EncoderFactory instance;

    public static EncoderFactory getInstance() {
        return instance;
    }

    public abstract BinaryEncoder createBinaryEncoder(Encoding var1) throws UnsupportedEncodingException;

    public abstract StringEncoder createStringEncoder(Encoding var1) throws UnsupportedEncodingException;

    static {
        Optional<DefaultEncoderFactory> property = Configurator.getObjectProperty(KEY_FACTORY_CLASS);
        instance = property.orElse(new DefaultEncoderFactory());
    }
}

