/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Messages;
import org.apache.batik.bridge.ScriptSecurity;
import org.apache.batik.util.ParsedURL;

public class DefaultScriptSecurity
implements ScriptSecurity {
    public static final String DATA_PROTOCOL = "data";
    public static final String ERROR_CANNOT_ACCESS_DOCUMENT_URL = "DefaultScriptSecurity.error.cannot.access.document.url";
    public static final String ERROR_SCRIPT_FROM_DIFFERENT_URL = "DefaultScriptSecurity.error.script.from.different.url";
    protected SecurityException se;

    @Override
    public void checkLoadScript() {
        if (this.se != null) {
            throw this.se;
        }
    }

    public DefaultScriptSecurity(String scriptType, ParsedURL scriptURL, ParsedURL docURL) {
        if (docURL == null || "application/java-archive".equals(scriptType)) {
            this.se = new SecurityException(Messages.formatMessage(ERROR_CANNOT_ACCESS_DOCUMENT_URL, new Object[]{scriptURL}));
        } else {
            String docHost = docURL.getHost();
            String scriptHost = scriptURL.getHost();
            if (scriptHost == null && scriptURL.getPath() != null) {
                scriptHost = new ParsedURL(scriptURL.getPath()).getHost();
            }
            if (!(docHost == scriptHost || docHost != null && docHost.equals(scriptHost) || docURL.equals((Object)scriptURL) || scriptURL != null && DATA_PROTOCOL.equals(scriptURL.getProtocol()))) {
                this.se = new SecurityException(Messages.formatMessage(ERROR_SCRIPT_FROM_DIFFERENT_URL, new Object[]{scriptURL}));
            }
        }
    }
}

