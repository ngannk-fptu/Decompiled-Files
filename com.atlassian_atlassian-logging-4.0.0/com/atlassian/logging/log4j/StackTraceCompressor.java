/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.logging.log4j;

import com.atlassian.logging.log4j.NewLineSupport;
import com.atlassian.logging.log4j.SplitValueParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class StackTraceCompressor {
    private static final Pattern CALL_SITE = Pattern.compile("(\\(.*:[0-9]+\\)) \\[.*\\]");
    private final int minimumLines;
    private final boolean showEludedSummary;
    private final Set<String> filteredFrames;
    private final Set<String> filterEveryThingAfterFrames;
    private final String filterEveryThingAfterMessage;
    private final Set<String> markerAtFrames;
    private final String markerAtMessage;
    private final String filterReplacementToken;

    private StackTraceCompressor(Builder builder) {
        this.minimumLines = builder.minimumLines;
        this.showEludedSummary = builder.showEludedSummary;
        this.filteredFrames = this.parseFrames(builder.filteredFrames);
        this.filterEveryThingAfterFrames = this.parseFrames(builder.filterEveryThingAfterFrames);
        this.filterEveryThingAfterMessage = (String)StringUtils.defaultIfBlank((CharSequence)builder.filterEveryThingAfterMessage, (CharSequence)"\t\t(The rest of the stack trace has been filtered ...)");
        this.markerAtFrames = this.parseFrames(builder.markerAtFrames);
        this.markerAtMessage = (String)StringUtils.defaultIfBlank((CharSequence)builder.markerAtMessage, (CharSequence)NewLineSupport.NL);
        this.filterReplacementToken = (String)StringUtils.defaultIfBlank((CharSequence)builder.filterReplacementToken, (CharSequence)"... ");
    }

    private Set<String> parseFrames(String frameSpec) {
        return Collections.unmodifiableSet(new SplitValueParser(",", "at ").parse(frameSpec));
    }

    public static Builder defaultBuilder(int minimumLines, boolean showEludedSummary) {
        return new Builder(minimumLines, showEludedSummary);
    }

    public int getMinimumLines() {
        return this.minimumLines;
    }

    public boolean isShowEludedSummary() {
        return this.showEludedSummary;
    }

    public Set<String> getFilteredFrames() {
        return this.filteredFrames;
    }

    public Set<String> getFilterEveryThingAfterFrames() {
        return this.filterEveryThingAfterFrames;
    }

    public String getFilterEveryThingAfterMessage() {
        return this.filterEveryThingAfterMessage;
    }

    public Set<String> getMarkerAtFrames() {
        return this.markerAtFrames;
    }

    public String getMarkerAtMessage() {
        return this.markerAtMessage;
    }

    public String getFilterReplacementToken() {
        return this.filterReplacementToken;
    }

    public void filterStackTrace(StringBuffer buffer, String[] stackTraceLines) {
        int lineCount = 0;
        int filteredCount = 0;
        boolean ignoreLinesUntilEnd = false;
        boolean markerDue = false;
        ArrayList<String> eludedLineSummary = new ArrayList<String>(stackTraceLines.length);
        for (lineCount = this.outputMinimumLines(buffer, stackTraceLines, lineCount, this.getMinimumLines()); lineCount < stackTraceLines.length; ++lineCount) {
            String summary;
            String stackTraceLine = stackTraceLines[lineCount];
            boolean filteredLine = false;
            if (this.causedBy(stackTraceLine)) {
                this.appendSkipIndicators(buffer, filteredCount, eludedLineSummary);
                lineCount = this.outputMinimumLines(buffer, stackTraceLines, lineCount, this.getMinimumLines()) - 1;
                ignoreLinesUntilEnd = false;
                continue;
            }
            if (this.lineMatchesPattern(stackTraceLine, this.getFilterEveryThingAfterFrames())) {
                this.appendSkipIndicators(buffer, filteredCount, eludedLineSummary);
                buffer.append(this.getFilterEveryThingAfterMessage());
                filteredCount = 0;
                ignoreLinesUntilEnd = true;
            } else if (this.lineMatchesPattern(stackTraceLine, this.getFilteredFrames())) {
                ++filteredCount;
                filteredLine = true;
            }
            if (ignoreLinesUntilEnd) {
                filteredLine = true;
            }
            if (!filteredLine) {
                this.appendSkipIndicators(buffer, filteredCount, eludedLineSummary);
                if (markerDue) {
                    buffer.append(this.getMarkerAtMessage()).append(NewLineSupport.NL);
                    markerDue = false;
                }
                buffer.append(stackTraceLine);
                if (this.lineMatchesPattern(stackTraceLine, this.getMarkerAtFrames())) {
                    markerDue = true;
                }
                filteredCount = 0;
                continue;
            }
            if (!this.isShowEludedSummary() || !StringUtils.isNotBlank((CharSequence)(summary = StackTraceCompressor.makeEludedSummary(stackTraceLine)))) continue;
            eludedLineSummary.add(summary);
        }
        this.appendSkipIndicators(buffer, filteredCount, eludedLineSummary);
    }

    public void filterStackTrace(StringBuffer buffer, StackTraceElement[] stackTraceLines) {
        this.filterStackTrace(buffer, (String[])Arrays.stream(stackTraceLines).map(StackTraceElement::toString).toArray(String[]::new));
    }

    private int outputMinimumLines(StringBuffer buffer, String[] stackTraceLines, int lineCount, int minimumLines) {
        int minLines = Math.min(lineCount + minimumLines, stackTraceLines.length);
        while (lineCount < minLines) {
            String stackTraceLine = stackTraceLines[lineCount];
            buffer.append(stackTraceLine);
            if (lineCount < minLines - 1) {
                buffer.append(NewLineSupport.NL);
            }
            ++lineCount;
        }
        return lineCount;
    }

    private void appendSkipIndicators(StringBuffer buffer, int filteredCount, List<String> eludedLineSummary) {
        if (filteredCount > 0) {
            buffer.append(NewLineSupport.NL).append("\t").append(this.getFilterReplacementToken()).append(filteredCount).append(" filtered");
        }
        if (this.isShowEludedSummary()) {
            for (String summary : eludedLineSummary) {
                buffer.append(" ").append(summary);
            }
            eludedLineSummary.clear();
        }
        int lastChar = buffer.length() - 1;
        if (buffer.lastIndexOf(NewLineSupport.NL) != lastChar) {
            buffer.append(NewLineSupport.NL);
        }
    }

    private boolean lineMatchesPattern(String string, Set<String> filterSet) {
        if (!filterSet.isEmpty()) {
            for (String aFilterSet : filterSet) {
                if (!string.trim().startsWith(aFilterSet)) continue;
                return true;
            }
        }
        return false;
    }

    protected boolean causedBy(String stackTraceLine) {
        return stackTraceLine.startsWith("Caused by:");
    }

    private static String makeEludedSummary(String stackTraceLine) {
        Matcher matcher = CALL_SITE.matcher(stackTraceLine);
        if (matcher.find() && matcher.groupCount() >= 1) {
            return matcher.group(1);
        }
        return null;
    }

    public static class Builder {
        private int minimumLines;
        private boolean showEludedSummary;
        private String filteredFrames;
        private String filterEveryThingAfterFrames;
        private String filterEveryThingAfterMessage;
        private String markerAtFrames;
        private String markerAtMessage;
        private String filterReplacementToken;

        public Builder(int minimumLines, boolean showEludedSummary) {
            this.minimumLines = minimumLines;
            this.showEludedSummary = showEludedSummary;
        }

        public StackTraceCompressor build() {
            return new StackTraceCompressor(this);
        }

        public Builder minimumLines(int minimumLines) {
            this.minimumLines = minimumLines;
            return this;
        }

        public Builder showEludedSummary(boolean showEludedSummary) {
            this.showEludedSummary = showEludedSummary;
            return this;
        }

        public Builder filteredFrames(String frameSpec) {
            this.filteredFrames = frameSpec;
            return this;
        }

        public Builder filteredEveryThingAfterFrames(String frameSpec) {
            this.filterEveryThingAfterFrames = frameSpec;
            return this;
        }

        public Builder filteredEveryThingAfterMessage(String filterEveryThingAfterMessage) {
            this.filterEveryThingAfterMessage = filterEveryThingAfterMessage;
            return this;
        }

        public Builder markerAtFrames(String frameSpec) {
            this.markerAtFrames = frameSpec;
            return this;
        }

        public Builder markerAtMessage(String markerAtMessage) {
            this.markerAtMessage = markerAtMessage;
            return this;
        }

        public Builder replacementToken(String replacementToken) {
            this.filterReplacementToken = replacementToken;
            return this;
        }
    }
}

