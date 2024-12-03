/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.dump;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Fiber;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.ws.commons.xmlutil.Converter;
import com.sun.xml.ws.dump.MessageDumper;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingDumpTube
extends AbstractFilterTubeImpl {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private MessageDumper messageDumper;
    private final Level loggingLevel;
    private final Position position;
    private final int tubeId;

    public LoggingDumpTube(Level loggingLevel, Position position, Tube tubelineHead) {
        super(tubelineHead);
        this.position = position;
        this.loggingLevel = loggingLevel;
        this.tubeId = ID_GENERATOR.incrementAndGet();
    }

    public void setLoggedTubeName(String loggedTubeName) {
        assert (this.messageDumper == null);
        this.messageDumper = new MessageDumper(loggedTubeName, Logger.getLogger(loggedTubeName), this.loggingLevel);
    }

    private LoggingDumpTube(LoggingDumpTube original, TubeCloner cloner) {
        super(original, cloner);
        this.messageDumper = original.messageDumper;
        this.loggingLevel = original.loggingLevel;
        this.position = original.position;
        this.tubeId = ID_GENERATOR.incrementAndGet();
    }

    @Override
    public LoggingDumpTube copy(TubeCloner cloner) {
        return new LoggingDumpTube(this, cloner);
    }

    @Override
    public NextAction processRequest(Packet request) {
        if (this.messageDumper.isLoggable()) {
            Packet dumpPacket = request != null ? request.copy(true) : null;
            this.messageDumper.dump(MessageDumper.MessageType.Request, this.position.requestState, Converter.toString(dumpPacket), this.tubeId, Fiber.current().owner.id);
        }
        return super.processRequest(request);
    }

    @Override
    public NextAction processResponse(Packet response) {
        if (this.messageDumper.isLoggable()) {
            Packet dumpPacket = response != null ? response.copy(true) : null;
            this.messageDumper.dump(MessageDumper.MessageType.Response, this.position.responseState, Converter.toString(dumpPacket), this.tubeId, Fiber.current().owner.id);
        }
        return super.processResponse(response);
    }

    @Override
    public NextAction processException(Throwable t) {
        if (this.messageDumper.isLoggable()) {
            this.messageDumper.dump(MessageDumper.MessageType.Exception, this.position.responseState, Converter.toString(t), this.tubeId, Fiber.current().owner.id);
        }
        return super.processException(t);
    }

    @Override
    public void preDestroy() {
        super.preDestroy();
    }

    public static enum Position {
        Before(MessageDumper.ProcessingState.Received, MessageDumper.ProcessingState.Processed),
        After(MessageDumper.ProcessingState.Processed, MessageDumper.ProcessingState.Received);

        private final MessageDumper.ProcessingState requestState;
        private final MessageDumper.ProcessingState responseState;

        private Position(MessageDumper.ProcessingState requestState, MessageDumper.ProcessingState responseState) {
            this.requestState = requestState;
            this.responseState = responseState;
        }
    }
}

