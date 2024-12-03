/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.packageadmin.ExportedPackage
 *  org.osgi.service.packageadmin.PackageAdmin
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.util.StringTokenizer;
import org.apache.felix.shell.Command;
import org.apache.felix.shell.impl.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

public class ExportsCommandImpl
implements Command {
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$osgi$service$packageadmin$PackageAdmin;

    public ExportsCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "exports";
    }

    public String getUsage() {
        return "exports <id> ...";
    }

    public String getShortDescription() {
        return "list exported packages.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        PackageAdmin pa;
        ServiceReference ref = this.m_context.getServiceReference((class$org$osgi$service$packageadmin$PackageAdmin == null ? (class$org$osgi$service$packageadmin$PackageAdmin = ExportsCommandImpl.class$("org.osgi.service.packageadmin.PackageAdmin")) : class$org$osgi$service$packageadmin$PackageAdmin).getName());
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
                    ExportedPackage[] exports = pa.getExportedPackages(bundle);
                    if (separatorNeeded) {
                        out.println("");
                    }
                    this.printExports(out, bundle, exports);
                    separatorNeeded = true;
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

    private void printExports(PrintStream out, Bundle target, ExportedPackage[] exports) {
        String title = target + " exports:";
        out.println(title);
        out.println(Util.getUnderlineString(title));
        if (exports != null && exports.length > 0) {
            for (int i = 0; i < exports.length; ++i) {
                out.println(exports[i]);
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

