/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import java.util.ArrayList;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Mapping;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class NSStack {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$NSStack == null ? (class$org$apache$axis$utils$NSStack = NSStack.class$("org.apache.axis.utils.NSStack")) : class$org$apache$axis$utils$NSStack).getName());
    private Mapping[] stack;
    private int top = 0;
    private int iterator = 0;
    private int currentDefaultNS = -1;
    private boolean optimizePrefixes = true;
    private final boolean traceEnabled = log.isTraceEnabled();
    static /* synthetic */ Class class$org$apache$axis$utils$NSStack;

    public NSStack(boolean optimizePrefixes) {
        this.optimizePrefixes = optimizePrefixes;
        this.stack = new Mapping[32];
        this.stack[0] = null;
    }

    public NSStack() {
        this.stack = new Mapping[32];
        this.stack[0] = null;
    }

    public void push() {
        ++this.top;
        if (this.top >= this.stack.length) {
            Mapping[] newstack = new Mapping[this.stack.length * 2];
            System.arraycopy(this.stack, 0, newstack, 0, this.stack.length);
            this.stack = newstack;
        }
        if (this.traceEnabled) {
            log.trace((Object)("NSPush (" + this.stack.length + ")"));
        }
        this.stack[this.top] = null;
    }

    public void pop() {
        this.clearFrame();
        --this.top;
        if (this.top < this.currentDefaultNS) {
            this.currentDefaultNS = this.top;
            while (this.currentDefaultNS > 0 && (this.stack[this.currentDefaultNS] == null || this.stack[this.currentDefaultNS].getPrefix().length() != 0)) {
                --this.currentDefaultNS;
            }
        }
        if (this.top == 0) {
            if (this.traceEnabled) {
                log.trace((Object)("NSPop (" + Messages.getMessage("empty00") + ")"));
            }
            return;
        }
        if (this.traceEnabled) {
            log.trace((Object)("NSPop (" + this.stack.length + ")"));
        }
    }

    public ArrayList cloneFrame() {
        if (this.stack[this.top] == null) {
            return null;
        }
        ArrayList<Mapping> clone = new ArrayList<Mapping>();
        Mapping map = this.topOfFrame();
        while (map != null) {
            clone.add(map);
            map = this.next();
        }
        return clone;
    }

    private void clearFrame() {
        while (this.stack[this.top] != null) {
            --this.top;
        }
    }

    public Mapping topOfFrame() {
        this.iterator = this.top;
        while (this.stack[this.iterator] != null) {
            --this.iterator;
        }
        ++this.iterator;
        return this.next();
    }

    public Mapping next() {
        if (this.iterator > this.top) {
            return null;
        }
        return this.stack[this.iterator++];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(String namespaceURI, String prefix) {
        int idx = this.top;
        prefix = prefix.intern();
        try {
            int cursor = this.top;
            while (this.stack[cursor] != null) {
                if (this.stack[cursor].getPrefix() == prefix) {
                    this.stack[cursor].setNamespaceURI(namespaceURI);
                    idx = cursor;
                    Object var6_5 = null;
                    if (prefix.length() == 0) {
                        this.currentDefaultNS = idx;
                    }
                    return;
                }
                --cursor;
            }
            this.push();
            this.stack[this.top] = new Mapping(namespaceURI, prefix);
            idx = this.top;
        }
        catch (Throwable throwable) {
            Object var6_7 = null;
            if (prefix.length() == 0) {
                this.currentDefaultNS = idx;
            }
            throw throwable;
        }
        Object var6_6 = null;
        if (prefix.length() == 0) {
            this.currentDefaultNS = idx;
        }
    }

    public String getPrefix(String namespaceURI, boolean noDefault) {
        if (namespaceURI == null || namespaceURI.length() == 0) {
            return null;
        }
        if (this.optimizePrefixes && !noDefault && this.currentDefaultNS > 0 && this.stack[this.currentDefaultNS] != null && namespaceURI == this.stack[this.currentDefaultNS].getNamespaceURI()) {
            return "";
        }
        namespaceURI = namespaceURI.intern();
        block0: for (int cursor = this.top; cursor > 0; --cursor) {
            Mapping map = this.stack[cursor];
            if (map == null || map.getNamespaceURI() != namespaceURI) continue;
            String possiblePrefix = map.getPrefix();
            if (noDefault && possiblePrefix.length() == 0) continue;
            int cursor2 = this.top;
            while (cursor2 != cursor) {
                map = this.stack[cursor2];
                if (map != null && possiblePrefix == map.getPrefix()) continue block0;
                --cursor2;
            }
            return possiblePrefix;
        }
        return null;
    }

    public String getPrefix(String namespaceURI) {
        return this.getPrefix(namespaceURI, false);
    }

    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        prefix = prefix.intern();
        for (int cursor = this.top; cursor > 0; --cursor) {
            Mapping map = this.stack[cursor];
            if (map == null || map.getPrefix() != prefix) continue;
            return map.getNamespaceURI();
        }
        return null;
    }

    public void dump(String dumpPrefix) {
        for (int cursor = this.top; cursor > 0; --cursor) {
            Mapping map = this.stack[cursor];
            if (map == null) {
                log.trace((Object)(dumpPrefix + Messages.getMessage("stackFrame00")));
                continue;
            }
            log.trace((Object)(dumpPrefix + map.getNamespaceURI() + " -> " + map.getPrefix()));
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

