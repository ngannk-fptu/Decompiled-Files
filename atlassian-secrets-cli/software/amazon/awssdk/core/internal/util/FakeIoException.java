/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.util;

import java.io.IOException;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class FakeIoException
extends IOException {
    private static final long serialVersionUID = 1L;

    public FakeIoException(String message) {
        super(message);
    }
}

