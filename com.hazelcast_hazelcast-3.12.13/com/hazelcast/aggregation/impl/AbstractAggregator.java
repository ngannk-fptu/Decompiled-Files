/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aggregation.impl;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.internal.json.NonTerminalJsonValue;
import com.hazelcast.query.impl.Extractable;
import com.hazelcast.query.impl.getters.MultiResult;
import java.util.List;
import java.util.Map;

public abstract class AbstractAggregator<I, E, R>
extends Aggregator<I, R> {
    protected String attributePath;

    public AbstractAggregator() {
        this(null);
    }

    public AbstractAggregator(String attributePath) {
        this.attributePath = attributePath;
    }

    @Override
    public final void accumulate(I entry) {
        Object extractedValue = this.extract(entry);
        if (extractedValue instanceof MultiResult) {
            boolean nullEmptyTargetSkipped = false;
            MultiResult multiResult = (MultiResult)extractedValue;
            List results = multiResult.getResults();
            for (int i = 0; i < results.size(); ++i) {
                Object result = results.get(i);
                if (result == null && multiResult.isNullEmptyTarget() && !nullEmptyTargetSkipped) {
                    nullEmptyTargetSkipped = true;
                    continue;
                }
                this.accumulateExtracted(entry, results.get(i));
            }
        } else if (extractedValue != NonTerminalJsonValue.INSTANCE) {
            this.accumulateExtracted(entry, extractedValue);
        }
    }

    private <T> T extract(I input) {
        if (this.attributePath == null) {
            if (input instanceof Map.Entry) {
                return (T)((Map.Entry)input).getValue();
            }
        } else if (input instanceof Extractable) {
            return (T)((Extractable)input).getAttributeValue(this.attributePath);
        }
        throw new IllegalArgumentException("Can't extract " + this.attributePath + " from the given input");
    }

    protected abstract void accumulateExtracted(I var1, E var2);
}

