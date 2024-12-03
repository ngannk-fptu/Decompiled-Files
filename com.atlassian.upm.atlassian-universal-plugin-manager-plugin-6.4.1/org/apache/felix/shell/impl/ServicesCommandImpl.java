/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.felix.shell.Command;
import org.apache.felix.shell.impl.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ServicesCommandImpl
implements Command {
    private static final String IN_USE_SWITCH = "-u";
    private static final String SHOW_ALL_SWITCH = "-a";
    private BundleContext m_context = null;
    static /* synthetic */ Class class$org$apache$felix$shell$Command;

    public ServicesCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "services";
    }

    public String getUsage() {
        return "services [-u] [-a] [<id> ...]";
    }

    public String getShortDescription() {
        return "list registered or used services.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        ArrayList<String> tokens = new ArrayList<String>();
        int i = 0;
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
            ++i;
        }
        boolean inUse = false;
        boolean showAll = false;
        if (tokens.contains(IN_USE_SWITCH)) {
            tokens.remove(IN_USE_SWITCH);
            inUse = true;
        }
        if (tokens.contains(SHOW_ALL_SWITCH)) {
            tokens.remove(SHOW_ALL_SWITCH);
            showAll = true;
        }
        if (tokens.size() >= 1) {
            while (tokens.size() > 0) {
                String id = (String)tokens.remove(0);
                boolean headerPrinted = false;
                boolean needSeparator = false;
                try {
                    long l = Long.parseLong(id);
                    Bundle bundle = this.m_context.getBundle(l);
                    if (bundle != null) {
                        ServiceReference[] refs = null;
                        refs = inUse ? bundle.getServicesInUse() : bundle.getRegisteredServices();
                        for (int refIdx = 0; refs != null && refIdx < refs.length; ++refIdx) {
                            String[] objectClass = (String[])refs[refIdx].getProperty("objectClass");
                            boolean print = true;
                            for (int ocIdx = 0; !showAll && ocIdx < objectClass.length; ++ocIdx) {
                                if (!objectClass[ocIdx].equals((class$org$apache$felix$shell$Command == null ? ServicesCommandImpl.class$("org.apache.felix.shell.Command") : class$org$apache$felix$shell$Command).getName())) continue;
                                print = false;
                            }
                            if (!headerPrinted) {
                                headerPrinted = true;
                                String title = Util.getBundleName(bundle);
                                title = inUse ? title + " uses:" : title + " provides:";
                                out.println("");
                                out.println(title);
                                out.println(Util.getUnderlineString(title));
                            }
                            if (!showAll && !print) continue;
                            if (needSeparator) {
                                out.println("----");
                            }
                            String[] keys = refs[refIdx].getPropertyKeys();
                            for (int keyIdx = 0; keys != null && keyIdx < keys.length; ++keyIdx) {
                                Object v = refs[refIdx].getProperty(keys[keyIdx]);
                                out.println(keys[keyIdx] + " = " + Util.getValueString(v));
                            }
                            needSeparator = true;
                        }
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
            if (bundles != null) {
                for (int bundleIdx = 0; bundleIdx < bundles.length; ++bundleIdx) {
                    boolean headerPrinted = false;
                    ServiceReference[] refs = null;
                    refs = inUse ? bundles[bundleIdx].getServicesInUse() : bundles[bundleIdx].getRegisteredServices();
                    for (int refIdx = 0; refs != null && refIdx < refs.length; ++refIdx) {
                        String[] objectClass = (String[])refs[refIdx].getProperty("objectClass");
                        boolean print = true;
                        for (int ocIdx = 0; !showAll && ocIdx < objectClass.length; ++ocIdx) {
                            if (!objectClass[ocIdx].equals((class$org$apache$felix$shell$Command == null ? ServicesCommandImpl.class$("org.apache.felix.shell.Command") : class$org$apache$felix$shell$Command).getName())) continue;
                            print = false;
                        }
                        if (!showAll && !print) continue;
                        if (!headerPrinted) {
                            headerPrinted = true;
                            String title = Util.getBundleName(bundles[bundleIdx]);
                            title = inUse ? title + " uses:" : title + " provides:";
                            out.println("\n" + title);
                            out.println(Util.getUnderlineString(title));
                        }
                        out.println(Util.getValueString(objectClass));
                    }
                }
            } else {
                out.println("There are no registered services.");
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

