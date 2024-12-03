/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import java.util.Map;
import org.jboss.logging.Logger;

public interface LoggerProvider {
    public Logger getLogger(String var1);

    public void clearMdc();

    public Object putMdc(String var1, Object var2);

    public Object getMdc(String var1);

    public void removeMdc(String var1);

    public Map<String, Object> getMdcMap();

    public void clearNdc();

    public String getNdc();

    public int getNdcDepth();

    public String popNdc();

    public String peekNdc();

    public void pushNdc(String var1);

    public void setNdcMaxDepth(int var1);
}

