/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import java.util.Map;

public interface Actionable {
    public Map<String, Runnable> actions(Object ... var1) throws Exception;

    public String tooltip(Object ... var1) throws Exception;

    public String title(Object ... var1) throws Exception;
}

