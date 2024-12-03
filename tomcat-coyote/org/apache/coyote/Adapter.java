/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.tomcat.util.net.SocketEvent;

public interface Adapter {
    public void service(Request var1, Response var2) throws Exception;

    public boolean prepare(Request var1, Response var2) throws Exception;

    public boolean asyncDispatch(Request var1, Response var2, SocketEvent var3) throws Exception;

    public void log(Request var1, Response var2, long var3);

    public void checkRecycled(Request var1, Response var2);

    public String getDomain();
}

