/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.core.async.AsyncRequestBody
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.signer.AsyncRequestBodySigner
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.auth.signer.internal;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.auth.signer.internal.Aws4SignerRequestParams;
import software.amazon.awssdk.auth.signer.internal.BaseAws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.signer.AsyncRequestBodySigner;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public abstract class BaseAsyncAws4Signer
extends BaseAws4Signer
implements AsyncRequestBodySigner {
    private static final Logger LOG = Logger.loggerFor(BaseAsyncAws4Signer.class);
    private static final Pattern AUTHENTICATION_HEADER_PATTERN = Pattern.compile("AWS4-HMAC-SHA256\\sCredential=(\\S+)\\sSignedHeaders=(\\S+)\\sSignature=(\\S+)");

    protected BaseAsyncAws4Signer() {
    }

    public AsyncRequestBody signAsyncRequestBody(SdkHttpFullRequest request, AsyncRequestBody asyncRequestBody, ExecutionAttributes executionAttributes) {
        Aws4SignerParams signingParams = this.extractSignerParams(Aws4SignerParams.builder(), executionAttributes).build();
        Aws4SignerRequestParams requestParams = new Aws4SignerRequestParams(signingParams);
        return this.signAsync(request, asyncRequestBody, requestParams, signingParams);
    }

    @SdkTestInternalApi
    protected final AsyncRequestBody signAsync(SdkHttpFullRequest request, AsyncRequestBody asyncRequestBody, Aws4SignerRequestParams requestParams, Aws4SignerParams signingParams) {
        String headerSignature = this.getHeaderSignature(request);
        return this.transformRequestProvider(headerSignature, requestParams, signingParams, asyncRequestBody);
    }

    protected abstract AsyncRequestBody transformRequestProvider(String var1, Aws4SignerRequestParams var2, Aws4SignerParams var3, AsyncRequestBody var4);

    private String getHeaderSignature(SdkHttpFullRequest request) {
        Matcher matcher;
        Optional authHeader = request.firstMatchingHeader("Authorization");
        if (authHeader.isPresent() && (matcher = AUTHENTICATION_HEADER_PATTERN.matcher((CharSequence)authHeader.get())).matches()) {
            String headerSignature = matcher.group(3);
            return headerSignature;
        }
        throw SdkClientException.builder().message("Signature is missing in AUTHORIZATION header!").build();
    }
}

