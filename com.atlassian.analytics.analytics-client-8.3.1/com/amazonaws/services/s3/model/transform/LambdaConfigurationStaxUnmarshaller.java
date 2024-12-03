/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.CloudFunctionConfiguration;
import com.amazonaws.services.s3.model.Filter;
import com.amazonaws.services.s3.model.LambdaConfiguration;
import com.amazonaws.services.s3.model.NotificationConfiguration;
import com.amazonaws.services.s3.model.transform.FilterStaxUnmarshaller;
import com.amazonaws.transform.SimpleTypeStaxUnmarshallers;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.stream.events.XMLEvent;

class LambdaConfigurationStaxUnmarshaller
implements Unmarshaller<Map.Entry<String, NotificationConfiguration>, StaxUnmarshallerContext> {
    private static final LambdaConfigurationStaxUnmarshaller instance = new LambdaConfigurationStaxUnmarshaller();

    public static LambdaConfigurationStaxUnmarshaller getInstance() {
        return instance;
    }

    private LambdaConfigurationStaxUnmarshaller() {
    }

    @Override
    public Map.Entry<String, NotificationConfiguration> unmarshall(StaxUnmarshallerContext context) throws Exception {
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;
        if (context.isStartOfDocument()) {
            ++targetDepth;
        }
        String id = null;
        ArrayList<String> events = new ArrayList<String>();
        Filter filter = null;
        String functionArn = null;
        String invocationRole = null;
        while (true) {
            XMLEvent xmlEvent;
            if ((xmlEvent = context.nextEvent()).isEndDocument()) {
                return this.createLambdaConfig(id, events, functionArn, invocationRole, filter);
            }
            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (context.testExpression("Id", targetDepth)) {
                    id = SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context);
                    continue;
                }
                if (context.testExpression("Event", targetDepth)) {
                    events.add(SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
                if (context.testExpression("Filter", targetDepth)) {
                    filter = FilterStaxUnmarshaller.getInstance().unmarshall(context);
                    continue;
                }
                if (context.testExpression("CloudFunction", targetDepth)) {
                    functionArn = SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context);
                    continue;
                }
                if (!context.testExpression("InvocationRole", targetDepth)) continue;
                invocationRole = SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context);
                continue;
            }
            if (xmlEvent.isEndElement() && context.getCurrentDepth() < originalDepth) break;
        }
        return this.createLambdaConfig(id, events, functionArn, invocationRole, filter);
    }

    private Map.Entry<String, NotificationConfiguration> createLambdaConfig(String id, List<String> events, String functionArn, String invocationRole, Filter filter) {
        NotificationConfiguration config = invocationRole == null ? new LambdaConfiguration(functionArn, events.toArray(new String[0])) : new CloudFunctionConfiguration(invocationRole, functionArn, events.toArray(new String[0]));
        return new AbstractMap.SimpleEntry<String, NotificationConfiguration>(id, config.withFilter(filter));
    }
}

