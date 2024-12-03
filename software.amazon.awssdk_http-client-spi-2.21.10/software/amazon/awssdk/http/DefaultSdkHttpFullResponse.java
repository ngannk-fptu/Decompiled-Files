/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.internal.http.LowCopyListMap;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@Immutable
class DefaultSdkHttpFullResponse
implements SdkHttpFullResponse {
    private static final long serialVersionUID = 1L;
    private final String statusText;
    private final int statusCode;
    private final transient AbortableInputStream content;
    private transient LowCopyListMap.ForBuildable headers;

    private DefaultSdkHttpFullResponse(Builder builder) {
        this.statusCode = Validate.isNotNegative((int)builder.statusCode, (String)"Status code must not be negative.");
        this.statusText = builder.statusText;
        this.content = builder.content;
        this.headers = builder.headers.forBuildable();
    }

    @Override
    public Map<String, List<String>> headers() {
        return this.headers.forExternalRead();
    }

    @Override
    public List<String> matchingHeaders(String header) {
        return Collections.unmodifiableList(this.headers.forInternalRead().getOrDefault(header, Collections.emptyList()));
    }

    @Override
    public Optional<String> firstMatchingHeader(String headerName) {
        List<String> headers = this.headers.forInternalRead().get(headerName);
        if (headers == null || headers.isEmpty()) {
            return Optional.empty();
        }
        String header = headers.get(0);
        if (StringUtils.isEmpty((CharSequence)header)) {
            return Optional.empty();
        }
        return Optional.of(header);
    }

    @Override
    public Optional<String> firstMatchingHeader(Collection<String> headersToFind) {
        for (String headerName : headersToFind) {
            Optional<String> header = this.firstMatchingHeader(headerName);
            if (!header.isPresent()) continue;
            return header;
        }
        return Optional.empty();
    }

    @Override
    public void forEachHeader(BiConsumer<? super String, ? super List<String>> consumer) {
        this.headers.forInternalRead().forEach((k, v) -> consumer.accept((String)k, (List<String>)Collections.unmodifiableList(v)));
    }

    @Override
    public int numHeaders() {
        return this.headers.forInternalRead().size();
    }

    @Override
    public Optional<AbortableInputStream> content() {
        return Optional.ofNullable(this.content);
    }

    @Override
    public Optional<String> statusText() {
        return Optional.ofNullable(this.statusText);
    }

    @Override
    public int statusCode() {
        return this.statusCode;
    }

    @Override
    public SdkHttpFullResponse.Builder toBuilder() {
        return new Builder(this);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.headers = LowCopyListMap.emptyHeaders().forBuildable();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultSdkHttpFullResponse that = (DefaultSdkHttpFullResponse)o;
        return this.statusCode == that.statusCode && Objects.equals(this.statusText, that.statusText) && Objects.equals(this.headers, that.headers);
    }

    public int hashCode() {
        int result = this.statusText != null ? this.statusText.hashCode() : 0;
        result = 31 * result + this.statusCode;
        result = 31 * result + Objects.hashCode(this.headers);
        return result;
    }

    static final class Builder
    implements SdkHttpFullResponse.Builder {
        private String statusText;
        private int statusCode;
        private AbortableInputStream content;
        private LowCopyListMap.ForBuilder headers;

        Builder() {
            this.headers = LowCopyListMap.emptyHeaders();
        }

        private Builder(DefaultSdkHttpFullResponse defaultSdkHttpFullResponse) {
            this.statusText = defaultSdkHttpFullResponse.statusText;
            this.statusCode = defaultSdkHttpFullResponse.statusCode;
            this.content = defaultSdkHttpFullResponse.content;
            this.headers = defaultSdkHttpFullResponse.headers.forBuilder();
        }

        @Override
        public String statusText() {
            return this.statusText;
        }

        @Override
        public Builder statusText(String statusText) {
            this.statusText = statusText;
            return this;
        }

        @Override
        public int statusCode() {
            return this.statusCode;
        }

        @Override
        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        @Override
        public AbortableInputStream content() {
            return this.content;
        }

        @Override
        public Builder content(AbortableInputStream content) {
            this.content = content;
            return this;
        }

        @Override
        public Builder putHeader(String headerName, List<String> headerValues) {
            Validate.paramNotNull((Object)headerName, (String)"headerName");
            Validate.paramNotNull(headerValues, (String)"headerValues");
            this.headers.forInternalWrite().put(headerName, new ArrayList<String>(headerValues));
            return this;
        }

        @Override
        public SdkHttpFullResponse.Builder appendHeader(String headerName, String headerValue) {
            Validate.paramNotNull((Object)headerName, (String)"headerName");
            Validate.paramNotNull((Object)headerValue, (String)"headerValue");
            this.headers.forInternalWrite().computeIfAbsent(headerName, k -> new ArrayList()).add(headerValue);
            return this;
        }

        @Override
        public Builder headers(Map<String, List<String>> headers) {
            Validate.paramNotNull(headers, (String)"headers");
            this.headers.setFromExternal(headers);
            return this;
        }

        @Override
        public Builder removeHeader(String headerName) {
            this.headers.forInternalWrite().remove(headerName);
            return this;
        }

        @Override
        public Builder clearHeaders() {
            this.headers.clear();
            return this;
        }

        @Override
        public Map<String, List<String>> headers() {
            return CollectionUtils.unmodifiableMapOfLists(this.headers.forInternalRead());
        }

        @Override
        public List<String> matchingHeaders(String header) {
            return Collections.unmodifiableList(this.headers.forInternalRead().getOrDefault(header, Collections.emptyList()));
        }

        @Override
        public Optional<String> firstMatchingHeader(String headerName) {
            List<String> headers = this.headers.forInternalRead().get(headerName);
            if (headers == null || headers.isEmpty()) {
                return Optional.empty();
            }
            String header = headers.get(0);
            if (StringUtils.isEmpty((CharSequence)header)) {
                return Optional.empty();
            }
            return Optional.of(header);
        }

        @Override
        public Optional<String> firstMatchingHeader(Collection<String> headersToFind) {
            for (String headerName : headersToFind) {
                Optional<String> header = this.firstMatchingHeader(headerName);
                if (!header.isPresent()) continue;
                return header;
            }
            return Optional.empty();
        }

        @Override
        public void forEachHeader(BiConsumer<? super String, ? super List<String>> consumer) {
            this.headers.forInternalRead().forEach((k, v) -> consumer.accept((String)k, (List<String>)Collections.unmodifiableList(v)));
        }

        @Override
        public int numHeaders() {
            return this.headers.forInternalRead().size();
        }

        @Override
        public SdkHttpFullResponse build() {
            return new DefaultSdkHttpFullResponse(this);
        }
    }
}

