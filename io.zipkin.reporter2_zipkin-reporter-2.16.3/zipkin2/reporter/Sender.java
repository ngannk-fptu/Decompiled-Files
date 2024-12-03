/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zipkin2.Call
 *  zipkin2.Component
 *  zipkin2.codec.Encoding
 */
package zipkin2.reporter;

import java.util.Collections;
import java.util.List;
import zipkin2.Call;
import zipkin2.Component;
import zipkin2.codec.Encoding;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.internal.InternalReporter;

public abstract class Sender
extends Component {
    public abstract Encoding encoding();

    public abstract int messageMaxBytes();

    public abstract int messageSizeInBytes(List<byte[]> var1);

    public int messageSizeInBytes(int encodedSizeInBytes) {
        return this.messageSizeInBytes(Collections.singletonList(new byte[encodedSizeInBytes]));
    }

    public abstract Call<Void> sendSpans(List<byte[]> var1);

    static {
        InternalReporter.instance = new InternalReporter(){

            @Override
            public AsyncReporter.Builder toBuilder(AsyncReporter<?> asyncReporter) {
                return ((AsyncReporter.BoundedAsyncReporter)asyncReporter).toBuilder();
            }
        };
    }
}

