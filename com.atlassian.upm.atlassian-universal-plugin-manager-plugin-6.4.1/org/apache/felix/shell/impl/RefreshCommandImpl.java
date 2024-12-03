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

public class RefreshCommandImpl
implements Command {
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$osgi$service$packageadmin$PackageAdmin;

    public RefreshCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "refresh";
    }

    public String getUsage() {
        return "refresh [<id> ...]";
    }

    public String getShortDescription() {
        return "refresh packages.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        ServiceReference ref;
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        ArrayList<Bundle> bundleList = new ArrayList<Bundle>();
        if (st.hasMoreTokens()) {
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
                catch (Exception ex) {
                    err.println(ex.toString());
                }
            }
        }
        if ((ref = this.m_context.getServiceReference((class$org$osgi$service$packageadmin$PackageAdmin == null ? (class$org$osgi$service$packageadmin$PackageAdmin = RefreshCommandImpl.class$("org.osgi.service.packageadmin.PackageAdmin")) : class$org$osgi$service$packageadmin$PackageAdmin).getName())) == null) {
            out.println("PackageAdmin service is unavailable.");
            return;
        }
        PackageAdmin pa = (PackageAdmin)this.m_context.getService(ref);
        if (pa == null) {
            out.println("PackageAdmin service is unavailable.");
            return;
        }
        pa.refreshPackages(bundleList.size() == 0 ? null : bundleList.toArray(new Bundle[bundleList.size()]));
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

