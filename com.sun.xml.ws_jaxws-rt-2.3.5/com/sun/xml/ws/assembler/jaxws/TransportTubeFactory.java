/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.assembler.jaxws;

import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;
import com.sun.xml.ws.resources.TubelineassemblyMessages;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

@Deprecated
public final class TransportTubeFactory
implements TubeFactory {
    private static final Logger LOG = Logger.getLogger(TransportTubeFactory.class.getName());
    private final TubeFactory taf;

    public TransportTubeFactory() {
        LOG.warning(TubelineassemblyMessages.MASM_0050_DEPRECATED_TUBE(TransportTubeFactory.class.getName(), "com.sun.xml.ws.assembler.metro.jaxws.TransportTubeFactory"));
        try {
            this.taf = (TubeFactory)Class.forName("com.sun.xml.ws.assembler.metro.jaxws.TransportTubeFactory").getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ReflectiveOperationException | SecurityException ex) {
            LOG.fine(TubelineassemblyMessages.MASM_0014_UNABLE_TO_LOAD_CLASS("com.sun.xml.ws.assembler.metro.jaxws.TransportTubeFactory"));
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
        return this.taf.createTube(context);
    }

    @Override
    public Tube createTube(ServerTubelineAssemblyContext context) throws WebServiceException {
        return this.taf.createTube(context);
    }
}

