/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.cvslib;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;

public class CvsVersion
extends AbstractCvsTask {
    static final long VERSION_1_11_2 = 11102L;
    static final long MULTIPLY = 100L;
    private String clientVersion;
    private String serverVersion;
    private String clientVersionProperty;
    private String serverVersionProperty;

    public String getClientVersion() {
        return this.clientVersion;
    }

    public String getServerVersion() {
        return this.serverVersion;
    }

    public void setClientVersionProperty(String clientVersionProperty) {
        this.clientVersionProperty = clientVersionProperty;
    }

    public void setServerVersionProperty(String serverVersionProperty) {
        this.serverVersionProperty = serverVersionProperty;
    }

    public boolean supportsCvsLogWithSOption() {
        if (this.serverVersion == null) {
            return false;
        }
        StringTokenizer tokenizer = new StringTokenizer(this.serverVersion, ".");
        long counter = 10000L;
        long version = 0L;
        while (tokenizer.hasMoreTokens()) {
            int i;
            String s = tokenizer.nextToken();
            for (i = 0; i < s.length() && Character.isDigit(s.charAt(i)); ++i) {
            }
            String s2 = s.substring(0, i);
            version += counter * Long.parseLong(s2);
            if (counter == 1L) break;
            counter /= 100L;
        }
        return version >= 11102L;
    }

    @Override
    public void execute() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.setOutputStream(bos);
        ByteArrayOutputStream berr = new ByteArrayOutputStream();
        this.setErrorStream(berr);
        this.setCommand("version");
        super.execute();
        String output = bos.toString();
        this.log("Received version response \"" + output + "\"", 4);
        StringTokenizer st = new StringTokenizer(output);
        boolean client = false;
        boolean server = false;
        String cvs = null;
        String cachedVersion = null;
        boolean haveReadAhead = false;
        while (haveReadAhead || st.hasMoreTokens()) {
            String currentToken = haveReadAhead ? cachedVersion : st.nextToken();
            haveReadAhead = false;
            if ("Client:".equals(currentToken)) {
                client = true;
            } else if ("Server:".equals(currentToken)) {
                server = true;
            } else if (currentToken.startsWith("(CVS") && currentToken.endsWith(")")) {
                String string = cvs = currentToken.length() == 5 ? "" : " " + currentToken;
            }
            if (!client && !server && cvs != null && cachedVersion == null && st.hasMoreTokens()) {
                cachedVersion = st.nextToken();
                haveReadAhead = true;
                continue;
            }
            if (client && cvs != null) {
                if (st.hasMoreTokens()) {
                    this.clientVersion = st.nextToken() + cvs;
                }
                client = false;
                cvs = null;
                continue;
            }
            if (server && cvs != null) {
                if (st.hasMoreTokens()) {
                    this.serverVersion = st.nextToken() + cvs;
                }
                server = false;
                cvs = null;
                continue;
            }
            if (!"(client/server)".equals(currentToken) || cvs == null || cachedVersion == null || client || server) continue;
            server = true;
            client = true;
            this.clientVersion = this.serverVersion = cachedVersion + cvs;
            cvs = null;
            cachedVersion = null;
        }
        if (this.clientVersionProperty != null) {
            this.getProject().setNewProperty(this.clientVersionProperty, this.clientVersion);
        }
        if (this.serverVersionProperty != null) {
            this.getProject().setNewProperty(this.serverVersionProperty, this.serverVersion);
        }
    }
}

