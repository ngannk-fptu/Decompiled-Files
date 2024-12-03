/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build;

import java.util.Map;
import java.util.concurrent.Callable;

public interface RunSession {
    public String getName();

    public String getLabel();

    public int getJdb();

    public void stderr(Appendable var1) throws Exception;

    public void stdout(Appendable var1) throws Exception;

    public void stdin(String var1) throws Exception;

    public int launch() throws Exception;

    public void cancel() throws Exception;

    public Map<String, Object> getProperties();

    public int getExitCode();

    public String getHost();

    public int getAgent();

    public void waitTillStarted(long var1) throws InterruptedException;

    public long getTimeout();

    public boolean validate(Callable<Boolean> var1) throws Exception;
}

