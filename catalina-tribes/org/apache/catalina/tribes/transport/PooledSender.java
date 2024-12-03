/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.transport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.transport.AbstractSender;
import org.apache.catalina.tribes.transport.DataSender;
import org.apache.catalina.tribes.transport.MultiPointSender;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public abstract class PooledSender
extends AbstractSender
implements MultiPointSender {
    private static final Log log = LogFactory.getLog(PooledSender.class);
    protected static final StringManager sm = StringManager.getManager("org.apache.catalina.tribes.transport");
    private final SenderQueue queue = new SenderQueue(this, this.poolSize);
    private int poolSize = 25;
    private long maxWait = 3000L;

    public abstract DataSender getNewDataSender();

    public DataSender getSender() {
        return this.queue.getSender(this.getMaxWait());
    }

    public void returnSender(DataSender sender) {
        sender.keepalive();
        this.queue.returnSender(sender);
    }

    @Override
    public synchronized void connect() throws IOException {
        this.queue.open();
        this.setConnected(true);
    }

    @Override
    public synchronized void disconnect() {
        this.queue.close();
        this.setConnected(false);
    }

    public int getInPoolSize() {
        return this.queue.getInPoolSize();
    }

    public int getInUsePoolSize() {
        return this.queue.getInUsePoolSize();
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
        this.queue.setLimit(poolSize);
    }

    public int getPoolSize() {
        return this.poolSize;
    }

    public long getMaxWait() {
        return this.maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    @Override
    public boolean keepalive() {
        return this.queue == null ? false : this.queue.checkIdleKeepAlive();
    }

    @Override
    public void add(Member member) {
    }

    @Override
    public void remove(Member member) {
    }

    private static class SenderQueue {
        private int limit = 25;
        PooledSender parent = null;
        private List<DataSender> notinuse = null;
        private List<DataSender> inuse = null;
        private boolean isOpen = true;

        SenderQueue(PooledSender parent, int limit) {
            this.limit = limit;
            this.parent = parent;
            this.notinuse = new ArrayList<DataSender>();
            this.inuse = new ArrayList<DataSender>();
        }

        public int getLimit() {
            return this.limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public synchronized int getInUsePoolSize() {
            return this.inuse.size();
        }

        public synchronized int getInPoolSize() {
            return this.notinuse.size();
        }

        public synchronized boolean checkIdleKeepAlive() {
            DataSender[] list = this.notinuse.toArray(new DataSender[0]);
            boolean result = false;
            for (DataSender dataSender : list) {
                result |= dataSender.keepalive();
            }
            return result;
        }

        public synchronized DataSender getSender(long timeout) {
            long start = System.currentTimeMillis();
            while (true) {
                if (!this.isOpen) {
                    throw new IllegalStateException(sm.getString("pooledSender.closed.queue"));
                }
                DataSender sender = null;
                if (this.notinuse.size() == 0 && this.inuse.size() < this.limit) {
                    sender = this.parent.getNewDataSender();
                } else if (this.notinuse.size() > 0) {
                    sender = this.notinuse.remove(0);
                }
                if (sender != null) {
                    this.inuse.add(sender);
                    return sender;
                }
                long delta = System.currentTimeMillis() - start;
                if (delta > timeout && timeout > 0L) {
                    return null;
                }
                try {
                    this.wait(Math.max(timeout - delta, 1L));
                }
                catch (InterruptedException interruptedException) {
                }
            }
        }

        public synchronized void returnSender(DataSender sender) {
            block5: {
                if (!this.isOpen) {
                    sender.disconnect();
                    return;
                }
                this.inuse.remove(sender);
                if (this.notinuse.size() < this.getLimit()) {
                    this.notinuse.add(sender);
                } else {
                    try {
                        sender.disconnect();
                    }
                    catch (Exception e) {
                        if (!log.isDebugEnabled()) break block5;
                        log.debug((Object)sm.getString("PooledSender.senderDisconnectFail"), (Throwable)e);
                    }
                }
            }
            this.notifyAll();
        }

        public synchronized void close() {
            DataSender sender;
            this.isOpen = false;
            Object[] unused = this.notinuse.toArray();
            Object[] used = this.inuse.toArray();
            for (Object value : unused) {
                sender = (DataSender)value;
                sender.disconnect();
            }
            for (Object o : used) {
                sender = (DataSender)o;
                sender.disconnect();
            }
            this.notinuse.clear();
            this.inuse.clear();
            this.notifyAll();
        }

        public synchronized void open() {
            this.isOpen = true;
            this.notifyAll();
        }
    }
}

