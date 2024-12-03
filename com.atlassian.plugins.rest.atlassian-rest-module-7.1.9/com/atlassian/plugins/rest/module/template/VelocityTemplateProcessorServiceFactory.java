/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.templaterenderer.velocity.one.six.VelocityTemplateRenderer
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.plugins.rest.module.template;

import com.atlassian.plugins.rest.module.template.VelocityTemplateProcessor;
import com.atlassian.templaterenderer.velocity.one.six.VelocityTemplateRenderer;
import com.sun.jersey.spi.template.TemplateProcessor;
import java.io.IOException;
import java.io.OutputStream;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class VelocityTemplateProcessorServiceFactory
implements ServiceFactory,
TemplateProcessor {
    public Object getService(Bundle bundle2, ServiceRegistration serviceRegistration) {
        ServiceTracker serviceTracker = new ServiceTracker(bundle2.getBundleContext(), VelocityTemplateRenderer.class.getName(), null);
        serviceTracker.open();
        return new VelocityTemplateProcessor(serviceTracker);
    }

    public void ungetService(Bundle bundle2, ServiceRegistration serviceRegistration, Object service) {
        ((VelocityTemplateProcessor)service).closeTemplateRendererServiceTracker();
    }

    @Override
    public String resolve(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(String s, Object o, OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }
}

