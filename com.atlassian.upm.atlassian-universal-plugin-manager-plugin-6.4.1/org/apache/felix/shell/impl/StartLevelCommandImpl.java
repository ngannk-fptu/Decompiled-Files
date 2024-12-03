/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.startlevel.StartLevel
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.util.StringTokenizer;
import org.apache.felix.shell.Command;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;

public class StartLevelCommandImpl
implements Command {
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$osgi$service$startlevel$StartLevel;

    public StartLevelCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "startlevel";
    }

    public String getUsage() {
        return "startlevel [<level>]";
    }

    public String getShortDescription() {
        return "get or set framework start level.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        ServiceReference ref = this.m_context.getServiceReference((class$org$osgi$service$startlevel$StartLevel == null ? (class$org$osgi$service$startlevel$StartLevel = StartLevelCommandImpl.class$("org.osgi.service.startlevel.StartLevel")) : class$org$osgi$service$startlevel$StartLevel).getName());
        if (ref == null) {
            out.println("StartLevel service is unavailable.");
            return;
        }
        StartLevel sl = (StartLevel)this.m_context.getService(ref);
        if (sl == null) {
            out.println("StartLevel service is unavailable.");
            return;
        }
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        if (st.countTokens() == 0) {
            out.println("Level " + sl.getStartLevel());
        } else if (st.countTokens() >= 1) {
            String levelStr = st.nextToken().trim();
            try {
                int level = Integer.parseInt(levelStr);
                sl.setStartLevel(level);
            }
            catch (NumberFormatException ex) {
                err.println("Unable to parse integer '" + levelStr + "'.");
            }
            catch (Exception ex) {
                err.println(ex.toString());
            }
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

