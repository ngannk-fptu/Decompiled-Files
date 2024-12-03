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
import com.sun.xml.ws.dump.MessageDumpingFeature;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

final class MessageDumpingTube
extends AbstractFilterTubeImpl {
    static final String DEFAULT_MSGDUMP_LOGGING_ROOT = "com.sun.xml.ws.messagedump";
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private final MessageDumper messageDumper;
    private final int tubeId;
    private final MessageDumpingFeature messageDumpingFeature;

    MessageDumpingTube(Tube next, MessageDumpingFeature feature) {
        super(next);
        this.messageDumpingFeature = feature;
        this.tubeId = ID_GENERATOR.incrementAndGet();
        this.messageDumper = new MessageDumper("MesageDumpingTube", Logger.getLogger(feature.getMessageLoggingRoot()), feature.getMessageLoggingLevel());
    }

    MessageDumpingTube(MessageDumpingTube that, TubeCloner cloner) {
        super(that, cloner);
        this.messageDumpingFeature = that.messageDumpingFeature;
        this.tubeId = ID_GENERATOR.incrementAndGet();
        this.messageDumper = that.messageDumper;
    }

    @Override
    public MessageDumpingTube copy(TubeCloner cloner) {
        return new MessageDumpingTube(this, cloner);
    }

    @Override
    public NextAction processRequest(Packet request) {
        this.dump(MessageDumper.MessageType.Request, Converter.toString(request), Fiber.current().owner.id);
        return super.processRequest(request);
    }

    @Override
    public NextAction processResponse(Packet response) {
        this.dump(MessageDumper.MessageType.Response, Converter.toString(response), Fiber.current().owner.id);
        return super.processResponse(response);
    }

    @Override
    public NextAction processException(Throwable t) {
        this.dump(MessageDumper.MessageType.Exception, Converter.toString(t), Fiber.current().owner.id);
        return super.processException(t);
    }

    protected final void dump(MessageDumper.MessageType messageType, String message, String engineId) {
        String logMessage;
        if (this.messageDumpingFeature.getMessageLoggingStatus()) {
            this.messageDumper.setLoggingLevel(this.messageDumpingFeature.getMessageLoggingLevel());
            logMessage = this.messageDumper.dump(messageType, MessageDumper.ProcessingState.Received, message, this.tubeId, engineId);
        } else {
            logMessage = this.messageDumper.createLogMessage(messageType, MessageDumper.ProcessingState.Received, this.tubeId, engineId, message);
        }
        this.messageDumpingFeature.offerMessage(logMessage);
    }
}

