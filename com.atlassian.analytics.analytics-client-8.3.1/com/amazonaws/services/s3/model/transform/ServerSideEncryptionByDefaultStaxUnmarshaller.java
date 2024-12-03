/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.ServerSideEncryptionByDefault;
import com.amazonaws.transform.SimpleTypeStaxUnmarshallers;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import javax.xml.stream.events.XMLEvent;

class ServerSideEncryptionByDefaultStaxUnmarshaller
implements Unmarshaller<ServerSideEncryptionByDefault, StaxUnmarshallerContext> {
    private static final ServerSideEncryptionByDefaultStaxUnmarshaller instance = new ServerSideEncryptionByDefaultStaxUnmarshaller();

    public static ServerSideEncryptionByDefaultStaxUnmarshaller getInstance() {
        return instance;
    }

    private ServerSideEncryptionByDefaultStaxUnmarshaller() {
    }

    @Override
    public ServerSideEncryptionByDefault unmarshall(StaxUnmarshallerContext context) throws Exception {
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;
        if (context.isStartOfDocument()) {
            ++targetDepth;
        }
        ServerSideEncryptionByDefault sseByDefault = new ServerSideEncryptionByDefault();
        while (true) {
            XMLEvent xmlEvent;
            if ((xmlEvent = context.nextEvent()).isEndDocument()) {
                return sseByDefault;
            }
            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (context.testExpression("SSEAlgorithm", targetDepth)) {
                    sseByDefault.setSSEAlgorithm(SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context));
                }
                if (!context.testExpression("KMSMasterKeyID", targetDepth)) continue;
                sseByDefault.setKMSMasterKeyID(SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context));
                continue;
            }
            if (xmlEvent.isEndElement() && context.getCurrentDepth() < originalDepth) break;
        }
        return sseByDefault;
    }
}

