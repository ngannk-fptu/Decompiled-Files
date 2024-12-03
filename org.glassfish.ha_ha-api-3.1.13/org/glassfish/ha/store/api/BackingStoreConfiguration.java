/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.api;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.glassfish.ha.store.util.KeyTransformer;

public class BackingStoreConfiguration<K extends Serializable, V extends Serializable> {
    public static final String BASE_DIRECTORY_NAME = "base.directory.name";
    public static final String NO_OP_PERSISTENCE_TYPE = "noop";
    public static final String START_GMS = "start.gms";
    private String clusterName;
    private String instanceName;
    private String storeName;
    private String shortUniqueName;
    private String storeType;
    private long maxIdleTimeInSeconds = -1L;
    private String relaxVersionCheck;
    private long maxLoadWaitTimeInSeconds;
    private File baseDirectory;
    private Class<K> keyClazz;
    private Class<V> valueClazz;
    private boolean synchronousSave;
    private long typicalPayloadSizeInKiloBytes;
    private Logger logger;
    private Map<String, Object> vendorSpecificSettings = new HashMap<String, Object>();
    private ClassLoader classLoader;
    private boolean startGroupService;
    private KeyTransformer<K> keyTransformer;

    public String getClusterName() {
        return this.clusterName;
    }

    public BackingStoreConfiguration<K, V> setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public BackingStoreConfiguration<K, V> setInstanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public BackingStoreConfiguration<K, V> setStoreName(String storeName) {
        this.storeName = storeName;
        return this;
    }

    public String getShortUniqueName() {
        return this.shortUniqueName;
    }

    public BackingStoreConfiguration<K, V> setShortUniqueName(String shortUniqueName) {
        this.shortUniqueName = shortUniqueName;
        return this;
    }

    public String getStoreType() {
        return this.storeType;
    }

    public BackingStoreConfiguration<K, V> setStoreType(String storeType) {
        this.storeType = storeType;
        return this;
    }

    public long getMaxIdleTimeInSeconds() {
        return this.maxIdleTimeInSeconds;
    }

    public BackingStoreConfiguration<K, V> setMaxIdleTimeInSeconds(long maxIdleTimeInSeconds) {
        this.maxIdleTimeInSeconds = maxIdleTimeInSeconds;
        return this;
    }

    public String getRelaxVersionCheck() {
        return this.relaxVersionCheck;
    }

    public BackingStoreConfiguration<K, V> setRelaxVersionCheck(String relaxVersionCheck) {
        this.relaxVersionCheck = relaxVersionCheck;
        return this;
    }

    public long getMaxLoadWaitTimeInSeconds() {
        return this.maxLoadWaitTimeInSeconds;
    }

    public BackingStoreConfiguration<K, V> setMaxLoadWaitTimeInSeconds(long maxLoadWaitTimeInSeconds) {
        this.maxLoadWaitTimeInSeconds = maxLoadWaitTimeInSeconds;
        return this;
    }

    public File getBaseDirectory() {
        return this.baseDirectory;
    }

    public BackingStoreConfiguration<K, V> setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
        return this;
    }

    public Class<K> getKeyClazz() {
        return this.keyClazz;
    }

    public BackingStoreConfiguration<K, V> setKeyClazz(Class<K> kClazz) {
        this.keyClazz = kClazz;
        return this;
    }

    public Class<V> getValueClazz() {
        return this.valueClazz;
    }

    public BackingStoreConfiguration<K, V> setValueClazz(Class<V> vClazz) {
        this.valueClazz = vClazz;
        return this;
    }

    public boolean isSynchronousSave() {
        return this.synchronousSave;
    }

    public BackingStoreConfiguration<K, V> setSynchronousSave(boolean synchronousSave) {
        this.synchronousSave = synchronousSave;
        return this;
    }

    public long getTypicalPayloadSizeInKiloBytes() {
        return this.typicalPayloadSizeInKiloBytes;
    }

    public BackingStoreConfiguration<K, V> setTypicalPayloadSizeInKiloBytes(long typicalPayloadSizeInKiloBytes) {
        this.typicalPayloadSizeInKiloBytes = typicalPayloadSizeInKiloBytes;
        return this;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public BackingStoreConfiguration<K, V> setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public Map<String, Object> getVendorSpecificSettings() {
        return this.vendorSpecificSettings;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public BackingStoreConfiguration<K, V> setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public boolean getStartGroupService() {
        return this.startGroupService;
    }

    public BackingStoreConfiguration<K, V> setStartGroupService(boolean startGroupService) {
        this.startGroupService = startGroupService;
        return this;
    }

    public String toString() {
        return "BackingStoreConfiguration{clusterName='" + this.clusterName + '\'' + ", instanceName='" + this.instanceName + '\'' + ", storeName='" + this.storeName + '\'' + ", shortUniqueName='" + this.shortUniqueName + '\'' + ", storeType='" + this.storeType + '\'' + ", maxIdleTimeInSeconds=" + this.maxIdleTimeInSeconds + ", relaxVersionCheck='" + this.relaxVersionCheck + '\'' + ", maxLoadWaitTimeInSeconds=" + this.maxLoadWaitTimeInSeconds + ", baseDirectoryName='" + this.baseDirectory + '\'' + ", keyClazz=" + this.keyClazz + ", valueClazz=" + this.valueClazz + ", synchronousSave=" + this.synchronousSave + ", typicalPayloadSizeInKiloBytes=" + this.typicalPayloadSizeInKiloBytes + ", vendorSpecificSettings=" + this.vendorSpecificSettings + '}';
    }
}

