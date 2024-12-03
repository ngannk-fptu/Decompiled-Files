/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.FilterRule;
import com.amazonaws.transform.SimpleTypeStaxUnmarshallers;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;
import javax.xml.stream.events.XMLEvent;

class FilterRuleStaxUnmarshaller
implements Unmarshaller<FilterRule, StaxUnmarshallerContext> {
    private static final FilterRuleStaxUnmarshaller instance = new FilterRuleStaxUnmarshaller();

    public static FilterRuleStaxUnmarshaller getInstance() {
        return instance;
    }

    private FilterRuleStaxUnmarshaller() {
    }

    @Override
    public FilterRule unmarshall(StaxUnmarshallerContext context) throws Exception {
        int originalDepth = context.getCurrentDepth();
        int targetDepth = originalDepth + 1;
        if (context.isStartOfDocument()) {
            ++targetDepth;
        }
        FilterRule filter = new FilterRule();
        while (true) {
            XMLEvent xmlEvent;
            if ((xmlEvent = context.nextEvent()).isEndDocument()) {
                return filter;
            }
            if (xmlEvent.isAttribute() || xmlEvent.isStartElement()) {
                if (context.testExpression("Name", targetDepth)) {
                    filter.setName(SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context));
                    continue;
                }
                if (!context.testExpression("Value", targetDepth)) continue;
                filter.setValue(SimpleTypeStaxUnmarshallers.StringStaxUnmarshaller.getInstance().unmarshall(context));
                continue;
            }
            if (xmlEvent.isEndElement() && context.getCurrentDepth() < originalDepth) break;
        }
        return filter;
    }
}

