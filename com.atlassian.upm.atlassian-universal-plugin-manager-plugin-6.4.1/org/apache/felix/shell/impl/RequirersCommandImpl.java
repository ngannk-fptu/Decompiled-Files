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

public class RequirersCommandImpl
implements Command {
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$osgi$service$packageadmin$PackageAdmin;

    public RequirersCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "requirers";
    }

    public String getUsage() {
        return "requirers <id> ...";
    }

    public String getShortDescription() {
        return "list requiring bundles.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        PackageAdmin pa;
        ServiceReference ref = this.m_context.getServiceReference((class$org$osgi$service$packageadmin$PackageAdmin == null ? (class$org$osgi$service$packageadmin$PackageAdmin = RequirersCommandImpl.class$("org.osgi.service.packageadmin.PackageAdmin")) : class$org$osgi$service$packageadmin$PackageAdmin).getName());
        PackageAdmin packageAdmin = pa = ref == null ? null : (PackageAdmin)this.m_context.getService(ref);
        if (pa == null) {
            out.println("PackageAdmin service is unavailable.");
            return;
        }
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        if (st.hasMoreTokens()) {
            boolean separatorNeeded = false;
            while (st.hasMoreTokens()) {
                String id = st.nextToken();
                try {
                    long l = Long.parseLong(id);
                    Bundle bundle = this.m_context.getBundle(l);
                    RequiredBundle[] rbs = pa.getRequiredBundles(bundle.getSymbolicName());
                    for (int i = 0; rbs != null && i < rbs.length; ++i) {
                        if (rbs[i].getBundle() != bundle) continue;
                        if (separatorNeeded) {
                            out.println("");
                        }
                        this.printRequiredBundles(out, bundle, rbs[i].getRequiringBundles());
                        separatorNeeded = true;
                    }
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

    private void printRequiredBundles(PrintStream out, Bundle target, Bundle[] requirers) {
        String title = target + " required by:";
        out.println(title);
        out.println(Util.getUnderlineString(title));
        if (requirers != null && requirers.length > 0) {
            for (int i = 0; i < requirers.length; ++i) {
                out.println(requirers[i]);
            }
        } else {
            out.println("Nothing");
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

