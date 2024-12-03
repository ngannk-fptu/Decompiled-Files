/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.core.internal.http;

import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.internal.http.InterruptMonitor;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.io.ReleasableInputStream;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class StreamManagingStage<OutputT>
implements RequestPipeline<SdkHttpFullRequest, Response<OutputT>> {
    private static final Logger log = Logger.loggerFor(StreamManagingStage.class);
    private final RequestPipeline<SdkHttpFullRequest, Response<OutputT>> wrapped;

    public StreamManagingStage(RequestPipeline<SdkHttpFullRequest, Response<OutputT>> wrapped) {
        this.wrapped = wrapped;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Response<OutputT> execute(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        ClosingStreamProvider toBeClosed = null;
        if (request.contentStreamProvider().isPresent()) {
            toBeClosed = StreamManagingStage.createManagedProvider((ContentStreamProvider)request.contentStreamProvider().get());
            request = request.toBuilder().contentStreamProvider((ContentStreamProvider)toBeClosed).build();
        }
        try {
            InterruptMonitor.checkInterrupted();
            Response<OutputT> response = this.wrapped.execute(request, context);
            return response;
        }
        finally {
            if (toBeClosed != null) {
                toBeClosed.closeCurrentStream();
            }
        }
    }

    private static ClosingStreamProvider createManagedProvider(ContentStreamProvider contentStreamProvider) {
        return new ClosingStreamProvider(contentStreamProvider);
    }

    private static class ClosingStreamProvider
    implements ContentStreamProvider {
        private final ContentStreamProvider wrapped;
        private InputStream currentStream;

        ClosingStreamProvider(ContentStreamProvider wrapped) {
            this.wrapped = wrapped;
        }

        public InputStream newStream() {
            this.currentStream = this.wrapped.newStream();
            return ReleasableInputStream.wrap(this.currentStream).disableClose();
        }

        void closeCurrentStream() {
            if (this.currentStream != null) {
                FunctionalUtils.invokeSafely(this.currentStream::close);
                this.currentStream = null;
            }
        }
    }
}

