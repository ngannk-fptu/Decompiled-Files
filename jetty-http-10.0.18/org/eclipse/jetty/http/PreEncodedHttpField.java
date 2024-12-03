/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.TypeUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.http;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.ServiceLoader;
import org.eclipse.jetty.http.Http1FieldPreEncoder;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFieldPreEncoder;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreEncodedHttpField
extends HttpField {
    private static final Logger LOG = LoggerFactory.getLogger(PreEncodedHttpField.class);
    private static final HttpFieldPreEncoder[] __encoders;
    private final byte[][] _encodedField = new byte[__encoders.length][];

    private static int index(HttpVersion version) {
        switch (version) {
            case HTTP_1_0: 
            case HTTP_1_1: {
                return 0;
            }
            case HTTP_2: {
                return 1;
            }
            case HTTP_3: {
                return 2;
            }
        }
        return -1;
    }

    public PreEncodedHttpField(HttpHeader header, String name, String value) {
        super(header, name, value);
        for (int i = 0; i < __encoders.length; ++i) {
            if (__encoders[i] == null) continue;
            this._encodedField[i] = __encoders[i].getEncodedField(header, name, value);
        }
    }

    public PreEncodedHttpField(HttpHeader header, String value) {
        this(header, header.asString(), value);
    }

    public PreEncodedHttpField(String name, String value) {
        this(null, name, value);
    }

    public void putTo(ByteBuffer bufferInFillMode, HttpVersion version) {
        bufferInFillMode.put(this._encodedField[PreEncodedHttpField.index(version)]);
    }

    public int getEncodedLength(HttpVersion version) {
        return this._encodedField[PreEncodedHttpField.index(version)].length;
    }

    static {
        ArrayList encoders = new ArrayList();
        TypeUtil.serviceProviderStream(ServiceLoader.load(HttpFieldPreEncoder.class)).forEach(provider -> {
            try {
                HttpFieldPreEncoder encoder = (HttpFieldPreEncoder)provider.get();
                if (PreEncodedHttpField.index(encoder.getHttpVersion()) >= 0) {
                    encoders.add(encoder);
                }
            }
            catch (Error | RuntimeException e) {
                LOG.debug("Unable to add HttpFieldPreEncoder", e);
            }
        });
        LOG.debug("HttpField encoders loaded: {}", encoders);
        int size = 1;
        for (HttpFieldPreEncoder e : encoders) {
            size = Math.max(size, PreEncodedHttpField.index(e.getHttpVersion()) + 1);
        }
        __encoders = new HttpFieldPreEncoder[size];
        for (HttpFieldPreEncoder e : encoders) {
            int i = PreEncodedHttpField.index(e.getHttpVersion());
            if (__encoders[i] == null) {
                PreEncodedHttpField.__encoders[i] = e;
                continue;
            }
            LOG.warn("multiple PreEncoders for {}", (Object)e.getHttpVersion());
        }
        if (__encoders[0] == null) {
            PreEncodedHttpField.__encoders[0] = new Http1FieldPreEncoder();
        }
    }
}

