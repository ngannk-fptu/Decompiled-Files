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
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.felix.shell.Command;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class StopCommandImpl
implements Command {
    private static final String TRANSIENT_SWITCH = "-t";
    private BundleContext m_context = null;

    public StopCommandImpl(BundleContext context) {
        this.m_context = context;
    }

    public String getName() {
        return "stop";
    }

    public String getUsage() {
        return "stop [-t] <id> [<id> ...]";
    }

    public String getShortDescription() {
        return "stop bundle(s).";
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
        boolean isTransient = false;
        if (tokens.contains(TRANSIENT_SWITCH)) {
            tokens.remove(TRANSIENT_SWITCH);
            isTransient = true;
        }
        if (tokens.size() >= 1) {
            while (tokens.size() > 0) {
                String id = ((String)tokens.remove(0)).trim();
                try {
                    long l = Long.parseLong(id);
                    Bundle bundle = this.m_context.getBundle(l);
                    if (bundle != null) {
                        bundle.stop(isTransient ? 1 : 0);
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

