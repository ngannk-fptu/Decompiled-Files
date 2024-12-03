/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public class StringInputStream
extends ByteArrayInputStream {
    private final String string;

    public StringInputStream(String s) {
        super(s.getBytes(StandardCharsets.UTF_8));
        this.string = s;
    }

    public String getString() {
        return this.string;
    }
}

