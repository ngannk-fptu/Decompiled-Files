/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeSet;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.EncodableHistogram;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogReader;

public class HistogramLogProcessor
extends Thread {
    static final String versionString = "Histogram Log Processor version 2.1.12";
    private final HistogramLogProcessorConfiguration config;
    private HistogramLogReader logReader;
    private int lineNumber = 0;

    private void outputTimeRange(PrintStream log, String title) {
        log.format(Locale.US, "#[%s between %.3f and", title, this.config.rangeStartTimeSec);
        if (this.config.rangeEndTimeSec < Double.MAX_VALUE) {
            log.format(" %.3f", this.config.rangeEndTimeSec);
        } else {
            log.format(" %s", "<Infinite>");
        }
        log.format(" seconds (relative to StartTime)]\n", new Object[0]);
    }

    private void outputStartTime(PrintStream log, Double startTime) {
        log.format(Locale.US, "#[StartTime: %.3f (seconds since epoch), %s]\n", startTime, new Date((long)(startTime * 1000.0)).toString());
    }

    EncodableHistogram copyCorrectedForCoordinatedOmission(EncodableHistogram inputHistogram) {
        long expectedInterval;
        EncodableHistogram histogram = inputHistogram;
        if (histogram instanceof DoubleHistogram) {
            if (this.config.expectedIntervalForCoordinatedOmissionCorrection > 0.0) {
                histogram = ((DoubleHistogram)histogram).copyCorrectedForCoordinatedOmission(this.config.expectedIntervalForCoordinatedOmissionCorrection);
            }
        } else if (histogram instanceof Histogram && (expectedInterval = (long)this.config.expectedIntervalForCoordinatedOmissionCorrection) > 0L) {
            histogram = ((Histogram)histogram).copyCorrectedForCoordinatedOmission(expectedInterval);
        }
        return histogram;
    }

    private EncodableHistogram getIntervalHistogram() {
        EncodableHistogram histogram = null;
        try {
            histogram = this.logReader.nextIntervalHistogram(this.config.rangeStartTimeSec, this.config.rangeEndTimeSec);
            if (this.config.expectedIntervalForCoordinatedOmissionCorrection > 0.0) {
                histogram = this.copyCorrectedForCoordinatedOmission(histogram);
            }
        }
        catch (RuntimeException ex) {
            System.err.println("Log file parsing error at line number " + this.lineNumber + ": line appears to be malformed.");
            if (this.config.verbose) {
                throw ex;
            }
            System.exit(1);
        }
        ++this.lineNumber;
        return histogram;
    }

    private EncodableHistogram getIntervalHistogram(String tag) {
        EncodableHistogram histogram;
        if (tag == null) {
            while ((histogram = this.getIntervalHistogram()) != null && histogram.getTag() != null) {
            }
        } else {
            while ((histogram = this.getIntervalHistogram()) != null && !tag.equals(histogram.getTag())) {
            }
        }
        return histogram;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        String movingWindowLogFormat;
        String logFormat;
        PrintStream timeIntervalLog = null;
        PrintStream movingWindowLog = null;
        PrintStream histogramPercentileLog = System.out;
        double firstStartTime = 0.0;
        boolean timeIntervalLogLegendWritten = false;
        boolean movingWindowLogLegendWritten = false;
        LinkedList<EncodableHistogram> movingWindowQueue = new LinkedList<EncodableHistogram>();
        if (this.config.listTags) {
            EncodableHistogram histogram;
            TreeSet<String> tags = new TreeSet<String>();
            boolean nullTagFound = false;
            while ((histogram = this.getIntervalHistogram()) != null) {
                String tag = histogram.getTag();
                if (tag != null) {
                    tags.add(histogram.getTag());
                    continue;
                }
                nullTagFound = true;
            }
            System.out.println("Tags found in input file:");
            if (nullTagFound) {
                System.out.println("[NO TAG (default)]");
            }
            for (String tag : tags) {
                System.out.println(tag);
            }
            return;
        }
        if (this.config.logFormatCsv) {
            logFormat = "%.3f,%d,%.3f,%.3f,%.3f,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f\n";
            movingWindowLogFormat = "%.3f,%d,%.3f,%.3f\n";
        } else {
            logFormat = "%4.3f: I:%d ( %7.3f %7.3f %7.3f ) T:%d ( %7.3f %7.3f %7.3f %7.3f %7.3f %7.3f )\n";
            movingWindowLogFormat = "%4.3f: I:%d P:%7.3f M:%7.3f\n";
        }
        try {
            EncodableHistogram movingWindowSumHistogram;
            EncodableHistogram intervalHistogram;
            boolean logUsesDoubleHistograms;
            if (this.config.outputFileName != null) {
                try {
                    timeIntervalLog = new PrintStream(new FileOutputStream(this.config.outputFileName), false);
                    this.outputTimeRange(timeIntervalLog, "Interval percentile log");
                }
                catch (FileNotFoundException ex) {
                    System.err.println("Failed to open output file " + this.config.outputFileName);
                }
                String hgrmOutputFileName = this.config.outputFileName + ".hgrm";
                try {
                    histogramPercentileLog = new PrintStream(new FileOutputStream(hgrmOutputFileName), false);
                    this.outputTimeRange(histogramPercentileLog, "Overall percentile distribution");
                }
                catch (FileNotFoundException ex) {
                    System.err.println("Failed to open percentiles histogram output file " + hgrmOutputFileName);
                }
                if (this.config.movingWindow) {
                    String movingWindowOutputFileName = this.config.outputFileName + ".mwp";
                    try {
                        movingWindowLog = new PrintStream(new FileOutputStream(movingWindowOutputFileName), false);
                        this.outputTimeRange(movingWindowLog, "Moving window log for " + this.config.movingWindowPercentileToReport + " percentile");
                    }
                    catch (FileNotFoundException ex) {
                        System.err.println("Failed to open moving window output file " + movingWindowOutputFileName);
                    }
                }
            }
            Histogram accumulatedRegularHistogram = (logUsesDoubleHistograms = (intervalHistogram = this.getIntervalHistogram(this.config.tag)) instanceof DoubleHistogram) ? new Histogram(3) : ((Histogram)intervalHistogram).copy();
            accumulatedRegularHistogram.reset();
            accumulatedRegularHistogram.setAutoResize(true);
            DoubleHistogram accumulatedDoubleHistogram = logUsesDoubleHistograms ? ((DoubleHistogram)intervalHistogram).copy() : new DoubleHistogram(3);
            accumulatedDoubleHistogram.reset();
            accumulatedDoubleHistogram.setAutoResize(true);
            EncodableHistogram encodableHistogram = movingWindowSumHistogram = logUsesDoubleHistograms ? new DoubleHistogram(3) : new Histogram(3);
            while (intervalHistogram != null) {
                if (intervalHistogram instanceof DoubleHistogram) {
                    if (!logUsesDoubleHistograms) {
                        throw new IllegalStateException("Encountered a DoubleHistogram line in a log of Histograms.");
                    }
                    accumulatedDoubleHistogram.add((DoubleHistogram)intervalHistogram);
                } else {
                    if (logUsesDoubleHistograms) {
                        throw new IllegalStateException("Encountered a Histogram line in a log of DoubleHistograms.");
                    }
                    accumulatedRegularHistogram.add((Histogram)intervalHistogram);
                }
                long windowCutOffTimeStamp = intervalHistogram.getEndTimeStamp() - this.config.movingWindowLengthInMsec;
                if (this.config.movingWindow) {
                    EncodableHistogram head;
                    if (movingWindowSumHistogram instanceof DoubleHistogram && intervalHistogram instanceof DoubleHistogram) {
                        ((DoubleHistogram)movingWindowSumHistogram).add((DoubleHistogram)intervalHistogram);
                    } else if (movingWindowSumHistogram instanceof Histogram && intervalHistogram instanceof Histogram) {
                        ((Histogram)movingWindowSumHistogram).add((Histogram)intervalHistogram);
                    }
                    while ((head = (EncodableHistogram)movingWindowQueue.peek()) != null && head.getEndTimeStamp() <= windowCutOffTimeStamp) {
                        EncodableHistogram prevHist = (EncodableHistogram)movingWindowQueue.remove();
                        if (movingWindowSumHistogram instanceof DoubleHistogram) {
                            if (prevHist == null) continue;
                            ((DoubleHistogram)movingWindowSumHistogram).subtract((DoubleHistogram)prevHist);
                            continue;
                        }
                        if (!(movingWindowSumHistogram instanceof Histogram) || prevHist == null) continue;
                        ((Histogram)movingWindowSumHistogram).subtract((Histogram)prevHist);
                    }
                    movingWindowQueue.add(intervalHistogram);
                }
                if (firstStartTime == 0.0 && this.logReader.getStartTimeSec() != 0.0) {
                    firstStartTime = this.logReader.getStartTimeSec();
                    this.outputStartTime(histogramPercentileLog, firstStartTime);
                    if (timeIntervalLog != null) {
                        this.outputStartTime(timeIntervalLog, firstStartTime);
                    }
                }
                if (timeIntervalLog != null) {
                    if (!timeIntervalLogLegendWritten) {
                        timeIntervalLogLegendWritten = true;
                        if (this.config.logFormatCsv) {
                            timeIntervalLog.println("\"Timestamp\",\"Int_Count\",\"Int_50%\",\"Int_90%\",\"Int_Max\",\"Total_Count\",\"Total_50%\",\"Total_90%\",\"Total_99%\",\"Total_99.9%\",\"Total_99.99%\",\"Total_Max\"");
                        } else {
                            timeIntervalLog.println("Time: IntervalPercentiles:count ( 50% 90% Max ) TotalPercentiles:count ( 50% 90% 99% 99.9% 99.99% Max )");
                        }
                    }
                    if (logUsesDoubleHistograms) {
                        timeIntervalLog.format(Locale.US, logFormat, (double)intervalHistogram.getEndTimeStamp() / 1000.0 - this.logReader.getStartTimeSec(), ((DoubleHistogram)intervalHistogram).getTotalCount(), ((DoubleHistogram)intervalHistogram).getValueAtPercentile(50.0) / this.config.outputValueUnitRatio, ((DoubleHistogram)intervalHistogram).getValueAtPercentile(90.0) / this.config.outputValueUnitRatio, ((DoubleHistogram)intervalHistogram).getMaxValue() / this.config.outputValueUnitRatio, accumulatedDoubleHistogram.getTotalCount(), accumulatedDoubleHistogram.getValueAtPercentile(50.0) / this.config.outputValueUnitRatio, accumulatedDoubleHistogram.getValueAtPercentile(90.0) / this.config.outputValueUnitRatio, accumulatedDoubleHistogram.getValueAtPercentile(99.0) / this.config.outputValueUnitRatio, accumulatedDoubleHistogram.getValueAtPercentile(99.9) / this.config.outputValueUnitRatio, accumulatedDoubleHistogram.getValueAtPercentile(99.99) / this.config.outputValueUnitRatio, accumulatedDoubleHistogram.getMaxValue() / this.config.outputValueUnitRatio);
                    } else {
                        timeIntervalLog.format(Locale.US, logFormat, (double)intervalHistogram.getEndTimeStamp() / 1000.0 - this.logReader.getStartTimeSec(), ((Histogram)intervalHistogram).getTotalCount(), (double)((Histogram)intervalHistogram).getValueAtPercentile(50.0) / this.config.outputValueUnitRatio, (double)((Histogram)intervalHistogram).getValueAtPercentile(90.0) / this.config.outputValueUnitRatio, (double)((Histogram)intervalHistogram).getMaxValue() / this.config.outputValueUnitRatio, accumulatedRegularHistogram.getTotalCount(), (double)accumulatedRegularHistogram.getValueAtPercentile(50.0) / this.config.outputValueUnitRatio, (double)accumulatedRegularHistogram.getValueAtPercentile(90.0) / this.config.outputValueUnitRatio, (double)accumulatedRegularHistogram.getValueAtPercentile(99.0) / this.config.outputValueUnitRatio, (double)accumulatedRegularHistogram.getValueAtPercentile(99.9) / this.config.outputValueUnitRatio, (double)accumulatedRegularHistogram.getValueAtPercentile(99.99) / this.config.outputValueUnitRatio, (double)accumulatedRegularHistogram.getMaxValue() / this.config.outputValueUnitRatio);
                    }
                }
                if (movingWindowLog != null) {
                    if (!movingWindowLogLegendWritten) {
                        movingWindowLogLegendWritten = true;
                        if (this.config.logFormatCsv) {
                            movingWindowLog.println("\"Timestamp\",\"Window_Count\",\"" + this.config.movingWindowPercentileToReport + "%'ile\",\"Max\"");
                        } else {
                            movingWindowLog.println("Time: WindoCount " + this.config.movingWindowPercentileToReport + "%'ile Max");
                        }
                    }
                    if (intervalHistogram instanceof DoubleHistogram) {
                        movingWindowLog.format(Locale.US, movingWindowLogFormat, (double)intervalHistogram.getEndTimeStamp() / 1000.0 - this.logReader.getStartTimeSec(), ((DoubleHistogram)movingWindowSumHistogram).getTotalCount(), ((DoubleHistogram)movingWindowSumHistogram).getValueAtPercentile(this.config.movingWindowPercentileToReport) / this.config.outputValueUnitRatio, ((DoubleHistogram)movingWindowSumHistogram).getMaxValue() / this.config.outputValueUnitRatio);
                    } else {
                        movingWindowLog.format(Locale.US, movingWindowLogFormat, (double)intervalHistogram.getEndTimeStamp() / 1000.0 - this.logReader.getStartTimeSec(), ((Histogram)movingWindowSumHistogram).getTotalCount(), (double)((Histogram)movingWindowSumHistogram).getValueAtPercentile(this.config.movingWindowPercentileToReport) / this.config.outputValueUnitRatio, (double)((Histogram)movingWindowSumHistogram).getMaxValue() / this.config.outputValueUnitRatio);
                    }
                }
                intervalHistogram = this.getIntervalHistogram(this.config.tag);
            }
            if (logUsesDoubleHistograms) {
                accumulatedDoubleHistogram.outputPercentileDistribution(histogramPercentileLog, this.config.percentilesOutputTicksPerHalf, this.config.outputValueUnitRatio, this.config.logFormatCsv);
            } else {
                accumulatedRegularHistogram.outputPercentileDistribution(histogramPercentileLog, this.config.percentilesOutputTicksPerHalf, this.config.outputValueUnitRatio, this.config.logFormatCsv);
            }
        }
        finally {
            if (timeIntervalLog != null) {
                timeIntervalLog.close();
            }
            if (movingWindowLog != null) {
                movingWindowLog.close();
            }
            if (histogramPercentileLog != System.out) {
                histogramPercentileLog.close();
            }
        }
    }

    public HistogramLogProcessor(String[] args) throws FileNotFoundException {
        this.setName("HistogramLogProcessor");
        this.config = new HistogramLogProcessorConfiguration(args);
        this.logReader = this.config.inputFileName != null ? new HistogramLogReader(this.config.inputFileName) : new HistogramLogReader(System.in);
    }

    public static void main(String[] args) {
        try {
            HistogramLogProcessor processor = new HistogramLogProcessor(args);
            processor.start();
        }
        catch (FileNotFoundException ex) {
            System.err.println("failed to open input file.");
        }
    }

    private static class HistogramLogProcessorConfiguration {
        boolean verbose = false;
        String outputFileName = null;
        String inputFileName = null;
        String tag = null;
        double rangeStartTimeSec = 0.0;
        double rangeEndTimeSec = Double.MAX_VALUE;
        boolean logFormatCsv = false;
        boolean listTags = false;
        boolean allTags = false;
        boolean movingWindow = false;
        double movingWindowPercentileToReport = 99.0;
        long movingWindowLengthInMsec = 60000L;
        int percentilesOutputTicksPerHalf = 5;
        Double outputValueUnitRatio = 1000000.0;
        double expectedIntervalForCoordinatedOmissionCorrection = 0.0;
        String errorMessage = "";

        HistogramLogProcessorConfiguration(String[] args) {
            boolean askedForHelp = false;
            try {
                for (int i = 0; i < args.length; ++i) {
                    if (args[i].equals("-csv")) {
                        this.logFormatCsv = true;
                        continue;
                    }
                    if (args[i].equals("-v")) {
                        this.verbose = true;
                        continue;
                    }
                    if (args[i].equals("-listtags")) {
                        this.listTags = true;
                        continue;
                    }
                    if (args[i].equals("-alltags")) {
                        this.allTags = true;
                        continue;
                    }
                    if (args[i].equals("-i")) {
                        this.inputFileName = args[++i];
                        continue;
                    }
                    if (args[i].equals("-tag")) {
                        this.tag = args[++i];
                        continue;
                    }
                    if (args[i].equals("-mwp")) {
                        this.movingWindowPercentileToReport = Double.parseDouble(args[++i]);
                        this.movingWindow = true;
                        continue;
                    }
                    if (args[i].equals("-mwpl")) {
                        this.movingWindowLengthInMsec = Long.parseLong(args[++i]);
                        this.movingWindow = true;
                        continue;
                    }
                    if (args[i].equals("-start")) {
                        this.rangeStartTimeSec = Double.parseDouble(args[++i]);
                        continue;
                    }
                    if (args[i].equals("-end")) {
                        this.rangeEndTimeSec = Double.parseDouble(args[++i]);
                        continue;
                    }
                    if (args[i].equals("-o")) {
                        this.outputFileName = args[++i];
                        continue;
                    }
                    if (args[i].equals("-percentilesOutputTicksPerHalf")) {
                        this.percentilesOutputTicksPerHalf = Integer.parseInt(args[++i]);
                        continue;
                    }
                    if (args[i].equals("-outputValueUnitRatio")) {
                        this.outputValueUnitRatio = Double.parseDouble(args[++i]);
                        continue;
                    }
                    if (args[i].equals("-correctLogWithKnownCoordinatedOmission")) {
                        this.expectedIntervalForCoordinatedOmissionCorrection = Double.parseDouble(args[++i]);
                        continue;
                    }
                    if (args[i].equals("-h")) {
                        askedForHelp = true;
                        throw new Exception("Help: " + args[i]);
                    }
                    throw new Exception("Invalid args: " + args[i]);
                }
            }
            catch (Exception e) {
                this.errorMessage = "Error: Histogram Log Processor version 2.1.12 launched with the following args:\n";
                for (String arg : args) {
                    this.errorMessage = this.errorMessage + arg + " ";
                }
                if (!askedForHelp) {
                    this.errorMessage = this.errorMessage + "\nWhich was parsed as an error, indicated by the following exception:\n" + e;
                    System.err.println(this.errorMessage);
                }
                String validArgs = "\"[-csv] [-v] [-i inputFileName] [-o outputFileName] [-tag tag] [-start rangeStartTimeSec] [-end rangeEndTimeSec] [-outputValueUnitRatio r] [-correctLogWithKnownCoordinatedOmission i] [-listtags]";
                System.err.println("valid arguments = \"[-csv] [-v] [-i inputFileName] [-o outputFileName] [-tag tag] [-start rangeStartTimeSec] [-end rangeEndTimeSec] [-outputValueUnitRatio r] [-correctLogWithKnownCoordinatedOmission i] [-listtags]");
                System.err.println(" [-h]                                         help\n [-v]                                         Provide verbose error output\n [-csv]                                       Use CSV format for output log files\n [-i logFileName]                             File name of Histogram Log to process (default is standard input)\n [-o outputFileName]                          File name to output to (default is standard output)\n [-tag tag]                                   The tag (default no tag) of the histogram lines to be processed\n [-start rangeStartTimeSec]                   The start time for the range in the file, in seconds (default 0.0)\n [-end rangeEndTimeSec]                       The end time for the range in the file, in seconds (default is infinite)\n [-outputValueUnitRatio r]                    The scaling factor by which to divide histogram recorded values units\n                                              in output. [default = 1000000.0 (1 msec in nsec)]\n [-correctLogWithKnownCoordinatedOmission i]  When the supplied expected interval i is than 0, performs coordinated\n                                              omission corection on the input log's interval histograms by adding\n                                              missing values as appropriate based on the supplied expected interval\n                                              value i (in wahtever units the log histograms were recorded with). This\n                                              feature should only be used when the input log is known to have been\n                                              recorded with coordinated ommisions, and when an expected interval is known.\n [-listtags]                                  list all tags found on histogram lines the input file.");
                System.exit(1);
            }
        }
    }
}

