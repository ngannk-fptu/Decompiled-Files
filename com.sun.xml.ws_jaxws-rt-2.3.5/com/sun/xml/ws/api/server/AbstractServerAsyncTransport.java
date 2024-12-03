/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.server;

import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.server.TransportBackChannel;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.ws.util.Pool;
import java.io.IOException;

public abstract class AbstractServerAsyncTransport<T> {
    private final WSEndpoint endpoint;
    private final CodecPool codecPool;

    public AbstractServerAsyncTransport(WSEndpoint endpoint) {
        this.endpoint = endpoint;
        this.codecPool = new CodecPool(endpoint);
    }

    protected Packet decodePacket(T connection, @NotNull Codec codec) throws IOException {
        Packet packet = new Packet();
        packet.acceptableMimeTypes = this.getAcceptableMimeTypes(connection);
        packet.addSatellite(this.getPropertySet(connection));
        packet.transportBackChannel = this.getTransportBackChannel(connection);
        return packet;
    }

    protected abstract void encodePacket(T var1, @NotNull Packet var2, @NotNull Codec var3) throws IOException;

    @Nullable
    protected abstract String getAcceptableMimeTypes(T var1);

    @Nullable
    protected abstract TransportBackChannel getTransportBackChannel(T var1);

    @NotNull
    protected abstract PropertySet getPropertySet(T var1);

    @NotNull
    protected abstract WebServiceContextDelegate getWebServiceContextDelegate(T var1);

    protected void handle(final T connection) throws IOException {
        final Codec codec = (Codec)this.codecPool.take();
        Packet request = this.decodePacket(connection, codec);
        if (!request.getMessage().isFault()) {
            this.endpoint.schedule(request, new WSEndpoint.CompletionCallback(){

                @Override
                public void onCompletion(@NotNull Packet response) {
                    try {
                        AbstractServerAsyncTransport.this.encodePacket(connection, response, codec);
                    }
                    catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    AbstractServerAsyncTransport.this.codecPool.recycle(codec);
                }
            });
        }
    }

    private static final class CodecPool
    extends Pool<Codec> {
        WSEndpoint endpoint;

        CodecPool(WSEndpoint endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        protected Codec create() {
            return this.endpoint.createCodec();
        }
    }
}

