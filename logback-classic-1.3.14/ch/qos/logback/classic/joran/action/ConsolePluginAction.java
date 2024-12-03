/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.Appender
 *  ch.qos.logback.core.Context
 *  ch.qos.logback.core.joran.action.Action
 *  ch.qos.logback.core.joran.spi.ActionException
 *  ch.qos.logback.core.joran.spi.SaxEventInterpretationContext
 */
package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.SocketAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.SaxEventInterpretationContext;
import org.xml.sax.Attributes;

public class ConsolePluginAction
extends Action {
    private static final String PORT_ATTR = "port";
    private static final Integer DEFAULT_PORT = 4321;

    public void begin(SaxEventInterpretationContext ec, String name, Attributes attributes) throws ActionException {
        String portStr = attributes.getValue(PORT_ATTR);
        Integer port = null;
        if (portStr == null) {
            port = DEFAULT_PORT;
        } else {
            try {
                port = Integer.valueOf(portStr);
            }
            catch (NumberFormatException ex) {
                this.addError("Port " + portStr + " in ConsolePlugin config is not a correct number");
                this.addError("Abandoning configuration of ConsolePlugin.");
                return;
            }
        }
        LoggerContext lc = (LoggerContext)ec.getContext();
        SocketAppender appender = new SocketAppender();
        appender.setContext((Context)lc);
        appender.setIncludeCallerData(true);
        appender.setRemoteHost("localhost");
        appender.setPort(port);
        appender.start();
        Logger root = lc.getLogger("ROOT");
        root.addAppender((Appender<ILoggingEvent>)appender);
        this.addInfo("Sending LoggingEvents to the plugin using port " + port);
    }

    public void end(SaxEventInterpretationContext ec, String name) throws ActionException {
    }
}

