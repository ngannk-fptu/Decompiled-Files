/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.InvalidSyntaxException
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.FatalBeanException
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 */
package org.eclipse.gemini.blueprint.extender.internal.support;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.OsgiBeanFactoryPostProcessor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class OsgiBeanFactoryPostProcessorAdapter
implements BeanFactoryPostProcessor {
    private static final Log log = LogFactory.getLog(OsgiBeanFactoryPostProcessorAdapter.class);
    private final BundleContext bundleContext;
    private List<OsgiBeanFactoryPostProcessor> osgiPostProcessors;

    public OsgiBeanFactoryPostProcessorAdapter(BundleContext bundleContext, List<OsgiBeanFactoryPostProcessor> postProcessors) {
        this.bundleContext = bundleContext;
        this.osgiPostProcessors = postProcessors;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        boolean trace = log.isTraceEnabled();
        Throwable processingException = null;
        for (OsgiBeanFactoryPostProcessor osgiPostProcessor : this.osgiPostProcessors) {
            if (trace) {
                log.trace((Object)("Calling OsgiBeanFactoryPostProcessor " + osgiPostProcessor + " for bean factory " + beanFactory));
            }
            try {
                osgiPostProcessor.postProcessBeanFactory(this.bundleContext, beanFactory);
            }
            catch (InvalidSyntaxException ex) {
                processingException = ex;
            }
            catch (BundleException ex) {
                processingException = ex;
            }
            if (processingException == null) continue;
            if (log.isDebugEnabled()) {
                log.debug((Object)("PostProcessor " + osgiPostProcessor + " threw exception"), processingException);
            }
            throw new FatalBeanException("Error encountered while executing OSGi post processing", processingException);
        }
    }
}

