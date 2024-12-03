/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Messages;
import org.apache.batik.bridge.ScriptSecurity;

public class NoLoadScriptSecurity
implements ScriptSecurity {
    public static final String ERROR_NO_SCRIPT_OF_TYPE_ALLOWED = "NoLoadScriptSecurity.error.no.script.of.type.allowed";
    protected SecurityException se;

    @Override
    public void checkLoadScript() {
        throw this.se;
    }

    public NoLoadScriptSecurity(String scriptType) {
        this.se = new SecurityException(Messages.formatMessage(ERROR_NO_SCRIPT_OF_TYPE_ALLOWED, new Object[]{scriptType}));
    }
}

