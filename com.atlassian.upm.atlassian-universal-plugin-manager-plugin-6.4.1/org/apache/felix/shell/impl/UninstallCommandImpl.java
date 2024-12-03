/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleException
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.util.StringTokenizer;
import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class UninstallCommandImpl
implements Command {
    private BundleContext m_context = null;

    public UninstallCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "uninstall";
    }

    public String getUsage() {
        return "uninstall <id> [<id> ...]";
    }

    public String getShortDescription() {
        return "uninstall bundle(s).";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        if (st.countTokens() >= 1) {
            while (st.hasMoreTokens()) {
                String id = st.nextToken().trim();
                try {
                    long l = Long.parseLong(id);
                    Bundle bundle = this.m_context.getBundle(l);
                    if (bundle != null) {
                        bundle.uninstall();
                        continue;
                    }
                    err.println("Bundle ID " + id + " is invalid.");
                }
                catch (NumberFormatException ex) {
                    err.println("Unable to parse id '" + id + "'.");
                }
                catch (BundleException ex) {
                    if (ex.getNestedException() != null) {
                        err.println(ex.getNestedException().toString());
                        continue;
                    }
                    err.println(ex.toString());
                }
                catch (Exception ex) {
                    err.println(ex.toString());
                }
            }
        } else {
            err.println("Incorrect number of arguments");
        }
    }
}

