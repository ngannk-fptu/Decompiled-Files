/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpResponse
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.core.internal.async.SdkPublishers
 *  software.amazon.awssdk.http.AbortableInputStream
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.StringInputStream
 */
package software.amazon.awssdk.services.s3.internal.handlers;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Predicate;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.internal.async.SdkPublishers;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyRequest;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.StringInputStream;

@SdkInternalApi
public final class GetBucketPolicyInterceptor
implements ExecutionInterceptor {
    private static final String XML_ENVELOPE_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Policy><![CDATA[";
    private static final String XML_ENVELOPE_SUFFIX = "]]></Policy>";
    private static final Predicate<Context.ModifyHttpResponse> INTERCEPTOR_CONTEXT_PREDICATE = context -> context.request() instanceof GetBucketPolicyRequest && context.httpResponse().isSuccessful();

    public Optional<InputStream> modifyHttpResponseContent(Context.ModifyHttpResponse context, ExecutionAttributes executionAttributes) {
        String policy;
        if (INTERCEPTOR_CONTEXT_PREDICATE.test(context) && (policy = (String)context.responseBody().map(r -> (String)FunctionalUtils.invokeSafely(() -> IoUtils.toUtf8String((InputStream)r))).orElse(null)) != null) {
            String xml = XML_ENVELOPE_PREFIX + policy + XML_ENVELOPE_SUFFIX;
            return Optional.of(AbortableInputStream.create((InputStream)new StringInputStream(xml)));
        }
        return context.responseBody();
    }

    public Optional<Publisher<ByteBuffer>> modifyAsyncHttpResponseContent(Context.ModifyHttpResponse context, ExecutionAttributes executionAttributes) {
        if (INTERCEPTOR_CONTEXT_PREDICATE.test(context)) {
            return context.responsePublisher().map(body -> SdkPublishers.envelopeWrappedPublisher((Publisher)body, (String)XML_ENVELOPE_PREFIX, (String)XML_ENVELOPE_SUFFIX));
        }
        return context.responsePublisher();
    }
}

