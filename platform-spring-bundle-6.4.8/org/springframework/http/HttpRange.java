/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class HttpRange {
    private static final int MAX_RANGES = 100;
    private static final String BYTE_RANGE_PREFIX = "bytes=";

    public ResourceRegion toResourceRegion(Resource resource) {
        Assert.isTrue(resource.getClass() != InputStreamResource.class, "Cannot convert an InputStreamResource to a ResourceRegion");
        long contentLength = HttpRange.getLengthFor(resource);
        long start = this.getRangeStart(contentLength);
        long end = this.getRangeEnd(contentLength);
        Assert.isTrue(start < contentLength, () -> "'position' exceeds the resource length " + contentLength);
        return new ResourceRegion(resource, start, end - start + 1L);
    }

    public abstract long getRangeStart(long var1);

    public abstract long getRangeEnd(long var1);

    public static HttpRange createByteRange(long firstBytePos) {
        return new ByteRange(firstBytePos, null);
    }

    public static HttpRange createByteRange(long firstBytePos, long lastBytePos) {
        return new ByteRange(firstBytePos, lastBytePos);
    }

    public static HttpRange createSuffixRange(long suffixLength) {
        return new SuffixByteRange(suffixLength);
    }

    public static List<HttpRange> parseRanges(@Nullable String ranges) {
        if (!StringUtils.hasLength(ranges)) {
            return Collections.emptyList();
        }
        if (!ranges.startsWith(BYTE_RANGE_PREFIX)) {
            throw new IllegalArgumentException("Range '" + ranges + "' does not start with 'bytes='");
        }
        String[] tokens = StringUtils.tokenizeToStringArray(ranges = ranges.substring(BYTE_RANGE_PREFIX.length()), ",");
        if (tokens.length > 100) {
            throw new IllegalArgumentException("Too many ranges: " + tokens.length);
        }
        ArrayList<HttpRange> result = new ArrayList<HttpRange>(tokens.length);
        for (String token : tokens) {
            result.add(HttpRange.parseRange(token));
        }
        return result;
    }

    private static HttpRange parseRange(String range) {
        Assert.hasLength(range, "Range String must not be empty");
        int dashIdx = range.indexOf(45);
        if (dashIdx > 0) {
            long firstPos = Long.parseLong(range.substring(0, dashIdx));
            if (dashIdx < range.length() - 1) {
                Long lastPos = Long.parseLong(range.substring(dashIdx + 1));
                return new ByteRange(firstPos, lastPos);
            }
            return new ByteRange(firstPos, null);
        }
        if (dashIdx == 0) {
            long suffixLength = Long.parseLong(range.substring(1));
            return new SuffixByteRange(suffixLength);
        }
        throw new IllegalArgumentException("Range '" + range + "' does not contain \"-\"");
    }

    public static List<ResourceRegion> toResourceRegions(List<HttpRange> ranges, Resource resource) {
        if (CollectionUtils.isEmpty(ranges)) {
            return Collections.emptyList();
        }
        ArrayList<ResourceRegion> regions = new ArrayList<ResourceRegion>(ranges.size());
        for (HttpRange range : ranges) {
            regions.add(range.toResourceRegion(resource));
        }
        if (ranges.size() > 1) {
            long length = HttpRange.getLengthFor(resource);
            long total = 0L;
            for (ResourceRegion region : regions) {
                total += region.getCount();
            }
            if (total >= length) {
                throw new IllegalArgumentException("The sum of all ranges (" + total + ") should be less than the resource length (" + length + ")");
            }
        }
        return regions;
    }

    private static long getLengthFor(Resource resource) {
        try {
            long contentLength = resource.contentLength();
            Assert.isTrue(contentLength > 0L, "Resource content length should be > 0");
            return contentLength;
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Failed to obtain Resource content length", ex);
        }
    }

    public static String toString(Collection<HttpRange> ranges) {
        Assert.notEmpty(ranges, "Ranges Collection must not be empty");
        StringJoiner builder = new StringJoiner(", ", BYTE_RANGE_PREFIX, "");
        for (HttpRange range : ranges) {
            builder.add(range.toString());
        }
        return builder.toString();
    }

    private static class SuffixByteRange
    extends HttpRange {
        private final long suffixLength;

        public SuffixByteRange(long suffixLength) {
            if (suffixLength < 0L) {
                throw new IllegalArgumentException("Invalid suffix length: " + suffixLength);
            }
            this.suffixLength = suffixLength;
        }

        @Override
        public long getRangeStart(long length) {
            if (this.suffixLength < length) {
                return length - this.suffixLength;
            }
            return 0L;
        }

        @Override
        public long getRangeEnd(long length) {
            return length - 1L;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SuffixByteRange)) {
                return false;
            }
            SuffixByteRange otherRange = (SuffixByteRange)other;
            return this.suffixLength == otherRange.suffixLength;
        }

        public int hashCode() {
            return Long.hashCode(this.suffixLength);
        }

        public String toString() {
            return "-" + this.suffixLength;
        }
    }

    private static class ByteRange
    extends HttpRange {
        private final long firstPos;
        @Nullable
        private final Long lastPos;

        public ByteRange(long firstPos, @Nullable Long lastPos) {
            this.assertPositions(firstPos, lastPos);
            this.firstPos = firstPos;
            this.lastPos = lastPos;
        }

        private void assertPositions(long firstBytePos, @Nullable Long lastBytePos) {
            if (firstBytePos < 0L) {
                throw new IllegalArgumentException("Invalid first byte position: " + firstBytePos);
            }
            if (lastBytePos != null && lastBytePos < firstBytePos) {
                throw new IllegalArgumentException("firstBytePosition=" + firstBytePos + " should be less then or equal to lastBytePosition=" + lastBytePos);
            }
        }

        @Override
        public long getRangeStart(long length) {
            return this.firstPos;
        }

        @Override
        public long getRangeEnd(long length) {
            if (this.lastPos != null && this.lastPos < length) {
                return this.lastPos;
            }
            return length - 1L;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ByteRange)) {
                return false;
            }
            ByteRange otherRange = (ByteRange)other;
            return this.firstPos == otherRange.firstPos && ObjectUtils.nullSafeEquals(this.lastPos, otherRange.lastPos);
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.firstPos) * 31 + ObjectUtils.nullSafeHashCode(this.lastPos);
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.firstPos);
            builder.append('-');
            if (this.lastPos != null) {
                builder.append(this.lastPos);
            }
            return builder.toString();
        }
    }
}

