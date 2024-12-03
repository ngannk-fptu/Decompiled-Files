/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Stopwatch
 *  org.eclipse.gemini.blueprint.service.importer.ServiceReferenceProxy
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.confluence.lifecycle;

import com.atlassian.fugue.Option;
import com.google.common.base.Stopwatch;
import java.util.concurrent.Callable;
import org.eclipse.gemini.blueprint.service.importer.ServiceReferenceProxy;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceExecutionTemplate<S, R>
implements Callable<Option<R>> {
    private static final Logger log = LoggerFactory.getLogger(ServiceExecutionTemplate.class);
    protected final ServiceReference serviceReference;

    public ServiceExecutionTemplate(ServiceReference serviceReference) {
        this.serviceReference = serviceReference instanceof ServiceReferenceProxy ? ((ServiceReferenceProxy)serviceReference).getTargetServiceReference() : serviceReference;
    }

    protected String serviceReferenceToString() {
        return this.serviceReference.toString();
    }

    @Override
    public Option<R> call() throws Exception {
        Bundle bundle = this.serviceReference.getBundle();
        if (bundle == null) {
            return Option.none();
        }
        Object service = bundle.getBundleContext().getService(this.serviceReference);
        if (service == null) {
            return Option.none();
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        R result = this.execute(service);
        log.debug("{} took {}", new Object[]{this.serviceReferenceToString(), stopwatch});
        if (result == null) {
            return Option.none();
        }
        return Option.some(result);
    }

    protected abstract R execute(S var1);
}

