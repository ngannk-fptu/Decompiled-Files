/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 */
package org.apache.lucene.analysis.payloads;

import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.DelimitedPayloadTokenFilter;
import org.apache.lucene.analysis.payloads.FloatEncoder;
import org.apache.lucene.analysis.payloads.IdentityEncoder;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class DelimitedPayloadTokenFilterFactory
extends TokenFilterFactory
implements ResourceLoaderAware {
    public static final String ENCODER_ATTR = "encoder";
    public static final String DELIMITER_ATTR = "delimiter";
    private final String encoderClass;
    private final char delimiter;
    private PayloadEncoder encoder;

    public DelimitedPayloadTokenFilterFactory(Map<String, String> args) {
        super(args);
        this.encoderClass = this.require(args, ENCODER_ATTR);
        this.delimiter = this.getChar(args, DELIMITER_ATTR, '|');
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    public DelimitedPayloadTokenFilter create(TokenStream input) {
        return new DelimitedPayloadTokenFilter(input, this.delimiter, this.encoder);
    }

    @Override
    public void inform(ResourceLoader loader) {
        this.encoder = this.encoderClass.equals("float") ? new FloatEncoder() : (this.encoderClass.equals("integer") ? new IntegerEncoder() : (this.encoderClass.equals("identity") ? new IdentityEncoder() : loader.newInstance(this.encoderClass, PayloadEncoder.class)));
    }
}

