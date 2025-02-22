/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.ext.EventData
 */
package org.apache.logging.slf4j;

import java.util.Map;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.slf4j.ext.EventData;

public class EventDataConverter {
    public Message convertEvent(String message, Object[] objects, Throwable throwable) {
        try {
            EventData data = objects != null && objects[0] instanceof EventData ? (EventData)objects[0] : new EventData(message);
            StructuredDataMessage msg = new StructuredDataMessage(data.getEventId(), data.getMessage(), data.getEventType());
            for (Map.Entry entry : data.getEventMap().entrySet()) {
                String key = (String)entry.getKey();
                if ("EventType".equals(key) || "EventId".equals(key) || "EventMessage".equals(key)) continue;
                msg.put(key, String.valueOf(entry.getValue()));
            }
            return msg;
        }
        catch (Exception ex) {
            return new ParameterizedMessage(message, objects, throwable);
        }
    }
}

