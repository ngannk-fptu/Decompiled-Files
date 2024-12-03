/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.MDC
 *  org.apache.log4j.NDC
 */
package org.jboss.logging;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.jboss.logging.Log4jLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.LoggerProvider;

final class Log4jLoggerProvider
implements LoggerProvider {
    Log4jLoggerProvider() {
    }

    @Override
    public Logger getLogger(String name) {
        return new Log4jLogger("".equals(name) ? "ROOT" : name);
    }

    @Override
    public void clearMdc() {
        MDC.clear();
    }

    @Override
    public Object getMdc(String key) {
        return MDC.get((String)key);
    }

    @Override
    public Map<String, Object> getMdcMap() {
        Hashtable map = MDC.getContext();
        return map == null ? Collections.emptyMap() : map;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object putMdc(String key, Object val) {
        try {
            Object object = MDC.get((String)key);
            return object;
        }
        finally {
            MDC.put((String)key, (Object)val);
        }
    }

    @Override
    public void removeMdc(String key) {
        MDC.remove((String)key);
    }

    @Override
    public void clearNdc() {
        NDC.remove();
    }

    @Override
    public String getNdc() {
        return NDC.get();
    }

    @Override
    public int getNdcDepth() {
        return NDC.getDepth();
    }

    @Override
    public String peekNdc() {
        return NDC.peek();
    }

    @Override
    public String popNdc() {
        return NDC.pop();
    }

    @Override
    public void pushNdc(String message) {
        NDC.push((String)message);
    }

    @Override
    public void setNdcMaxDepth(int maxDepth) {
        NDC.setMaxDepth((int)maxDepth);
    }
}

