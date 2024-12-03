/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.transform.StaxUnmarshallerContext;
import com.amazonaws.transform.Unmarshaller;

public class VoidStaxUnmarshaller<T>
implements Unmarshaller<T, StaxUnmarshallerContext> {
    @Override
    public T unmarshall(StaxUnmarshallerContext context) throws Exception {
        while (!context.nextEvent().isEndDocument()) {
        }
        return null;
    }
}

