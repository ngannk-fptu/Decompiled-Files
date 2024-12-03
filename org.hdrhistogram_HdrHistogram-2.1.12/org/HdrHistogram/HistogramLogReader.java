/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.zip.DataFormatException;
import org.HdrHistogram.EncodableHistogram;
import org.HdrHistogram.HistogramLogScanner;

public class HistogramLogReader
implements Closeable {
    private final HistogramLogScanner scanner;
    private final HistogramLogScanner.EventHandler handler = new HistogramLogScanner.EventHandler(){

        @Override
        public boolean onComment(String comment) {
            return false;
        }

        @Override
        public boolean onBaseTime(double secondsSinceEpoch) {
            HistogramLogReader.this.baseTimeSec = secondsSinceEpoch;
            HistogramLogReader.this.observedBaseTime = true;
            return false;
        }

        @Override
        public boolean onStartTime(double secondsSinceEpoch) {
            HistogramLogReader.this.startTimeSec = secondsSinceEpoch;
            HistogramLogReader.this.observedStartTime = true;
            return false;
        }

        @Override
        public boolean onHistogram(String tag, double timestamp, double length, HistogramLogScanner.EncodableHistogramSupplier lazyReader) {
            EncodableHistogram histogram;
            double startTimeStampToCheckRangeOn;
            double logTimeStampInSec = timestamp;
            if (!HistogramLogReader.this.observedStartTime) {
                HistogramLogReader.this.startTimeSec = logTimeStampInSec;
                HistogramLogReader.this.observedStartTime = true;
            }
            if (!HistogramLogReader.this.observedBaseTime) {
                if (logTimeStampInSec < HistogramLogReader.this.startTimeSec - 3.1536E7) {
                    HistogramLogReader.this.baseTimeSec = HistogramLogReader.this.startTimeSec;
                } else {
                    HistogramLogReader.this.baseTimeSec = 0.0;
                }
                HistogramLogReader.this.observedBaseTime = true;
            }
            double absoluteStartTimeStampSec = logTimeStampInSec + HistogramLogReader.this.baseTimeSec;
            double offsetStartTimeStampSec = absoluteStartTimeStampSec - HistogramLogReader.this.startTimeSec;
            double intervalLengthSec = length;
            double absoluteEndTimeStampSec = absoluteStartTimeStampSec + intervalLengthSec;
            double d = startTimeStampToCheckRangeOn = HistogramLogReader.this.absolute ? absoluteStartTimeStampSec : offsetStartTimeStampSec;
            if (startTimeStampToCheckRangeOn < HistogramLogReader.this.rangeStartTimeSec) {
                return false;
            }
            if (startTimeStampToCheckRangeOn > HistogramLogReader.this.rangeEndTimeSec) {
                return true;
            }
            try {
                histogram = lazyReader.read();
            }
            catch (DataFormatException e) {
                return true;
            }
            histogram.setStartTimeStamp((long)(absoluteStartTimeStampSec * 1000.0));
            histogram.setEndTimeStamp((long)(absoluteEndTimeStampSec * 1000.0));
            histogram.setTag(tag);
            HistogramLogReader.this.nextHistogram = histogram;
            return true;
        }

        @Override
        public boolean onException(Throwable t) {
            if (t instanceof NoSuchElementException) {
                return true;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            throw new RuntimeException(t);
        }
    };
    private double startTimeSec = 0.0;
    private boolean observedStartTime = false;
    private double baseTimeSec = 0.0;
    private boolean observedBaseTime = false;
    private boolean absolute;
    private double rangeStartTimeSec;
    private double rangeEndTimeSec;
    private EncodableHistogram nextHistogram;

    public HistogramLogReader(String inputFileName) throws FileNotFoundException {
        this.scanner = new HistogramLogScanner(new File(inputFileName));
    }

    public HistogramLogReader(InputStream inputStream) {
        this.scanner = new HistogramLogScanner(inputStream);
    }

    public HistogramLogReader(File inputFile) throws FileNotFoundException {
        this.scanner = new HistogramLogScanner(inputFile);
    }

    public double getStartTimeSec() {
        return this.startTimeSec;
    }

    public EncodableHistogram nextIntervalHistogram(double startTimeSec, double endTimeSec) {
        return this.nextIntervalHistogram(startTimeSec, endTimeSec, false);
    }

    public EncodableHistogram nextAbsoluteIntervalHistogram(double absoluteStartTimeSec, double absoluteEndTimeSec) {
        return this.nextIntervalHistogram(absoluteStartTimeSec, absoluteEndTimeSec, true);
    }

    public EncodableHistogram nextIntervalHistogram() {
        return this.nextIntervalHistogram(0.0, 9.223372036854776E18, true);
    }

    private EncodableHistogram nextIntervalHistogram(double rangeStartTimeSec, double rangeEndTimeSec, boolean absolute) {
        this.rangeStartTimeSec = rangeStartTimeSec;
        this.rangeEndTimeSec = rangeEndTimeSec;
        this.absolute = absolute;
        this.scanner.process(this.handler);
        EncodableHistogram histogram = this.nextHistogram;
        this.nextHistogram = null;
        return histogram;
    }

    public boolean hasNext() {
        return this.scanner.hasNextLine();
    }

    @Override
    public void close() {
        this.scanner.close();
    }
}

