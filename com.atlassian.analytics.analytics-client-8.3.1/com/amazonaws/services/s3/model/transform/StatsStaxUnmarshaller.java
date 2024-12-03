/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.Stats;
import com.amazonaws.transform.SimpleTypeStaxUnmarshallers;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import javax.xml.stream.events.XMLEvent;

class StatsStaxUnmarshaller
implements Unmarshaller<Stats, StaxUnmarshallerContext> {
    private static final StatsStaxUnmarshaller instance = new StatsStaxUnmarshaller();

    public static StatsStaxUnmarshaller getInstance() {
        return instance;
    }

    private StatsStaxUnmarshaller() {
    }

    @Override
    public Stats unmarshall(StaxUnmarshallerContext context) throws Exception {
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;
        if (context.isStartOfDocument()) {
            ++targetDepth;
        }
        Stats result = new Stats();
        while (true) {
            XMLEvent xmlEvent;
            if ((xmlEvent = context.nextEvent()).isEndDocument()) {
                return result;
            }
            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (context.testExpression("BytesScanned", targetDepth)) {
                    result.setBytesScanned(SimpleTypeStaxUnmarshallers.LongStaxUnmarshaller.getInstance().unmarshall(context));
                }
                if (context.testExpression("BytesReturned", targetDepth)) {
                    result.setBytesReturned(SimpleTypeStaxUnmarshallers.LongStaxUnmarshaller.getInstance().unmarshall(context));
                }
                if (!context.testExpression("BytesProcessed", targetDepth)) continue;
                result.setBytesProcessed(SimpleTypeStaxUnmarshallers.LongStaxUnmarshaller.getInstance().unmarshall(context));
                continue;
            }
            if (xmlEvent.isEndElement() && context.getCurrentDepth() < originalDepth) break;
        }
        return result;
    }
}

