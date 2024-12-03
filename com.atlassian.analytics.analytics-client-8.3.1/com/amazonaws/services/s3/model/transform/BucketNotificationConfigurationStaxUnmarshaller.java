/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.EventBridgeConfiguration;
import com.amazonaws.services.s3.model.NotificationConfiguration;
import com.amazonaws.services.s3.model.transform.EventBridgeConfigurationStaxUnmarshaller;
import com.amazonaws.services.s3.model.transform.LambdaConfigurationStaxUnmarshaller;
import com.amazonaws.services.s3.model.transform.QueueConfigurationStaxUnmarshaller;
import com.amazonaws.services.s3.model.transform.TopicConfigurationStaxUnmarshaller;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import com.amazonaws.util.XmlUtils;
import java.io.InputStream;
import java.util.Map;
import javax.xml.stream.events.XMLEvent;

public class BucketNotificationConfigurationStaxUnmarshaller
implements Unmarshaller<BucketNotificationConfiguration, InputStream> {
    private static final BucketNotificationConfigurationStaxUnmarshaller instance = new BucketNotificationConfigurationStaxUnmarshaller();

    public static BucketNotificationConfigurationStaxUnmarshaller getInstance() {
        return instance;
    }

    private BucketNotificationConfigurationStaxUnmarshaller() {
    }

    @Override
    public BucketNotificationConfiguration unmarshall(InputStream inputStream) throws Exception {
        StaxUnmarshallerContext context = new StaxUnmarshallerContext(XmlUtils.getXmlInputFactory().createXMLEventReader(inputStream));
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;
        if (context.isStartOfDocument()) {
            ++targetDepth;
        }
        BucketNotificationConfiguration config = new BucketNotificationConfiguration();
        while (true) {
            XMLEvent xmlEvent;
            if ((xmlEvent = context.nextEvent()).isEndDocument()) {
                return config;
            }
            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                Map.Entry<String, NotificationConfiguration> entry;
                if (context.testExpression("TopicConfiguration", targetDepth)) {
                    entry = TopicConfigurationStaxUnmarshaller.getInstance().unmarshall(context);
                    config.addConfiguration(entry.getKey(), entry.getValue());
                    continue;
                }
                if (context.testExpression("QueueConfiguration", targetDepth)) {
                    entry = QueueConfigurationStaxUnmarshaller.getInstance().unmarshall(context);
                    config.addConfiguration(entry.getKey(), entry.getValue());
                    continue;
                }
                if (context.testExpression("CloudFunctionConfiguration", targetDepth)) {
                    entry = LambdaConfigurationStaxUnmarshaller.getInstance().unmarshall(context);
                    config.addConfiguration(entry.getKey(), entry.getValue());
                    continue;
                }
                if (!context.testExpression("EventBridgeConfiguration", targetDepth)) continue;
                EventBridgeConfiguration eventBridgeConfig = EventBridgeConfigurationStaxUnmarshaller.getInstance().unmarshall(context);
                config.setEventBridgeConfiguration(eventBridgeConfig);
                continue;
            }
            if (xmlEvent.isEndElement() && context.getCurrentDepth() < originalDepth) break;
        }
        return config;
    }
}

