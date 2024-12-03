/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.ScriptSecurity;
import org.apache.batik.util.ParsedURL;

public class RelaxedScriptSecurity
implements ScriptSecurity {
    @Override
    public void checkLoadScript() {
    }

    public RelaxedScriptSecurity(String scriptType, ParsedURL scriptURL, ParsedURL docURL) {
    }
}

