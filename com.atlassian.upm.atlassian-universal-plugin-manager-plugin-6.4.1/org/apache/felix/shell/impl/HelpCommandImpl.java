/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import org.apache.felix.shell.Command;
import org.apache.felix.shell.ShellService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class HelpCommandImpl
implements Command {
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$apache$felix$shell$ShellService;

    public HelpCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "help";
    }

    public String getUsage() {
        return "help";
    }

    public String getShortDescription() {
        return "display impl commands.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        try {
            ServiceReference ref = this.m_context.getServiceReference((class$org$apache$felix$shell$ShellService == null ? (class$org$apache$felix$shell$ShellService = HelpCommandImpl.class$("org.apache.felix.shell.ShellService")) : class$org$apache$felix$shell$ShellService).getName());
            if (ref != null) {
                ShellService ss = (ShellService)this.m_context.getService(ref);
                String[] cmds = ss.getCommands();
                String[] usage = new String[cmds.length];
                String[] desc = new String[cmds.length];
                int maxUsage = 0;
                for (int i = 0; i < cmds.length; ++i) {
                    usage[i] = ss.getCommandUsage(cmds[i]);
                    desc[i] = ss.getCommandDescription(cmds[i]);
                    if (usage[i] == null || desc[i] == null) continue;
                    maxUsage = Math.max(maxUsage, usage[i].length());
                }
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < cmds.length; ++i) {
                    if (usage[i] == null || desc[i] == null) continue;
                    sb.delete(0, sb.length());
                    for (int j = 0; j < maxUsage - usage[i].length(); ++j) {
                        sb.append(' ');
                    }
                    out.println(usage[i] + sb + " - " + desc[i]);
                }
            } else {
                err.println("No ShellService is unavailable.");
            }
        }
        catch (Exception ex) {
            err.println(ex.toString());
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

