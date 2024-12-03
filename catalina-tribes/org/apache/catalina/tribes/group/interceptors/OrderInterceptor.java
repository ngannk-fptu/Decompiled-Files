/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group.interceptors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.util.StringManager;

public class OrderInterceptor
extends ChannelInterceptorBase {
    protected static final StringManager sm = StringManager.getManager(OrderInterceptor.class);
    private final Map<Member, Counter> outcounter = new HashMap<Member, Counter>();
    private final Map<Member, Counter> incounter = new HashMap<Member, Counter>();
    private final Map<Member, MessageOrder> incoming = new HashMap<Member, MessageOrder>();
    private long expire = 3000L;
    private boolean forwardExpired = true;
    private int maxQueue = Integer.MAX_VALUE;
    final ReentrantReadWriteLock inLock = new ReentrantReadWriteLock(true);
    final ReentrantReadWriteLock outLock = new ReentrantReadWriteLock(true);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        if (!this.okToProcess(msg.getOptions())) {
            super.sendMessage(destination, msg, payload);
            return;
        }
        ChannelException cx = null;
        for (Member member : destination) {
            try {
                int nr = 0;
                this.outLock.writeLock().lock();
                try {
                    nr = this.incCounter(member);
                }
                finally {
                    this.outLock.writeLock().unlock();
                }
                msg.getMessage().append(nr);
                try {
                    this.getNext().sendMessage(new Member[]{member}, msg, payload);
                }
                finally {
                    msg.getMessage().trim(4);
                }
            }
            catch (ChannelException x) {
                if (cx == null) {
                    cx = x;
                }
                cx.addFaultyMember(x.getFaultyMembers());
            }
        }
        if (cx != null) {
            throw cx;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void messageReceived(ChannelMessage msg) {
        if (!this.okToProcess(msg.getOptions())) {
            super.messageReceived(msg);
            return;
        }
        int msgnr = XByteBuffer.toInt(msg.getMessage().getBytesDirect(), msg.getMessage().getLength() - 4);
        msg.getMessage().trim(4);
        MessageOrder order = new MessageOrder(msgnr, (ChannelMessage)msg.deepclone());
        this.inLock.writeLock().lock();
        try {
            if (this.processIncoming(order)) {
                this.processLeftOvers(msg.getAddress(), false);
            }
        }
        finally {
            this.inLock.writeLock().unlock();
        }
    }

    protected void processLeftOvers(Member member, boolean force) {
        MessageOrder tmp = this.incoming.get(member);
        if (force) {
            Counter cnt = this.getInCounter(member);
            cnt.setCounter(Integer.MAX_VALUE);
        }
        if (tmp != null) {
            this.processIncoming(tmp);
        }
    }

    protected boolean processIncoming(MessageOrder order) {
        boolean empty;
        boolean result = false;
        Member member = order.getMessage().getAddress();
        Counter cnt = this.getInCounter(member);
        MessageOrder tmp = this.incoming.get(member);
        if (tmp != null) {
            order = MessageOrder.add(tmp, order);
        }
        while (order != null && order.getMsgNr() <= cnt.getCounter()) {
            if (order.getMsgNr() == cnt.getCounter()) {
                cnt.inc();
            } else if (order.getMsgNr() > cnt.getCounter()) {
                cnt.setCounter(order.getMsgNr());
            }
            super.messageReceived(order.getMessage());
            order.setMessage(null);
            order = order.next;
        }
        MessageOrder head = order;
        MessageOrder prev = null;
        tmp = order;
        boolean bl = order != null ? order.getCount() >= this.maxQueue : (empty = false);
        while (tmp != null) {
            if (tmp.isExpired(this.expire) || empty) {
                if (tmp == head) {
                    head = tmp.next;
                }
                cnt.setCounter(tmp.getMsgNr() + 1);
                if (this.getForwardExpired()) {
                    super.messageReceived(tmp.getMessage());
                }
                tmp.setMessage(null);
                tmp = tmp.next;
                if (prev != null) {
                    prev.next = tmp;
                }
                result = true;
                continue;
            }
            prev = tmp;
            tmp = tmp.next;
        }
        if (head == null) {
            this.incoming.remove(member);
        } else {
            this.incoming.put(member, head);
        }
        return result;
    }

    @Override
    public void memberAdded(Member member) {
        super.memberAdded(member);
    }

    @Override
    public void memberDisappeared(Member member) {
        this.incounter.remove(member);
        this.outcounter.remove(member);
        this.processLeftOvers(member, true);
        super.memberDisappeared(member);
    }

    protected int incCounter(Member mbr) {
        Counter cnt = this.getOutCounter(mbr);
        return cnt.inc();
    }

    protected Counter getInCounter(Member mbr) {
        Counter cnt = this.incounter.get(mbr);
        if (cnt == null) {
            cnt = new Counter();
            cnt.inc();
            this.incounter.put(mbr, cnt);
        }
        return cnt;
    }

    protected Counter getOutCounter(Member mbr) {
        Counter cnt = this.outcounter.get(mbr);
        if (cnt == null) {
            cnt = new Counter();
            this.outcounter.put(mbr, cnt);
        }
        return cnt;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public void setForwardExpired(boolean forwardExpired) {
        this.forwardExpired = forwardExpired;
    }

    public void setMaxQueue(int maxQueue) {
        this.maxQueue = maxQueue;
    }

    public long getExpire() {
        return this.expire;
    }

    public boolean getForwardExpired() {
        return this.forwardExpired;
    }

    public int getMaxQueue() {
        return this.maxQueue;
    }

    protected static class MessageOrder {
        private final long received = System.currentTimeMillis();
        private MessageOrder next;
        private final int msgNr;
        private ChannelMessage msg = null;

        public MessageOrder(int msgNr, ChannelMessage msg) {
            this.msgNr = msgNr;
            this.msg = msg;
        }

        public boolean isExpired(long expireTime) {
            return System.currentTimeMillis() - this.received > expireTime;
        }

        public ChannelMessage getMessage() {
            return this.msg;
        }

        public void setMessage(ChannelMessage msg) {
            this.msg = msg;
        }

        public void setNext(MessageOrder order) {
            this.next = order;
        }

        public MessageOrder getNext() {
            return this.next;
        }

        public int getCount() {
            int counter = 1;
            MessageOrder tmp = this.next;
            while (tmp != null) {
                ++counter;
                tmp = tmp.next;
            }
            return counter;
        }

        public static MessageOrder add(MessageOrder head, MessageOrder add) {
            if (head == null) {
                return add;
            }
            if (add == null) {
                return head;
            }
            if (head == add) {
                return add;
            }
            if (head.getMsgNr() > add.getMsgNr()) {
                add.next = head;
                return add;
            }
            MessageOrder iter = head;
            MessageOrder prev = null;
            while (iter.getMsgNr() < add.getMsgNr() && iter.next != null) {
                prev = iter;
                iter = iter.next;
            }
            if (iter.getMsgNr() < add.getMsgNr()) {
                add.next = iter.next;
                iter.next = add;
            } else if (iter.getMsgNr() > add.getMsgNr()) {
                prev.next = add;
                add.next = iter;
            } else {
                throw new ArithmeticException(sm.getString("orderInterceptor.messageAdded.sameCounter"));
            }
            return head;
        }

        public int getMsgNr() {
            return this.msgNr;
        }
    }

    protected static class Counter {
        private final AtomicInteger value = new AtomicInteger(0);

        protected Counter() {
        }

        public int getCounter() {
            return this.value.get();
        }

        public void setCounter(int counter) {
            this.value.set(counter);
        }

        public int inc() {
            return this.value.addAndGet(1);
        }
    }
}

