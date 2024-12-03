/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group;

import javax.management.ObjectName;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.jmx.JmxRegistry;

public abstract class ChannelInterceptorBase
implements ChannelInterceptor {
    private ChannelInterceptor next;
    private ChannelInterceptor previous;
    private Channel channel;
    protected int optionFlag = 0;
    private ObjectName oname = null;

    public boolean okToProcess(int messageFlags) {
        if (this.optionFlag == 0) {
            return true;
        }
        return (this.optionFlag & messageFlags) == this.optionFlag;
    }

    @Override
    public final void setNext(ChannelInterceptor next) {
        this.next = next;
    }

    @Override
    public final ChannelInterceptor getNext() {
        return this.next;
    }

    @Override
    public final void setPrevious(ChannelInterceptor previous) {
        this.previous = previous;
    }

    @Override
    public void setOptionFlag(int optionFlag) {
        this.optionFlag = optionFlag;
    }

    @Override
    public final ChannelInterceptor getPrevious() {
        return this.previous;
    }

    @Override
    public int getOptionFlag() {
        return this.optionFlag;
    }

    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        if (this.getNext() != null) {
            this.getNext().sendMessage(destination, msg, payload);
        }
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        if (this.getPrevious() != null) {
            this.getPrevious().messageReceived(msg);
        }
    }

    @Override
    public void memberAdded(Member member) {
        if (this.getPrevious() != null) {
            this.getPrevious().memberAdded(member);
        }
    }

    @Override
    public void memberDisappeared(Member member) {
        if (this.getPrevious() != null) {
            this.getPrevious().memberDisappeared(member);
        }
    }

    @Override
    public void heartbeat() {
        if (this.getNext() != null) {
            this.getNext().heartbeat();
        }
    }

    @Override
    public boolean hasMembers() {
        if (this.getNext() != null) {
            return this.getNext().hasMembers();
        }
        return false;
    }

    @Override
    public Member[] getMembers() {
        if (this.getNext() != null) {
            return this.getNext().getMembers();
        }
        return null;
    }

    @Override
    public Member getMember(Member mbr) {
        if (this.getNext() != null) {
            return this.getNext().getMember(mbr);
        }
        return null;
    }

    @Override
    public Member getLocalMember(boolean incAlive) {
        if (this.getNext() != null) {
            return this.getNext().getLocalMember(incAlive);
        }
        return null;
    }

    @Override
    public void start(int svc) throws ChannelException {
        JmxRegistry jmxRegistry;
        if (this.getNext() != null) {
            this.getNext().start(svc);
        }
        if ((jmxRegistry = JmxRegistry.getRegistry(this.channel)) != null) {
            this.oname = jmxRegistry.registerJmx(",component=Interceptor,interceptorName=" + this.getClass().getSimpleName(), this);
        }
    }

    @Override
    public void stop(int svc) throws ChannelException {
        if (this.getNext() != null) {
            this.getNext().stop(svc);
        }
        if (this.oname != null) {
            JmxRegistry.getRegistry(this.channel).unregisterJmx(this.oname);
            this.oname = null;
        }
        this.channel = null;
    }

    @Override
    public void fireInterceptorEvent(ChannelInterceptor.InterceptorEvent event) {
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}

