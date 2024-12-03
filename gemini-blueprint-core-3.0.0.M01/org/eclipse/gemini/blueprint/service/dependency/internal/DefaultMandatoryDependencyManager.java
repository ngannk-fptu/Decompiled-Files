/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.service.dependency.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.service.dependency.internal.MandatoryServiceDependencyManager;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.controller.ExporterControllerUtils;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.controller.ExporterInternalActions;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.support.AbstractOsgiServiceImportFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.internal.controller.ImporterControllerUtils;
import org.eclipse.gemini.blueprint.service.importer.support.internal.controller.ImporterInternalActions;
import org.eclipse.gemini.blueprint.service.importer.support.internal.dependency.ImporterStateListener;
import org.eclipse.gemini.blueprint.util.internal.BeanFactoryUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class DefaultMandatoryDependencyManager
implements MandatoryServiceDependencyManager,
BeanFactoryAware,
DisposableBean {
    private static final Log log = LogFactory.getLog(DefaultMandatoryDependencyManager.class);
    private final ConcurrentMap<String, Object> exportersSeen = new ConcurrentHashMap<String, Object>(4);
    private static final Object VALUE = new Object();
    private final Map<Object, Map<Object, Boolean>> exporterToImporterDeps = new ConcurrentHashMap<Object, Map<Object, Boolean>>(8);
    private final Map<Object, ImporterStateListener> exporterListener = new ConcurrentHashMap<Object, ImporterStateListener>(8);
    private final ConcurrentMap<Object, String> importerToName = new ConcurrentHashMap<Object, String>(8);
    private final Map<Object, String> exporterToName = new ConcurrentHashMap<Object, String>(8);
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void addServiceExporter(Object exporter, String exporterBeanName) {
        Assert.hasText((String)exporterBeanName);
        if (this.exportersSeen.putIfAbsent(exporterBeanName, VALUE) == null) {
            String beanName = exporterBeanName;
            if (this.beanFactory.isFactoryBean(exporterBeanName)) {
                beanName = "&" + exporterBeanName;
            }
            if (!this.beanFactory.isSingleton(beanName)) {
                log.info((Object)("Exporter [" + beanName + "] is not singleton and will not be tracked"));
            } else {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Exporter [" + beanName + "] is being tracked for dependencies"));
                }
                this.exporterToName.put(exporter, exporterBeanName);
                ExporterInternalActions controller = ExporterControllerUtils.getControllerFor(exporter);
                controller.registerServiceAtStartup(false);
                this.discoverDependentImporterFor(exporterBeanName, exporter);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void discoverDependentImporterFor(String exporterBeanName, Object exporter) {
        boolean trace = log.isTraceEnabled();
        String[] importerA = BeanFactoryUtils.getTransitiveDependenciesForBean(this.beanFactory, exporterBeanName, true, OsgiServiceProxyFactoryBean.class);
        String[] importerB = BeanFactoryUtils.getTransitiveDependenciesForBean(this.beanFactory, exporterBeanName, true, OsgiServiceCollectionProxyFactoryBean.class);
        Object[] importerNames = StringUtils.concatenateStringArrays((String[])importerA, (String[])importerB);
        LinkedHashMap<Object, Object> dependingImporters = new LinkedHashMap<Object, Object>(importerNames.length);
        if (trace) {
            log.trace((Object)("Exporter [" + exporterBeanName + "] depends (transitively) on the following importers:" + ObjectUtils.nullSafeToString((Object[])importerNames)));
        }
        ImporterDependencyListener listener = new ImporterDependencyListener(exporter);
        this.exporterListener.put(exporter, listener);
        for (int i = 0; i < importerNames.length; ++i) {
            if (this.beanFactory.isSingleton((String)importerNames[i])) {
                Object importer = this.beanFactory.getBean((String)importerNames[i]);
                if (this.isMandatory(importer)) {
                    dependingImporters.put(importer, importerNames[i]);
                    this.importerToName.putIfAbsent(importer, (String)importerNames[i]);
                    continue;
                }
                if (!trace) continue;
                log.trace((Object)("Importer [" + (String)importerNames[i] + "] is optional; skipping it"));
                continue;
            }
            if (!trace) continue;
            log.trace((Object)("Importer [" + (String)importerNames[i] + "] is a non-singleton; ignoring it"));
        }
        if (trace) {
            log.trace((Object)("After filtering, exporter [" + exporterBeanName + "] depends on importers:" + dependingImporters.values()));
        }
        Set filteredImporters = dependingImporters.keySet();
        Object object = exporter;
        synchronized (object) {
            LinkedHashMap<Object, Boolean> importerStatuses = new LinkedHashMap<Object, Boolean>(filteredImporters.size());
            for (Object importer : filteredImporters) {
                importerStatuses.put(importer, this.isSatisfied(importer));
                this.addListener(importer, listener);
            }
            this.exporterToImporterDeps.put(exporter, importerStatuses);
            if (!this.checkIfExporterShouldStart(exporter, importerStatuses)) {
                this.callUnregisterOnStartup(exporter);
            }
        }
    }

    private boolean checkIfExporterShouldStart(Object exporter, Map<Object, Boolean> importers) {
        if (!importers.containsValue(Boolean.FALSE)) {
            this.startExporter(exporter);
            if (log.isDebugEnabled()) {
                log.trace((Object)("Exporter [" + this.exporterToName.get(exporter) + "] started; all its dependencies are satisfied"));
            }
            return true;
        }
        ArrayList unsatisfiedDependencies = new ArrayList(importers.size());
        for (Map.Entry<Object, Boolean> entry : importers.entrySet()) {
            if (!Boolean.FALSE.equals(entry.getValue())) continue;
            unsatisfiedDependencies.add(this.importerToName.get(entry.getKey()));
        }
        if (log.isTraceEnabled()) {
            log.trace((Object)("Exporter [" + this.exporterToName.get(exporter) + "] not started; there are still unsatisfied dependencies " + unsatisfiedDependencies));
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeServiceExporter(Object bean, String beanName) {
        Map<Object, Boolean> importers;
        if (log.isTraceEnabled()) {
            log.trace((Object)("Removing exporter [" + beanName + "]"));
        }
        ImporterStateListener stateListener = this.exporterListener.remove(bean);
        Object object = bean;
        synchronized (object) {
            importers = this.exporterToImporterDeps.remove(bean);
        }
        if (importers != null) {
            for (Object importer : importers.keySet()) {
                this.removeListener(importer, stateListener);
            }
        }
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, (Object)beanFactory);
        this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }

    public void destroy() {
        this.exportersSeen.clear();
        this.exporterListener.clear();
        this.exporterToImporterDeps.clear();
        this.exporterToName.clear();
        this.importerToName.clear();
    }

    private void startExporter(Object exporter) {
        ExporterControllerUtils.getControllerFor(exporter).registerService();
    }

    private void stopExporter(Object exporter) {
        ExporterControllerUtils.getControllerFor(exporter).unregisterService();
    }

    private void callUnregisterOnStartup(Object exporter) {
        ExporterControllerUtils.getControllerFor(exporter).callUnregisterOnStartup();
    }

    private void addListener(Object importer, ImporterStateListener stateListener) {
        ImporterInternalActions controller = ImporterControllerUtils.getControllerFor(importer);
        controller.addStateListener(stateListener);
    }

    private void removeListener(Object importer, ImporterStateListener stateListener) {
        ImporterInternalActions controller = ImporterControllerUtils.getControllerFor(importer);
        controller.removeStateListener(stateListener);
    }

    private boolean isSatisfied(Object importer) {
        return ImporterControllerUtils.getControllerFor(importer).isSatisfied();
    }

    private boolean isMandatory(Object importer) {
        if (importer instanceof AbstractOsgiServiceImportFactoryBean) {
            return Availability.MANDATORY.equals((Object)((AbstractOsgiServiceImportFactoryBean)importer).getAvailability());
        }
        return false;
    }

    private class ImporterDependencyListener
    implements ImporterStateListener {
        private final Object exporter;
        private final String exporterName;

        private ImporterDependencyListener(Object exporter) {
            this.exporter = exporter;
            this.exporterName = (String)DefaultMandatoryDependencyManager.this.exporterToName.get(exporter);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void importerSatisfied(Object importer, OsgiServiceDependency dependency) {
            boolean trace = log.isTraceEnabled();
            boolean exporterRemoved = false;
            Object object = this.exporter;
            synchronized (object) {
                Map importers = (Map)DefaultMandatoryDependencyManager.this.exporterToImporterDeps.get(this.exporter);
                boolean bl = exporterRemoved = importers == null;
                if (!exporterRemoved) {
                    importers.put(importer, Boolean.TRUE);
                    if (trace) {
                        log.trace((Object)("Importer [" + (String)DefaultMandatoryDependencyManager.this.importerToName.get(importer) + "] is satisfied; checking the rest of the dependencies for exporter " + (String)DefaultMandatoryDependencyManager.this.exporterToName.get(this.exporter)));
                    }
                    DefaultMandatoryDependencyManager.this.checkIfExporterShouldStart(this.exporter, importers);
                }
            }
            if (exporterRemoved && trace) {
                log.trace((Object)("Exporter [" + this.exporterName + "] removed; ignoring dependency [" + dependency.getBeanName() + "] update"));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void importerUnsatisfied(Object importer, OsgiServiceDependency dependency) {
            boolean exporterRemoved = false;
            Object object = this.exporter;
            synchronized (object) {
                Map importers = (Map)DefaultMandatoryDependencyManager.this.exporterToImporterDeps.get(this.exporter);
                boolean bl = exporterRemoved = importers == null;
                if (!exporterRemoved) {
                    importers.put(importer, Boolean.FALSE);
                }
            }
            boolean trace = log.isTraceEnabled();
            if (!exporterRemoved) {
                if (trace) {
                    log.trace((Object)("Exporter [" + this.exporterName + "] stopped; transitive OSGi dependency [" + dependency.getBeanName() + "] is unsatifised"));
                }
                DefaultMandatoryDependencyManager.this.stopExporter(this.exporter);
            } else if (trace) {
                log.trace((Object)("Exporter [" + this.exporterName + "] removed; ignoring dependency [" + dependency.getBeanName() + "] update"));
            }
        }
    }
}

