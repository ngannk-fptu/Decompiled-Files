/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.BytesWrapper;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class ResponseBytes<ResponseT>
extends BytesWrapper {
    private final ResponseT response;

    private ResponseBytes(ResponseT response, byte[] bytes) {
        super(bytes);
        this.response = Validate.paramNotNull(response, "response");
    }

    public static <ResponseT> ResponseBytes<ResponseT> fromInputStream(ResponseT response, InputStream stream) throws UncheckedIOException {
        return new ResponseBytes<ResponseT>(response, FunctionalUtils.invokeSafely(() -> IoUtils.toByteArray(stream)));
    }

    public static <ResponseT> ResponseBytes<ResponseT> fromByteArray(ResponseT response, byte[] bytes) {
        return new ResponseBytes<ResponseT>(response, Arrays.copyOf(bytes, bytes.length));
    }

    public static <ResponseT> ResponseBytes<ResponseT> fromByteArrayUnsafe(ResponseT response, byte[] bytes) {
        return new ResponseBytes<ResponseT>(response, bytes);
    }

    public ResponseT response() {
        return this.response;
    }

    public String toString() {
        return ToString.builder("ResponseBytes").add("response", this.response).add("bytes", this.asByteArrayUnsafe()).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ResponseBytes that = (ResponseBytes)o;
        return this.response != null ? this.response.equals(that.response) : that.response == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.response != null ? this.response.hashCode() : 0);
        return result;
    }
}

