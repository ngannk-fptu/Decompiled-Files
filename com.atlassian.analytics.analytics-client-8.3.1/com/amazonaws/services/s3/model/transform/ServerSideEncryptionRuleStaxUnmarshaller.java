/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.ServerSideEncryptionRule;
import com.amazonaws.services.s3.model.transform.ServerSideEncryptionByDefaultStaxUnmarshaller;
import com.amazonaws.transform.SimpleTypeStaxUnmarshallers;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import javax.xml.stream.events.XMLEvent;

class ServerSideEncryptionRuleStaxUnmarshaller
implements Unmarshaller<ServerSideEncryptionRule, StaxUnmarshallerContext> {
    private static final ServerSideEncryptionRuleStaxUnmarshaller instance = new ServerSideEncryptionRuleStaxUnmarshaller();

    public static ServerSideEncryptionRuleStaxUnmarshaller getInstance() {
        return instance;
    }

    private ServerSideEncryptionRuleStaxUnmarshaller() {
    }

    @Override
    public ServerSideEncryptionRule unmarshall(StaxUnmarshallerContext context) throws Exception {
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;
        if (context.isStartOfDocument()) {
            ++targetDepth;
        }
        ServerSideEncryptionRule rule = new ServerSideEncryptionRule();
        while (true) {
            XMLEvent xmlEvent;
            if ((xmlEvent = context.nextEvent()).isEndDocument()) {
                return rule;
            }
            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (context.testExpression("ApplyServerSideEncryptionByDefault", targetDepth)) {
                    rule.setApplyServerSideEncryptionByDefault(ServerSideEncryptionByDefaultStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
                if (!context.testExpression("BucketKeyEnabled", targetDepth)) continue;
                rule.setBucketKeyEnabled(SimpleTypeStaxUnmarshallers.BooleanStaxUnmarshaller.getInstance().unmarshall(context));
                continue;
            }
            if (xmlEvent.isEndElement() && context.getCurrentDepth() < originalDepth) break;
        }
        return rule;
    }
}

