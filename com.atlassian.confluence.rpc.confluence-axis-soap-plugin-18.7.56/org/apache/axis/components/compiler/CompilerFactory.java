/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.components.compiler;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.compiler.Compiler;
import org.apache.axis.components.compiler.Javac;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class CompilerFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$components$compiler$CompilerFactory == null ? (class$org$apache$axis$components$compiler$CompilerFactory = CompilerFactory.class$("org.apache.axis.components.compiler.CompilerFactory")) : class$org$apache$axis$components$compiler$CompilerFactory).getName());
    static /* synthetic */ Class class$org$apache$axis$components$compiler$CompilerFactory;
    static /* synthetic */ Class class$org$apache$axis$components$compiler$Compiler;

    public static Compiler getCompiler() {
        Compiler compiler = (Compiler)AxisProperties.newInstance(class$org$apache$axis$components$compiler$Compiler == null ? (class$org$apache$axis$components$compiler$Compiler = CompilerFactory.class$("org.apache.axis.components.compiler.Compiler")) : class$org$apache$axis$components$compiler$Compiler);
        if (compiler == null) {
            log.debug((Object)Messages.getMessage("defaultCompiler"));
            compiler = new Javac();
        }
        log.debug((Object)("axis.Compiler:" + compiler.getClass().getName()));
        return compiler;
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
        AxisProperties.setClassOverrideProperty(class$org$apache$axis$components$compiler$Compiler == null ? (class$org$apache$axis$components$compiler$Compiler = CompilerFactory.class$("org.apache.axis.components.compiler.Compiler")) : class$org$apache$axis$components$compiler$Compiler, "axis.Compiler");
        AxisProperties.setClassDefault(class$org$apache$axis$components$compiler$Compiler == null ? (class$org$apache$axis$components$compiler$Compiler = CompilerFactory.class$("org.apache.axis.components.compiler.Compiler")) : class$org$apache$axis$components$compiler$Compiler, "org.apache.axis.components.compiler.Javac");
    }
}

