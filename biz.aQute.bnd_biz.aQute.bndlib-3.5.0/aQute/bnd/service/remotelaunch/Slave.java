/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.remotelaunch;

import java.util.List;

public interface Slave {
    public void sync(String var1, byte[] var2) throws Exception;

    public void update(String var1, byte[] var2) throws Exception;

    public void close() throws Exception;

    public void launch(List<String> var1) throws Exception;

    public String prefix() throws Exception;
}

