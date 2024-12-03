/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.io;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.EventListener;
import org.eclipse.jetty.io.EndPoint;

public interface Connection
extends Closeable {
    public void addEventListener(EventListener var1);

    public void removeEventListener(EventListener var1);

    public void onOpen();

    public void onClose(Throwable var1);

    public EndPoint getEndPoint();

    @Override
    public void close();

    public boolean onIdleExpired();

    public long getMessagesIn();

    public long getMessagesOut();

    public long getBytesIn();

    public long getBytesOut();

    public long getCreatedTimeStamp();

    public static interface Listener
    extends EventListener {
        public void onOpened(Connection var1);

        public void onClosed(Connection var1);

        public static class Adapter
        implements Listener {
            @Override
            public void onOpened(Connection connection) {
            }

            @Override
            public void onClosed(Connection connection) {
            }
        }
    }

    public static interface UpgradeTo {
        public void onUpgradeTo(ByteBuffer var1);
    }

    public static interface UpgradeFrom {
        public ByteBuffer onUpgradeFrom();
    }
}

