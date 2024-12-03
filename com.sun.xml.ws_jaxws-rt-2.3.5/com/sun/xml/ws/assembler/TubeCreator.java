/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.logging.Logger
 */
package com.sun.xml.ws.assembler;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;
import com.sun.xml.ws.assembler.dev.TubelineAssemblyContextUpdater;
import com.sun.xml.ws.resources.TubelineassemblyMessages;
import com.sun.xml.ws.runtime.config.TubeFactoryConfig;

final class TubeCreator {
    private static final Logger LOGGER = Logger.getLogger(TubeCreator.class);
    private final TubeFactory factory;
    private final String msgDumpPropertyBase;

    TubeCreator(TubeFactoryConfig config, ClassLoader tubeFactoryClassLoader) {
        String className = config.getClassName();
        try {
            Class<?> factoryClass = this.isJDKInternal(className) ? Class.forName(className, true, TubeCreator.class.getClassLoader()) : Class.forName(className, true, tubeFactoryClassLoader);
            if (!TubeFactory.class.isAssignableFrom(factoryClass)) {
                throw new RuntimeException(TubelineassemblyMessages.MASM_0015_CLASS_DOES_NOT_IMPLEMENT_INTERFACE(factoryClass.getName(), TubeFactory.class.getName()));
            }
            Class<?> typedClass = factoryClass;
            this.factory = (TubeFactory)typedClass.newInstance();
            this.msgDumpPropertyBase = this.factory.getClass().getName() + ".dump";
        }
        catch (InstantiationException ex) {
            throw (RuntimeException)LOGGER.logSevereException((Throwable)new RuntimeException(TubelineassemblyMessages.MASM_0016_UNABLE_TO_INSTANTIATE_TUBE_FACTORY(className), ex), true);
        }
        catch (IllegalAccessException ex) {
            throw (RuntimeException)LOGGER.logSevereException((Throwable)new RuntimeException(TubelineassemblyMessages.MASM_0016_UNABLE_TO_INSTANTIATE_TUBE_FACTORY(className), ex), true);
        }
        catch (ClassNotFoundException ex) {
            throw (RuntimeException)LOGGER.logSevereException((Throwable)new RuntimeException(TubelineassemblyMessages.MASM_0017_UNABLE_TO_LOAD_TUBE_FACTORY_CLASS(className), ex), true);
        }
    }

    Tube createTube(ClientTubelineAssemblyContext context) {
        return this.factory.createTube(context);
    }

    Tube createTube(ServerTubelineAssemblyContext context) {
        return this.factory.createTube(context);
    }

    void updateContext(ClientTubelineAssemblyContext context) {
        if (this.factory instanceof TubelineAssemblyContextUpdater) {
            ((TubelineAssemblyContextUpdater)((Object)this.factory)).prepareContext(context);
        }
    }

    void updateContext(ServerTubelineAssemblyContext context) {
        if (this.factory instanceof TubelineAssemblyContextUpdater) {
            ((TubelineAssemblyContextUpdater)((Object)this.factory)).prepareContext(context);
        }
    }

    String getMessageDumpPropertyBase() {
        return this.msgDumpPropertyBase;
    }

    private boolean isJDKInternal(String className) {
        return className.startsWith("com.sun.xml.internal.ws");
    }
}

