/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.impl.io.EmptyInputStream
 */
package com.amazonaws.http;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.internal.SdkFilterInputStream;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.amazonaws.transform.VoidStaxUnmarshaller;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.XmlUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.io.EmptyInputStream;

public class StaxResponseHandler<T>
implements HttpResponseHandler<AmazonWebServiceResponse<T>> {
    private Unmarshaller<T, StaxUnmarshallerContext> responseUnmarshaller;
    private final boolean needsConnectionLeftOpen;
    private final boolean isPayloadXML;
    private static final Log log = LogFactory.getLog((String)"com.amazonaws.request");

    public StaxResponseHandler(Unmarshaller<T, StaxUnmarshallerContext> responseUnmarshaller, boolean needsConnectionLeftOpen, boolean isPayloadXML) {
        this.responseUnmarshaller = responseUnmarshaller;
        if (this.responseUnmarshaller == null) {
            this.responseUnmarshaller = new VoidStaxUnmarshaller();
        }
        this.needsConnectionLeftOpen = needsConnectionLeftOpen;
        this.isPayloadXML = isPayloadXML;
    }

    public StaxResponseHandler(Unmarshaller<T, StaxUnmarshallerContext> responseUnmarshaller) {
        this(responseUnmarshaller, false, true);
    }

    @Override
    public AmazonWebServiceResponse<T> handle(HttpResponse response) throws Exception {
        XMLEventReader eventReader;
        log.trace((Object)"Parsing service response XML");
        InputStream content = response.getContent();
        if (content == null || !this.shouldParsePayloadAsXml()) {
            content = new ByteArrayInputStream("<eof/>".getBytes(StringUtils.UTF8));
        } else if (content instanceof SdkFilterInputStream && ((SdkFilterInputStream)content).getDelegateStream() instanceof EmptyInputStream) {
            content = new ByteArrayInputStream("<eof/>".getBytes(StringUtils.UTF8));
        }
        try {
            eventReader = XmlUtils.getXmlInputFactory().createXMLEventReader(content);
        }
        catch (XMLStreamException e) {
            throw this.handleXmlStreamException(e);
        }
        try {
            AmazonWebServiceResponse<T> awsResponse = new AmazonWebServiceResponse<T>();
            StaxUnmarshallerContext unmarshallerContext = new StaxUnmarshallerContext(eventReader, response.getHeaders(), response);
            unmarshallerContext.registerMetadataExpression("ResponseMetadata/RequestId", 2, "AWS_REQUEST_ID");
            unmarshallerContext.registerMetadataExpression("requestId", 2, "AWS_REQUEST_ID");
            this.registerAdditionalMetadataExpressions(unmarshallerContext);
            T result = this.responseUnmarshaller.unmarshall(unmarshallerContext);
            awsResponse.setResult(result);
            Map<String, String> metadata = unmarshallerContext.getMetadata();
            Map<String, String> responseHeaders = response.getHeaders();
            if (responseHeaders != null) {
                if (responseHeaders.get("x-amzn-RequestId") != null) {
                    metadata.put("AWS_REQUEST_ID", responseHeaders.get("x-amzn-RequestId"));
                }
                if (responseHeaders.get("x-amz-id-2") != null) {
                    metadata.put("AWS_EXTENDED_REQUEST_ID", responseHeaders.get("x-amz-id-2"));
                }
            }
            awsResponse.setResponseMetadata(this.getResponseMetadata(metadata));
            log.trace((Object)"Done parsing service response");
            AmazonWebServiceResponse<T> amazonWebServiceResponse = awsResponse;
            return amazonWebServiceResponse;
        }
        catch (XMLStreamException e) {
            throw this.handleXmlStreamException(e);
        }
        finally {
            try {
                eventReader.close();
            }
            catch (XMLStreamException e) {
                log.warn((Object)"Error closing xml parser", (Throwable)e);
            }
        }
    }

    private Exception handleXmlStreamException(XMLStreamException e) throws Exception {
        if (e.getNestedException() instanceof IOException) {
            return new IOException(e);
        }
        return e;
    }

    protected ResponseMetadata getResponseMetadata(Map<String, String> metadata) {
        return new ResponseMetadata(metadata);
    }

    protected void registerAdditionalMetadataExpressions(StaxUnmarshallerContext unmarshallerContext) {
    }

    @Override
    public boolean needsConnectionLeftOpen() {
        return this.needsConnectionLeftOpen;
    }

    private boolean shouldParsePayloadAsXml() {
        return !this.needsConnectionLeftOpen && this.isPayloadXML;
    }
}

