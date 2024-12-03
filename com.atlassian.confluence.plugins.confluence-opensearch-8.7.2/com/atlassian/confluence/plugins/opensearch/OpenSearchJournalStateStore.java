/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.journal.ExportedJournalStateStore
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  javax.annotation.PostConstruct
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.opensearch.client.opensearch.OpenSearchClient
 *  org.opensearch.client.opensearch._types.OpenSearchException
 *  org.opensearch.client.opensearch._types.query_dsl.Query
 *  org.opensearch.client.opensearch.core.ExistsSourceRequest
 *  org.opensearch.client.opensearch.core.GetRequest
 *  org.opensearch.client.opensearch.core.GetResponse
 *  org.opensearch.client.opensearch.core.IndexRequest
 *  org.opensearch.client.opensearch.indices.CreateIndexRequest
 *  org.opensearch.client.opensearch.indices.ExistsRequest
 *  org.opensearch.client.transport.endpoints.BooleanResponse
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 */
package com.atlassian.confluence.plugins.opensearch;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.journal.ExportedJournalStateStore;
import com.atlassian.confluence.plugins.opensearch.OpenSearchConfig;
import com.atlassian.confluence.plugins.opensearch.OpenSearchIndexWriter;
import com.atlassian.confluence.plugins.opensearch.johnson.JohnsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.ExistsSourceRequest;
import org.opensearch.client.opensearch.core.GetRequest;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.transport.endpoints.BooleanResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

public class OpenSearchJournalStateStore
implements ExportedJournalStateStore {
    private final OpenSearchClient openSearchClient;
    private final OpenSearchConfig openSearchConfig;

    public OpenSearchJournalStateStore(OpenSearchClient openSearchClient, OpenSearchConfig openSearchConfig) {
        this.openSearchClient = Objects.requireNonNull(openSearchClient);
        this.openSearchConfig = Objects.requireNonNull(openSearchConfig);
    }

    @PostConstruct
    public void createJournalIndex() {
        try {
            BooleanResponse response = this.openSearchClient.indices().exists(ExistsRequest.of(r -> r.index(this.openSearchConfig.getJournalIndexName(), new String[0])));
            if (!response.value()) {
                this.openSearchClient.indices().create(CreateIndexRequest.of(r -> r.index(this.openSearchConfig.getJournalIndexName())));
            }
        }
        catch (IOException exception) {
            JohnsonUtils.raiseStartupErrorIfNotExistFor(exception);
        }
    }

    public void setMostRecentId(@NonNull JournalIdentifier journalId, long id) throws DataAccessException {
        try {
            this.openSearchClient.index(IndexRequest.of(i -> i.index(this.openSearchConfig.getJournalIndexName()).id(journalId.getJournalName()).document((Object)new JournalIdEntry(id))));
        }
        catch (IOException | OpenSearchException e) {
            throw new DataAccessResourceFailureException("Failed to write id " + id + " for journal '" + journalId.getJournalName(), e);
        }
    }

    public long getMostRecentId(@NonNull JournalIdentifier journalId) throws DataAccessException {
        try {
            BooleanResponse exists = this.openSearchClient.existsSource(ExistsSourceRequest.of(e -> e.id(journalId.getJournalName()).index(this.openSearchConfig.getJournalIndexName())));
            if (exists.value()) {
                GetResponse response = this.openSearchClient.get(GetRequest.of(g -> g.index(this.openSearchConfig.getJournalIndexName()).id(journalId.getJournalName())), JournalIdEntry.class);
                return Optional.ofNullable((JournalIdEntry)response.source()).map(JournalIdEntry::getId).orElse(0L);
            }
            return 0L;
        }
        catch (IOException | OpenSearchException e2) {
            throw new DataAccessResourceFailureException("Failed to read id for journal '" + journalId.getJournalName(), e2);
        }
    }

    public void resetAllJournalStates() throws DataAccessException {
        try {
            OpenSearchIndexWriter.tryDelete(this.openSearchClient, this.openSearchConfig.getJournalIndexName(), Query.of(q -> q.matchAll(m -> m)));
        }
        catch (IOException e) {
            throw new DataAccessResourceFailureException("Failed to reset all journal states", (Throwable)e);
        }
    }

    public static class JournalIdEntry {
        private final Long id;

        public JournalIdEntry(@JsonProperty(value="id") Long id) {
            this.id = id;
        }

        public Long getId() {
            return this.id;
        }
    }
}

