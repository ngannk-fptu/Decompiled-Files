/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.QueueConfiguration;
import com.amazonaws.services.s3.model.transform.NotificationConfigurationStaxUnmarshaller;
import com.amazonaws.transform.SimpleTypeStaxUnmarshallers;
import com.amazonaws.transform.StaxUnmarshallerContext;

class QueueConfigurationStaxUnmarshaller
extends NotificationConfigurationStaxUnmarshaller<QueueConfiguration> {
    private static final QueueConfigurationStaxUnmarshaller instance = new QueueConfigurationStaxUnmarshaller();

    public static QueueConfigurationStaxUnmarshaller getInstance() {
        return instance;
    }

    private QueueConfigurationStaxUnmarshaller() {
    }

    @Override
    protected boolean handleXmlEvent(QueueConfiguration queueConfig, StaxUnmarshallerContext context, int targetDepth) throws Exception {
        if (context.testExpression("Queue", targetDepth)) {
            queueConfig.setQueueARN(SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context));
            return true;
        }
        return false;
    }

    @Override
    protected QueueConfiguration createConfiguration() {
        return new QueueConfiguration();
    }
}

