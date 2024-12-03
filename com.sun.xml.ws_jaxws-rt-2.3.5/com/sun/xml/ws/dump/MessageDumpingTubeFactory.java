/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.dump;

import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;
import com.sun.xml.ws.dump.MessageDumpingFeature;
import com.sun.xml.ws.dump.MessageDumpingTube;
import javax.xml.ws.WebServiceException;

public final class MessageDumpingTubeFactory
implements TubeFactory {
    @Override
    public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
        MessageDumpingFeature messageDumpingFeature = context.getBinding().getFeature(MessageDumpingFeature.class);
        if (messageDumpingFeature != null) {
            return new MessageDumpingTube(context.getTubelineHead(), messageDumpingFeature);
        }
        return context.getTubelineHead();
    }

    @Override
    public Tube createTube(ServerTubelineAssemblyContext context) throws WebServiceException {
        MessageDumpingFeature messageDumpingFeature = context.getEndpoint().getBinding().getFeature(MessageDumpingFeature.class);
        if (messageDumpingFeature != null) {
            return new MessageDumpingTube(context.getTubelineHead(), messageDumpingFeature);
        }
        return context.getTubelineHead();
    }
}

