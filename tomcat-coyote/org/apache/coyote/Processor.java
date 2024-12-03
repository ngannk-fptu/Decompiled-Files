/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.UpgradeToken;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketWrapperBase;

public interface Processor {
    public AbstractEndpoint.Handler.SocketState process(SocketWrapperBase<?> var1, SocketEvent var2) throws IOException;

    public UpgradeToken getUpgradeToken();

    public boolean isUpgrade();

    public boolean isAsync();

    public void timeoutAsync(long var1);

    public Request getRequest();

    public void recycle();

    public void setSslSupport(SSLSupport var1);

    public ByteBuffer getLeftoverInput();

    public void pause();

    public boolean checkAsyncTimeoutGeneration();
}

