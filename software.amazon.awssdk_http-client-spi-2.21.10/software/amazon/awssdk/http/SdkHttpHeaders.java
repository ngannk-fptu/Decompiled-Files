/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.http;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkPublicApi
@Immutable
public interface SdkHttpHeaders {
    public Map<String, List<String>> headers();

    default public Optional<String> firstMatchingHeader(String header) {
        return SdkHttpUtils.firstMatchingHeader(this.headers(), (String)header);
    }

    default public Optional<String> firstMatchingHeader(Collection<String> headersToFind) {
        return SdkHttpUtils.firstMatchingHeaderFromCollection(this.headers(), headersToFind);
    }

    default public List<String> matchingHeaders(String header) {
        return SdkHttpUtils.allMatchingHeaders(this.headers(), (String)header).collect(Collectors.toList());
    }

    default public void forEachHeader(BiConsumer<? super String, ? super List<String>> consumer) {
        this.headers().forEach(consumer);
    }

    default public int numHeaders() {
        return this.headers().size();
    }
}

