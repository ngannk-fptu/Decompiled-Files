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
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubelineAssembler;
import com.sun.xml.ws.assembler.DefaultClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.DefaultServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.MetroConfigName;
import com.sun.xml.ws.assembler.MetroConfigNameImpl;
import com.sun.xml.ws.assembler.TubeCreator;
import com.sun.xml.ws.assembler.TubelineAssemblyContextImpl;
import com.sun.xml.ws.assembler.TubelineAssemblyController;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubelineAssemblyDecorator;
import com.sun.xml.ws.dump.LoggingDumpTube;
import com.sun.xml.ws.resources.TubelineassemblyMessages;
import com.sun.xml.ws.util.ServiceFinder;
import java.util.Collection;
import java.util.logging.Level;

public class MetroTubelineAssembler
implements TubelineAssembler {
    private static final String COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE = "com.sun.metro.soap.dump";
    public static final MetroConfigName JAXWS_TUBES_CONFIG_NAMES = new MetroConfigNameImpl("jaxws-tubes-default.xml", "jaxws-tubes.xml");
    private static final Logger LOGGER = Logger.getLogger(MetroTubelineAssembler.class);
    private final BindingID bindingId;
    private final TubelineAssemblyController tubelineAssemblyController;

    public MetroTubelineAssembler(BindingID bindingId, MetroConfigName metroConfigName) {
        this.bindingId = bindingId;
        this.tubelineAssemblyController = new TubelineAssemblyController(metroConfigName);
    }

    TubelineAssemblyController getTubelineAssemblyController() {
        return this.tubelineAssemblyController;
    }

    @Override
    @NotNull
    public Tube createClient(@NotNull ClientTubeAssemblerContext jaxwsContext) {
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer("Assembling client-side tubeline for WS endpoint: " + jaxwsContext.getAddress().getURI().toString());
        }
        ClientTubelineAssemblyContext context = this.createClientContext(jaxwsContext);
        Collection<TubeCreator> tubeCreators = this.tubelineAssemblyController.getTubeCreators(context);
        for (TubeCreator tubeCreator : tubeCreators) {
            tubeCreator.updateContext(context);
        }
        TubelineAssemblyDecorator decorator = TubelineAssemblyDecorator.composite(ServiceFinder.find(TubelineAssemblyDecorator.class, context.getContainer()));
        boolean first = true;
        TubelineAssemblyContextImpl contextImpl = (TubelineAssemblyContextImpl)((Object)context);
        for (TubeCreator tubeCreator : tubeCreators) {
            MessageDumpingInfo msgDumpInfo = this.setupMessageDumping(tubeCreator.getMessageDumpPropertyBase(), Side.Client);
            Tube oldTubelineHead = context.getTubelineHead();
            LoggingDumpTube afterDumpTube = null;
            if (msgDumpInfo.dumpAfter) {
                afterDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.After, context.getTubelineHead());
                contextImpl.setTubelineHead(afterDumpTube);
            }
            if (!contextImpl.setTubelineHead(decorator.decorateClient(tubeCreator.createTube(context), context))) {
                if (afterDumpTube != null) {
                    contextImpl.setTubelineHead(oldTubelineHead);
                }
            } else {
                String loggedTubeName = context.getTubelineHead().getClass().getName();
                if (afterDumpTube != null) {
                    afterDumpTube.setLoggedTubeName(loggedTubeName);
                }
                if (msgDumpInfo.dumpBefore) {
                    LoggingDumpTube beforeDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.Before, context.getTubelineHead());
                    beforeDumpTube.setLoggedTubeName(loggedTubeName);
                    contextImpl.setTubelineHead(beforeDumpTube);
                }
            }
            if (!first) continue;
            contextImpl.setTubelineHead(decorator.decorateClientTail(context.getTubelineHead(), context));
            first = false;
        }
        return decorator.decorateClientHead(context.getTubelineHead(), context);
    }

    @Override
    @NotNull
    public Tube createServer(@NotNull ServerTubeAssemblerContext jaxwsContext) {
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer("Assembling endpoint tubeline for WS endpoint: " + jaxwsContext.getEndpoint().getServiceName() + "::" + jaxwsContext.getEndpoint().getPortName());
        }
        ServerTubelineAssemblyContext context = this.createServerContext(jaxwsContext);
        Collection<TubeCreator> tubeCreators = this.tubelineAssemblyController.getTubeCreators(context);
        for (TubeCreator tubeCreator : tubeCreators) {
            tubeCreator.updateContext(context);
        }
        TubelineAssemblyDecorator decorator = TubelineAssemblyDecorator.composite(ServiceFinder.find(TubelineAssemblyDecorator.class, context.getEndpoint().getContainer()));
        boolean first = true;
        TubelineAssemblyContextImpl contextImpl = (TubelineAssemblyContextImpl)((Object)context);
        for (TubeCreator tubeCreator : tubeCreators) {
            MessageDumpingInfo msgDumpInfo = this.setupMessageDumping(tubeCreator.getMessageDumpPropertyBase(), Side.Endpoint);
            Tube oldTubelineHead = context.getTubelineHead();
            LoggingDumpTube afterDumpTube = null;
            if (msgDumpInfo.dumpAfter) {
                afterDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.After, context.getTubelineHead());
                contextImpl.setTubelineHead(afterDumpTube);
            }
            if (!contextImpl.setTubelineHead(decorator.decorateServer(tubeCreator.createTube(context), context))) {
                if (afterDumpTube != null) {
                    contextImpl.setTubelineHead(oldTubelineHead);
                }
            } else {
                String loggedTubeName = context.getTubelineHead().getClass().getName();
                if (afterDumpTube != null) {
                    afterDumpTube.setLoggedTubeName(loggedTubeName);
                }
                if (msgDumpInfo.dumpBefore) {
                    LoggingDumpTube beforeDumpTube = new LoggingDumpTube(msgDumpInfo.logLevel, LoggingDumpTube.Position.Before, context.getTubelineHead());
                    beforeDumpTube.setLoggedTubeName(loggedTubeName);
                    contextImpl.setTubelineHead(beforeDumpTube);
                }
            }
            if (!first) continue;
            contextImpl.setTubelineHead(decorator.decorateServerTail(context.getTubelineHead(), context));
            first = false;
        }
        return decorator.decorateServerHead(context.getTubelineHead(), context);
    }

    private MessageDumpingInfo setupMessageDumping(String msgDumpSystemPropertyBase, Side side) {
        boolean dumpBefore = false;
        boolean dumpAfter = false;
        Level logLevel = Level.INFO;
        Boolean value = this.getBooleanValue(COMMON_MESSAGE_DUMP_SYSTEM_PROPERTY_BASE);
        if (value != null) {
            dumpBefore = value;
            dumpAfter = value;
        }
        dumpBefore = (value = this.getBooleanValue("com.sun.metro.soap.dump.before")) != null ? value : dumpBefore;
        value = this.getBooleanValue("com.sun.metro.soap.dump.after");
        dumpAfter = value != null ? value : dumpAfter;
        Level levelValue = this.getLevelValue("com.sun.metro.soap.dump.level");
        if (levelValue != null) {
            logLevel = levelValue;
        }
        if ((value = this.getBooleanValue("com.sun.metro.soap.dump." + side.toString())) != null) {
            dumpBefore = value;
            dumpAfter = value;
        }
        dumpBefore = (value = this.getBooleanValue("com.sun.metro.soap.dump." + side.toString() + ".before")) != null ? value : dumpBefore;
        value = this.getBooleanValue("com.sun.metro.soap.dump." + side.toString() + ".after");
        dumpAfter = value != null ? value : dumpAfter;
        levelValue = this.getLevelValue("com.sun.metro.soap.dump." + side.toString() + ".level");
        if (levelValue != null) {
            logLevel = levelValue;
        }
        if ((value = this.getBooleanValue(msgDumpSystemPropertyBase)) != null) {
            dumpBefore = value;
            dumpAfter = value;
        }
        dumpBefore = (value = this.getBooleanValue(msgDumpSystemPropertyBase + ".before")) != null ? value : dumpBefore;
        value = this.getBooleanValue(msgDumpSystemPropertyBase + ".after");
        dumpAfter = value != null ? value : dumpAfter;
        levelValue = this.getLevelValue(msgDumpSystemPropertyBase + ".level");
        if (levelValue != null) {
            logLevel = levelValue;
        }
        if ((value = this.getBooleanValue(msgDumpSystemPropertyBase = msgDumpSystemPropertyBase + "." + side.toString())) != null) {
            dumpBefore = value;
            dumpAfter = value;
        }
        dumpBefore = (value = this.getBooleanValue(msgDumpSystemPropertyBase + ".before")) != null ? value : dumpBefore;
        value = this.getBooleanValue(msgDumpSystemPropertyBase + ".after");
        dumpAfter = value != null ? value : dumpAfter;
        levelValue = this.getLevelValue(msgDumpSystemPropertyBase + ".level");
        if (levelValue != null) {
            logLevel = levelValue;
        }
        return new MessageDumpingInfo(dumpBefore, dumpAfter, logLevel);
    }

    private Boolean getBooleanValue(String propertyName) {
        Boolean retVal = null;
        String stringValue = System.getProperty(propertyName);
        if (stringValue != null) {
            retVal = Boolean.valueOf(stringValue);
            LOGGER.fine(TubelineassemblyMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(propertyName, retVal));
        }
        return retVal;
    }

    private Level getLevelValue(String propertyName) {
        Level retVal = null;
        String stringValue = System.getProperty(propertyName);
        if (stringValue != null) {
            LOGGER.fine(TubelineassemblyMessages.MASM_0018_MSG_LOGGING_SYSTEM_PROPERTY_SET_TO_VALUE(propertyName, stringValue));
            try {
                retVal = Level.parse(stringValue);
            }
            catch (IllegalArgumentException ex) {
                LOGGER.warning(TubelineassemblyMessages.MASM_0019_MSG_LOGGING_SYSTEM_PROPERTY_ILLEGAL_VALUE(propertyName, stringValue), (Throwable)ex);
            }
        }
        return retVal;
    }

    protected ServerTubelineAssemblyContext createServerContext(ServerTubeAssemblerContext jaxwsContext) {
        return new DefaultServerTubelineAssemblyContext(jaxwsContext);
    }

    protected ClientTubelineAssemblyContext createClientContext(ClientTubeAssemblerContext jaxwsContext) {
        return new DefaultClientTubelineAssemblyContext(jaxwsContext);
    }

    private static class MessageDumpingInfo {
        final boolean dumpBefore;
        final boolean dumpAfter;
        final Level logLevel;

        MessageDumpingInfo(boolean dumpBefore, boolean dumpAfter, Level logLevel) {
            this.dumpBefore = dumpBefore;
            this.dumpAfter = dumpAfter;
            this.logLevel = logLevel;
        }
    }

    private static enum Side {
        Client("client"),
        Endpoint("endpoint");

        private final String name;

        private Side(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}

