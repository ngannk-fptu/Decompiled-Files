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

public class EmbededScriptSecurity
implements ScriptSecurity {
    public static final String DATA_PROTOCOL = "data";
    public static final String ERROR_CANNOT_ACCESS_DOCUMENT_URL = "DefaultScriptSecurity.error.cannot.access.document.url";
    public static final String ERROR_SCRIPT_NOT_EMBEDED = "EmbededScriptSecurity.error.script.not.embeded";
    protected SecurityException se;

    @Override
    public void checkLoadScript() {
        if (this.se != null) {
            throw this.se;
        }
    }

    public EmbededScriptSecurity(String scriptType, ParsedURL scriptURL, ParsedURL docURL) {
        if (docURL == null) {
            this.se = new SecurityException(Messages.formatMessage(ERROR_CANNOT_ACCESS_DOCUMENT_URL, new Object[]{scriptURL}));
        } else if (!(docURL.equals((Object)scriptURL) || scriptURL != null && DATA_PROTOCOL.equals(scriptURL.getProtocol()))) {
            this.se = new SecurityException(Messages.formatMessage(ERROR_SCRIPT_NOT_EMBEDED, new Object[]{scriptURL}));
        }
    }
}

