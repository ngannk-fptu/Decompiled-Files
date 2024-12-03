/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.search.SearchPlatformConfig
 *  com.atlassian.confluence.search.v2.Index
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.search.SearchPlatformConfig;
import com.atlassian.confluence.search.v2.Index;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class OpenSearchConfig
implements SearchPlatformConfig {
    @VisibleForTesting
    static final String HTTP_URL_KEY = "opensearch.http.url";
    @VisibleForTesting
    static final String CONTENT_INDEX_NAME_KEY = "opensearch.index.content.name";
    @VisibleForTesting
    static final String CONTENT_INDEX_NAME_DEFAULT = "confluence-content";
    @VisibleForTesting
    static final String CHANGE_INDEX_NAME_KEY = "opensearch.index.change.name";
    @VisibleForTesting
    static final String CHANGE_INDEX_NAME_DEFAULT = "confluence-change";
    @VisibleForTesting
    static final String CUSTOM_INDEX_PREFIX_KEY = "opensearch.index.custom.prefix";
    @VisibleForTesting
    static final String CUSTOM_INDEX_PREFIX_DEFAULT = "confluence-custom-";
    @VisibleForTesting
    static final String JOURNAL_INDEX_NAME_KEY = "opensearch.index.journal.name";
    @VisibleForTesting
    static final String JOURNAL_INDEX_NAME_DEFAULT = "confluence-journal";
    @VisibleForTesting
    static final String AWS_REGION = "opensearch.aws.region";
    @VisibleForTesting
    static final String USERNAME = "opensearch.username";
    @VisibleForTesting
    static final String PASSWORD = "opensearch.password";
    @VisibleForTesting
    static final String CONNECT_TIMEOUT = "opensearch.http.connect.timeout";
    @VisibleForTesting
    static final Long CONNECT_TIMEOUT_DEFAULT = 5L;
    @VisibleForTesting
    static final String REQUEST_CONNECT_TIMEOUT = "opensearch.http.request.connect.timeout";
    @VisibleForTesting
    static final Long REQUEST_CONNECT_TIMEOUT_DEFAULT = 1L;
    @VisibleForTesting
    static final String SOCKET_TIMEOUT = "opensearch.http.socket.timeout";
    @VisibleForTesting
    static final Integer SOCKET_TIMEOUT_DEFAULT = 5;
    @VisibleForTesting
    static final String BULK_API_BATCH_SIZE = "opensearch.bulk.api.batch.size";
    @VisibleForTesting
    static final Integer BULK_API_BATCH_SIZE_DEFAULT = 0x500000;
    private final ApplicationConfiguration applicationConfig;
    private final Map<Index, String> indexMap = new HashMap<Index, String>();

    public OpenSearchConfig(ApplicationConfiguration applicationConfig) {
        this.applicationConfig = Objects.requireNonNull(applicationConfig, "applicationConfig is required");
        this.refresh();
    }

    public void refresh() {
        this.indexMap.clear();
        this.indexMap.put(Index.CONTENT, this.getContentIndexName());
        this.indexMap.put(Index.CHANGE, this.getChangeIndexName());
    }

    public String getHttpUrl() {
        return this.getPropertyAsString(HTTP_URL_KEY).orElseThrow(() -> new IllegalStateException("Missing property in application config: opensearch.http.url"));
    }

    public String getIndexName(Index index) {
        switch (index.getType()) {
            case SYSTEM: {
                return this.indexMap.get(index);
            }
        }
        return this.getCustomIndexPrefix() + index.getName();
    }

    public String getContentIndexName() {
        return this.getPropertyAsString(CONTENT_INDEX_NAME_KEY).orElse(CONTENT_INDEX_NAME_DEFAULT);
    }

    public String getChangeIndexName() {
        return this.getPropertyAsString(CHANGE_INDEX_NAME_KEY).orElse(CHANGE_INDEX_NAME_DEFAULT);
    }

    public String getCustomIndexPrefix() {
        return this.getPropertyAsString(CUSTOM_INDEX_PREFIX_KEY).orElse(CUSTOM_INDEX_PREFIX_DEFAULT);
    }

    public String getJournalIndexName() {
        return this.getPropertyAsString(JOURNAL_INDEX_NAME_KEY).orElse(JOURNAL_INDEX_NAME_DEFAULT);
    }

    public Optional<Pair<String, String>> getUsernamePassword() {
        return this.getPropertyAsString(USERNAME).map(username -> Pair.of((Object)username, (Object)this.getPropertyAsString(PASSWORD).orElse("")));
    }

    public Optional<String> getAWSRegion() {
        return this.getPropertyAsString(AWS_REGION);
    }

    public Long getConnectRequestTimeout() {
        return this.getPropertyAsLong(REQUEST_CONNECT_TIMEOUT).orElse(REQUEST_CONNECT_TIMEOUT_DEFAULT);
    }

    public Long getConnectTimout() {
        return this.getPropertyAsLong(CONNECT_TIMEOUT).orElse(CONNECT_TIMEOUT_DEFAULT);
    }

    public Integer getSocketTimeout() {
        return this.getPropertyAsInt(SOCKET_TIMEOUT).orElse(SOCKET_TIMEOUT_DEFAULT);
    }

    public Integer getBulkApiBatchSize() {
        return this.getPropertyAsInt(BULK_API_BATCH_SIZE).orElse(BULK_API_BATCH_SIZE_DEFAULT);
    }

    private Optional<String> getPropertyAsString(String key) {
        return OpenSearchConfig.firstNonNull(() -> System.getProperty(key), () -> (String)this.applicationConfig.getProperty((Object)key));
    }

    private static <T> Optional<T> firstNonNull(Supplier<T> ... suppliers) {
        return Arrays.stream(suppliers).map(Supplier::get).filter(Objects::nonNull).findFirst();
    }

    private Optional<Long> getPropertyAsLong(String key) {
        return this.getPropertyAsString(key).map(Long::valueOf);
    }

    private Optional<Integer> getPropertyAsInt(String key) {
        return this.getPropertyAsString(key).map(Integer::valueOf);
    }

    public boolean isSharedIndex() {
        return true;
    }
}

