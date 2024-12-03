/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.core.async.SdkPublisher
 *  software.amazon.awssdk.core.pagination.async.AsyncPageFetcher
 *  software.amazon.awssdk.core.pagination.async.ResponsesSubscription
 *  software.amazon.awssdk.core.pagination.async.ResponsesSubscription$Builder
 *  software.amazon.awssdk.core.util.PaginatorUtils
 */
package software.amazon.awssdk.services.secretsmanager.paginators;

import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.pagination.async.AsyncPageFetcher;
import software.amazon.awssdk.core.pagination.async.ResponsesSubscription;
import software.amazon.awssdk.core.util.PaginatorUtils;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.secretsmanager.internal.UserAgentUtils;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretVersionIdsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretVersionIdsResponse;

public class ListSecretVersionIdsPublisher
implements SdkPublisher<ListSecretVersionIdsResponse> {
    private final SecretsManagerAsyncClient client;
    private final ListSecretVersionIdsRequest firstRequest;
    private final AsyncPageFetcher nextPageFetcher;
    private boolean isLastPage;

    public ListSecretVersionIdsPublisher(SecretsManagerAsyncClient client, ListSecretVersionIdsRequest firstRequest) {
        this(client, firstRequest, false);
    }

    private ListSecretVersionIdsPublisher(SecretsManagerAsyncClient client, ListSecretVersionIdsRequest firstRequest, boolean isLastPage) {
        this.client = client;
        this.firstRequest = UserAgentUtils.applyPaginatorUserAgent(firstRequest);
        this.isLastPage = isLastPage;
        this.nextPageFetcher = new ListSecretVersionIdsResponseFetcher();
    }

    public void subscribe(Subscriber<? super ListSecretVersionIdsResponse> subscriber) {
        subscriber.onSubscribe((Subscription)((ResponsesSubscription.Builder)((ResponsesSubscription.Builder)ResponsesSubscription.builder().subscriber(subscriber)).nextPageFetcher(this.nextPageFetcher)).build());
    }

    private class ListSecretVersionIdsResponseFetcher
    implements AsyncPageFetcher<ListSecretVersionIdsResponse> {
        private ListSecretVersionIdsResponseFetcher() {
        }

        public boolean hasNextPage(ListSecretVersionIdsResponse previousPage) {
            return PaginatorUtils.isOutputTokenAvailable((Object)previousPage.nextToken());
        }

        public CompletableFuture<ListSecretVersionIdsResponse> nextPage(ListSecretVersionIdsResponse previousPage) {
            if (previousPage == null) {
                return ListSecretVersionIdsPublisher.this.client.listSecretVersionIds(ListSecretVersionIdsPublisher.this.firstRequest);
            }
            return ListSecretVersionIdsPublisher.this.client.listSecretVersionIds((ListSecretVersionIdsRequest)((Object)ListSecretVersionIdsPublisher.this.firstRequest.toBuilder().nextToken(previousPage.nextToken()).build()));
        }
    }
}

