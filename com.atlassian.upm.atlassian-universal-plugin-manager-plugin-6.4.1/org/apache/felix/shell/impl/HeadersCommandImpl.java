/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.StringTokenizer;
import org.apache.felix.shell.Command;
import org.apache.felix.shell.impl.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class HeadersCommandImpl
implements Command {
    private BundleContext m_context = null;

    public HeadersCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "headers";
    }

    public String getUsage() {
        return "headers [<id> ...]";
    }

    public String getShortDescription() {
        return "display bundle header properties.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        if (st.hasMoreTokens()) {
            while (st.hasMoreTokens()) {
                String id = st.nextToken().trim();
                try {
                    long l = Long.parseLong(id);
                    Bundle bundle = this.m_context.getBundle(l);
                    if (bundle != null) {
                        this.printHeaders(out, bundle);
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
        } else {
            Bundle[] bundles = this.m_context.getBundles();
            for (int i = 0; i < bundles.length; ++i) {
                this.printHeaders(out, bundles[i]);
            }
        }
    }

    private void printHeaders(PrintStream out, Bundle bundle) {
        String title = Util.getBundleName(bundle);
        out.println("\n" + title);
        out.println(Util.getUnderlineString(title));
        Dictionary dict = bundle.getHeaders();
        Enumeration keys = dict.keys();
        while (keys.hasMoreElements()) {
            String k = (String)keys.nextElement();
            Object v = dict.get(k);
            out.println(k + " = " + Util.getValueString(v));
        }
    }
}

