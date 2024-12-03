/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.ServiceReference
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import org.apache.felix.shell.CdCommand;
import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

public class InstallCommandImpl
implements Command {
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$apache$felix$shell$CdCommand;

    public InstallCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "install";
    }

    public String getUsage() {
        return "install <URL> [<URL> ...]";
    }

    public String getShortDescription() {
        return "install bundle(s).";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        if (st.countTokens() >= 1) {
            StringBuffer sb = new StringBuffer();
            while (st.hasMoreTokens()) {
                String location = st.nextToken().trim();
                Bundle bundle = this.install(location, out, err);
                if (bundle == null) continue;
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(bundle.getBundleId());
            }
            if (sb.toString().indexOf(44) > 0) {
                out.println("Bundle IDs: " + sb.toString());
            } else if (sb.length() > 0) {
                out.println("Bundle ID: " + sb.toString());
            }
        } else {
            err.println("Incorrect number of arguments");
        }
    }

    protected Bundle install(String location, PrintStream out, PrintStream err) {
        String abs = this.absoluteLocation(location);
        if (abs == null) {
            err.println("Malformed location: " + location);
        } else {
            try {
                return this.m_context.installBundle(abs, null);
            }
            catch (IllegalStateException ex) {
                err.println(ex.toString());
            }
            catch (BundleException ex) {
                if (ex.getNestedException() != null) {
                    err.println(ex.getNestedException().toString());
                } else {
                    err.println(ex.toString());
                }
            }
            catch (Exception ex) {
                err.println(ex.toString());
            }
        }
        return null;
    }

    private String absoluteLocation(String location) {
        try {
            new URL(location);
        }
        catch (MalformedURLException ex) {
            String baseURL = "";
            try {
                ServiceReference ref = this.m_context.getServiceReference((class$org$apache$felix$shell$CdCommand == null ? (class$org$apache$felix$shell$CdCommand = InstallCommandImpl.class$("org.apache.felix.shell.CdCommand")) : class$org$apache$felix$shell$CdCommand).getName());
                if (ref != null) {
                    CdCommand cd = (CdCommand)this.m_context.getService(ref);
                    baseURL = cd.getBaseURL();
                    baseURL = baseURL == null ? "" : baseURL;
                    this.m_context.ungetService(ref);
                }
                String theURL = baseURL + location;
                new URL(theURL);
                location = theURL;
            }
            catch (Exception ex2) {
                // empty catch block
            }
        }
        return location;
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

