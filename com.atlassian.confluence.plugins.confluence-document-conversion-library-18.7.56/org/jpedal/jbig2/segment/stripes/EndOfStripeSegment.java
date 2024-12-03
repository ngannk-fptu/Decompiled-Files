/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.segment.stripes;

import java.io.IOException;
import org.jpedal.jbig2.JBIG2Exception;
import org.jpedal.jbig2.decoders.JBIG2StreamDecoder;
import org.jpedal.jbig2.segment.Segment;

public class EndOfStripeSegment
extends Segment {
    public EndOfStripeSegment(JBIG2StreamDecoder jBIG2StreamDecoder) {
        super(jBIG2StreamDecoder);
    }

    public void readSegment() throws IOException, JBIG2Exception {
        for (int i = 0; i < this.getSegmentHeader().getSegmentDataLength(); ++i) {
            this.decoder.readByte();
        }
    }
}

