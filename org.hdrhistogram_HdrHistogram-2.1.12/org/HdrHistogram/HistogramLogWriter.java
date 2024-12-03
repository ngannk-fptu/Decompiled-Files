/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.HdrHistogram.Base64Helper;
import org.HdrHistogram.EncodableHistogram;

public class HistogramLogWriter {
    private static final String HISTOGRAM_LOG_FORMAT_VERSION = "1.3";
    private static Pattern containsDelimeterPattern = Pattern.compile(".[, \\r\\n].");
    private Matcher containsDelimeterMatcher = containsDelimeterPattern.matcher("");
    private final PrintStream log;
    private ByteBuffer targetBuffer;
    private long baseTime = 0L;

    public HistogramLogWriter(String outputFileName) throws FileNotFoundException {
        this.log = new PrintStream(outputFileName);
    }

    public HistogramLogWriter(File outputFile) throws FileNotFoundException {
        this.log = new PrintStream(outputFile);
    }

    public HistogramLogWriter(OutputStream outputStream) {
        this.log = new PrintStream(outputStream);
    }

    public HistogramLogWriter(PrintStream printStream) {
        this.log = printStream;
    }

    public void close() {
        this.log.close();
    }

    public synchronized void outputIntervalHistogram(double startTimeStampSec, double endTimeStampSec, EncodableHistogram histogram, double maxValueUnitRatio) {
        if (this.targetBuffer == null || this.targetBuffer.capacity() < histogram.getNeededByteBufferCapacity()) {
            this.targetBuffer = ByteBuffer.allocate(histogram.getNeededByteBufferCapacity()).order(ByteOrder.BIG_ENDIAN);
        }
        this.targetBuffer.clear();
        int compressedLength = histogram.encodeIntoCompressedByteBuffer(this.targetBuffer, 9);
        byte[] compressedArray = Arrays.copyOf(this.targetBuffer.array(), compressedLength);
        String tag = histogram.getTag();
        if (tag == null) {
            this.log.format(Locale.US, "%.3f,%.3f,%.3f,%s\n", startTimeStampSec, endTimeStampSec - startTimeStampSec, histogram.getMaxValueAsDouble() / maxValueUnitRatio, Base64Helper.printBase64Binary(compressedArray));
        } else {
            this.containsDelimeterMatcher.reset(tag);
            if (this.containsDelimeterMatcher.matches()) {
                throw new IllegalArgumentException("Tag string cannot contain commas, spaces, or line breaks");
            }
            this.log.format(Locale.US, "Tag=%s,%.3f,%.3f,%.3f,%s\n", tag, startTimeStampSec, endTimeStampSec - startTimeStampSec, histogram.getMaxValueAsDouble() / maxValueUnitRatio, Base64Helper.printBase64Binary(compressedArray));
        }
    }

    public void outputIntervalHistogram(double startTimeStampSec, double endTimeStampSec, EncodableHistogram histogram) {
        this.outputIntervalHistogram(startTimeStampSec, endTimeStampSec, histogram, 1000000.0);
    }

    public void outputIntervalHistogram(EncodableHistogram histogram) {
        this.outputIntervalHistogram((double)(histogram.getStartTimeStamp() - this.baseTime) / 1000.0, (double)(histogram.getEndTimeStamp() - this.baseTime) / 1000.0, histogram);
    }

    public void outputStartTime(long startTimeMsec) {
        this.log.format(Locale.US, "#[StartTime: %.3f (seconds since epoch), %s]\n", (double)startTimeMsec / 1000.0, new Date(startTimeMsec).toString());
    }

    public void outputBaseTime(long baseTimeMsec) {
        this.log.format(Locale.US, "#[BaseTime: %.3f (seconds since epoch)]\n", (double)baseTimeMsec / 1000.0);
    }

    public void outputComment(String comment) {
        this.log.format("#%s\n", comment);
    }

    public void outputLegend() {
        this.log.println("\"StartTimestamp\",\"Interval_Length\",\"Interval_Max\",\"Interval_Compressed_Histogram\"");
    }

    public void outputLogFormatVersion() {
        this.outputComment("[Histogram log format version 1.3]");
    }

    public void setBaseTime(long baseTimeMsec) {
        this.baseTime = baseTimeMsec;
    }

    public long getBaseTime() {
        return this.baseTime;
    }
}

