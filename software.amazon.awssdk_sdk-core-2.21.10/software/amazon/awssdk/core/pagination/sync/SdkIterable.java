/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.core.pagination.sync;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public interface SdkIterable<T>
extends Iterable<T> {
    default public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }
}

