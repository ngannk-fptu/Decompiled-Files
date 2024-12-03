/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.Abortable
 *  software.amazon.awssdk.http.AbortableInputStream
 *  software.amazon.awssdk.utils.Validate
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
        super((InputStream)in);
        this.response = Validate.paramNotNull(resp, (String)"response");
        this.abortable = (Abortable)Validate.paramNotNull((Object)in, (String)"abortableInputStream");
    }

    public ResponseInputStream(ResponseT resp, InputStream in) {
        super(in);
        this.response = Validate.paramNotNull(resp, (String)"response");
        this.abortable = in instanceof Abortable ? (Abortable)in : null;
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

