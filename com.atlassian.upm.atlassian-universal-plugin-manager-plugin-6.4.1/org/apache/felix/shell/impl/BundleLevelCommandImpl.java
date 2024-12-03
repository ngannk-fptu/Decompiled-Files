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

public class BundleLevelCommandImpl
implements Command {
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$osgi$service$startlevel$StartLevel;

    public BundleLevelCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "bundlelevel";
    }

    public String getUsage() {
        return "bundlelevel <level> <id> ... | <id>";
    }

    public String getShortDescription() {
        return "set or get bundle start level.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        block19: {
            ServiceReference ref = this.m_context.getServiceReference((class$org$osgi$service$startlevel$StartLevel == null ? (class$org$osgi$service$startlevel$StartLevel = BundleLevelCommandImpl.class$("org.osgi.service.startlevel.StartLevel")) : class$org$osgi$service$startlevel$StartLevel).getName());
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
            if (st.countTokens() == 1) {
                Bundle bundle = null;
                String token = null;
                try {
                    token = st.nextToken();
                    long id = Long.parseLong(token);
                    bundle = this.m_context.getBundle(id);
                    if (bundle != null) {
                        out.println("Bundle " + token + " is level " + sl.getBundleStartLevel(bundle));
                        break block19;
                    }
                    err.println("Bundle ID " + token + " is invalid.");
                }
                catch (NumberFormatException ex) {
                    err.println("Unable to parse integer '" + token + "'.");
                }
                catch (Exception ex) {
                    err.println(ex.toString());
                }
            } else if (st.countTokens() > 1) {
                Bundle bundle = null;
                String token = null;
                int startLevel = -1;
                try {
                    token = st.nextToken();
                    startLevel = Integer.parseInt(token);
                }
                catch (NumberFormatException ex) {
                    err.println("Unable to parse start level '" + token + "'.");
                }
                if (startLevel > 0) {
                    while (st.hasMoreTokens()) {
                        try {
                            token = st.nextToken();
                            long id = Long.parseLong(token);
                            bundle = this.m_context.getBundle(id);
                            if (bundle != null) {
                                sl.setBundleStartLevel(bundle, startLevel);
                                continue;
                            }
                            err.println("Bundle ID '" + token + "' is invalid.");
                        }
                        catch (NumberFormatException ex) {
                            err.println("Unable to parse bundle ID '" + token + "'.");
                        }
                        catch (Exception ex) {
                            err.println(ex.toString());
                        }
                    }
                } else {
                    err.println("Invalid start level.");
                }
            } else {
                err.println("Incorrect number of arguments.");
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

