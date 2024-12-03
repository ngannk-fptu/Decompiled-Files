/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.CoreConstants
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;

public class LineSeparatorConverter
extends ClassicConverter {
    public String convert(ILoggingEvent event) {
        return CoreConstants.LINE_SEPARATOR;
    }
}

