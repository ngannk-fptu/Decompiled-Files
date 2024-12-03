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
import software.amazon.awssdk.services.secretsmanager.model.ListSecretsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretsResponse;

public class ListSecretsIterable
implements SdkIterable<ListSecretsResponse> {
    private final SecretsManagerClient client;
    private final ListSecretsRequest firstRequest;
    private final SyncPageFetcher nextPageFetcher;

    public ListSecretsIterable(SecretsManagerClient client, ListSecretsRequest firstRequest) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.nextPageFetcher = new ListSecretsResponseFetcher();
    }

    public Iterator<ListSecretsResponse> iterator() {
        return PaginatedResponsesIterator.builder().nextPageFetcher(this.nextPageFetcher).build();
    }

    private class ListSecretsResponseFetcher
    implements SyncPageFetcher<ListSecretsResponse> {
        private ListSecretsResponseFetcher() {
        }

        public boolean hasNextPage(ListSecretsResponse previousPage) {
            return PaginatorUtils.isOutputTokenAvailable((Object)previousPage.nextToken());
        }

        public ListSecretsResponse nextPage(ListSecretsResponse previousPage) {
            if (previousPage == null) {
                return ListSecretsIterable.this.client.listSecrets(ListSecretsIterable.this.firstRequest);
            }
            return ListSecretsIterable.this.client.listSecrets((ListSecretsRequest)((Object)ListSecretsIterable.this.firstRequest.toBuilder().nextToken(previousPage.nextToken()).build()));
        }
    }
}

