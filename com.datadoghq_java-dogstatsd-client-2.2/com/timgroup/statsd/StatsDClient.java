/*
 * Decompiled with CFR 0.152.
 */
package com.timgroup.statsd;

import com.timgroup.statsd.Event;
import com.timgroup.statsd.ServiceCheck;

public interface StatsDClient {
    public void stop();

    public void count(String var1, long var2, String ... var4);

    public void count(String var1, long var2, double var4, String ... var6);

    public void incrementCounter(String var1, String ... var2);

    public void incrementCounter(String var1, double var2, String ... var4);

    public void increment(String var1, String ... var2);

    public void increment(String var1, double var2, String ... var4);

    public void decrementCounter(String var1, String ... var2);

    public void decrementCounter(String var1, double var2, String ... var4);

    public void decrement(String var1, String ... var2);

    public void decrement(String var1, double var2, String ... var4);

    public void recordGaugeValue(String var1, double var2, String ... var4);

    public void recordGaugeValue(String var1, double var2, double var4, String ... var6);

    public void gauge(String var1, double var2, String ... var4);

    public void gauge(String var1, double var2, double var4, String ... var6);

    public void recordGaugeValue(String var1, long var2, String ... var4);

    public void recordGaugeValue(String var1, long var2, double var4, String ... var6);

    public void gauge(String var1, long var2, String ... var4);

    public void gauge(String var1, long var2, double var4, String ... var6);

    public void recordExecutionTime(String var1, long var2, String ... var4);

    public void recordExecutionTime(String var1, long var2, double var4, String ... var6);

    public void time(String var1, long var2, String ... var4);

    public void time(String var1, long var2, double var4, String ... var6);

    public void recordHistogramValue(String var1, double var2, String ... var4);

    public void recordHistogramValue(String var1, double var2, double var4, String ... var6);

    public void histogram(String var1, double var2, String ... var4);

    public void histogram(String var1, double var2, double var4, String ... var6);

    public void recordHistogramValue(String var1, long var2, String ... var4);

    public void recordHistogramValue(String var1, long var2, double var4, String ... var6);

    public void histogram(String var1, long var2, String ... var4);

    public void histogram(String var1, long var2, double var4, String ... var6);

    public void recordEvent(Event var1, String ... var2);

    public void recordServiceCheckRun(ServiceCheck var1);

    public void serviceCheck(ServiceCheck var1);

    public void recordSetValue(String var1, String var2, String ... var3);
}

