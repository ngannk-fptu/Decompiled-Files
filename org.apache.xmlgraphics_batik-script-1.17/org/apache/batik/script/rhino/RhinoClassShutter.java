/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mozilla.javascript.ClassShutter
 */
package org.apache.batik.script.rhino;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.ClassShutter;

public class RhinoClassShutter
implements ClassShutter {
    public static final List<String> WHITELIST = new ArrayList<String>();

    public boolean visibleToScripts(String fullClassName) {
        for (String v : WHITELIST) {
            if (!fullClassName.matches(v)) continue;
            return true;
        }
        return false;
    }
}

