/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.paginators;

import java.util.Iterator;
import software.amazon.awssdk.core.pagination.sync.PaginatedResponsesIterator;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.core.pagination.sync.SyncPageFetcher;
import software.amazon.awssdk.core.util.PaginatorUtils;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.internal.UserAgentUtils;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretVersionIdsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretVersionIdsResponse;

public class ListSecretVersionIdsIterable
implements SdkIterable<ListSecretVersionIdsResponse> {
    private final SecretsManagerClient client;
    private final ListSecretVersionIdsRequest firstRequest;
    private final SyncPageFetcher nextPageFetcher;

    public ListSecretVersionIdsIterable(SecretsManagerClient client, ListSecretVersionIdsRequest firstRequest) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.nextPageFetcher = new ListSecretVersionIdsResponseFetcher();
    }

    @Override
    public Iterator<ListSecretVersionIdsResponse> iterator() {
        return PaginatedResponsesIterator.builder().nextPageFetcher(this.nextPageFetcher).build();
    }

    private class ListSecretVersionIdsResponseFetcher
    implements SyncPageFetcher<ListSecretVersionIdsResponse> {
        private ListSecretVersionIdsResponseFetcher() {
        }

        @Override
        public boolean hasNextPage(ListSecretVersionIdsResponse previousPage) {
            return PaginatorUtils.isOutputTokenAvailable(previousPage.nextToken());
        }

        @Override
        public ListSecretVersionIdsResponse nextPage(ListSecretVersionIdsResponse previousPage) {
            if (previousPage == null) {
                return ListSecretVersionIdsIterable.this.client.listSecretVersionIds(ListSecretVersionIdsIterable.this.firstRequest);
            }
            return ListSecretVersionIdsIterable.this.client.listSecretVersionIds((ListSecretVersionIdsRequest)ListSecretVersionIdsIterable.this.firstRequest.toBuilder().nextToken(previousPage.nextToken()).build());
        }
    }
}

