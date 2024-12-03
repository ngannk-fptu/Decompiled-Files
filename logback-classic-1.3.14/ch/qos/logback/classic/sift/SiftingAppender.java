/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.joran.spi.DefaultClass
 *  ch.qos.logback.core.sift.Discriminator
 *  ch.qos.logback.core.sift.SiftingAppenderBase
 *  org.slf4j.Marker
 */
package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.DefaultClass;
import ch.qos.logback.core.sift.Discriminator;
import ch.qos.logback.core.sift.SiftingAppenderBase;
import java.util.List;
import org.slf4j.Marker;

public class SiftingAppender
extends SiftingAppenderBase<ILoggingEvent> {
    protected long getTimestamp(ILoggingEvent event) {
        return event.getTimeStamp();
    }

    @DefaultClass(value=MDCBasedDiscriminator.class)
    public void setDiscriminator(Discriminator<ILoggingEvent> discriminator) {
        super.setDiscriminator(discriminator);
    }

    protected boolean eventMarksEndOfLife(ILoggingEvent event) {
        List<Marker> markers = event.getMarkerList();
        if (markers == null) {
            return false;
        }
        for (Marker m : markers) {
            if (!m.contains(ClassicConstants.FINALIZE_SESSION_MARKER)) continue;
            return true;
        }
        return false;
    }
}

