/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.spans;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanPositionCheckQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.ToStringUtils;

public class SpanPayloadCheckQuery
extends SpanPositionCheckQuery {
    protected final Collection<byte[]> payloadToMatch;

    public SpanPayloadCheckQuery(SpanQuery match, Collection<byte[]> payloadToMatch) {
        super(match);
        if (match instanceof SpanNearQuery) {
            throw new IllegalArgumentException("SpanNearQuery not allowed");
        }
        this.payloadToMatch = payloadToMatch;
    }

    @Override
    protected SpanPositionCheckQuery.AcceptStatus acceptPosition(Spans spans) throws IOException {
        boolean result = spans.isPayloadAvailable();
        if (result) {
            Collection<byte[]> candidate = spans.getPayload();
            if (candidate.size() == this.payloadToMatch.size()) {
                Iterator<byte[]> toMatchIter = this.payloadToMatch.iterator();
                for (byte[] candBytes : candidate) {
                    if (Arrays.equals(candBytes, toMatchIter.next())) continue;
                    return SpanPositionCheckQuery.AcceptStatus.NO;
                }
                return SpanPositionCheckQuery.AcceptStatus.YES;
            }
            return SpanPositionCheckQuery.AcceptStatus.NO;
        }
        return SpanPositionCheckQuery.AcceptStatus.YES;
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("spanPayCheck(");
        buffer.append(this.match.toString(field));
        buffer.append(", payloadRef: ");
        for (byte[] bytes : this.payloadToMatch) {
            ToStringUtils.byteArray(buffer, bytes);
            buffer.append(';');
        }
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public SpanPayloadCheckQuery clone() {
        SpanPayloadCheckQuery result = new SpanPayloadCheckQuery((SpanQuery)this.match.clone(), this.payloadToMatch);
        result.setBoost(this.getBoost());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpanPayloadCheckQuery)) {
            return false;
        }
        SpanPayloadCheckQuery other = (SpanPayloadCheckQuery)o;
        return this.payloadToMatch.equals(other.payloadToMatch) && this.match.equals(other.match) && this.getBoost() == other.getBoost();
    }

    @Override
    public int hashCode() {
        int h = this.match.hashCode();
        h ^= h << 8 | h >>> 25;
        h ^= this.payloadToMatch.hashCode();
        return h ^= Float.floatToRawIntBits(this.getBoost());
    }
}

