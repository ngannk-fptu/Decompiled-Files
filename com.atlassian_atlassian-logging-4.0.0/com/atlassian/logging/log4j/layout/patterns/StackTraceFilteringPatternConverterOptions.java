/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.logging.log4j.layout.patterns;

import com.atlassian.logging.log4j.NewLineSupport;

public class StackTraceFilteringPatternConverterOptions {
    private static final StackTraceFilteringPatternConverterOptions DEFAULT = new StackTraceFilteringPatternConverterOptions();
    public static final String MINIMUM_LINES = "minimumLines";
    private int minimumLines = 6;
    public static final String SHOW_ELUDED_SUMMARY = "showEludedSummary";
    private boolean showEludedSummary = false;
    public static final String FILTERED_FRAMES = "filteredFrames";
    private String filteredFrames;
    public static final String FILTER_EVERYTHING_AFTER_FRAMES = "filterEverythingAfterFrames";
    private String filterEverythingAfterFrames;
    public static final String FILTER_EVERYTHING_AFTER_MESSAGE = "filterEverythingAfterMessage";
    private String filterEverythingAfterMessage = "\t\t(The rest of the stack trace has been filtered ...)";
    public static final String MARKER_AT_FRAMES = "markerAtFrames";
    private String markerAtFrames;
    public static final String MARKER_AT_MESSAGE = "markerAtMessage";
    private String markerAtMessage = NewLineSupport.NL;
    public static final String FILTER_REPLACEMENT_TOKEN = "filterReplacementToken";
    private String filterReplacementToken = "... ";
    public static final String FILTERING_APPLIED = "filteringApplied";
    private boolean filteringApplied = false;
    public static final String FILTERING_APPLIED_TO_DEBUG_LEVEL = "filteringAppliedToDebugLevel";
    private boolean filteringAppliedToDebugLevel = false;
    public static final String STACKTRACE_PACKAGING_EXAMINED = "stackTracePackagingExamined";
    private boolean stackTracePackagingExamined = true;

    private StackTraceFilteringPatternConverterOptions() {
    }

    public StackTraceFilteringPatternConverterOptions(int minimumLines, boolean showEludedSummary, String filteredFrames, String filterEverythingAfterFrames, String filterEverythingAfterMessage, String markerAtFrames, String markerAtMessage, String filterReplacementToken, boolean filteringApplied, boolean filteringAppliedToDebugLevel, boolean stackTracePackagingExamined) {
        this.minimumLines = minimumLines;
        this.showEludedSummary = showEludedSummary;
        this.filteredFrames = filteredFrames;
        this.filterEverythingAfterFrames = filterEverythingAfterFrames;
        this.filterEverythingAfterMessage = filterEverythingAfterMessage;
        this.markerAtFrames = markerAtFrames;
        this.markerAtMessage = markerAtMessage;
        this.filterReplacementToken = filterReplacementToken;
        this.filteringApplied = filteringApplied;
        this.filteringAppliedToDebugLevel = filteringAppliedToDebugLevel;
        this.stackTracePackagingExamined = stackTracePackagingExamined;
    }

