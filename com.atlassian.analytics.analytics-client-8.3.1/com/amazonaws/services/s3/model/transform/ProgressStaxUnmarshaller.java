/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.model.Progress;
import com.amazonaws.services.s3.model.Stats;
import com.amazonaws.services.s3.model.transform.StatsStaxUnmarshaller;
import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

class ProgressStaxUnmarshaller
implements Unmarshaller<Progress, StaxUnmarshallerContext> {
    private static final ProgressStaxUnmarshaller instance = new ProgressStaxUnmarshaller();

    public static ProgressStaxUnmarshaller getInstance() {
        return instance;
    }

    private ProgressStaxUnmarshaller() {
    }

    @Override
    public Progress unmarshall(StaxUnmarshallerContext context) throws Exception {
        Stats queryStats = StatsStaxUnmarshaller.getInstance().unmarshall(context);
        return new Progress().withBytesProcessed(queryStats.getBytesProcessed()).withBytesReturned(queryStats.getBytesReturned()).withBytesScanned(queryStats.getBytesScanned());
    }
}

