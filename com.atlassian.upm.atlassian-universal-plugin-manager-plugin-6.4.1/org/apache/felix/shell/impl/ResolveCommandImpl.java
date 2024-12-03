/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.packageadmin.PackageAdmin
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public class ResolveCommandImpl
implements Command {
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$osgi$service$packageadmin$PackageAdmin;

    public ResolveCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "resolve";
    }

    public String getUsage() {
        return "resolve [<id> ...]";
    }

    public String getShortDescription() {
        return "attempt to resolve the specified bundles.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        ServiceReference ref = this.m_context.getServiceReference((class$org$osgi$service$packageadmin$PackageAdmin == null ? (class$org$osgi$service$packageadmin$PackageAdmin = ResolveCommandImpl.class$("org.osgi.service.packageadmin.PackageAdmin")) : class$org$osgi$service$packageadmin$PackageAdmin).getName());
        if (ref == null) {
            out.println("PackageAdmin service is unavailable.");
            return;
        }
        PackageAdmin pa = (PackageAdmin)this.m_context.getService(ref);
        if (pa == null) {
            out.println("PackageAdmin service is unavailable.");
            return;
        }
        Bundle[] bundles = null;
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        if (st.countTokens() >= 1) {
            ArrayList<Bundle> bundleList = new ArrayList<Bundle>();
            while (st.hasMoreTokens()) {
                String id = st.nextToken().trim();
                try {
                    long l = Long.parseLong(id);
                    Bundle bundle = this.m_context.getBundle(l);
                    if (bundle != null) {
                        bundleList.add(bundle);
                        continue;
                    }
                    err.println("Bundle ID " + id + " is invalid.");
                }
                catch (NumberFormatException ex) {
                    err.println("Unable to parse id '" + id + "'.");
                }
            }
            if (bundleList.size() > 0) {
                bundles = bundleList.toArray(new Bundle[bundleList.size()]);
            }
        }
        pa.resolveBundles(bundles);
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

