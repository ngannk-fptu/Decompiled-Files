/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.pattern.ConverterKeys
 *  org.apache.logging.log4j.core.pattern.LogEventPatternConverter
 */
package com.atlassian.logging.log4j.layout.patterns;

import com.atlassian.logging.log4j.LogMessageUtil;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(name="NewLineIndentingLogMessagePatternConverter", category="Converter")
@ConverterKeys(value={"nlm"})
public class NewLineIndentingLogMessagePatternConverter
extends LogEventPatternConverter {
    private final String lineIndent;

    public NewLineIndentingLogMessagePatternConverter(String[] options) {
        super("NewLineIndentingLogMessagePatternConverter", "NewLineIndentingLogMessagePatternConverter");
        this.lineIndent = this.extractLineIndentOption(options);
    }

    public static NewLineIndentingLogMessagePatternConverter newInstance(String[] options) {
        return new NewLineIndentingLogMessagePatternConverter(options);
    }

    public void format(LogEvent event, StringBuilder toAppendTo) {
        toAppendTo.append(LogMessageUtil.appendLineIndent(event.getMessage().getFormattedMessage(), this.lineIndent));
    }

    private String extractLineIndentOption(String[] options) {
        String lineIndent = "    ";
        int optionsCount = options.length;
        if (optionsCount == 1) {
            lineIndent = options[0];
        }
        return lineIndent;
    }
}

