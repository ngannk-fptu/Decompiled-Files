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

import java.io.IOException;
import java.io.InputStream;
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

public class UpdateCommandImpl
implements Command {
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$apache$felix$shell$CdCommand;

    public UpdateCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "update";
    }

    public String getUsage() {
        return "update <id> [<URL>]";
    }

    public String getShortDescription() {
        return "update bundle.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        block13: {
            StringTokenizer st = new StringTokenizer(s, " ");
            st.nextToken();
            if (st.countTokens() == 1 || st.countTokens() == 2) {
                String location;
                String id = st.nextToken().trim();
                String string = location = st.countTokens() == 0 ? null : st.nextToken().trim();
                if (location != null && (location = this.absoluteLocation(location)) == null) {
                    err.println("Malformed location: " + location);
                }
                try {
                    long l = Long.parseLong(id);
                    Bundle bundle = this.m_context.getBundle(l);
                    if (bundle != null) {
                        if (location != null) {
                            InputStream is = new URL(location).openStream();
                            bundle.update(is);
                        } else {
                            bundle.update();
                        }
                        break block13;
                    }
                    err.println("Bundle ID " + id + " is invalid.");
                }
                catch (NumberFormatException ex) {
                    err.println("Unable to parse id '" + id + "'.");
                }
                catch (MalformedURLException ex) {
                    err.println("Unable to parse URL.");
                }
                catch (IOException ex) {
                    err.println("Unable to open input stream: " + ex);
                }
                catch (BundleException ex) {
                    if (ex.getNestedException() != null) {
                        err.println(ex.getNestedException().toString());
                        break block13;
                    }
                    err.println(ex.toString());
                }
                catch (Exception ex) {
                    err.println(ex.toString());
                }
            } else {
                err.println("Incorrect number of arguments");
            }
        }
    }

    private String absoluteLocation(String location) {
        try {
            new URL(location);
        }
        catch (MalformedURLException ex) {
            String baseURL = "";
            try {
                ServiceReference ref = this.m_context.getServiceReference((class$org$apache$felix$shell$CdCommand == null ? (class$org$apache$felix$shell$CdCommand = UpdateCommandImpl.class$("org.apache.felix.shell.CdCommand")) : class$org$apache$felix$shell$CdCommand).getName());
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

