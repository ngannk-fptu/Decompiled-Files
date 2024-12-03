/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.pattern.ConverterKeys
 *  org.apache.logging.log4j.core.pattern.LogEventPatternConverter
 */
package com.atlassian.logging.log4j.layout.patterns;

import com.atlassian.logging.log4j.NewLineSupport;
import com.atlassian.logging.log4j.StackTraceCompressor;
import com.atlassian.logging.log4j.StackTraceInfo;
import com.atlassian.logging.log4j.layout.patterns.StackTraceFilteringPatternConverterOptions;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(name="StackTraceFilteringPatternConverter", category="Converter")
@ConverterKeys(value={"stf"})
public class StackTraceFilteringPatternConverter
extends LogEventPatternConverter {
    StackTraceCompressor stackTraceCompressor;
    StackTraceFilteringPatternConverterOptions converterOptions;

    StackTraceFilteringPatternConverter(String[] options) {
        super("StackTraceFilteringPatternConverter", "StackTraceFilteringPatternConverter");
        StackTraceFilteringPatternConverterOptions converterOptions;
        this.converterOptions = converterOptions = StackTraceFilteringPatternConverterOptions.newInstance(options);
        this.stackTraceCompressor = StackTraceCompressor.defaultBuilder(converterOptions.getMinimumLines(), converterOptions.isShowEludedSummary()).filteredFrames(converterOptions.getFilteredFrames()).filteredEveryThingAfterFrames(converterOptions.getFilterEveryThingAfterFrames()).filteredEveryThingAfterMessage(converterOptions.getFilterEverythingAfterMessage()).markerAtFrames(converterOptions.getMarkerAtFrames()).markerAtMessage(converterOptions.getMarkerAtMessage()).replacementToken(converterOptions.getFilterReplacementToken()).build();
    }

    public static StackTraceFilteringPatternConverter newInstance(String[] options) {
        return new StackTraceFilteringPatternConverter(options);
    }

    public void format(LogEvent event, StringBuilder toAppendTo) {
        Throwable throwable = event.getThrown();
        if (throwable != null) {
            toAppendTo.append(this.formatStackTrace(event, throwable));
        }
    }

    private String formatStackTrace(LogEvent event, Throwable throwable) {
        StringBuffer buffer = new StringBuffer();
        String[] stackTraceLines = this.getThrowableStrRep(throwable);
        if (!this.converterOptions.isFilteringApplied() || Level.DEBUG.equals((Object)event.getLevel()) && !this.converterOptions.isFilteringAppliedToDebugLevel()) {
            this.outputPlainThrowable(buffer, stackTraceLines);
        } else {
            this.stackTraceCompressor.filterStackTrace(buffer, stackTraceLines);
        }
        return buffer.toString();
    }

    private void outputPlainThrowable(StringBuffer buffer, String[] stackTraceLines) {
        NewLineSupport.join(buffer, stackTraceLines);
    }

    protected String[] getThrowableStrRep(Throwable throwable) {
        return new StackTraceInfo(throwable, "    ", this.converterOptions.isStackTracePackagingExamined()).getThrowableStrRep();
    }
}

