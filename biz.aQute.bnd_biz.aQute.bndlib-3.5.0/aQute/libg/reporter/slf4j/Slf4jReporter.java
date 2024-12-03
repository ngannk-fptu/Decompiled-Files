/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.libg.reporter.slf4j;

import aQute.lib.strings.Strings;
import aQute.libg.reporter.ReporterAdapter;
import aQute.libg.slf4j.GradleLogging;
import aQute.service.reporter.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jReporter
extends ReporterAdapter {
    final Logger logger;

    public Slf4jReporter(Class<?> loggingClass) {
        this.logger = LoggerFactory.getLogger(loggingClass);
    }

    public Slf4jReporter() {
        this.logger = LoggerFactory.getLogger((String)"default");
    }

    @Override
    public Reporter.SetLocation error(String format, Object ... args) {
        Reporter.SetLocation location = super.error(format, args);
        if (this.logger.isErrorEnabled()) {
            this.logger.error("{}", (Object)Strings.format(format, args));
        }
        return location;
    }

    @Override
    public Reporter.SetLocation warning(String format, Object ... args) {
        Reporter.SetLocation location = super.warning(format, args);
        if (this.logger.isWarnEnabled()) {
            this.logger.warn("{}", (Object)Strings.format(format, args));
        }
        return location;
    }

    @Override
    @Deprecated
    public void trace(String format, Object ... args) {
        super.trace(format, args);
        if (this.isTrace()) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("{}", (Object)Strings.format(format, args));
            }
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug("{}", (Object)Strings.format(format, args));
        }
    }

    @Override
    @Deprecated
    public void progress(float progress, String format, Object ... args) {
        super.progress(progress, format, args);
        if (this.logger.isInfoEnabled(GradleLogging.LIFECYCLE)) {
            this.logger.info(GradleLogging.LIFECYCLE, "{}", (Object)Strings.format(format, args));
        }
    }

    @Override
    public Reporter.SetLocation exception(Throwable t, String format, Object ... args) {
        Reporter.SetLocation location = super.exception(t, format, args);
        if (this.logger.isErrorEnabled()) {
            this.logger.error("{}", (Object)Strings.format(format, args), (Object)t);
        }
        return location;
    }

    public static Reporter getAlternative(Class<?> class1, Reporter reporter) {
        if (reporter == null) {
            return new Slf4jReporter(class1);
        }
        return reporter;
    }
}

