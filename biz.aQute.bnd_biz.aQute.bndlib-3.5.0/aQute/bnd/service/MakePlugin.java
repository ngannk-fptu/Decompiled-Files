/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Resource;
import java.util.Map;

public interface MakePlugin {
    public Resource make(Builder var1, String var2, Map<String, String> var3) throws Exception;
}

