/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.uuid;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.commons.logging.Log;

public abstract class UUIDGenFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$uuid$UUIDGenFactory == null ? (class$org$apache$axis$components$uuid$UUIDGenFactory = UUIDGenFactory.class$("org.apache.axis.components.uuid.UUIDGenFactory")) : class$org$apache$axis$components$uuid$UUIDGenFactory).getName());
    static /* synthetic */ Class class$org$apache$axis$components$uuid$UUIDGenFactory;
    static /* synthetic */ Class class$org$apache$axis$components$uuid$UUIDGen;

    public static UUIDGen getUUIDGen() {
        UUIDGen uuidgen = (UUIDGen)AxisProperties.newInstance(class$org$apache$axis$components$uuid$UUIDGen == null ? (class$org$apache$axis$components$uuid$UUIDGen = UUIDGenFactory.class$("org.apache.axis.components.uuid.UUIDGen")) : class$org$apache$axis$components$uuid$UUIDGen);
        log.debug((Object)("axis.UUIDGenerator:" + uuidgen.getClass().getName()));
        return uuidgen;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        AxisProperties.setClassOverrideProperty(class$org$apache$axis$components$uuid$UUIDGen == null ? (class$org$apache$axis$components$uuid$UUIDGen = UUIDGenFactory.class$("org.apache.axis.components.uuid.UUIDGen")) : class$org$apache$axis$components$uuid$UUIDGen, "axis.UUIDGenerator");
        AxisProperties.setClassDefault(class$org$apache$axis$components$uuid$UUIDGen == null ? (class$org$apache$axis$components$uuid$UUIDGen = UUIDGenFactory.class$("org.apache.axis.components.uuid.UUIDGen")) : class$org$apache$axis$components$uuid$UUIDGen, "org.apache.axis.components.uuid.FastUUIDGen");
    }
}

