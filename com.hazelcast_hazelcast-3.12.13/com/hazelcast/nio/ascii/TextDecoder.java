/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.ascii;

import com.hazelcast.internal.ascii.CommandParser;
import com.hazelcast.internal.ascii.TextCommand;
import com.hazelcast.internal.ascii.TextCommandConstants;
import com.hazelcast.internal.ascii.TextCommandService;
import com.hazelcast.internal.ascii.memcache.ErrorCommand;
import com.hazelcast.internal.ascii.rest.HttpCommand;
import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.networking.InboundHandler;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ConnectionType;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ascii.TextEncoder;
import com.hazelcast.nio.ascii.TextParsers;
import com.hazelcast.nio.ascii.TextProtocolFilter;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.StringUtil;
import java.io.IOException;
import java.nio.ByteBuffer;

@PrivateApi
public abstract class TextDecoder
extends InboundHandler<ByteBuffer, Void> {
    private static final int INITIAL_CAPACITY = 256;
    private static final int MAX_CAPACITY = 65536;
    private ByteBuffer commandLineBuffer = ByteBuffer.allocate(256);
    private boolean commandLineRead;
    private TextCommand command;
    private final TextCommandService textCommandService;
    private final TextEncoder encoder;
    private final TcpIpConnection connection;
    private boolean connectionTypeSet;
    private long requestIdGen;
    private final TextProtocolFilter textProtocolFilter;
    private final ILogger logger;
    private final TextParsers textParsers;
    private final boolean rootDecoder;

    public TextDecoder(TcpIpConnection connection, TextEncoder encoder, TextProtocolFilter textProtocolFilter, TextParsers textParsers, boolean rootDecoder) {
        IOService ioService = connection.getEndpointManager().getNetworkingService().getIoService();
        this.textCommandService = ioService.getTextCommandService();
        this.encoder = encoder;
        this.connection = connection;
        this.textProtocolFilter = textProtocolFilter;
        this.textParsers = textParsers;
        this.logger = ioService.getLoggingService().getLogger(this.getClass());
        this.rootDecoder = rootDecoder;
    }

    public void sendResponse(TextCommand command) {
        this.encoder.enqueue(command);
    }

    @Override
    public void handlerAdded() {
        if (this.rootDecoder) {
            this.initSrcBuffer();
        }
    }

    @Override
    public HandlerStatus onRead() throws Exception {
        ((ByteBuffer)this.src).flip();
        try {
            while (((ByteBuffer)this.src).hasRemaining()) {
                this.doRead((ByteBuffer)this.src);
            }
            HandlerStatus handlerStatus = HandlerStatus.CLEAN;
            return handlerStatus;
        }
        finally {
            IOUtil.compactOrClear((ByteBuffer)this.src);
        }
    }

    private void doRead(ByteBuffer bb) throws IOException {
        while (!this.commandLineRead && bb.hasRemaining()) {
            byte b = bb.get();
            char c = (char)b;
            if (c == '\n') {
                this.commandLineRead = true;
                continue;
            }
            if (c == '\r') continue;
            this.appendToBuffer(b);
        }
        if (this.commandLineRead) {
            if (this.command == null) {
                String commandLine = TextDecoder.toStringAndClear(this.commandLineBuffer);
                this.textProtocolFilter.filterConnection(commandLine, this.connection);
                if (!this.connection.isAlive()) {
                    this.reset();
                    return;
                }
                this.processCmd(commandLine);
            }
            if (this.command != null) {
                boolean complete = this.command.readFrom(bb);
                if (complete) {
                    this.publishRequest(this.command);
                    this.reset();
                }
            } else {
                this.reset();
            }
        }
    }

    private void appendToBuffer(byte b) throws IOException {
        if (!this.commandLineBuffer.hasRemaining()) {
            this.expandBuffer();
        }
        this.commandLineBuffer.put(b);
    }

    private void expandBuffer() throws IOException {
        if (this.commandLineBuffer.capacity() == 65536) {
            throw new IOException("Max command size capacity [65536] has been reached!");
        }
        int capacity = this.commandLineBuffer.capacity() << 1;
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Expanding buffer capacity to " + capacity);
        }
        ByteBuffer newBuffer = ByteBuffer.allocate(capacity);
        this.commandLineBuffer.flip();
        newBuffer.put(this.commandLineBuffer);
        this.commandLineBuffer = newBuffer;
    }

    private void reset() {
        this.command = null;
        this.commandLineBuffer.clear();
        this.commandLineRead = false;
    }

    private static String toStringAndClear(ByteBuffer bb) {
        if (bb == null) {
            return "";
        }
        String result = bb.position() == 0 ? "" : StringUtil.bytesToString(bb.array(), 0, bb.position());
        bb.clear();
        return result;
    }

    public void publishRequest(TextCommand command) {
        long l;
        if (!this.isCommandTypeEnabled(command)) {
            return;
        }
        if (command.shouldReply()) {
            long l2 = this.requestIdGen;
            l = l2;
            this.requestIdGen = l2 + 1L;
        } else {
            l = -1L;
        }
        long requestId = l;
        command.init(this, requestId);
        this.textCommandService.processRequest(command);
    }

    private boolean isCommandTypeEnabled(TextCommand command) {
        if (!this.connectionTypeSet) {
            if (command instanceof HttpCommand) {
                this.connection.setType(ConnectionType.REST_CLIENT);
            } else {
                this.connection.setType(ConnectionType.MEMCACHE_CLIENT);
            }
            this.connectionTypeSet = true;
        }
        return true;
    }

    private void processCmd(String cmd) {
        try {
            int space = cmd.indexOf(32);
            String operation = space == -1 ? cmd : cmd.substring(0, space);
            CommandParser commandParser = this.textParsers.getParser(operation);
            this.command = commandParser != null ? commandParser.parser(this, cmd, space) : new ErrorCommand(TextCommandConstants.TextCommandType.UNKNOWN);
        }
        catch (Throwable t) {
            this.logger.finest(t);
            this.command = new ErrorCommand(TextCommandConstants.TextCommandType.ERROR_CLIENT, "Invalid command: " + cmd);
        }
    }

    public TextEncoder getEncoder() {
        return this.encoder;
    }

    public void closeConnection() {
        this.connection.close(null, null);
    }
}

