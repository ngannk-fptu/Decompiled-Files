/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import org.apache.felix.shell.Command;
import org.osgi.framework.BundleContext;

public class VersionCommandImpl
implements Command {
    private BundleContext m_context = null;

    public VersionCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "version";
    }

    public String getUsage() {
        return "version";
    }

    public String getShortDescription() {
        return "display version of framework.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        out.println(this.m_context.getProperty("felix.version"));
    }
}

