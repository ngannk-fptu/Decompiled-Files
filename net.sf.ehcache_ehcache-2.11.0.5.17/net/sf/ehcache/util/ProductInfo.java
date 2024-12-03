/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.sf.ehcache.CacheException;

public class ProductInfo {
    private static final String EHCACHE_VERSION_RESOURCE = "/net/sf/ehcache/version.properties";
    private static final String UNKNOWN = "UNKNOWN";
    private final Properties props = new Properties();

    public ProductInfo() {
        this.parseProductInfo(EHCACHE_VERSION_RESOURCE);
    }

    public ProductInfo(String resource) {
        this.parseProductInfo(resource);
    }

    public ProductInfo(InputStream resource) {
        try {
            this.props.load(resource);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (resource != null) {
                try {
                    resource.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private void parseProductInfo(String resource) {
        InputStream in = ProductInfo.class.getResourceAsStream(resource);
        if (in == null) {
            throw new RuntimeException("Can't find resource: " + resource);
        }
        try {
            this.props.load(in);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                in.close();
            }
            catch (IOException iOException) {}
        }
    }

    public String getName() {
        return this.props.getProperty("product-name", UNKNOWN);
    }

    public String getVersion() {
        return this.props.getProperty("version", UNKNOWN);
    }

    public String getBuildJdk() {
        return this.props.getProperty("build-jdk", UNKNOWN);
    }

    public String getBuildTime() {
        return this.props.getProperty("build-time", UNKNOWN);
    }

    public String getBuildRevision() {
        return this.props.getProperty("build-revision", UNKNOWN);
    }

    public String getPatchLevel() {
        return this.props.getProperty("patch-level", UNKNOWN);
    }

    public String getRequiredCoreVersion() {
        return this.props.getProperty("required-core-version");
    }

    public boolean isEnterprise() {
        return Boolean.parseBoolean(this.props.getProperty("enterprise"));
    }

    public void assertRequiredCoreVersionPresent() {
        boolean ignoreVersionCheck = Boolean.getBoolean("terracotta.ehcache.versioncheck.skip");
        String requiredCoreVersion = this.getRequiredCoreVersion();
        if (ignoreVersionCheck || requiredCoreVersion == null) {
            return;
        }
        ProductInfo coreProductInfo = new ProductInfo();
        String coreVersion = coreProductInfo.getVersion();
        if (!coreVersion.equals(requiredCoreVersion)) {
            String msg = this.getName() + " version [" + this.getVersion() + "] only works with ehcache-core version [" + requiredCoreVersion + "] (found version [" + coreVersion + "] on the classpath).  Please make sure both versions are compatible!";
            throw new CacheException(msg);
        }
    }

    public String toString() {
        Object versionString = String.format("%s version %s was built on %s, at revision %s, with jdk %s", this.getName(), this.getVersion(), this.getBuildTime(), this.getBuildRevision(), this.getBuildJdk());
        if (!UNKNOWN.equals(this.getPatchLevel())) {
            versionString = (String)versionString + ". Patch level " + this.getPatchLevel();
        }
        return versionString;
    }
}

