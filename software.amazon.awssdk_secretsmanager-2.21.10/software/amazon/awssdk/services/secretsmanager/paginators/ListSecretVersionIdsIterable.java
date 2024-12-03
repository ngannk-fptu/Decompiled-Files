/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.pagination.sync.PaginatedResponsesIterator
 *  software.amazon.awssdk.core.pagination.sync.SdkIterable
 *  software.amazon.awssdk.core.pagination.sync.SyncPageFetcher
 *  software.amazon.awssdk.core.util.PaginatorUtils
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

    public Iterator<ListSecretVersionIdsResponse> iterator() {
        return PaginatedResponsesIterator.builder().nextPageFetcher(this.nextPageFetcher).build();
    }

    private class ListSecretVersionIdsResponseFetcher
    implements SyncPageFetcher<ListSecretVersionIdsResponse> {
        private ListSecretVersionIdsResponseFetcher() {
        }

        public boolean hasNextPage(ListSecretVersionIdsResponse previousPage) {
            return PaginatorUtils.isOutputTokenAvailable((Object)previousPage.nextToken());
        }

        public ListSecretVersionIdsResponse nextPage(ListSecretVersionIdsResponse previousPage) {
            if (previousPage == null) {
                return ListSecretVersionIdsIterable.this.client.listSecretVersionIds(ListSecretVersionIdsIterable.this.firstRequest);
            }
            return ListSecretVersionIdsIterable.this.client.listSecretVersionIds((ListSecretVersionIdsRequest)((Object)ListSecretVersionIdsIterable.this.firstRequest.toBuilder().nextToken(previousPage.nextToken()).build()));
        }
    }
}

