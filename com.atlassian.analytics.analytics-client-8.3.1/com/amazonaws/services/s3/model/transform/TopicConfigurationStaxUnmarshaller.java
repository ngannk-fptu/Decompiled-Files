/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.TopicConfiguration;
import com.amazonaws.services.s3.model.transform.NotificationConfigurationStaxUnmarshaller;
import com.amazonaws.transform.SimpleTypeStaxUnmarshallers;
import com.amazonaws.transform.StaxUnmarshallerContext;

class TopicConfigurationStaxUnmarshaller
extends NotificationConfigurationStaxUnmarshaller<TopicConfiguration> {
    private static final TopicConfigurationStaxUnmarshaller instance = new TopicConfigurationStaxUnmarshaller();

    public static TopicConfigurationStaxUnmarshaller getInstance() {
        return instance;
    }

    private TopicConfigurationStaxUnmarshaller() {
    }

    @Override
    protected boolean handleXmlEvent(TopicConfiguration topicConfig, StaxUnmarshallerContext context, int targetDepth) throws Exception {
        if (context.testExpression("Topic", targetDepth)) {
            topicConfig.setTopicARN(SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context));
            return true;
        }
        return false;
    }

    @Override
    protected TopicConfiguration createConfiguration() {
        return new TopicConfiguration();
    }
}

