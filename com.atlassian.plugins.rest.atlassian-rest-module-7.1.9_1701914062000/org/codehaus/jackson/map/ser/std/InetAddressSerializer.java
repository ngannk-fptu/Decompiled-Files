/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.net.InetAddress;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.ser.std.ScalarSerializerBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class InetAddressSerializer
extends ScalarSerializerBase<InetAddress> {
    public static final InetAddressSerializer instance = new InetAddressSerializer();

    public InetAddressSerializer() {
        super(InetAddress.class);
    }

    @Override
    public void serialize(InetAddress value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        String str = value.toString().trim();
        int ix = str.indexOf(47);
        if (ix >= 0) {
            str = ix == 0 ? str.substring(1) : str.substring(0, ix);
        }
        jgen.writeString(str);
    }

    @Override
    public void serializeWithType(InetAddress value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForScalar(value, jgen, InetAddress.class);
        this.serialize(value, jgen, provider);
        typeSer.writeTypeSuffixForScalar(value, jgen);
    }
}

