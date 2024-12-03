/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.log;

import java.util.List;

public interface Logger {
    public void error(String var1, Object ... var2);

    public void warning(String var1, Object ... var2);

    public void progress(String var1, Object ... var2);

    public List<String> getWarnings();

    public List<String> getErrors();

    public List<String> getProgress();

    public boolean isPedantic();
}

