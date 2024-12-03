/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.web.rangerequest;

import com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException;
import com.google.common.collect.ComparisonChain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class RangeRequest
implements Comparable<RangeRequest> {
    private final long firstByte;
    private final Long lastByte;
    private final long contentLength;
    private static final String RANGE = "\\s*(-?\\d+)\\s*-?\\s*(\\d*)?\\s*";
    private static final String RANGE_REGEX = "^\\s*([A-Za-z]*)\\s*=\\s*(-?\\d+)\\s*-?\\s*(\\d*)?\\s*(?:,\\s*(-?\\d+)\\s*-?\\s*(\\d*)?\\s*)*$";
    public static final Pattern RANGE_PATTERN = Pattern.compile("^\\s*([A-Za-z]*)\\s*=\\s*(-?\\d+)\\s*-?\\s*(\\d*)?\\s*(?:,\\s*(-?\\d+)\\s*-?\\s*(\\d*)?\\s*)*$");
    private static final String BYTES_UNIT = "bytes";

    public RangeRequest(long firstByte, long contentLength) {
        this.firstByte = firstByte;
        this.lastByte = null;
        this.contentLength = contentLength;
    }

    public RangeRequest(long firstByte, long lastByte, long contentLength) {
        this.firstByte = firstByte;
        this.lastByte = lastByte >= contentLength ? contentLength - 1L : lastByte;
        this.contentLength = contentLength;
    }

    public long getOffset() {
        if (this.firstByte < 0L) {
            return this.contentLength - 1L + this.firstByte;
        }
        return this.firstByte;
    }

    public long getEnd() {
        if (null == this.lastByte) {
            return this.contentLength - 1L;
        }
        return this.lastByte;
    }

    public long getRangeLength() {
        return this.getEnd() - this.getOffset() + 1L;
    }

    public long getContentLength() {
        return this.contentLength;
    }

    public static RangeRequest parse(String headerValue, long contentLength) throws RangeNotSatisfiableException {
        Matcher range = RANGE_PATTERN.matcher(headerValue);
        if (range.matches()) {
            String unit = range.group(1);
            if (!BYTES_UNIT.equals(unit)) {
                throw new UnsupportedOperationException("Range requests must be specified in bytes, unit requested was " + unit);
            }
            ArrayList<RangeRequest> requests = new ArrayList<RangeRequest>();
            for (int i = 2; i < range.groupCount(); i += 2) {
                String offsetText = range.group(i);
                String endText = range.group(i + 1);
                if (offsetText == null && endText == null) continue;
                requests.add(RangeRequest.parseRange(offsetText, endText, contentLength));
            }
            return RangeRequest.collapseRanges(requests);
        }
        throw new UnsupportedOperationException("A valid range must be specified, supplied header was " + headerValue);
    }

    private static RangeRequest collapseRanges(List<RangeRequest> requests) throws RangeNotSatisfiableException {
        Collections.sort(requests);
        Iterator<RangeRequest> iter = requests.iterator();
        RangeRequest merged = iter.next();
        while (iter.hasNext()) {
            merged = merged.merge(iter.next());
        }
        return merged;
    }

    private static RangeRequest parseRange(String offsetText, String endText, long contentLength) throws RangeNotSatisfiableException {
        Long end;
        long offset = RangeRequest.parseByteString(offsetText);
        Long l = end = StringUtils.isNotEmpty((CharSequence)endText) ? Long.valueOf(RangeRequest.parseByteString(endText)) : null;
        if (end == null) {
            return new RangeRequest(offset, contentLength);
        }
        if (offset < 0L || end < offset) {
            throw new UnsupportedOperationException("A valid range must be specified, requested range was " + offsetText + ", " + endText);
        }
        if (offset >= contentLength) {
            throw new RangeNotSatisfiableException("The provided range is invalid, requested range was " + offsetText + ", " + endText + ", content length was   " + contentLength);
        }
        return new RangeRequest(offset, end, contentLength);
    }

    private static long parseByteString(String byteString) {
        try {
            return Long.parseLong(byteString);
        }
        catch (NumberFormatException e) {
            throw new UnsupportedOperationException("A numeric byte must be specified, requested range was " + byteString, e);
        }
    }

    @Override
    public int compareTo(RangeRequest that) {
        return ComparisonChain.start().compare(this.getOffset(), that.getOffset()).compare(this.getEnd(), that.getEnd()).result();
    }

    private RangeRequest merge(RangeRequest other) throws RangeNotSatisfiableException {
        if (other.getOffset() > this.getEnd()) {
            throw new RangeNotSatisfiableException("The provided range is invalid");
        }
        long end = other.getEnd() > this.getEnd() ? other.getEnd() : this.getEnd();
        return new RangeRequest(this.getOffset(), end, this.getContentLength());
    }
}

