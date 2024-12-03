/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.logging.Logger
 */
package com.sun.xml.ws.assembler;

import com.sun.istack.NotNull;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.assembler.MetroConfigLoader;
import com.sun.xml.ws.assembler.MetroConfigName;
import com.sun.xml.ws.assembler.TubeCreator;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.resources.TubelineassemblyMessages;
import com.sun.xml.ws.runtime.config.TubeFactoryConfig;
import com.sun.xml.ws.runtime.config.TubeFactoryList;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.namespace.QName;

final class TubelineAssemblyController {
    private final MetroConfigName metroConfigName;

    TubelineAssemblyController(MetroConfigName metroConfigName) {
        this.metroConfigName = metroConfigName;
    }

    Collection<TubeCreator> getTubeCreators(ClientTubelineAssemblyContext context) {
        URI endpointUri = context.getPortInfo() != null ? this.createEndpointComponentUri(context.getPortInfo().getServiceName(), context.getPortInfo().getPortName()) : null;
        MetroConfigLoader configLoader = new MetroConfigLoader(context.getContainer(), this.metroConfigName);
        return this.initializeTubeCreators(configLoader.getClientSideTubeFactories(endpointUri));
    }

    Collection<TubeCreator> getTubeCreators(ServerTubelineAssemblyContext context) {
        URI endpointUri = context.getEndpoint() != null ? this.createEndpointComponentUri(context.getEndpoint().getServiceName(), context.getEndpoint().getPortName()) : null;
        MetroConfigLoader configLoader = new MetroConfigLoader(context.getEndpoint().getContainer(), this.metroConfigName);
        return this.initializeTubeCreators(configLoader.getEndpointSideTubeFactories(endpointUri));
    }

    private Collection<TubeCreator> initializeTubeCreators(TubeFactoryList tfl) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = tccl != null ? tccl : TubelineAssemblyController.class.getClassLoader();
        LinkedList<TubeCreator> tubeCreators = new LinkedList<TubeCreator>();
        for (TubeFactoryConfig tubeFactoryConfig : tfl.getTubeFactoryConfigs()) {
            tubeCreators.addFirst(new TubeCreator(tubeFactoryConfig, classLoader));
        }
        return tubeCreators;
    }

    private URI createEndpointComponentUri(@NotNull QName serviceName, @NotNull QName portName) {
        StringBuilder sb = new StringBuilder(serviceName.getNamespaceURI()).append("#wsdl11.port(").append(serviceName.getLocalPart()).append('/').append(portName.getLocalPart()).append(')');
        try {
            return new URI(sb.toString());
        }
        catch (URISyntaxException ex) {
            Logger.getLogger(TubelineAssemblyController.class).warning(TubelineassemblyMessages.MASM_0020_ERROR_CREATING_URI_FROM_GENERATED_STRING(sb.toString()), (Throwable)ex);
            return null;
        }
    }
}

