/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.ProductFilter
 *  javax.annotation.Nullable
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.spring.scanner.runtime.impl.util;

import com.atlassian.plugin.spring.scanner.ProductFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProductFilterUtil {
    private static ProductFilterUtil instance;
    static final String CLASS_ON_BAMBOO_CLASSPATH = "com.atlassian.bamboo.build.BuildExecutionManager";
    static final String CLASS_ON_BITBUCKET_CLASSPATH = "com.atlassian.bitbucket.repository.RepositoryService";
    static final String CLASS_ON_CONFLUENCE_CLASSPATH = "com.atlassian.confluence.core.ContentEntityManager";
    static final String CLASS_ON_FECRU_CLASSPATH = "com.atlassian.fisheye.spi.services.RepositoryService";
    static final String CLASS_ON_JIRA_CLASSPATH = "com.atlassian.jira.bc.issue.IssueService";
    static final String CLASS_ON_REFAPP_CLASSPATH = "com.atlassian.refapp.api.ConnectionProvider";
    static final String CLASS_ON_STASH_CLASSPATH = "com.atlassian.stash.repository.RepositoryService";
    private static final Logger log;
    private final AtomicReference<ProductFilter> filterForProduct = new AtomicReference();
    private static final Map<String, ProductFilter> PRODUCTS_TO_HOST_CLASSES;

    private ProductFilterUtil() {
    }

    public static ProductFilter getFilterForCurrentProduct(@Nullable BundleContext bundleContext) {
        return ProductFilterUtil.getInstance().getFilterForProduct(bundleContext);
    }

    public ProductFilter getFilterForProduct(@Nullable BundleContext bundleContext) {
        ProductFilter productFilter = this.filterForProduct.get();
        if (productFilter == null) {
            this.filterForProduct.compareAndSet(productFilter, this.detectProduct(bundleContext));
            productFilter = this.filterForProduct.get();
        }
        return productFilter;
    }

    private ProductFilter detectProduct(BundleContext bundleContext) {
        if (bundleContext == null) {
            log.warn("Couldn't detect product due to null bundleContext: will use ProductFilter.ALL");
            return ProductFilter.ALL;
        }
        for (Map.Entry<String, ProductFilter> entry : PRODUCTS_TO_HOST_CLASSES.entrySet()) {
            if (!this.detectService(bundleContext, entry.getKey())) continue;
            if (log.isDebugEnabled()) {
                log.debug("Detected product: {}", (Object)entry.getValue().name());
            }
            return entry.getValue();
        }
        log.warn("Couldn't detect product, no known services found: will use ProductFilter.ALL");
        return ProductFilter.ALL;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean detectService(BundleContext bundleContext, String serviceClassName) {
        ServiceReference serviceReference = null;
        try {
            serviceReference = bundleContext.getServiceReference(serviceClassName);
            boolean bl = serviceReference != null;
            return bl;
        }
        finally {
            if (serviceReference != null) {
                bundleContext.ungetService(serviceReference);
            }
        }
    }

    private static ProductFilterUtil getInstance() {
        if (instance == null) {
            instance = new ProductFilterUtil();
        }
        return instance;
    }

    static {
        log = LoggerFactory.getLogger(ProductFilterUtil.class);
        PRODUCTS_TO_HOST_CLASSES = new HashMap<String, ProductFilter>();
        PRODUCTS_TO_HOST_CLASSES.put(CLASS_ON_BAMBOO_CLASSPATH, ProductFilter.BAMBOO);
        PRODUCTS_TO_HOST_CLASSES.put(CLASS_ON_BITBUCKET_CLASSPATH, ProductFilter.BITBUCKET);
        PRODUCTS_TO_HOST_CLASSES.put(CLASS_ON_CONFLUENCE_CLASSPATH, ProductFilter.CONFLUENCE);
        PRODUCTS_TO_HOST_CLASSES.put(CLASS_ON_FECRU_CLASSPATH, ProductFilter.FECRU);
        PRODUCTS_TO_HOST_CLASSES.put(CLASS_ON_JIRA_CLASSPATH, ProductFilter.JIRA);
        PRODUCTS_TO_HOST_CLASSES.put(CLASS_ON_REFAPP_CLASSPATH, ProductFilter.REFAPP);
        PRODUCTS_TO_HOST_CLASSES.put(CLASS_ON_STASH_CLASSPATH, ProductFilter.STASH);
    }
}

