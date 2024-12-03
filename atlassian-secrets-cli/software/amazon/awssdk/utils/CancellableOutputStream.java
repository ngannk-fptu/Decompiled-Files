/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.io.OutputStream;
import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public abstract class CancellableOutputStream
extends OutputStream {
    public abstract void cancel();
}

