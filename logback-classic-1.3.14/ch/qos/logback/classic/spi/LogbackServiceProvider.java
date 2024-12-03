/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Context
 *  ch.qos.logback.core.joran.spi.JoranException
 *  ch.qos.logback.core.status.StatusUtil
 *  ch.qos.logback.core.util.StatusPrinter
 *  org.slf4j.ILoggerFactory
 *  org.slf4j.IMarkerFactory
 *  org.slf4j.helpers.BasicMarkerFactory
 *  org.slf4j.helpers.Util
 *  org.slf4j.spi.MDCAdapter
 *  org.slf4j.spi.SLF4JServiceProvider
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class LogbackServiceProvider
implements SLF4JServiceProvider {
    static final String NULL_CS_URL = "http://logback.qos.ch/codes.html#null_CS";
    public static String REQUESTED_API_VERSION = "2.0.99";
    private LoggerContext defaultLoggerContext;
    private IMarkerFactory markerFactory;
    private LogbackMDCAdapter mdcAdapter;

    public void initialize() {
        this.defaultLoggerContext = new LoggerContext();
        this.defaultLoggerContext.setName("default");
        this.initializeLoggerContext();
        this.defaultLoggerContext.start();
        this.markerFactory = new BasicMarkerFactory();
        this.mdcAdapter = new LogbackMDCAdapter();
        this.defaultLoggerContext.setMDCAdapter(this.mdcAdapter);
    }

    private void initializeLoggerContext() {
        try {
            try {
                new ContextInitializer(this.defaultLoggerContext).autoConfig();
            }
            catch (JoranException je) {
                Util.report((String)"Failed to auto configure default logger context", (Throwable)je);
            }
            if (!StatusUtil.contextHasStatusListener((Context)this.defaultLoggerContext)) {
                StatusPrinter.printInCaseOfErrorsOrWarnings((Context)this.defaultLoggerContext);
            }
        }
        catch (Exception t) {
            Util.report((String)("Failed to instantiate [" + LoggerContext.class.getName() + "]"), (Throwable)t);
        }
    }

    public ILoggerFactory getLoggerFactory() {
        return this.defaultLoggerContext;
    }

    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }

    public MDCAdapter getMDCAdapter() {
        return this.mdcAdapter;
    }

    public String getRequestedApiVersion() {
        return REQUESTED_API_VERSION;
    }
}

