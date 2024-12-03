/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.Abortable;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public final class AbortableInputStream
extends FilterInputStream
implements Abortable {
    private final Abortable abortable;

    private AbortableInputStream(InputStream delegate, Abortable abortable) {
        super(Validate.paramNotNull(delegate, "delegate"));
        this.abortable = Validate.paramNotNull(abortable, "abortable");
    }

    public static AbortableInputStream create(InputStream delegate, Abortable abortable) {
        return new AbortableInputStream(delegate, abortable);
    }

    public static AbortableInputStream create(InputStream delegate) {
        if (delegate instanceof Abortable) {
            return new AbortableInputStream(delegate, (Abortable)((Object)delegate));
        }
        return new AbortableInputStream(delegate, () -> {});
    }

    public static AbortableInputStream createEmpty() {
        return AbortableInputStream.create(new ByteArrayInputStream(new byte[0]));
    }

    @Override
    public void abort() {
        this.abortable.abort();
    }

    @SdkTestInternalApi
    public InputStream delegate() {
        return this.in;
    }
}

