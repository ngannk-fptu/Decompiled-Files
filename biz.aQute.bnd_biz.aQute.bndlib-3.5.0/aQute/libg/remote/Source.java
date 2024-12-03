/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.remote;

import aQute.libg.remote.Area;
import aQute.libg.remote.Event;
import java.io.IOException;

public interface Source {
    public byte[] getData(String var1) throws Exception;

    public void event(Event var1, Area var2) throws Exception;

    public void output(String var1, CharSequence var2, boolean var3) throws IOException;
}

