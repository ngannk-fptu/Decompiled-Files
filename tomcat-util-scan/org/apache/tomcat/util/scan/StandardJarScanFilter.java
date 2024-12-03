/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.JarScanFilter
 *  org.apache.tomcat.JarScanType
 *  org.apache.tomcat.util.file.Matcher
 */
package org.apache.tomcat.util.scan;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.util.file.Matcher;

public class StandardJarScanFilter
implements JarScanFilter {
    private final ReadWriteLock configurationLock = new ReentrantReadWriteLock();
    private static final String defaultSkip;
    private static final String defaultScan;
    private static final Set<String> defaultSkipSet;
    private static final Set<String> defaultScanSet;
    private static final boolean defaultSkipAll;
    private String tldSkip = defaultSkip;
    private String tldScan;
    private final Set<String> tldSkipSet = new HashSet<String>(defaultSkipSet);
    private final Set<String> tldScanSet;
    private boolean defaultTldScan = true;
    private String pluggabilitySkip;
    private String pluggabilityScan;
    private final Set<String> pluggabilitySkipSet;
    private final Set<String> pluggabilityScanSet;
    private boolean defaultPluggabilityScan = true;

    public StandardJarScanFilter() {
        this.tldScan = defaultScan;
        this.tldScanSet = new HashSet<String>(defaultScanSet);
        this.pluggabilitySkip = defaultSkip;
        this.pluggabilitySkipSet = new HashSet<String>(defaultSkipSet);
        this.pluggabilityScan = defaultScan;
        this.pluggabilityScanSet = new HashSet<String>(defaultScanSet);
    }

    public String getTldSkip() {
        return this.tldSkip;
    }

    public void setTldSkip(String tldSkip) {
        this.tldSkip = tldSkip;
        Lock writeLock = this.configurationLock.writeLock();
        writeLock.lock();
        try {
            StandardJarScanFilter.populateSetFromAttribute(tldSkip, this.tldSkipSet);
        }
        finally {
            writeLock.unlock();
        }
    }

    public String getTldScan() {
        return this.tldScan;
    }

    public void setTldScan(String tldScan) {
        this.tldScan = tldScan;
        Lock writeLock = this.configurationLock.writeLock();
        writeLock.lock();
        try {
            StandardJarScanFilter.populateSetFromAttribute(tldScan, this.tldScanSet);
        }
        finally {
            writeLock.unlock();
        }
    }

    public boolean isSkipAll() {
        return defaultSkipAll;
    }

    public boolean isDefaultTldScan() {
        return this.defaultTldScan;
    }

    public void setDefaultTldScan(boolean defaultTldScan) {
        this.defaultTldScan = defaultTldScan;
    }

    public String getPluggabilitySkip() {
        return this.pluggabilitySkip;
    }

    public void setPluggabilitySkip(String pluggabilitySkip) {
        this.pluggabilitySkip = pluggabilitySkip;
        Lock writeLock = this.configurationLock.writeLock();
        writeLock.lock();
        try {
            StandardJarScanFilter.populateSetFromAttribute(pluggabilitySkip, this.pluggabilitySkipSet);
        }
        finally {
            writeLock.unlock();
        }
    }

    public String getPluggabilityScan() {
        return this.pluggabilityScan;
    }

    public void setPluggabilityScan(String pluggabilityScan) {
        this.pluggabilityScan = pluggabilityScan;
        Lock writeLock = this.configurationLock.writeLock();
        writeLock.lock();
        try {
            StandardJarScanFilter.populateSetFromAttribute(pluggabilityScan, this.pluggabilityScanSet);
        }
        finally {
            writeLock.unlock();
        }
    }

    public boolean isDefaultPluggabilityScan() {
        return this.defaultPluggabilityScan;
    }

    public void setDefaultPluggabilityScan(boolean defaultPluggabilityScan) {
        this.defaultPluggabilityScan = defaultPluggabilityScan;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean check(JarScanType jarScanType, String jarName) {
        Lock readLock = this.configurationLock.readLock();
        readLock.lock();
        try {
            Set<String> toScan;
            Set<String> toSkip;
            boolean defaultScan;
            switch (jarScanType) {
                case TLD: {
                    defaultScan = this.defaultTldScan;
                    toSkip = this.tldSkipSet;
                    toScan = this.tldScanSet;
                    break;
                }
                case PLUGGABILITY: {
                    defaultScan = this.defaultPluggabilityScan;
                    toSkip = this.pluggabilitySkipSet;
                    toScan = this.pluggabilityScanSet;
                    break;
                }
                default: {
                    defaultScan = true;
                    toSkip = defaultSkipSet;
                    toScan = defaultScanSet;
                }
            }
            if (defaultScan) {
                if (Matcher.matchName(toSkip, (String)jarName)) {
                    if (Matcher.matchName(toScan, (String)jarName)) {
                        boolean bl = true;
                        return bl;
                    }
                    boolean bl = false;
                    return bl;
                }
                boolean bl = true;
                return bl;
            }
            if (Matcher.matchName(toScan, (String)jarName)) {
                if (Matcher.matchName(toSkip, (String)jarName)) {
                    boolean bl = false;
                    return bl;
                }
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            readLock.unlock();
        }
    }

    private static void populateSetFromAttribute(String attribute, Set<String> set) {
        set.clear();
        if (attribute != null) {
            StringTokenizer tokenizer = new StringTokenizer(attribute, ",");
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken().trim();
                if (token.length() <= 0) continue;
                set.add(token);
            }
        }
    }

    static {
        defaultSkipSet = new HashSet<String>();
        defaultScanSet = new HashSet<String>();
        defaultSkip = System.getProperty("tomcat.util.scan.StandardJarScanFilter.jarsToSkip");
        StandardJarScanFilter.populateSetFromAttribute(defaultSkip, defaultSkipSet);
        defaultScan = System.getProperty("tomcat.util.scan.StandardJarScanFilter.jarsToScan");
        StandardJarScanFilter.populateSetFromAttribute(defaultScan, defaultScanSet);
        defaultSkipAll = (defaultSkipSet.contains("*") || defaultSkipSet.contains("*.jar")) && defaultScanSet.isEmpty();
    }
}

