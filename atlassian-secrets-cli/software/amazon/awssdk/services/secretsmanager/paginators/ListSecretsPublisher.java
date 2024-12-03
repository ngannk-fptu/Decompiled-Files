/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.paginators;

import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.pagination.async.AsyncPageFetcher;
import software.amazon.awssdk.core.pagination.async.ResponsesSubscription;
import software.amazon.awssdk.core.util.PaginatorUtils;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.secretsmanager.internal.UserAgentUtils;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretsResponse;

public class ListSecretsPublisher
implements SdkPublisher<ListSecretsResponse> {
    private final SecretsManagerAsyncClient client;
    private final ListSecretsRequest firstRequest;
    private final AsyncPageFetcher nextPageFetcher;
    private boolean isLastPage;

    public ListSecretsPublisher(SecretsManagerAsyncClient client, ListSecretsRequest firstRequest) {
        this(client, firstRequest, false);
    }

    private ListSecretsPublisher(SecretsManagerAsyncClient client, ListSecretsRequest firstRequest, boolean isLastPage) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.isLastPage = isLastPage;
        this.nextPageFetcher = new ListSecretsResponseFetcher();
    }

    @Override
    public void subscribe(Subscriber<? super ListSecretsResponse> subscriber) {
        subscriber.onSubscribe(((ResponsesSubscription.Builder)((ResponsesSubscription.Builder)ResponsesSubscription.builder().subscriber(subscriber)).nextPageFetcher(this.nextPageFetcher)).build());
    }

    private class ListSecretsResponseFetcher
    implements AsyncPageFetcher<ListSecretsResponse> {
        private ListSecretsResponseFetcher() {
        }

        @Override
        public boolean hasNextPage(ListSecretsResponse previousPage) {
            return PaginatorUtils.isOutputTokenAvailable(previousPage.nextToken());
        }

        @Override
        public CompletableFuture<ListSecretsResponse> nextPage(ListSecretsResponse previousPage) {
            if (previousPage == null) {
                return ListSecretsPublisher.this.client.listSecrets(ListSecretsPublisher.this.firstRequest);
            }
            return ListSecretsPublisher.this.client.listSecrets((ListSecretsRequest)ListSecretsPublisher.this.firstRequest.toBuilder().nextToken(previousPage.nextToken()).build());
        }
    }
}

