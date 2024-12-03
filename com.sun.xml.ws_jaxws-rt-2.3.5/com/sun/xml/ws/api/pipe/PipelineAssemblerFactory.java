/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe;

import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.pipe.PipelineAssembler;
import com.sun.xml.ws.util.ServiceFinder;
import com.sun.xml.ws.util.pipe.StandalonePipeAssembler;
import java.util.logging.Logger;

public abstract class PipelineAssemblerFactory {
    private static final Logger logger = Logger.getLogger(PipelineAssemblerFactory.class.getName());

    public abstract PipelineAssembler doCreate(BindingID var1);

    public static PipelineAssembler create(ClassLoader classLoader, BindingID bindingId) {
        for (PipelineAssemblerFactory factory : ServiceFinder.find(PipelineAssemblerFactory.class, classLoader)) {
            PipelineAssembler assembler = factory.doCreate(bindingId);
            if (assembler == null) continue;
            logger.fine(factory.getClass() + " successfully created " + assembler);
            return assembler;
        }
        return new StandalonePipeAssembler();
    }
}

