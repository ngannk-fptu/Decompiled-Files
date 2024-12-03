/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

public interface JmxEventEmitterMBean {
    public String getEvent();

    public void setEvent(String var1);

    public long send(Object var1);
}

