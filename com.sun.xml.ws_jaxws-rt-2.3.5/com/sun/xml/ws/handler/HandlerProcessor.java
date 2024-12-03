/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.ProtocolException
 *  javax.xml.ws.handler.Handler
 *  javax.xml.ws.handler.MessageContext
 */
package com.sun.xml.ws.handler;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.handler.HandlerTube;
import com.sun.xml.ws.handler.MessageUpdatableContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

abstract class HandlerProcessor<C extends MessageUpdatableContext> {
    boolean isClient;
    static final Logger logger = Logger.getLogger("com.sun.xml.ws.handler");
    private List<? extends Handler> handlers;
    WSBinding binding;
    private int index = -1;
    private HandlerTube owner;

    protected HandlerProcessor(HandlerTube owner, WSBinding binding, List<? extends Handler> chain) {
        this.owner = owner;
        if (chain == null) {
            chain = new ArrayList<Handler>();
        }
        this.handlers = chain;
        this.binding = binding;
    }

    int getIndex() {
        return this.index;
    }

    void setIndex(int i) {
        this.index = i;
    }

    public boolean callHandlersRequest(Direction direction, C context, boolean responseExpected) {
        boolean result;
        this.setDirection(direction, context);
        try {
            result = direction == Direction.OUTBOUND ? this.callHandleMessage(context, 0, this.handlers.size() - 1) : this.callHandleMessage(context, this.handlers.size() - 1, 0);
        }
        catch (ProtocolException pe) {
            logger.log(Level.FINER, "exception in handler chain", pe);
            if (responseExpected) {
                this.insertFaultMessage(context, pe);
                this.reverseDirection(direction, context);
                this.setHandleFaultProperty();
                if (direction == Direction.OUTBOUND) {
                    this.callHandleFault(context, this.getIndex() - 1, 0);
                } else {
                    this.callHandleFault(context, this.getIndex() + 1, this.handlers.size() - 1);
                }
                return false;
            }
            throw pe;
        }
        catch (RuntimeException re) {
            logger.log(Level.FINER, "exception in handler chain", re);
            throw re;
        }
        if (!result) {
            if (responseExpected) {
                this.reverseDirection(direction, context);
                if (direction == Direction.OUTBOUND) {
                    this.callHandleMessageReverse(context, this.getIndex() - 1, 0);
                } else {
                    this.callHandleMessageReverse(context, this.getIndex() + 1, this.handlers.size() - 1);
                }
            } else {
                this.setHandleFalseProperty();
            }
            return false;
        }
        return result;
    }

    public void callHandlersResponse(Direction direction, C context, boolean isFault) {
        this.setDirection(direction, context);
        try {
            if (isFault) {
                if (direction == Direction.OUTBOUND) {
                    this.callHandleFault(context, 0, this.handlers.size() - 1);
                } else {
                    this.callHandleFault(context, this.handlers.size() - 1, 0);
                }
            } else if (direction == Direction.OUTBOUND) {
                this.callHandleMessageReverse(context, 0, this.handlers.size() - 1);
            } else {
                this.callHandleMessageReverse(context, this.handlers.size() - 1, 0);
            }
        }
        catch (RuntimeException re) {
            logger.log(Level.FINER, "exception in handler chain", re);
            throw re;
        }
    }

    private void reverseDirection(Direction origDirection, C context) {
        if (origDirection == Direction.OUTBOUND) {
            ((MessageUpdatableContext)context).put("javax.xml.ws.handler.message.outbound", (Object)false);
        } else {
            ((MessageUpdatableContext)context).put("javax.xml.ws.handler.message.outbound", (Object)true);
        }
    }

    private void setDirection(Direction direction, C context) {
        if (direction == Direction.OUTBOUND) {
            ((MessageUpdatableContext)context).put("javax.xml.ws.handler.message.outbound", (Object)true);
        } else {
            ((MessageUpdatableContext)context).put("javax.xml.ws.handler.message.outbound", (Object)false);
        }
    }

    private void setHandleFaultProperty() {
        this.owner.setHandleFault();
    }

    private void setHandleFalseProperty() {
        this.owner.setHandleFalse();
    }

    abstract void insertFaultMessage(C var1, ProtocolException var2);

    private boolean callHandleMessage(C context, int start, int end) {
        int i;
        try {
            if (start > end) {
                for (i = start; i >= end; --i) {
                    if (this.handlers.get(i).handleMessage(context)) continue;
                    this.setIndex(i);
                    return false;
                }
            } else {
                while (i <= end) {
                    if (!this.handlers.get(i).handleMessage(context)) {
                        this.setIndex(i);
                        return false;
                    }
                    ++i;
                }
            }
        }
        catch (RuntimeException e) {
            this.setIndex(i);
            throw e;
        }
        return true;
    }

    private boolean callHandleMessageReverse(C context, int start, int end) {
        int i;
        if (this.handlers.isEmpty() || start == -1 || start == this.handlers.size()) {
            return false;
        }
        if (start > end) {
            for (i = start; i >= end; --i) {
                if (this.handlers.get(i).handleMessage(context)) continue;
                this.setHandleFalseProperty();
                return false;
            }
        } else {
            while (i <= end) {
                if (!this.handlers.get(i).handleMessage(context)) {
                    this.setHandleFalseProperty();
                    return false;
                }
                ++i;
            }
        }
        return true;
    }

    private boolean callHandleFault(C context, int start, int end) {
        int i;
        if (this.handlers.isEmpty() || start == -1 || start == this.handlers.size()) {
            return false;
        }
        if (start > end) {
            try {
                for (i = start; i >= end; --i) {
                    if (this.handlers.get(i).handleFault(context)) continue;
                    return false;
                }
            }
            catch (RuntimeException re) {
                logger.log(Level.FINER, "exception in handler chain", re);
                throw re;
            }
        }
        try {
            while (i <= end) {
                if (!this.handlers.get(i).handleFault(context)) {
                    return false;
                }
                ++i;
            }
        }
        catch (RuntimeException re) {
            logger.log(Level.FINER, "exception in handler chain", re);
            throw re;
        }
        return true;
    }

    void closeHandlers(MessageContext context, int start, int end) {
        if (this.handlers.isEmpty() || start == -1) {
            return;
        }
        if (start > end) {
            for (int i = start; i >= end; --i) {
                try {
                    this.handlers.get(i).close(context);
                    continue;
                }
                catch (RuntimeException re) {
                    logger.log(Level.INFO, "Exception ignored during close", re);
                }
            }
        } else {
            for (int i = start; i <= end; ++i) {
                try {
                    this.handlers.get(i).close(context);
                    continue;
                }
                catch (RuntimeException re) {
                    logger.log(Level.INFO, "Exception ignored during close", re);
                }
            }
        }
    }

    public static enum Direction {
        OUTBOUND,
        INBOUND;

    }

    public static enum RequestOrResponse {
        REQUEST,
        RESPONSE;

    }
}

