/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.PipelineAssembler;
import com.sun.xml.ws.api.pipe.PipelineAssemblerFactory;
import com.sun.xml.ws.api.pipe.ServerPipeAssemblerContext;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubelineAssembler;
import com.sun.xml.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.assembler.MetroTubelineAssembler;
import com.sun.xml.ws.util.ServiceFinder;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TubelineAssemblerFactory {
    private static final Logger logger = Logger.getLogger(TubelineAssemblerFactory.class.getName());

    public abstract TubelineAssembler doCreate(BindingID var1);

    public static TubelineAssembler create(ClassLoader classLoader, BindingID bindingId) {
        return TubelineAssemblerFactory.create(classLoader, bindingId, null);
    }

    public static TubelineAssembler create(ClassLoader classLoader, BindingID bindingId, @Nullable Container container) {
        Object assembler;
        TubelineAssembler tubelineAssembler;
        TubelineAssemblerFactory taf;
        if (container != null && (taf = container.getSPI(TubelineAssemblerFactory.class)) != null && (tubelineAssembler = taf.doCreate(bindingId)) != null) {
            return tubelineAssembler;
        }
        for (TubelineAssemblerFactory tubelineAssemblerFactory : ServiceFinder.find(TubelineAssemblerFactory.class, classLoader)) {
            assembler = tubelineAssemblerFactory.doCreate(bindingId);
            if (assembler == null) continue;
            logger.log(Level.FINE, "{0} successfully created {1}", new Object[]{tubelineAssemblerFactory.getClass(), assembler});
            return assembler;
        }
        for (PipelineAssemblerFactory pipelineAssemblerFactory : ServiceFinder.find(PipelineAssemblerFactory.class, classLoader)) {
            assembler = pipelineAssemblerFactory.doCreate(bindingId);
            if (assembler == null) continue;
            logger.log(Level.FINE, "{0} successfully created {1}", new Object[]{pipelineAssemblerFactory.getClass(), assembler});
            return new TubelineAssemblerAdapter((PipelineAssembler)assembler);
        }
        return new MetroTubelineAssembler(bindingId, MetroTubelineAssembler.JAXWS_TUBES_CONFIG_NAMES);
    }

    private static class TubelineAssemblerAdapter
    implements TubelineAssembler {
        private PipelineAssembler assembler;

        TubelineAssemblerAdapter(PipelineAssembler assembler) {
            this.assembler = assembler;
        }

        @Override
        @NotNull
        public Tube createClient(@NotNull ClientTubeAssemblerContext context) {
            ClientPipeAssemblerContext ctxt = new ClientPipeAssemblerContext(context.getAddress(), context.getWsdlModel(), context.getService(), context.getBinding(), context.getContainer());
            return PipeAdapter.adapt(this.assembler.createClient(ctxt));
        }

        @Override
        @NotNull
        public Tube createServer(@NotNull ServerTubeAssemblerContext context) {
            if (!(context instanceof ServerPipeAssemblerContext)) {
                throw new IllegalArgumentException(context + " is not instance of ServerPipeAssemblerContext");
            }
            return PipeAdapter.adapt(this.assembler.createServer((ServerPipeAssemblerContext)context));
        }
    }
}

