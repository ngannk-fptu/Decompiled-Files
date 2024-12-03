/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 */
package org.apache.felix.shell.impl;

import java.io.PrintStream;
import java.util.StringTokenizer;
import org.apache.felix.shell.CdCommand;
import org.osgi.framework.BundleContext;

public class CdCommandImpl
implements CdCommand {
    private BundleContext m_context = null;
    private String m_baseURL = "";

    public CdCommandImpl(BundleContext context) {
        this.m_context = context;
        String baseURL = this.m_context.getProperty("felix.shell.baseurl");
        this.setBaseURL(baseURL);
    }

    public String getName() {
        return "cd";
    }

    public String getUsage() {
        return "cd [<base-URL>]";
    }

    public String getShortDescription() {
        return "change or display base URL.";
    }

    public void execute(String s, PrintStream out, PrintStream err) {
        StringTokenizer st = new StringTokenizer(s, " ");
        st.nextToken();
        if (st.countTokens() == 0) {
            out.println(this.m_baseURL);
        } else if (st.countTokens() == 1) {
            this.setBaseURL(st.nextToken());
        } else {
            err.println("Incorrect number of arguments");
        }
    }

    public String getBaseURL() {
        return this.m_baseURL;
    }

    public void setBaseURL(String s) {
        this.m_baseURL = s == null ? "" : s;
    }
}

