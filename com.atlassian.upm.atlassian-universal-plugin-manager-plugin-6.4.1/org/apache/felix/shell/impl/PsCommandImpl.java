/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.startlevel.StartLevel
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.util.StringTokenizer;
import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;

public class PsCommandImpl
implements Command {
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$osgi$service$startlevel$StartLevel;

    public PsCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "ps";
    }

    public String getUsage() {
        return "ps [-l | -s | -u]";
    }

    public String getShortDescription() {
        return "list installed bundles.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        Bundle[] bundles;
        ServiceReference ref = this.m_context.getServiceReference((class$org$osgi$service$startlevel$StartLevel == null ? (class$org$osgi$service$startlevel$StartLevel = PsCommandImpl.class$("org.osgi.service.startlevel.StartLevel")) : class$org$osgi$service$startlevel$StartLevel).getName());
        StartLevel sl = null;
        if (ref != null) {
            sl = (StartLevel)this.m_context.getService(ref);
        }
        if (sl == null) {
            out.println("StartLevel service is unavailable.");
        }
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        boolean showLoc = false;
        boolean showSymbolic = false;
        boolean showUpdate = false;
        if (st.countTokens() >= 1) {
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                if (token.equals("-l")) {
                    showLoc = true;
                    continue;
                }
                if (token.equals("-s")) {
                    showSymbolic = true;
                    continue;
                }
                if (!token.equals("-u")) continue;
                showUpdate = true;
            }
        }
        if ((bundles = this.m_context.getBundles()) != null) {
            if (sl != null) {
                out.println("START LEVEL " + sl.getStartLevel());
            }
            String msg = " Name";
            if (showLoc) {
                msg = " Location";
            } else if (showSymbolic) {
                msg = " Symbolic name";
            } else if (showUpdate) {
                msg = " Update location";
            }
            String level = sl == null ? "" : "  Level ";
            out.println("   ID   State       " + level + msg);
            for (int i = 0; i < bundles.length; ++i) {
                String name = (String)bundles[i].getHeaders().get("Bundle-Name");
                name = name == null ? bundles[i].getSymbolicName() : name;
                String string = name = name == null ? bundles[i].getLocation() : name;
                if (showLoc) {
                    name = bundles[i].getLocation();
                } else if (showSymbolic) {
                    name = bundles[i].getSymbolicName();
                    name = name == null ? "<no symbolic name>" : name;
                } else if (showUpdate) {
                    name = (String)bundles[i].getHeaders().get("Bundle-UpdateLocation");
                    name = name == null ? bundles[i].getLocation() : name;
                }
                String version = (String)bundles[i].getHeaders().get("Bundle-Version");
                name = !showLoc && !showUpdate && version != null ? name + " (" + version + ")" : name;
                long l = bundles[i].getBundleId();
                String id = String.valueOf(l);
                level = sl == null ? "1" : String.valueOf(sl.getBundleStartLevel(bundles[i]));
                while (level.length() < 5) {
                    level = " " + level;
                }
                while (id.length() < 4) {
                    id = " " + id;
                }
                out.println("[" + id + "] [" + this.getStateString(bundles[i].getState()) + "] [" + level + "] " + name);
            }
        } else {
            out.println("There are no installed bundles.");
        }
    }

    public String getStateString(int i) {
        if (i == 32) {
            return "Active     ";
        }
        if (i == 2) {
            return "Installed  ";
        }
        if (i == 4) {
            return "Resolved   ";
        }
        if (i == 8) {
            return "Starting   ";
        }
        if (i == 16) {
            return "Stopping   ";
        }
        return "Unknown    ";
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

