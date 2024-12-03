/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.script;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.components.script.Script;
import org.apache.commons.logging.Log;

public class ScriptFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$script$ScriptFactory == null ? (class$org$apache$axis$components$script$ScriptFactory = ScriptFactory.class$("org.apache.axis.components.script.ScriptFactory")) : class$org$apache$axis$components$script$ScriptFactory).getName());
    static /* synthetic */ Class class$org$apache$axis$components$script$ScriptFactory;
    static /* synthetic */ Class class$org$apache$axis$components$script$Script;

    public static Script getScript() {
        Script script = (Script)AxisProperties.newInstance(class$org$apache$axis$components$script$Script == null ? (class$org$apache$axis$components$script$Script = ScriptFactory.class$("org.apache.axis.components.script.Script")) : class$org$apache$axis$components$script$Script);
        log.debug((Object)("axis.Script: " + script.getClass().getName()));
        return script;
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
        AxisProperties.setClassOverrideProperty(class$org$apache$axis$components$script$Script == null ? (class$org$apache$axis$components$script$Script = ScriptFactory.class$("org.apache.axis.components.script.Script")) : class$org$apache$axis$components$script$Script, "axis.Script");
        AxisProperties.setClassDefaults(class$org$apache$axis$components$script$Script == null ? (class$org$apache$axis$components$script$Script = ScriptFactory.class$("org.apache.axis.components.script.Script")) : class$org$apache$axis$components$script$Script, new String[]{"org.apache.axis.components.script.BSF"});
    }
}

