/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.NotificationConfiguration;
import com.amazonaws.services.s3.model.transform.FilterStaxUnmarshaller;
import com.amazonaws.transform.SimpleTypeStaxUnmarshallers;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import java.util.AbstractMap;
import java.util.Map;
import javax.xml.stream.events.XMLEvent;

abstract class NotificationConfigurationStaxUnmarshaller<T extends NotificationConfiguration>
implements Unmarshaller<Map.Entry<String, NotificationConfiguration>, StaxUnmarshallerContext> {
    NotificationConfigurationStaxUnmarshaller() {
    }

    @Override
    public Map.Entry<String, NotificationConfiguration> unmarshall(StaxUnmarshallerContext context) throws Exception {
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;
        if (context.isStartOfDocument()) {
            ++targetDepth;
        }
        T topicConfig = this.createConfiguration();
        String id = null;
        while (true) {
            XMLEvent xmlEvent;
            if ((xmlEvent = context.nextEvent()).isEndDocument()) {
                return new AbstractMap.SimpleEntry<Object, T>(id, topicConfig);
            }
            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (this.handleXmlEvent(topicConfig, context, targetDepth)) continue;
                if (context.testExpression("Id", targetDepth)) {
                    id = SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context);
                    continue;
                }
                if (context.testExpression("Event", targetDepth)) {
                    ((NotificationConfiguration)topicConfig).addEvent(SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
                if (!context.testExpression("Filter", targetDepth)) continue;
                ((NotificationConfiguration)topicConfig).setFilter(FilterStaxUnmarshaller.getInstance().unmarshall(context));
                continue;
            }
            if (xmlEvent.isEndElement() && context.getCurrentDepth() < originalDepth) break;
        }
        return new AbstractMap.SimpleEntry<Object, T>(id, topicConfig);
    }

    protected abstract T createConfiguration();

    protected abstract boolean handleXmlEvent(T var1, StaxUnmarshallerContext var2, int var3) throws Exception;
}

