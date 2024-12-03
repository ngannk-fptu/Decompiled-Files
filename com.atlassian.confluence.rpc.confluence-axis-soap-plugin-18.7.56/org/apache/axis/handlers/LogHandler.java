/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class LogHandler
extends BasicHandler {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$handlers$LogHandler == null ? (class$org$apache$axis$handlers$LogHandler = LogHandler.class$("org.apache.axis.handlers.LogHandler")) : class$org$apache$axis$handlers$LogHandler).getName());
    long start = -1L;
    private boolean writeToConsole = false;
    private String filename = "axis.log";
    static /* synthetic */ Class class$org$apache$axis$handlers$LogHandler;

    public void init() {
        super.init();
        Object opt = this.getOption("LogHandler.writeToConsole");
        if (opt != null && opt instanceof String && "true".equalsIgnoreCase((String)opt)) {
            this.writeToConsole = true;
        }
        if ((opt = this.getOption("LogHandler.fileName")) != null && opt instanceof String) {
            this.filename = (String)opt;
        }
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        log.debug((Object)"Enter: LogHandler::invoke");
        if (!msgContext.getPastPivot()) {
            this.start = System.currentTimeMillis();
        } else {
            this.logMessages(msgContext);
        }
        log.debug((Object)"Exit: LogHandler::invoke");
    }

    private void logMessages(MessageContext msgContext) throws AxisFault {
        try {
            PrintWriter writer = null;
            writer = this.getWriter();
            Message inMsg = msgContext.getRequestMessage();
            Message outMsg = msgContext.getResponseMessage();
            writer.println("=======================================================");
            if (this.start != -1L) {
                writer.println("= " + Messages.getMessage("elapsed00", "" + (System.currentTimeMillis() - this.start)));
            }
            writer.println("= " + Messages.getMessage("inMsg00", inMsg == null ? "null" : inMsg.getSOAPPartAsString()));
            writer.println("= " + Messages.getMessage("outMsg00", outMsg == null ? "null" : outMsg.getSOAPPartAsString()));
            writer.println("=======================================================");
            if (!this.writeToConsole) {
                writer.close();
            }
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            throw AxisFault.makeFault(e);
        }
    }

    private PrintWriter getWriter() throws IOException {
        PrintWriter writer;
        if (this.writeToConsole) {
            writer = new PrintWriter(System.out);
        } else {
            if (this.filename == null) {
                this.filename = "axis.log";
            }
            writer = new PrintWriter(new FileWriter(this.filename, true));
        }
        return writer;
    }

    public void onFault(MessageContext msgContext) {
        try {
            this.logMessages(msgContext);
        }
        catch (AxisFault axisFault) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)axisFault);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

