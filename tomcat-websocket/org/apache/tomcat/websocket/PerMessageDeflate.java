/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Extension
 *  javax.websocket.Extension$Parameter
 *  javax.websocket.SendHandler
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.websocket.Extension;
import javax.websocket.SendHandler;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.Constants;
import org.apache.tomcat.websocket.MessagePart;
import org.apache.tomcat.websocket.Transformation;
import org.apache.tomcat.websocket.TransformationResult;
import org.apache.tomcat.websocket.Util;
import org.apache.tomcat.websocket.WsExtension;
import org.apache.tomcat.websocket.WsExtensionParameter;

public class PerMessageDeflate
implements Transformation {
    private static final StringManager sm = StringManager.getManager(PerMessageDeflate.class);
    private static final String SERVER_NO_CONTEXT_TAKEOVER = "server_no_context_takeover";
    private static final String CLIENT_NO_CONTEXT_TAKEOVER = "client_no_context_takeover";
    private static final String SERVER_MAX_WINDOW_BITS = "server_max_window_bits";
    private static final String CLIENT_MAX_WINDOW_BITS = "client_max_window_bits";
    private static final int RSV_BITMASK = 4;
    private static final byte[] EOM_BYTES = new byte[]{0, 0, -1, -1};
    public static final String NAME = "permessage-deflate";
    private final boolean serverContextTakeover;
    private final int serverMaxWindowBits;
    private final boolean clientContextTakeover;
    private final int clientMaxWindowBits;
    private final boolean isServer;
    private final Inflater inflater = new Inflater(true);
    private final ByteBuffer readBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
    private final Deflater deflater = new Deflater(-1, true);
    private final byte[] EOM_BUFFER = new byte[EOM_BYTES.length + 1];
    private volatile Transformation next;
    private volatile boolean skipDecompression = false;
    private volatile ByteBuffer writeBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
    private volatile boolean firstCompressedFrameWritten = false;
    private volatile boolean emptyMessage = true;

    static PerMessageDeflate negotiate(List<List<Extension.Parameter>> preferences, boolean isServer) {
        for (List<Extension.Parameter> preference : preferences) {
            boolean ok = true;
            boolean serverContextTakeover = true;
            int serverMaxWindowBits = -1;
            boolean clientContextTakeover = true;
            int clientMaxWindowBits = -1;
            for (Extension.Parameter param : preference) {
                if (SERVER_NO_CONTEXT_TAKEOVER.equals(param.getName())) {
                    if (serverContextTakeover) {
                        serverContextTakeover = false;
                        continue;
                    }
                    throw new IllegalArgumentException(sm.getString("perMessageDeflate.duplicateParameter", new Object[]{SERVER_NO_CONTEXT_TAKEOVER}));
                }
                if (CLIENT_NO_CONTEXT_TAKEOVER.equals(param.getName())) {
                    if (clientContextTakeover) {
                        clientContextTakeover = false;
                        continue;
                    }
                    throw new IllegalArgumentException(sm.getString("perMessageDeflate.duplicateParameter", new Object[]{CLIENT_NO_CONTEXT_TAKEOVER}));
                }
                if (SERVER_MAX_WINDOW_BITS.equals(param.getName())) {
                    if (serverMaxWindowBits == -1) {
                        serverMaxWindowBits = Integer.parseInt(param.getValue());
                        if (serverMaxWindowBits < 8 || serverMaxWindowBits > 15) {
                            throw new IllegalArgumentException(sm.getString("perMessageDeflate.invalidWindowSize", new Object[]{SERVER_MAX_WINDOW_BITS, serverMaxWindowBits}));
                        }
                        if (!isServer || serverMaxWindowBits == 15) continue;
                        ok = false;
                        break;
                    }
                    throw new IllegalArgumentException(sm.getString("perMessageDeflate.duplicateParameter", new Object[]{SERVER_MAX_WINDOW_BITS}));
                }
                if (CLIENT_MAX_WINDOW_BITS.equals(param.getName())) {
                    if (clientMaxWindowBits == -1) {
                        if (param.getValue() == null) {
                            clientMaxWindowBits = 15;
                        } else {
                            clientMaxWindowBits = Integer.parseInt(param.getValue());
                            if (clientMaxWindowBits < 8 || clientMaxWindowBits > 15) {
                                throw new IllegalArgumentException(sm.getString("perMessageDeflate.invalidWindowSize", new Object[]{CLIENT_MAX_WINDOW_BITS, clientMaxWindowBits}));
                            }
                        }
                        if (isServer || clientMaxWindowBits == 15) continue;
                        ok = false;
                        break;
                    }
                    throw new IllegalArgumentException(sm.getString("perMessageDeflate.duplicateParameter", new Object[]{CLIENT_MAX_WINDOW_BITS}));
                }
                throw new IllegalArgumentException(sm.getString("perMessageDeflate.unknownParameter", new Object[]{param.getName()}));
            }
            if (!ok) continue;
            return new PerMessageDeflate(serverContextTakeover, serverMaxWindowBits, clientContextTakeover, clientMaxWindowBits, isServer);
        }
        return null;
    }

    private PerMessageDeflate(boolean serverContextTakeover, int serverMaxWindowBits, boolean clientContextTakeover, int clientMaxWindowBits, boolean isServer) {
        this.serverContextTakeover = serverContextTakeover;
        this.serverMaxWindowBits = serverMaxWindowBits;
        this.clientContextTakeover = clientContextTakeover;
        this.clientMaxWindowBits = clientMaxWindowBits;
        this.isServer = isServer;
    }

    @Override
    public TransformationResult getMoreData(byte opCode, boolean fin, int rsv, ByteBuffer dest) throws IOException {
        if (Util.isControl(opCode)) {
            return this.next.getMoreData(opCode, fin, rsv, dest);
        }
        if (!Util.isContinuation(opCode)) {
            boolean bl = this.skipDecompression = (rsv & 4) == 0;
        }
        if (this.skipDecompression) {
            return this.next.getMoreData(opCode, fin, rsv, dest);
        }
        boolean usedEomBytes = false;
        while (dest.remaining() > 0 || usedEomBytes) {
            int written;
            try {
                written = this.inflater.inflate(dest.array(), dest.arrayOffset() + dest.position(), dest.remaining());
            }
            catch (DataFormatException e) {
                throw new IOException(sm.getString("perMessageDeflate.deflateFailed"), e);
            }
            catch (NullPointerException e) {
                throw new IOException(sm.getString("perMessageDeflate.alreadyClosed"), e);
            }
            dest.position(dest.position() + written);
            if (this.inflater.needsInput() && !usedEomBytes) {
                this.readBuffer.clear();
                TransformationResult nextResult = this.next.getMoreData(opCode, fin, rsv ^ 4, this.readBuffer);
                this.inflater.setInput(this.readBuffer.array(), this.readBuffer.arrayOffset(), this.readBuffer.position());
                if (dest.hasRemaining()) {
                    if (TransformationResult.UNDERFLOW.equals((Object)nextResult)) {
                        return nextResult;
                    }
                    if (!TransformationResult.END_OF_FRAME.equals((Object)nextResult) || this.readBuffer.position() != 0) continue;
                    if (fin) {
                        this.inflater.setInput(EOM_BYTES);
                        usedEomBytes = true;
                        continue;
                    }
                    return TransformationResult.END_OF_FRAME;
                }
                if (this.readBuffer.position() > 0) {
                    return TransformationResult.OVERFLOW;
                }
                if (!fin) continue;
                this.inflater.setInput(EOM_BYTES);
                usedEomBytes = true;
                continue;
            }
            if (written != 0) continue;
            if (fin && (this.isServer && !this.clientContextTakeover || !this.isServer && !this.serverContextTakeover)) {
                try {
                    this.inflater.reset();
                }
                catch (NullPointerException e) {
                    throw new IOException(sm.getString("perMessageDeflate.alreadyClosed"), e);
                }
            }
            return TransformationResult.END_OF_FRAME;
        }
        return TransformationResult.OVERFLOW;
    }

    @Override
    public boolean validateRsv(int rsv, byte opCode) {
        if (Util.isControl(opCode)) {
            if ((rsv & 4) != 0) {
                return false;
            }
            if (this.next == null) {
                return true;
            }
            return this.next.validateRsv(rsv, opCode);
        }
        int rsvNext = rsv;
        if ((rsv & 4) != 0) {
            rsvNext = rsv ^ 4;
        }
        if (this.next == null) {
            return true;
        }
        return this.next.validateRsv(rsvNext, opCode);
    }

    @Override
    public Extension getExtensionResponse() {
        WsExtension result = new WsExtension(NAME);
        List params = result.getParameters();
        if (!this.serverContextTakeover) {
            params.add(new WsExtensionParameter(SERVER_NO_CONTEXT_TAKEOVER, null));
        }
        if (this.serverMaxWindowBits != -1) {
            params.add(new WsExtensionParameter(SERVER_MAX_WINDOW_BITS, Integer.toString(this.serverMaxWindowBits)));
        }
        if (!this.clientContextTakeover) {
            params.add(new WsExtensionParameter(CLIENT_NO_CONTEXT_TAKEOVER, null));
        }
        if (this.clientMaxWindowBits != -1) {
            params.add(new WsExtensionParameter(CLIENT_MAX_WINDOW_BITS, Integer.toString(this.clientMaxWindowBits)));
        }
        return result;
    }

    @Override
    public void setNext(Transformation t) {
        if (this.next == null) {
            this.next = t;
        } else {
            this.next.setNext(t);
        }
    }

    @Override
    public boolean validateRsvBits(int i) {
        if ((i & 4) != 0) {
            return false;
        }
        if (this.next == null) {
            return true;
        }
        return this.next.validateRsvBits(i | 4);
    }

    @Override
    public List<MessagePart> sendMessagePart(List<MessagePart> uncompressedParts) throws IOException {
        ArrayList<MessagePart> allCompressedParts = new ArrayList<MessagePart>();
        for (MessagePart uncompressedPart : uncompressedParts) {
            byte opCode = uncompressedPart.getOpCode();
            if (Util.isControl(opCode)) {
                allCompressedParts.add(uncompressedPart);
                continue;
            }
            boolean emptyPart = uncompressedPart.getPayload().limit() == 0;
            boolean bl = this.emptyMessage = this.emptyMessage && emptyPart;
            if (this.emptyMessage && uncompressedPart.isFin()) {
                allCompressedParts.add(uncompressedPart);
                continue;
            }
            ArrayList<MessagePart> compressedParts = new ArrayList<MessagePart>();
            ByteBuffer uncompressedPayload = uncompressedPart.getPayload();
            SendHandler uncompressedIntermediateHandler = uncompressedPart.getIntermediateHandler();
            if (uncompressedPayload.hasArray()) {
                this.deflater.setInput(uncompressedPayload.array(), uncompressedPayload.arrayOffset() + uncompressedPayload.position(), uncompressedPayload.remaining());
            } else {
                byte[] bytes = new byte[uncompressedPayload.remaining()];
                uncompressedPayload.get(bytes);
                this.deflater.setInput(bytes, 0, bytes.length);
            }
            int flush = uncompressedPart.isFin() ? 2 : 0;
            boolean deflateRequired = true;
            while (deflateRequired) {
                MessagePart compressedPart;
                ByteBuffer compressedPayload = this.writeBuffer;
                try {
                    int written = this.deflater.deflate(compressedPayload.array(), compressedPayload.arrayOffset() + compressedPayload.position(), compressedPayload.remaining(), flush);
                    compressedPayload.position(compressedPayload.position() + written);
                }
                catch (NullPointerException e) {
                    throw new IOException(sm.getString("perMessageDeflate.alreadyClosed"), e);
                }
                if (!uncompressedPart.isFin() && compressedPayload.hasRemaining() && this.deflater.needsInput()) break;
                this.writeBuffer = ByteBuffer.allocate(Constants.DEFAULT_BUFFER_SIZE);
                compressedPayload.flip();
                boolean fin = uncompressedPart.isFin();
                boolean full = compressedPayload.limit() == compressedPayload.capacity();
                boolean needsInput = this.deflater.needsInput();
                long blockingWriteTimeoutExpiry = uncompressedPart.getBlockingWriteTimeoutExpiry();
                if (fin && !full && needsInput) {
                    compressedPayload.limit(compressedPayload.limit() - EOM_BYTES.length);
                    compressedPart = new MessagePart(true, this.getRsv(uncompressedPart), opCode, compressedPayload, uncompressedIntermediateHandler, uncompressedIntermediateHandler, blockingWriteTimeoutExpiry);
                    deflateRequired = false;
                    this.startNewMessage();
                } else if (full && !needsInput) {
                    compressedPart = new MessagePart(false, this.getRsv(uncompressedPart), opCode, compressedPayload, uncompressedIntermediateHandler, uncompressedIntermediateHandler, blockingWriteTimeoutExpiry);
                } else if (!fin && full && needsInput) {
                    compressedPart = new MessagePart(false, this.getRsv(uncompressedPart), opCode, compressedPayload, uncompressedIntermediateHandler, uncompressedIntermediateHandler, blockingWriteTimeoutExpiry);
                    deflateRequired = false;
                } else if (fin && full && needsInput) {
                    int eomBufferWritten;
                    try {
                        eomBufferWritten = this.deflater.deflate(this.EOM_BUFFER, 0, this.EOM_BUFFER.length, 2);
                    }
                    catch (NullPointerException e) {
                        throw new IOException(sm.getString("perMessageDeflate.alreadyClosed"), e);
                    }
                    if (eomBufferWritten < this.EOM_BUFFER.length) {
                        compressedPayload.limit(compressedPayload.limit() - EOM_BYTES.length + eomBufferWritten);
                        compressedPart = new MessagePart(true, this.getRsv(uncompressedPart), opCode, compressedPayload, uncompressedIntermediateHandler, uncompressedIntermediateHandler, blockingWriteTimeoutExpiry);
                        deflateRequired = false;
                        this.startNewMessage();
                    } else {
                        this.writeBuffer.put(this.EOM_BUFFER, 0, eomBufferWritten);
                        compressedPart = new MessagePart(false, this.getRsv(uncompressedPart), opCode, compressedPayload, uncompressedIntermediateHandler, uncompressedIntermediateHandler, blockingWriteTimeoutExpiry);
                    }
                } else {
                    throw new IllegalStateException(sm.getString("perMessageDeflate.invalidState"));
                }
                compressedParts.add(compressedPart);
            }
            SendHandler uncompressedEndHandler = uncompressedPart.getEndHandler();
            int size = compressedParts.size();
            if (size > 0) {
                ((MessagePart)compressedParts.get(size - 1)).setEndHandler(uncompressedEndHandler);
            }
            allCompressedParts.addAll(compressedParts);
        }
        if (this.next == null) {
            return allCompressedParts;
        }
        return this.next.sendMessagePart(allCompressedParts);
    }

    private void startNewMessage() throws IOException {
        this.firstCompressedFrameWritten = false;
        this.emptyMessage = true;
        if (this.isServer && !this.serverContextTakeover || !this.isServer && !this.clientContextTakeover) {
            try {
                this.deflater.reset();
            }
            catch (NullPointerException e) {
                throw new IOException(sm.getString("perMessageDeflate.alreadyClosed"), e);
            }
        }
    }

    private int getRsv(MessagePart uncompressedMessagePart) {
        int result = uncompressedMessagePart.getRsv();
        if (!this.firstCompressedFrameWritten) {
            result += 4;
            this.firstCompressedFrameWritten = true;
        }
        return result;
    }

    @Override
    public void close() {
        this.next.close();
        this.inflater.end();
        this.deflater.end();
    }
}

