/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.json.SdkJsonGenerator;
import com.amazonaws.protocol.json.StructuredJsonGenerator;
import com.amazonaws.util.TimestampFormat;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;
import java.io.IOException;
import java.util.Date;

@SdkInternalApi
class SdkCborGenerator
extends SdkJsonGenerator {
    private static final int CBOR_TAG_TIMESTAP = 1;

    public SdkCborGenerator(JsonFactory factory, String contentType) {
        super(factory, contentType);
    }

    @Override
    public StructuredJsonGenerator writeValue(Date date, TimestampFormat timestampFormat) {
        if (!(this.getGenerator() instanceof CBORGenerator)) {
            throw new IllegalStateException("SdkCborGenerator is not created with a CBORGenerator.");
        }
        CBORGenerator generator = (CBORGenerator)this.getGenerator();
        try {
            generator.writeTag(1);
            generator.writeNumber(date.getTime());
        }
        catch (IOException e) {
            throw new SdkJsonGenerator.JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public byte[] getBytes() {
        try {
            return super.getBytes();
        }
        catch (NoSuchMethodError e) {
            throw new RuntimeException("Jackson jackson-core/jackson-dataformat-cbor incompatible library version detected.\nYou have two possible resolutions:\n\t\t1) Ensure the com.fasterxml.jackson.core:jackson-core & com.fasterxml.jackson.dataformat:jackson-dataformat-cbor libraries on your classpath have the same version number\n\t\t2) Disable CBOR wire-protocol by passing the -Dcom.amazonaws.sdk.disableCbor property or setting the AWS_CBOR_DISABLE environment variable (warning this may affect performance)", e);
        }
    }
}

