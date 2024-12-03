/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.io.SdkFilterInputStream;
import software.amazon.awssdk.http.Abortable;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class ResponseInputStream<ResponseT>
extends SdkFilterInputStream
implements Abortable {
    private final ResponseT response;
    private final Abortable abortable;

    public ResponseInputStream(ResponseT resp, AbortableInputStream in) {
        super(in);
        this.response = Validate.paramNotNull(resp, "response");
        this.abortable = Validate.paramNotNull(in, "abortableInputStream");
    }

    public ResponseInputStream(ResponseT resp, InputStream in) {
        super(in);
        this.response = Validate.paramNotNull(resp, "response");
        this.abortable = in instanceof Abortable ? (Abortable)((Object)in) : null;
    }

    public ResponseT response() {
        return this.response;
    }

    @Override
    public void abort() {
        if (this.abortable != null) {
            this.abortable.abort();
        }
    }
}