    public static StackTraceFilteringPatternConverterOptions newInstance(String[] options) {
        if (options == null || options.length == 0) {
            return DEFAULT;
        }
        int minimumLines = DEFAULT.getMinimumLines();
        boolean showEludedSummary = DEFAULT.isShowEludedSummary();
        String filteredFrames = DEFAULT.getFilteredFrames();
        String filterEveryThingAfterFrames = DEFAULT.getFilterEveryThingAfterFrames();
        String filterEveryThingAfterMessage = DEFAULT.getFilterEverythingAfterMessage();
        String markerAtFrames = DEFAULT.getMarkerAtFrames();
        String markerAtMessage = DEFAULT.getMarkerAtMessage();
        String filterReplacementToken = DEFAULT.getFilterReplacementToken();
        boolean filteringApplied = DEFAULT.isFilteringApplied();
        boolean filteringAppliedToDebugLevel = DEFAULT.isFilteringAppliedToDebugLevel();
        boolean stackTracePackagingExamined = DEFAULT.isStackTracePackagingExamined();
        for (String rawOption : options) {
            String option;
            if (rawOption == null || (option = rawOption.trim()).isEmpty()) continue;
            if (StackTraceFilteringPatternConverterOptions.isOption(MINIMUM_LINES, option)) {
                minimumLines = Integer.parseInt(StackTraceFilteringPatternConverterOptions.getOptionValue(MINIMUM_LINES, option));
                continue;
            }
            if (StackTraceFilteringPatternConverterOptions.isOption(SHOW_ELUDED_SUMMARY, option)) {
                showEludedSummary = Boolean.parseBoolean(StackTraceFilteringPatternConverterOptions.getOptionValue(SHOW_ELUDED_SUMMARY, option));
                continue;
            }
            if (StackTraceFilteringPatternConverterOptions.isOption(FILTERED_FRAMES, option)) {
                filteredFrames = StackTraceFilteringPatternConverterOptions.getOptionValue(FILTERED_FRAMES, option);
                continue;
            }
            if (StackTraceFilteringPatternConverterOptions.isOption(FILTER_EVERYTHING_AFTER_FRAMES, option)) {
                filterEveryThingAfterFrames = StackTraceFilteringPatternConverterOptions.getOptionValue(FILTER_EVERYTHING_AFTER_FRAMES, option);
                continue;
            }
            if (StackTraceFilteringPatternConverterOptions.isOption(FILTER_EVERYTHING_AFTER_MESSAGE, option)) {
                filterEveryThingAfterMessage = StackTraceFilteringPatternConverterOptions.getOptionValue(FILTER_EVERYTHING_AFTER_MESSAGE, option);
                continue;
            }
            if (StackTraceFilteringPatternConverterOptions.isOption(MARKER_AT_FRAMES, option)) {
                markerAtFrames = StackTraceFilteringPatternConverterOptions.getOptionValue(MARKER_AT_FRAMES, option);
                continue;
            }
            if (StackTraceFilteringPatternConverterOptions.isOption(MARKER_AT_MESSAGE, option)) {
                markerAtMessage = StackTraceFilteringPatternConverterOptions.getOptionValue(MARKER_AT_MESSAGE, option);
                continue;
            }
            if (StackTraceFilteringPatternConverterOptions.isOption(FILTER_REPLACEMENT_TOKEN, option)) {
                filterReplacementToken = StackTraceFilteringPatternConverterOptions.getOptionValue(FILTER_REPLACEMENT_TOKEN, option);
                continue;
            }
            if (StackTraceFilteringPatternConverterOptions.isOption(FILTERING_APPLIED, option)) {
                filteringApplied = Boolean.parseBoolean(StackTraceFilteringPatternConverterOptions.getOptionValue(FILTERING_APPLIED, option));
                continue;
            }
            if (StackTraceFilteringPatternConverterOptions.isOption(FILTERING_APPLIED_TO_DEBUG_LEVEL, option)) {
                filteringAppliedToDebugLevel = Boolean.parseBoolean(StackTraceFilteringPatternConverterOptions.getOptionValue(FILTERING_APPLIED_TO_DEBUG_LEVEL, option));
                continue;
            }
            if (!StackTraceFilteringPatternConverterOptions.isOption(STACKTRACE_PACKAGING_EXAMINED, option)) continue;
            stackTracePackagingExamined = Boolean.parseBoolean(StackTraceFilteringPatternConverterOptions.getOptionValue(STACKTRACE_PACKAGING_EXAMINED, option));
        }
        return new StackTraceFilteringPatternConverterOptions(minimumLines, showEludedSummary, filteredFrames, filterEveryThingAfterFrames, filterEveryThingAfterMessage, markerAtFrames, markerAtMessage, filterReplacementToken, filteringApplied, filteringAppliedToDebugLevel, stackTracePackagingExamined);
    }

    private static boolean isOption(String option, String toMatch) {
        return toMatch.startsWith(option + "(") && toMatch.endsWith(")");
    }

    private static String getOptionValue(String option, String toExtract) {
        return toExtract.substring((option + "(").length(), toExtract.length() - 1);
    }

    public int getMinimumLines() {
        return this.minimumLines;
    }

    public boolean isShowEludedSummary() {
        return this.showEludedSummary;
    }

    public String getFilteredFrames() {
        return this.filteredFrames;
    }

    public String getFilterEveryThingAfterFrames() {
        return this.filterEverythingAfterFrames;
    }

    public String getFilterEverythingAfterMessage() {
        return this.filterEverythingAfterMessage;
    }

    public String getMarkerAtFrames() {
        return this.markerAtFrames;
    }

    public String getMarkerAtMessage() {
        return this.markerAtMessage;
    }

    public String getFilterReplacementToken() {
        return this.filterReplacementToken;
    }

    public boolean isFilteringApplied() {
        return this.filteringApplied;
    }

    public boolean isFilteringAppliedToDebugLevel() {
        return this.filteringAppliedToDebugLevel;
    }

    public boolean isStackTracePackagingExamined() {
        return this.stackTracePackagingExamined;
    }
}

