/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.EventBridgeConfiguration;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import javax.xml.stream.events.XMLEvent;

class EventBridgeConfigurationStaxUnmarshaller
implements Unmarshaller<EventBridgeConfiguration, StaxUnmarshallerContext> {
    private static final EventBridgeConfigurationStaxUnmarshaller instance = new EventBridgeConfigurationStaxUnmarshaller();

    public static EventBridgeConfigurationStaxUnmarshaller getInstance() {
        return instance;
    }

    private EventBridgeConfigurationStaxUnmarshaller() {
    }

    @Override
    public EventBridgeConfiguration unmarshall(StaxUnmarshallerContext context) throws Exception {
        XMLEvent xmlEvent;
        int originalDepth = context.getCurrentDepth();
        while (!((xmlEvent = context.nextEvent()).isEndDocument() || xmlEvent.isEndElement() && context.getCurrentDepth() < originalDepth)) {
        }
        return new EventBridgeConfiguration();
    }
}

