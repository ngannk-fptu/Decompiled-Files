/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  zipkin2.reporter.ReporterMetrics
 *  zipkin2.reporter.Sender
 */
package zipkin2.reporter.beans;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import zipkin2.reporter.ReporterMetrics;
import zipkin2.reporter.Sender;

abstract class BaseAsyncFactoryBean
extends AbstractFactoryBean {
    Sender sender;
    ReporterMetrics metrics;
    Integer messageMaxBytes;
    Integer messageTimeout;
    Integer closeTimeout;
    Integer queuedMaxSpans;
    Integer queuedMaxBytes;

    BaseAsyncFactoryBean() {
    }

    public boolean isSingleton() {
        return true;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void setMetrics(ReporterMetrics metrics) {
        this.metrics = metrics;
    }

    public void setMessageMaxBytes(Integer messageMaxBytes) {
        this.messageMaxBytes = messageMaxBytes;
    }

    public void setMessageTimeout(Integer messageTimeout) {
        this.messageTimeout = messageTimeout;
    }

    public void setCloseTimeout(Integer closeTimeout) {
        this.closeTimeout = closeTimeout;
    }

    public void setQueuedMaxSpans(Integer queuedMaxSpans) {
        this.queuedMaxSpans = queuedMaxSpans;
    }

    public void setQueuedMaxBytes(Integer queuedMaxBytes) {
        this.queuedMaxBytes = queuedMaxBytes;
    }
}

