/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.assembler.dev;

import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import javax.xml.ws.WebServiceException;

public interface TubeFactory {
    public Tube createTube(ClientTubelineAssemblyContext var1) throws WebServiceException;

    public Tube createTube(ServerTubelineAssemblyContext var1) throws WebServiceException;
}

