/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.FunctionalUtils;

@Immutable
@SdkInternalApi
public final class RequestPipelineBuilder<InputT, OutputT> {
    private final Function<HttpClientDependencies, RequestPipeline<InputT, OutputT>> pipelineFactory;

    private RequestPipelineBuilder(Function<HttpClientDependencies, RequestPipeline<InputT, OutputT>> pipelineFactory) {
        this.pipelineFactory = pipelineFactory;
    }

    public static <InputT, OutputT> RequestPipelineBuilder<InputT, OutputT> first(Function<HttpClientDependencies, RequestPipeline<InputT, OutputT>> pipelineFactory) {
        return new RequestPipelineBuilder<InputT, OutputT>(pipelineFactory);
    }

    public static <InputT, OutputT> RequestPipelineBuilder<InputT, OutputT> first(Supplier<RequestPipeline<InputT, OutputT>> pipelineFactory) {
        return new RequestPipelineBuilder<InputT, OutputT>(d -> (RequestPipeline)pipelineFactory.get());
    }

    public <NewOutputT> RequestPipelineBuilder<InputT, NewOutputT> then(Function<HttpClientDependencies, RequestPipeline<OutputT, NewOutputT>> pipelineFactory) {
        return new RequestPipelineBuilder<InputT, OutputT>(r -> new ComposingRequestPipelineStage(this.pipelineFactory.apply((HttpClientDependencies)r), (RequestPipeline)pipelineFactory.apply((HttpClientDependencies)r)));
    }

    public static <InputT, OutputT> Function<HttpClientDependencies, RequestPipeline<CompletableFuture<InputT>, CompletableFuture<OutputT>>> async(Function<HttpClientDependencies, RequestPipeline<InputT, OutputT>> pipelineFactory) {
        return httpClientDependencies -> new AsyncRequestPipelineWrapper((RequestPipeline)pipelineFactory.apply((HttpClientDependencies)httpClientDependencies));
    }

    public static <InputT, OutputT> Function<HttpClientDependencies, RequestPipeline<CompletableFuture<InputT>, CompletableFuture<OutputT>>> async(Supplier<RequestPipeline<InputT, OutputT>> pipelineFactory) {
        return RequestPipelineBuilder.async(FunctionalUtils.toFunction(pipelineFactory));
    }

    public <NewOutputT> RequestPipelineBuilder<InputT, NewOutputT> then(Supplier<RequestPipeline<OutputT, NewOutputT>> pipelineFactory) {
        return new RequestPipelineBuilder<InputT, OutputT>(r -> new ComposingRequestPipelineStage(this.pipelineFactory.apply((HttpClientDependencies)r), (RequestPipeline)pipelineFactory.get()));
    }

    public <NewInputT, NewOutputT> RequestPipelineBuilder<NewInputT, NewOutputT> wrappedWith(BiFunction<HttpClientDependencies, RequestPipeline<InputT, OutputT>, RequestPipeline<NewInputT, NewOutputT>> wrappedFactory) {
        return new RequestPipelineBuilder<InputT, OutputT>(r -> (RequestPipeline)wrappedFactory.apply((HttpClientDependencies)r, this.pipelineFactory.apply((HttpClientDependencies)r)));
    }

    public <NewInputT, NewOutputT> RequestPipelineBuilder<NewInputT, NewOutputT> wrappedWith(Function<RequestPipeline<InputT, OutputT>, RequestPipeline<NewInputT, NewOutputT>> wrappedFactory) {
        return new RequestPipelineBuilder<InputT, OutputT>(d -> (RequestPipeline)wrappedFactory.apply(this.pipelineFactory.apply((HttpClientDependencies)d)));
    }

    public RequestPipeline<InputT, OutputT> build(HttpClientDependencies dependencies) {
        return this.pipelineFactory.apply(dependencies);
    }

    private static class AsyncRequestPipelineWrapper<InputT, OutputT>
    implements RequestPipeline<CompletableFuture<InputT>, CompletableFuture<OutputT>> {
        private final RequestPipeline<InputT, OutputT> delegate;

        private AsyncRequestPipelineWrapper(RequestPipeline<InputT, OutputT> delegate) {
            this.delegate = delegate;
        }

        @Override
        public CompletableFuture<OutputT> execute(CompletableFuture<InputT> inputFuture, RequestExecutionContext context) throws Exception {
            CompletionStage outputFuture = inputFuture.thenApply(FunctionalUtils.safeFunction(input -> this.delegate.execute(input, context)));
            return CompletableFutureUtils.forwardExceptionTo(outputFuture, inputFuture);
        }
    }

    private static class ComposingRequestPipelineStage<InputT, MiddleT, OutputT>
    implements RequestPipeline<InputT, OutputT> {
        private final RequestPipeline<InputT, MiddleT> first;
        private final RequestPipeline<MiddleT, OutputT> second;

        private ComposingRequestPipelineStage(RequestPipeline<InputT, MiddleT> first, RequestPipeline<MiddleT, OutputT> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public OutputT execute(InputT in, RequestExecutionContext context) throws Exception {
            return this.second.execute(this.first.execute(in, context), context);
        }
    }
}

