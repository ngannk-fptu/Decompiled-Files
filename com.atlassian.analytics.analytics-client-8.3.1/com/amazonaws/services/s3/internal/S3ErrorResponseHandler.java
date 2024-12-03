/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.services.s3.internal.AmazonS3ExceptionBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.XmlUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class S3ErrorResponseHandler
implements HttpResponseHandler<AmazonServiceException> {
    private static final Log log = LogFactory.getLog(S3ErrorResponseHandler.class);
    private final ClientConfiguration clientConfiguration;

    public S3ErrorResponseHandler(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    @Override
    public AmazonServiceException handle(HttpResponse httpResponse) throws XMLStreamException {
        AmazonServiceException exception = this.createException(httpResponse);
        exception.setHttpHeaders(httpResponse.getHeaders());
        return exception;
    }

    private AmazonServiceException createException(HttpResponse httpResponse) throws XMLStreamException {
        String content;
        block20: {
            InputStream is = httpResponse.getContent();
            String xmlContent = null;
            if (is == null || httpResponse.getRequest().getHttpMethod() == HttpMethodName.HEAD) {
                return this.createExceptionFromHeaders(httpResponse, null);
            }
            content = null;
            try {
                content = IOUtils.toString(is);
            }
            catch (IOException ioe) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Failed in parsing the error response : ", (Throwable)ioe);
                }
                return this.createExceptionFromHeaders(httpResponse, null);
            }
            XMLStreamReader reader = XmlUtils.getXmlInputFactory().createXMLStreamReader(new ByteArrayInputStream(content.getBytes(StringUtils.UTF8)));
            try {
                int targetDepth = 0;
                AmazonS3ExceptionBuilder exceptionBuilder = new AmazonS3ExceptionBuilder();
                exceptionBuilder.setErrorResponseXml(content);
                exceptionBuilder.setStatusCode(httpResponse.getStatusCode());
                exceptionBuilder.setCloudFrontId(httpResponse.getHeaders().get("X-Amz-Cf-Id"));
                String bucketRegion = httpResponse.getHeader("x-amz-bucket-region");
                if (bucketRegion != null) {
                    exceptionBuilder.addAdditionalDetail("x-amz-bucket-region", bucketRegion);
                }
                boolean hasErrorTagVisited = false;
                block10: while (reader.hasNext()) {
                    int event = reader.next();
                    switch (event) {
                        case 1: {
                            String tagName = reader.getLocalName();
                            if (++targetDepth == 1 && !S3ErrorTags.Error.toString().equals(tagName)) {
                                return this.createExceptionFromHeaders(httpResponse, "Unable to parse error response. Error XML Not in proper format." + content);
                            }
                            if (!S3ErrorTags.Error.toString().equals(tagName)) continue block10;
                            hasErrorTagVisited = true;
                            continue block10;
                        }
                        case 4: {
                            xmlContent = reader.getText();
                            if (xmlContent == null) continue block10;
                            xmlContent = xmlContent.trim();
                            continue block10;
                        }
                        case 2: {
                            String tagName = reader.getLocalName();
                            if (!hasErrorTagVisited || --targetDepth > 1) {
                                return this.createExceptionFromHeaders(httpResponse, "Unable to parse error response. Error XML Not in proper format." + content);
                            }
                            if (S3ErrorTags.Message.toString().equals(tagName)) {
                                exceptionBuilder.setErrorMessage(xmlContent);
                                continue block10;
                            }
                            if (S3ErrorTags.Code.toString().equals(tagName)) {
                                exceptionBuilder.setErrorCode(xmlContent);
                                continue block10;
                            }
                            if (S3ErrorTags.RequestId.toString().equals(tagName)) {
                                exceptionBuilder.setRequestId(xmlContent);
                                continue block10;
                            }
                            if (S3ErrorTags.HostId.toString().equals(tagName)) {
                                exceptionBuilder.setExtendedRequestId(xmlContent);
                                continue block10;
                            }
                            exceptionBuilder.addAdditionalDetail(tagName, xmlContent);
                            continue block10;
                        }
                        case 8: {
                            exceptionBuilder.setProxyHost(this.clientConfiguration.getProxyHost());
                            return exceptionBuilder.build();
                        }
                    }
                }
            }
            catch (Exception e) {
                if (!log.isDebugEnabled()) break block20;
                log.debug((Object)("Failed in parsing the error response : " + content), (Throwable)e);
            }
        }
        return this.createExceptionFromHeaders(httpResponse, content);
    }

    private AmazonS3Exception createExceptionFromHeaders(HttpResponse errorResponse, String errorResponseXml) {
        Map<String, String> headers = errorResponse.getHeaders();
        int statusCode = errorResponse.getStatusCode();
        AmazonS3ExceptionBuilder exceptionBuilder = new AmazonS3ExceptionBuilder();
        exceptionBuilder.setErrorMessage(errorResponse.getStatusText());
        exceptionBuilder.setErrorResponseXml(errorResponseXml);
        exceptionBuilder.setStatusCode(statusCode);
        exceptionBuilder.setExtendedRequestId(headers.get("x-amz-id-2"));
        exceptionBuilder.setRequestId(headers.get("x-amz-request-id"));
        exceptionBuilder.setCloudFrontId(headers.get("X-Amz-Cf-Id"));
        exceptionBuilder.setErrorCode(statusCode + " " + errorResponse.getStatusText());
        exceptionBuilder.addAdditionalDetail("x-amz-bucket-region", errorResponse.getHeaders().get("x-amz-bucket-region"));
        exceptionBuilder.setProxyHost(this.clientConfiguration.getProxyHost());
        return exceptionBuilder.build();
    }

    @Override
    public boolean needsConnectionLeftOpen() {
        return false;
    }

    private static enum S3ErrorTags {
        Error,
        Message,
        Code,
        RequestId,
        HostId;

    }
}

