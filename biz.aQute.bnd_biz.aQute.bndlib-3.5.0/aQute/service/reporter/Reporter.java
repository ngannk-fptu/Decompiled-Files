/*
 * Decompiled with CFR 0.152.
 */
package aQute.service.reporter;

import aQute.service.reporter.Report;

public interface Reporter
extends Report {
    public SetLocation error(String var1, Object ... var2);

    public SetLocation warning(String var1, Object ... var2);

    @Deprecated
    public void trace(String var1, Object ... var2);

    @Deprecated
    public void progress(float var1, String var2, Object ... var3);

    public SetLocation exception(Throwable var1, String var2, Object ... var3);

    public boolean isPedantic();

    public static interface SetLocation {
        public SetLocation file(String var1);

        public SetLocation header(String var1);

        public SetLocation context(String var1);

        public SetLocation method(String var1);

        public SetLocation line(int var1);

        public SetLocation reference(String var1);

        public SetLocation details(Object var1);

        public Report.Location location();

        public SetLocation length(int var1);
    }
}

