/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.ReadLimitInfo;
import com.amazonaws.Request;
import com.amazonaws.ResetException;
import com.amazonaws.SdkClientException;
import com.amazonaws.SignableRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AwsChunkedEncodingInputStream;
import com.amazonaws.auth.internal.AWS4SignerRequestParams;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.request.S3HandlerContextKeys;
import com.amazonaws.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;

public class AWSS3V4Signer
extends AWS4Signer {
    private static final String CONTENT_SHA_256 = "STREAMING-AWS4-HMAC-SHA256-PAYLOAD";
    private static final String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";

    public AWSS3V4Signer() {
        super(false);
    }

    @Override
    protected void processRequestPayload(SignableRequest<?> request, byte[] signature, byte[] signingKey, AWS4SignerRequestParams signerRequestParams) {
        if (this.useChunkEncoding(request)) {
            AwsChunkedEncodingInputStream chunkEncodededStream = new AwsChunkedEncodingInputStream(request.getContent(), signingKey, signerRequestParams.getFormattedSigningDateTime(), signerRequestParams.getScope(), BinaryUtils.toHex(signature), this);
            request.setContent(chunkEncodededStream);
        }
    }

    @Override
    protected String calculateContentHashPresign(SignableRequest<?> request) {
        return UNSIGNED_PAYLOAD;
    }

    @Override
    protected String calculateContentHash(SignableRequest<?> request) {
        request.addHeader("x-amz-content-sha256", "required");
        if (this.isPayloadSigningEnabled(request)) {
            if (this.useChunkEncoding(request)) {
                long originalContentLength;
                String contentLength = request.getHeaders().get("Content-Length");
                if (contentLength != null) {
                    originalContentLength = Long.parseLong(contentLength);
                } else {
                    try {
                        originalContentLength = AWSS3V4Signer.getContentLength(request);
                    }
                    catch (IOException e) {
                        throw new SdkClientException("Cannot get the content-length of the request content.", e);
                    }
                }
                request.addHeader("x-amz-decoded-content-length", Long.toString(originalContentLength));
                request.addHeader("Content-Length", Long.toString(AwsChunkedEncodingInputStream.calculateStreamContentLength(originalContentLength)));
                return CONTENT_SHA_256;
            }
            return super.calculateContentHash(request);
        }
        return UNSIGNED_PAYLOAD;
    }

    private boolean useChunkEncoding(SignableRequest<?> request) {
        if (!this.isPayloadSigningEnabled(request) || this.isChunkedEncodingDisabled(request)) {
            return false;
        }
        return request.getOriginalRequestObject() instanceof PutObjectRequest || request.getOriginalRequestObject() instanceof UploadPartRequest;
    }

    private boolean isChunkedEncodingDisabled(SignableRequest<?> signableRequest) {
        if (signableRequest instanceof Request) {
            Request request = (Request)signableRequest;
            Boolean isChunkedEncodingDisabled = request.getHandlerContext(S3HandlerContextKeys.IS_CHUNKED_ENCODING_DISABLED);
            return isChunkedEncodingDisabled != null && isChunkedEncodingDisabled != false;
        }
        return false;
    }

    private boolean isPayloadSigningEnabled(SignableRequest<?> signableRequest) {
        if (!signableRequest.getEndpoint().getScheme().equals("https")) {
            return true;
        }
        if (signableRequest instanceof Request) {
            Request request = (Request)signableRequest;
            Boolean isPayloadSigningEnabled = request.getHandlerContext(S3HandlerContextKeys.IS_PAYLOAD_SIGNING_ENABLED);
            return isPayloadSigningEnabled != null && isPayloadSigningEnabled != false;
        }
        return false;
    }

    static long getContentLength(SignableRequest<?> request) throws IOException {
        int read;
        InputStream content = request.getContent();
        if (!content.markSupported()) {
            throw new IllegalStateException("Bug: request input stream must have been made mark-and-resettable at this point");
        }
        ReadLimitInfo info = request.getReadLimitInfo();
        int readLimit = info.getReadLimit();
        long contentLength = 0L;
        byte[] tmp = new byte[4096];
        content.mark(readLimit);
        while ((read = content.read(tmp)) != -1) {
            contentLength += (long)read;
        }
        try {
            content.reset();
        }
        catch (IOException ex) {
            throw new ResetException("Failed to reset the input stream", ex);
        }
        return contentLength;
    }
}

