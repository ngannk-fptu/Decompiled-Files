/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.search.spans.SpanNearQuery;
import com.atlassian.lucene36.search.spans.SpanPositionCheckQuery;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.Spans;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
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
    public Object clone() {
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
        return ((Object)this.payloadToMatch).equals(other.payloadToMatch) && this.match.equals(other.match) && this.getBoost() == other.getBoost();
    }

    @Override
    public int hashCode() {
        int h = this.match.hashCode();
        h ^= h << 8 | h >>> 25;
        h ^= ((Object)this.payloadToMatch).hashCode();
        return h ^= Float.floatToRawIntBits(this.getBoost());
    }
}

