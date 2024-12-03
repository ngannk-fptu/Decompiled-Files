/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Appender
 *  ch.qos.logback.core.ConsoleAppender
 *  ch.qos.logback.core.Layout
 *  ch.qos.logback.core.encoder.Encoder
 *  ch.qos.logback.core.encoder.LayoutWrappingEncoder
 *  ch.qos.logback.core.spi.ContextAwareBase
 */
package ch.qos.logback.classic;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ConfiguratorRank;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

@ConfiguratorRank(value=-10)
public class BasicConfigurator
extends ContextAwareBase
implements Configurator {
    @Override
    public Configurator.ExecutionStatus configure(LoggerContext loggerContext) {
        this.addInfo("Setting up default configuration.");
        ConsoleAppender ca = new ConsoleAppender();
        ca.setContext(this.context);
        ca.setName("console");
        LayoutWrappingEncoder encoder = new LayoutWrappingEncoder();
        encoder.setContext(this.context);
        TTLLLayout layout = new TTLLLayout();
        layout.setContext(this.context);
        layout.start();
        encoder.setLayout((Layout)layout);
        ca.setEncoder((Encoder)encoder);
        ca.start();
        Logger rootLogger = loggerContext.getLogger("ROOT");
        rootLogger.addAppender((Appender<ILoggingEvent>)ca);
        return Configurator.ExecutionStatus.NEUTRAL;
    }
}

