/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.env;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.env.VaultPropertySourceNotFoundException;
import org.springframework.vault.core.util.KeyValueDelegate;
import org.springframework.vault.core.util.PropertyTransformer;
import org.springframework.vault.core.util.PropertyTransformers;
import org.springframework.vault.support.JsonMapFlattener;
import org.springframework.vault.support.VaultResponse;

public class VaultPropertySource
extends EnumerablePropertySource<VaultOperations> {
    private static Log logger = LogFactory.getLog(VaultPropertySource.class);
    private final String path;
    private final KeyValueDelegate keyValueDelegate;
    private final Map<String, Object> properties = new LinkedHashMap<String, Object>();
    private final PropertyTransformer propertyTransformer;
    private final boolean ignoreSecretNotFound;
    private final Object lock = new Object();

    public VaultPropertySource(VaultOperations vaultOperations, String path) {
        this(path, vaultOperations, path);
    }

    public VaultPropertySource(String name, VaultOperations vaultOperations, String path) {
        this(name, vaultOperations, path, PropertyTransformers.noop());
    }

    public VaultPropertySource(String name, VaultOperations vaultOperations, String path, PropertyTransformer propertyTransformer) {
        this(name, vaultOperations, path, propertyTransformer, true);
    }

    public VaultPropertySource(String name, VaultOperations vaultOperations, String path, PropertyTransformer propertyTransformer, boolean ignoreSecretNotFound) {
        super(name, vaultOperations);
        Assert.hasText(path, "Path name must contain at least one character");
        Assert.isTrue(!path.startsWith("/"), "Path name must not start with a slash (/)");
        Assert.notNull((Object)propertyTransformer, "PropertyTransformer must not be null");
        this.path = path;
        this.keyValueDelegate = new KeyValueDelegate(vaultOperations, LinkedHashMap::new);
        this.propertyTransformer = propertyTransformer.andThen(PropertyTransformers.removeNullProperties());
        this.ignoreSecretNotFound = ignoreSecretNotFound;
        this.loadProperties();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void loadProperties() {
        Object object = this.lock;
        synchronized (object) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Fetching properties from Vault at %s", this.path));
            }
            Map<String, Object> properties = null;
            RuntimeException error = null;
            try {
                properties = this.doGetProperties(this.path);
            }
            catch (RuntimeException e) {
                error = e;
            }
            if (properties == null) {
                String msg = String.format("Vault location [%s] not resolvable", this.path);
                if (!this.ignoreSecretNotFound) {
                    if (error == null) throw new VaultPropertySourceNotFoundException(msg);
                    throw new VaultPropertySourceNotFoundException(msg, error);
                }
                if (!logger.isInfoEnabled()) return;
                logger.info(String.format("%s: %s", msg, error != null ? error.getMessage() : "Not found"));
            } else {
                this.properties.putAll(this.doTransformProperties(properties));
            }
            return;
        }
    }

    @Override
    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    @Override
    public String[] getPropertyNames() {
        Set<String> strings = this.properties.keySet();
        return strings.toArray(new String[strings.size()]);
    }

    @Nullable
    protected Map<String, Object> doGetProperties(String path) throws VaultException {
        VaultResponse vaultResponse = this.keyValueDelegate.isVersioned(path) ? this.keyValueDelegate.getSecret(path) : ((VaultOperations)this.source).read(path);
        if (vaultResponse == null || vaultResponse.getData() == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("No properties found at %s", path));
            }
            return null;
        }
        return this.flattenMap((Map)vaultResponse.getData());
    }

    protected Map<String, Object> doTransformProperties(Map<String, Object> properties) {
        return this.propertyTransformer.transformProperties(properties);
    }

    @Deprecated
    protected Map<String, String> toStringMap(Map<String, Object> data) {
        return JsonMapFlattener.flattenToStringMap(data);
    }

    protected Map<String, Object> flattenMap(Map<String, Object> data) {
        return JsonMapFlattener.flatten(data);
    }
}

