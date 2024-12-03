/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ShutdownCommandImpl
implements Command {
    private BundleContext m_context = null;

    public ShutdownCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "shutdown";
    }

    public String getUsage() {
        return "shutdown";
    }

    public String getShortDescription() {
        return "shutdown framework.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        try {
            Bundle bundle = this.m_context.getBundle(0L);
            bundle.stop();
        }
        catch (Exception ex) {
            err.println(ex.toString());
        }
    }
}

