/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.services.s3.internal.eventstreaming.HeaderType;
import com.amazonaws.services.s3.internal.eventstreaming.HeaderValue;
import com.amazonaws.services.s3.internal.eventstreaming.Message;
import com.amazonaws.services.s3.model.SelectObjectContentEvent;
import com.amazonaws.services.s3.model.SelectObjectContentEventException;
import com.amazonaws.services.s3.model.transform.ProgressStaxUnmarshaller;
import com.amazonaws.services.s3.model.transform.StatsStaxUnmarshaller;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.util.XmlUtils;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

@SdkInternalApi
public abstract class SelectObjectContentEventUnmarshaller {
    public static SelectObjectContentEvent unmarshalMessage(Message message) {
        String messageType = SelectObjectContentEventUnmarshaller.getStringHeader(message, ":message-type");
        if ("error".equals(messageType)) {
            throw SelectObjectContentEventUnmarshaller.unmarshalErrorMessage(message);
        }
        if ("event".equals(messageType)) {
            return SelectObjectContentEventUnmarshaller.unmarshalEventMessage(message);
        }
        throw new SelectObjectContentEventException("Service returned unknown message type: " + messageType);
    }

    private static SelectObjectContentEventException unmarshalErrorMessage(Message message) {
        String errorCode = SelectObjectContentEventUnmarshaller.getStringHeader(message, ":error-code");
        String errorMessage = SelectObjectContentEventUnmarshaller.getStringHeader(message, ":error-message");
        SelectObjectContentEventException exception = new SelectObjectContentEventException("S3 returned an error: " + errorMessage + " (" + errorCode + ")");
        exception.setErrorCode(errorCode);
        exception.setErrorMessage(errorMessage);
        return exception;
    }

    private static SelectObjectContentEvent unmarshalEventMessage(Message message) {
        String eventType = SelectObjectContentEventUnmarshaller.getStringHeader(message, ":event-type");
        try {
            return SelectObjectContentEventUnmarshaller.forEventType(eventType).unmarshal(message);
        }
        catch (Exception e) {
            throw new SelectObjectContentEventException("Failed to read response event of type " + eventType, e);
        }
    }

    public static SelectObjectContentEventUnmarshaller forEventType(String eventType) {
        if ("Records".equals(eventType)) {
            return new RecordsEventUnmarshaller();
        }
        if ("Stats".equals(eventType)) {
            return new StatsEventUnmarshaller();
        }
        if ("Progress".equals(eventType)) {
            return new ProgressEventUnmarshaller();
        }
        if ("Cont".equals(eventType)) {
            return new ContinuationEventUnmarshaller();
        }
        if ("End".equals(eventType)) {
            return new EndEventUnmarshaller();
        }
        return new UnknownEventUnmarshaller();
    }

    private static String getStringHeader(Message message, String headerName) {
        HeaderValue header = message.getHeaders().get(headerName);
        if (header == null) {
            throw new SelectObjectContentEventException("Unexpected lack of '" + headerName + "' header from service.");
        }
        if (header.getType() != HeaderType.STRING) {
            throw new SelectObjectContentEventException("Unexpected non-string '" + headerName + "' header: " + (Object)((Object)header.getType()));
        }
        return header.getString();
    }

    public abstract SelectObjectContentEvent unmarshal(Message var1) throws Exception;

    private static StaxUnmarshallerContext payloadUnmarshaller(Message message) throws XMLStreamException {
        ByteArrayInputStream payloadStream = new ByteArrayInputStream(message.getPayload());
        XMLEventReader xmlEventReader = XmlUtils.getXmlInputFactory().createXMLEventReader(payloadStream);
        return new StaxUnmarshallerContext(xmlEventReader);
    }

    public static class UnknownEventUnmarshaller
    extends SelectObjectContentEventUnmarshaller {
        @Override
        public SelectObjectContentEvent unmarshal(Message message) {
            return new SelectObjectContentEvent();
        }
    }

    public static class EndEventUnmarshaller
    extends SelectObjectContentEventUnmarshaller {
        @Override
        public SelectObjectContentEvent.EndEvent unmarshal(Message message) {
            return new SelectObjectContentEvent.EndEvent();
        }
    }

    public static class ContinuationEventUnmarshaller
    extends SelectObjectContentEventUnmarshaller {
        @Override
        public SelectObjectContentEvent.ContinuationEvent unmarshal(Message message) {
            return new SelectObjectContentEvent.ContinuationEvent();
        }
    }

    public static class ProgressEventUnmarshaller
    extends SelectObjectContentEventUnmarshaller {
        @Override
        public SelectObjectContentEvent.ProgressEvent unmarshal(Message message) throws Exception {
            StaxUnmarshallerContext context = SelectObjectContentEventUnmarshaller.payloadUnmarshaller(message);
            return new SelectObjectContentEvent.ProgressEvent().withDetails(ProgressStaxUnmarshaller.getInstance().unmarshall(context));
        }
    }

    public static class StatsEventUnmarshaller
    extends SelectObjectContentEventUnmarshaller {
        @Override
        public SelectObjectContentEvent.StatsEvent unmarshal(Message message) throws Exception {
            StaxUnmarshallerContext context = SelectObjectContentEventUnmarshaller.payloadUnmarshaller(message);
            return new SelectObjectContentEvent.StatsEvent().withDetails(StatsStaxUnmarshaller.getInstance().unmarshall(context));
        }
    }

    public static class RecordsEventUnmarshaller
    extends SelectObjectContentEventUnmarshaller {
        @Override
        public SelectObjectContentEvent.RecordsEvent unmarshal(Message message) {
            return new SelectObjectContentEvent.RecordsEvent().withPayload(ByteBuffer.wrap(message.getPayload()));
        }
    }
}

