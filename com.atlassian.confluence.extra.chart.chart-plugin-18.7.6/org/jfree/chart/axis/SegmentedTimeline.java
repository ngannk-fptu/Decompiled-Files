/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.jfree.chart.axis.Timeline;

public class SegmentedTimeline
implements Timeline,
Cloneable,
Serializable {
    private static final long serialVersionUID = 1093779862539903110L;
    public static final long DAY_SEGMENT_SIZE = 86400000L;
    public static final long HOUR_SEGMENT_SIZE = 3600000L;
    public static final long FIFTEEN_MINUTE_SEGMENT_SIZE = 900000L;
    public static final long MINUTE_SEGMENT_SIZE = 60000L;
    public static long FIRST_MONDAY_AFTER_1900;
    public static TimeZone NO_DST_TIME_ZONE;
    public static TimeZone DEFAULT_TIME_ZONE;
    private Calendar workingCalendarNoDST;
    private Calendar workingCalendar = Calendar.getInstance();
    private long segmentSize;
    private int segmentsIncluded;
    private int segmentsExcluded;
    private int groupSegmentCount;
    private long startTime;
    private long segmentsIncludedSize;
    private long segmentsExcludedSize;
    private long segmentsGroupSize;
    private List exceptionSegments = new ArrayList();
    private SegmentedTimeline baseTimeline;
    private boolean adjustForDaylightSaving = false;

    public SegmentedTimeline(long segmentSize, int segmentsIncluded, int segmentsExcluded) {
        this.segmentSize = segmentSize;
        this.segmentsIncluded = segmentsIncluded;
        this.segmentsExcluded = segmentsExcluded;
        this.groupSegmentCount = this.segmentsIncluded + this.segmentsExcluded;
        this.segmentsIncludedSize = (long)this.segmentsIncluded * this.segmentSize;
        this.segmentsExcludedSize = (long)this.segmentsExcluded * this.segmentSize;
        this.segmentsGroupSize = this.segmentsIncludedSize + this.segmentsExcludedSize;
        int offset = TimeZone.getDefault().getRawOffset();
        SimpleTimeZone z = new SimpleTimeZone(offset, "UTC-" + offset);
        this.workingCalendarNoDST = new GregorianCalendar(z, Locale.getDefault());
    }

    public static long firstMondayAfter1900() {
        int offset = TimeZone.getDefault().getRawOffset();
        SimpleTimeZone z = new SimpleTimeZone(offset, "UTC-" + offset);
        GregorianCalendar cal = new GregorianCalendar(z);
        cal.set(1900, 0, 1, 0, 0, 0);
        cal.set(14, 0);
        while (cal.get(7) != 2) {
            ((Calendar)cal).add(5, 1);
        }
        return cal.getTime().getTime();
    }

    public static SegmentedTimeline newMondayThroughFridayTimeline() {
        SegmentedTimeline timeline = new SegmentedTimeline(86400000L, 5, 2);
        timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900());
        return timeline;
    }

    public static SegmentedTimeline newFifteenMinuteTimeline() {
        SegmentedTimeline timeline = new SegmentedTimeline(900000L, 28, 68);
        timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900() + 36L * timeline.getSegmentSize());
        timeline.setBaseTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        return timeline;
    }

    public boolean getAdjustForDaylightSaving() {
        return this.adjustForDaylightSaving;
    }

    public void setAdjustForDaylightSaving(boolean adjust) {
        this.adjustForDaylightSaving = adjust;
    }

    public void setStartTime(long millisecond) {
        this.startTime = millisecond;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public int getSegmentsExcluded() {
        return this.segmentsExcluded;
    }

    public long getSegmentsExcludedSize() {
        return this.segmentsExcludedSize;
    }

    public int getGroupSegmentCount() {
        return this.groupSegmentCount;
    }

    public long getSegmentsGroupSize() {
        return this.segmentsGroupSize;
    }

    public int getSegmentsIncluded() {
        return this.segmentsIncluded;
    }

    public long getSegmentsIncludedSize() {
        return this.segmentsIncludedSize;
    }

    public long getSegmentSize() {
        return this.segmentSize;
    }

    public List getExceptionSegments() {
        return Collections.unmodifiableList(this.exceptionSegments);
    }

    public void setExceptionSegments(List exceptionSegments) {
        this.exceptionSegments = exceptionSegments;
    }

    public SegmentedTimeline getBaseTimeline() {
        return this.baseTimeline;
    }

    public void setBaseTimeline(SegmentedTimeline baseTimeline) {
        if (baseTimeline != null) {
            if (baseTimeline.getSegmentSize() < this.segmentSize) {
                throw new IllegalArgumentException("baseTimeline.getSegmentSize() is smaller than segmentSize");
            }
            if (baseTimeline.getStartTime() > this.startTime) {
                throw new IllegalArgumentException("baseTimeline.getStartTime() is after startTime");
            }
            if (baseTimeline.getSegmentSize() % this.segmentSize != 0L) {
                throw new IllegalArgumentException("baseTimeline.getSegmentSize() is not multiple of segmentSize");
            }
            if ((this.startTime - baseTimeline.getStartTime()) % this.segmentSize != 0L) {
                throw new IllegalArgumentException("baseTimeline is not aligned");
            }
        }
        this.baseTimeline = baseTimeline;
    }

    public long toTimelineValue(long millisecond) {
        long result;
        long rawMilliseconds = millisecond - this.startTime;
        long groupMilliseconds = rawMilliseconds % this.segmentsGroupSize;
        long groupIndex = rawMilliseconds / this.segmentsGroupSize;
        if (groupMilliseconds >= this.segmentsIncludedSize) {
            result = this.toTimelineValue(this.startTime + this.segmentsGroupSize * (groupIndex + 1L));
        } else {
            Segment segment = this.getSegment(millisecond);
            if (segment.inExceptionSegments()) {
                int p;
                while ((p = this.binarySearchExceptionSegments(segment)) >= 0) {
                    millisecond = ((Segment)this.exceptionSegments.get(p)).getSegmentEnd() + 1L;
                    segment = this.getSegment(millisecond);
                }
                result = this.toTimelineValue(millisecond);
            } else {
                long shiftedSegmentedValue = millisecond - this.startTime;
                long x = shiftedSegmentedValue % this.segmentsGroupSize;
                long y = shiftedSegmentedValue / this.segmentsGroupSize;
                long wholeExceptionsBeforeDomainValue = this.getExceptionSegmentCount(this.startTime, millisecond - 1L);
                result = x < this.segmentsIncludedSize ? this.segmentsIncludedSize * y + x - wholeExceptionsBeforeDomainValue * this.segmentSize : this.segmentsIncludedSize * (y + 1L) - wholeExceptionsBeforeDomainValue * this.segmentSize;
            }
        }
        return result;
    }

    public long toTimelineValue(Date date) {
        return this.toTimelineValue(this.getTime(date));
    }

    public long toMillisecond(long timelineValue) {
        Segment result = new Segment(this.startTime + timelineValue + timelineValue / this.segmentsIncludedSize * this.segmentsExcludedSize);
        for (long lastIndex = this.startTime; lastIndex <= result.segmentStart; ++lastIndex) {
            long exceptionSegmentCount;
            while ((exceptionSegmentCount = this.getExceptionSegmentCount(lastIndex, result.millisecond / this.segmentSize * this.segmentSize - 1L)) > 0L) {
                lastIndex = result.segmentStart;
                int i = 0;
                while ((long)i < exceptionSegmentCount) {
                    do {
                        result.inc();
                    } while (result.inExcludeSegments());
                    ++i;
                }
            }
            lastIndex = result.segmentStart;
            while (result.inExceptionSegments() || result.inExcludeSegments()) {
                result.inc();
                lastIndex += this.segmentSize;
            }
        }
        return this.getTimeFromLong(result.millisecond);
    }

    public long getTimeFromLong(long date) {
        long result = date;
        if (this.adjustForDaylightSaving) {
            this.workingCalendarNoDST.setTime(new Date(date));
            this.workingCalendar.set(this.workingCalendarNoDST.get(1), this.workingCalendarNoDST.get(2), this.workingCalendarNoDST.get(5), this.workingCalendarNoDST.get(11), this.workingCalendarNoDST.get(12), this.workingCalendarNoDST.get(13));
            this.workingCalendar.set(14, this.workingCalendarNoDST.get(14));
            result = this.workingCalendar.getTime().getTime();
        }
        return result;
    }

    public boolean containsDomainValue(long millisecond) {
        Segment segment = this.getSegment(millisecond);
        return segment.inIncludeSegments();
    }

    public boolean containsDomainValue(Date date) {
        return this.containsDomainValue(this.getTime(date));
    }

    public boolean containsDomainRange(long domainValueStart, long domainValueEnd) {
        if (domainValueEnd < domainValueStart) {
            throw new IllegalArgumentException("domainValueEnd (" + domainValueEnd + ") < domainValueStart (" + domainValueStart + ")");
        }
        Segment segment = this.getSegment(domainValueStart);
        boolean contains = true;
        do {
            contains = segment.inIncludeSegments();
            if (segment.contains(domainValueEnd)) break;
            segment.inc();
        } while (contains);
        return contains;
    }

    public boolean containsDomainRange(Date dateDomainValueStart, Date dateDomainValueEnd) {
        return this.containsDomainRange(this.getTime(dateDomainValueStart), this.getTime(dateDomainValueEnd));
    }

    public void addException(long millisecond) {
        this.addException(new Segment(millisecond));
    }

    public void addException(long fromDomainValue, long toDomainValue) {
        this.addException(new SegmentRange(fromDomainValue, toDomainValue));
    }

    public void addException(Date exceptionDate) {
        this.addException(this.getTime(exceptionDate));
    }

    public void addExceptions(List exceptionList) {
        Iterator iter = exceptionList.iterator();
        while (iter.hasNext()) {
            this.addException((Date)iter.next());
        }
    }

    private void addException(Segment segment) {
        if (segment.inIncludeSegments()) {
            int p = this.binarySearchExceptionSegments(segment);
            this.exceptionSegments.add(-(p + 1), segment);
        }
    }

    public void addBaseTimelineException(long domainValue) {
        Segment baseSegment = this.baseTimeline.getSegment(domainValue);
        if (baseSegment.inIncludeSegments()) {
            Segment segment = this.getSegment(baseSegment.getSegmentStart());
            while (segment.getSegmentStart() <= baseSegment.getSegmentEnd()) {
                if (segment.inIncludeSegments()) {
                    long toDomainValue;
                    long fromDomainValue = segment.getSegmentStart();
                    do {
                        toDomainValue = segment.getSegmentEnd();
                        segment.inc();
                    } while (segment.inIncludeSegments());
                    this.addException(fromDomainValue, toDomainValue);
                    continue;
                }
                segment.inc();
            }
        }
    }

    public void addBaseTimelineException(Date date) {
        this.addBaseTimelineException(this.getTime(date));
    }

    public void addBaseTimelineExclusions(long fromBaseDomainValue, long toBaseDomainValue) {
        Segment baseSegment = this.baseTimeline.getSegment(fromBaseDomainValue);
        while (baseSegment.getSegmentStart() <= toBaseDomainValue && !baseSegment.inExcludeSegments()) {
            baseSegment.inc();
        }
        while (baseSegment.getSegmentStart() <= toBaseDomainValue) {
            long baseExclusionRangeEnd = baseSegment.getSegmentStart() + (long)this.baseTimeline.getSegmentsExcluded() * this.baseTimeline.getSegmentSize() - 1L;
            Segment segment = this.getSegment(baseSegment.getSegmentStart());
            while (segment.getSegmentStart() <= baseExclusionRangeEnd) {
                if (segment.inIncludeSegments()) {
                    long toDomainValue;
                    long fromDomainValue = segment.getSegmentStart();
                    do {
                        toDomainValue = segment.getSegmentEnd();
                        segment.inc();
                    } while (segment.inIncludeSegments());
                    this.addException(new BaseTimelineSegmentRange(fromDomainValue, toDomainValue));
                    continue;
                }
                segment.inc();
            }
            baseSegment.inc(this.baseTimeline.getGroupSegmentCount());
        }
    }

    public long getExceptionSegmentCount(long fromMillisecond, long toMillisecond) {
        if (toMillisecond < fromMillisecond) {
            return 0L;
        }
        int n = 0;
        Iterator iter = this.exceptionSegments.iterator();
        while (iter.hasNext()) {
            Segment segment = (Segment)iter.next();
            Segment intersection = segment.intersect(fromMillisecond, toMillisecond);
            if (intersection == null) continue;
            n = (int)((long)n + intersection.getSegmentCount());
        }
        return n;
    }

    public Segment getSegment(long millisecond) {
        return new Segment(millisecond);
    }

    public Segment getSegment(Date date) {
        return this.getSegment(this.getTime(date));
    }

    private boolean equals(Object o, Object p) {
        return o == p || o != null && o.equals(p);
    }

    public boolean equals(Object o) {
        if (o instanceof SegmentedTimeline) {
            SegmentedTimeline other = (SegmentedTimeline)o;
            boolean b0 = this.segmentSize == other.getSegmentSize();
            boolean b1 = this.segmentsIncluded == other.getSegmentsIncluded();
            boolean b2 = this.segmentsExcluded == other.getSegmentsExcluded();
            boolean b3 = this.startTime == other.getStartTime();
            boolean b4 = this.equals(this.exceptionSegments, other.getExceptionSegments());
            return b0 && b1 && b2 && b3 && b4;
        }
        return false;
    }

    public int hashCode() {
        int result = 19;
        result = 37 * result + (int)(this.segmentSize ^ this.segmentSize >>> 32);
        result = 37 * result + (int)(this.startTime ^ this.startTime >>> 32);
        return result;
    }

    private int binarySearchExceptionSegments(Segment segment) {
        int low = 0;
        int high = this.exceptionSegments.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            Segment midSegment = (Segment)this.exceptionSegments.get(mid);
            if (segment.contains(midSegment) || midSegment.contains(segment)) {
                return mid;
            }
            if (midSegment.before(segment)) {
                low = mid + 1;
                continue;
            }
            if (midSegment.after(segment)) {
                high = mid - 1;
                continue;
            }
            throw new IllegalStateException("Invalid condition.");
        }
        return -(low + 1);
    }

    public long getTime(Date date) {
        long result = date.getTime();
        if (this.adjustForDaylightSaving) {
            this.workingCalendar.setTime(date);
            this.workingCalendarNoDST.set(this.workingCalendar.get(1), this.workingCalendar.get(2), this.workingCalendar.get(5), this.workingCalendar.get(11), this.workingCalendar.get(12), this.workingCalendar.get(13));
            this.workingCalendarNoDST.set(14, this.workingCalendar.get(14));
            Date revisedDate = this.workingCalendarNoDST.getTime();
            result = revisedDate.getTime();
        }
        return result;
    }

    public Date getDate(long value) {
        this.workingCalendarNoDST.setTime(new Date(value));
        return this.workingCalendarNoDST.getTime();
    }

    public Object clone() throws CloneNotSupportedException {
        SegmentedTimeline clone = (SegmentedTimeline)super.clone();
        return clone;
    }

    static {
        DEFAULT_TIME_ZONE = TimeZone.getDefault();
        int offset = TimeZone.getDefault().getRawOffset();
        NO_DST_TIME_ZONE = new SimpleTimeZone(offset, "UTC-" + offset);
        GregorianCalendar cal = new GregorianCalendar(NO_DST_TIME_ZONE);
        cal.set(1900, 0, 1, 0, 0, 0);
        cal.set(14, 0);
        while (cal.get(7) != 2) {
            ((Calendar)cal).add(5, 1);
        }
        FIRST_MONDAY_AFTER_1900 = cal.getTime().getTime();
    }

    protected class BaseTimelineSegmentRange
    extends SegmentRange {
        public BaseTimelineSegmentRange(long fromDomainValue, long toDomainValue) {
            super(fromDomainValue, toDomainValue);
        }
    }

    protected class SegmentRange
    extends Segment {
        private long segmentCount;

        public SegmentRange(long fromMillisecond, long toMillisecond) {
            Segment start = SegmentedTimeline.this.getSegment(fromMillisecond);
            Segment end = SegmentedTimeline.this.getSegment(toMillisecond);
            this.millisecond = fromMillisecond;
            this.segmentNumber = this.calculateSegmentNumber(fromMillisecond);
            this.segmentStart = start.segmentStart;
            this.segmentEnd = end.segmentEnd;
            this.segmentCount = end.getSegmentNumber() - start.getSegmentNumber() + 1L;
        }

        public long getSegmentCount() {
            return this.segmentCount;
        }

        public Segment intersect(long from, long to) {
            long end;
            long start = Math.max(from, this.segmentStart);
            if (start <= (end = Math.min(to, this.segmentEnd))) {
                return new SegmentRange(start, end);
            }
            return null;
        }

        public boolean inIncludeSegments() {
            Segment segment = SegmentedTimeline.this.getSegment(this.segmentStart);
            while (segment.getSegmentStart() < this.segmentEnd) {
                if (!segment.inIncludeSegments()) {
                    return false;
                }
                segment.inc();
            }
            return true;
        }

        public boolean inExcludeSegments() {
            Segment segment = SegmentedTimeline.this.getSegment(this.segmentStart);
            while (segment.getSegmentStart() < this.segmentEnd) {
                if (!segment.inExceptionSegments()) {
                    return false;
                }
                segment.inc();
            }
            return true;
        }

        public void inc(long n) {
            throw new IllegalArgumentException("Not implemented in SegmentRange");
        }
    }

    public class Segment
    implements Comparable,
    Cloneable,
    Serializable {
        protected long segmentNumber;
        protected long segmentStart;
        protected long segmentEnd;
        protected long millisecond;

        protected Segment() {
        }

        protected Segment(long millisecond) {
            this.segmentNumber = this.calculateSegmentNumber(millisecond);
            this.segmentStart = SegmentedTimeline.this.startTime + this.segmentNumber * SegmentedTimeline.this.segmentSize;
            this.segmentEnd = this.segmentStart + SegmentedTimeline.this.segmentSize - 1L;
            this.millisecond = millisecond;
        }

        public long calculateSegmentNumber(long millis) {
            if (millis >= SegmentedTimeline.this.startTime) {
                return (millis - SegmentedTimeline.this.startTime) / SegmentedTimeline.this.segmentSize;
            }
            return (millis - SegmentedTimeline.this.startTime) / SegmentedTimeline.this.segmentSize - 1L;
        }

        public long getSegmentNumber() {
            return this.segmentNumber;
        }

        public long getSegmentCount() {
            return 1L;
        }

        public long getSegmentStart() {
            return this.segmentStart;
        }

        public long getSegmentEnd() {
            return this.segmentEnd;
        }

        public long getMillisecond() {
            return this.millisecond;
        }

        public Date getDate() {
            return SegmentedTimeline.this.getDate(this.millisecond);
        }

        public boolean contains(long millis) {
            return this.segmentStart <= millis && millis <= this.segmentEnd;
        }

        public boolean contains(long from, long to) {
            return this.segmentStart <= from && to <= this.segmentEnd;
        }

        public boolean contains(Segment segment) {
            return this.contains(segment.getSegmentStart(), segment.getSegmentEnd());
        }

        public boolean contained(long from, long to) {
            return from <= this.segmentStart && this.segmentEnd <= to;
        }

        public Segment intersect(long from, long to) {
            if (from <= this.segmentStart && this.segmentEnd <= to) {
                return this;
            }
            return null;
        }

        public boolean before(Segment other) {
            return this.segmentEnd < other.getSegmentStart();
        }

        public boolean after(Segment other) {
            return this.segmentStart > other.getSegmentEnd();
        }

        public boolean equals(Object object) {
            if (object instanceof Segment) {
                Segment other = (Segment)object;
                return this.segmentNumber == other.getSegmentNumber() && this.segmentStart == other.getSegmentStart() && this.segmentEnd == other.getSegmentEnd() && this.millisecond == other.getMillisecond();
            }
            return false;
        }

        public Segment copy() {
            try {
                return (Segment)this.clone();
            }
            catch (CloneNotSupportedException e) {
                return null;
            }
        }

        public int compareTo(Object object) {
            Segment other = (Segment)object;
            if (this.before(other)) {
                return -1;
            }
            if (this.after(other)) {
                return 1;
            }
            return 0;
        }

        public boolean inIncludeSegments() {
            if (this.getSegmentNumberRelativeToGroup() < (long)SegmentedTimeline.this.segmentsIncluded) {
                return !this.inExceptionSegments();
            }
            return false;
        }

        public boolean inExcludeSegments() {
            return this.getSegmentNumberRelativeToGroup() >= (long)SegmentedTimeline.this.segmentsIncluded;
        }

        private long getSegmentNumberRelativeToGroup() {
            long p = this.segmentNumber % (long)SegmentedTimeline.this.groupSegmentCount;
            if (p < 0L) {
                p += (long)SegmentedTimeline.this.groupSegmentCount;
            }
            return p;
        }

        public boolean inExceptionSegments() {
            return SegmentedTimeline.this.binarySearchExceptionSegments(this) >= 0;
        }

        public void inc(long n) {
            this.segmentNumber += n;
            long m = n * SegmentedTimeline.this.segmentSize;
            this.segmentStart += m;
            this.segmentEnd += m;
            this.millisecond += m;
        }

        public void inc() {
            this.inc(1L);
        }

        public void dec(long n) {
            this.segmentNumber -= n;
            long m = n * SegmentedTimeline.this.segmentSize;
            this.segmentStart -= m;
            this.segmentEnd -= m;
            this.millisecond -= m;
        }

        public void dec() {
            this.dec(1L);
        }

        public void moveIndexToStart() {
            this.millisecond = this.segmentStart;
        }

        public void moveIndexToEnd() {
            this.millisecond = this.segmentEnd;
        }
    }
}

