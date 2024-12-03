/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.view;

import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.view.ImportSupport;

@DefaultKey(value="import")
@ValidScope(value={"request"})
public class ImportTool
extends ImportSupport {
    public String read(Object obj) {
        if (obj == null) {
            this.LOG.warn((Object)"ImportTool.read(): url is null!");
            return null;
        }
        String url = String.valueOf(obj).trim();
        if (url.length() == 0) {
            this.LOG.warn((Object)"ImportTool.read(): url is empty string!");
            return null;
        }
        try {
            return this.acquireString(url);
        }
        catch (Exception ex) {
            this.LOG.error((Object)("ImportTool.read(): Exception while aquiring '" + url + "'"), (Throwable)ex);
            return null;
        }
    }
}

