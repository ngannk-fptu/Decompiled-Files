/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.ThreadContext
 */
package org.jboss.logging;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.ThreadContext;
import org.jboss.logging.Log4j2Logger;
import org.jboss.logging.LoggerProvider;

final class Log4j2LoggerProvider
implements LoggerProvider {
    Log4j2LoggerProvider() {
    }

    @Override
    public Log4j2Logger getLogger(String name) {
        return new Log4j2Logger(name);
    }

    @Override
    public void clearMdc() {
        ThreadContext.clearMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object putMdc(String key, Object value) {
        try {
            String string = ThreadContext.get((String)key);
            return string;
        }
        finally {
            ThreadContext.put((String)key, (String)String.valueOf(value));
        }
    }

    @Override
    public Object getMdc(String key) {
        return ThreadContext.get((String)key);
    }

    @Override
    public void removeMdc(String key) {
        ThreadContext.remove((String)key);
    }

    @Override
    public Map<String, Object> getMdcMap() {
        return new HashMap<String, Object>(ThreadContext.getImmutableContext());
    }

    @Override
    public void clearNdc() {
        ThreadContext.clearStack();
    }

    @Override
    public String getNdc() {
        return ThreadContext.peek();
    }

    @Override
    public int getNdcDepth() {
        return ThreadContext.getDepth();
    }

    @Override
    public String popNdc() {
        return ThreadContext.pop();
    }

    @Override
    public String peekNdc() {
        return ThreadContext.peek();
    }

    @Override
    public void pushNdc(String message) {
        ThreadContext.push((String)message);
    }

    @Override
    public void setNdcMaxDepth(int maxDepth) {
        ThreadContext.trim((int)maxDepth);
    }
}

