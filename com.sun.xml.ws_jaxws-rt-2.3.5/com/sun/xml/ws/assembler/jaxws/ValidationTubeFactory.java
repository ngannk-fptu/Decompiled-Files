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
import javax.xml.ws.WebServiceException;

public final class ValidationTubeFactory
implements TubeFactory {
    @Override
    public Tube createTube(ClientTubelineAssemblyContext context) throws WebServiceException {
        return context.getWrappedContext().createValidationTube(context.getTubelineHead());
    }

    @Override
    public Tube createTube(ServerTubelineAssemblyContext context) throws WebServiceException {
        return context.getWrappedContext().createValidationTube(context.getTubelineHead());
    }
}

