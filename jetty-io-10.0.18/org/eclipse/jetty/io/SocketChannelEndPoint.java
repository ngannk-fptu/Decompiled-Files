/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.io.SelectableChannelEndPoint;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketChannelEndPoint
extends SelectableChannelEndPoint {
    private static final Logger LOG = LoggerFactory.getLogger(SocketChannelEndPoint.class);

    public SocketChannelEndPoint(SocketChannel channel, ManagedSelector selector, SelectionKey key, Scheduler scheduler) {
        super(scheduler, channel, selector, key);
    }

    @Override
    public SocketChannel getChannel() {
        return (SocketChannel)super.getChannel();
    }

    @Override
    public SocketAddress getRemoteSocketAddress() {
        try {
            return this.getChannel().getRemoteAddress();
        }
        catch (Throwable x) {
            LOG.trace("Could not retrieve remote socket address", x);
            return null;
        }
    }

    @Override
    protected void doShutdownOutput() {
        block2: {
            try {
                this.getChannel().shutdownOutput();
            }
            catch (Throwable x) {
                if (!LOG.isDebugEnabled()) break block2;
                LOG.debug("Could not shutdown output for {}", (Object)this.getChannel(), (Object)x);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int fill(ByteBuffer buffer) throws IOException {
        int filled;
        if (this.isInputShutdown()) {
            return -1;
        }
        int pos = BufferUtil.flipToFill((ByteBuffer)buffer);
        try {
            filled = this.getChannel().read(buffer);
            if (filled > 0) {
                this.notIdle();
            } else if (filled == -1) {
                this.shutdownInput();
            }
        }
        catch (IOException e) {
            LOG.debug("Unable to shutdown output", (Throwable)e);
            this.shutdownInput();
            filled = -1;
        }
        finally {
            BufferUtil.flipToFlush((ByteBuffer)buffer, (int)pos);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("filled {} {}", (Object)filled, (Object)BufferUtil.toDetailString((ByteBuffer)buffer));
        }
        return filled;
    }

    @Override
    public boolean flush(ByteBuffer ... buffers) throws IOException {
        long flushed;
        try {
            flushed = this.getChannel().write(buffers);
            if (LOG.isDebugEnabled()) {
                LOG.debug("flushed {} {}", (Object)flushed, (Object)this);
            }
        }
        catch (IOException e) {
            throw new EofException(e);
        }
        if (flushed > 0L) {
            this.notIdle();
        }
        for (ByteBuffer b : buffers) {
            if (BufferUtil.isEmpty((ByteBuffer)b)) continue;
            return false;
        }
        return true;
    }
}

