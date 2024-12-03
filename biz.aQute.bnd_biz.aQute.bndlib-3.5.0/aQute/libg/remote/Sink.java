/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.remote;

import aQute.libg.remote.Area;
import aQute.libg.remote.Delta;
import aQute.libg.remote.Welcome;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Sink {
    public static final int version = 1;

    public boolean sync(String var1, Collection<Delta> var2) throws Exception;

    public Welcome getWelcome(int var1);

    public Collection<? extends Area> getAreas() throws Exception;

    public Area getArea(String var1) throws Exception;

    public boolean removeArea(String var1) throws Exception;

    public Area createArea(String var1) throws Exception;

    public boolean launch(String var1, Map<String, String> var2, List<String> var3) throws Exception;

    public int exit(String var1) throws Exception;

    public byte[] view(String var1, String var2) throws Exception;

    public void exit() throws Exception;

    public void input(String var1, String var2) throws Exception;

    public void cancel(String var1) throws Exception;

    public boolean clearCache();
}

