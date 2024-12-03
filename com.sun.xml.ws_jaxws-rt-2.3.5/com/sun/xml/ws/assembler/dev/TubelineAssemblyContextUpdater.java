/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.assembler.dev;

import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import javax.xml.ws.WebServiceException;

public interface TubelineAssemblyContextUpdater {
    public void prepareContext(ClientTubelineAssemblyContext var1) throws WebServiceException;

    public void prepareContext(ServerTubelineAssemblyContext var1) throws WebServiceException;
}

