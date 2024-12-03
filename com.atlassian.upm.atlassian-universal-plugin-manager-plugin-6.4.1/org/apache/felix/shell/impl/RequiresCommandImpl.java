/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.packageadmin.PackageAdmin
 *  org.osgi.service.packageadmin.RequiredBundle
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.util.StringTokenizer;
import org.apache.felix.shell.Command;
import org.apache.felix.shell.impl.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;

public class RequiresCommandImpl
implements Command {
    private final BundleContext m_context;
    private ServiceReference m_ref = null;
    static /* synthetic */ Class class$org$osgi$service$packageadmin$PackageAdmin;

    public RequiresCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "requires";
    }

    public String getUsage() {
        return "requires <id> ...";
    }

    public String getShortDescription() {
        return "list required bundles.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        if (st.hasMoreTokens()) {
            boolean separatorNeeded = false;
            while (st.hasMoreTokens()) {
                String id = st.nextToken().trim();
                try {
                    long l = Long.parseLong(id);
                    Bundle bundle = this.m_context.getBundle(l);
                    if (bundle != null) {
                        if (separatorNeeded) {
                            out.println("");
                        }
                        this.getImportedPackages(bundle, out, err);
                        separatorNeeded = true;
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
    }

    private void getImportedPackages(Bundle bundle, PrintStream out, PrintStream err) {
        PackageAdmin pa = this.getPackageAdmin();
        if (pa == null) {
            out.println("PackageAdmin service is unavailable.");
        } else {
            RequiredBundle[] rbs = pa.getRequiredBundles(null);
            String title = bundle + " requires:";
            out.println(title);
            out.println(Util.getUnderlineString(title));
            boolean found = false;
            for (int rbIdx = 0; rbIdx < rbs.length; ++rbIdx) {
                Bundle[] requirers = rbs[rbIdx].getRequiringBundles();
                for (int reqIdx = 0; requirers != null && reqIdx < requirers.length; ++reqIdx) {
                    if (requirers[reqIdx] != bundle) continue;
                    out.println(rbs[reqIdx]);
                    found = true;
                }
            }
            if (!found) {
                out.println("Nothing");
            }
            this.ungetPackageAdmin();
        }
    }

    private PackageAdmin getPackageAdmin() {
        PackageAdmin pa = null;
        this.m_ref = this.m_context.getServiceReference((class$org$osgi$service$packageadmin$PackageAdmin == null ? (class$org$osgi$service$packageadmin$PackageAdmin = RequiresCommandImpl.class$("org.osgi.service.packageadmin.PackageAdmin")) : class$org$osgi$service$packageadmin$PackageAdmin).getName());
        if (this.m_ref != null) {
            pa = (PackageAdmin)this.m_context.getService(this.m_ref);
        }
        return pa;
    }

    private void ungetPackageAdmin() {
        this.m_context.ungetService(this.m_ref);
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

