/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

@ProviderType
public interface Bundle
extends Comparable<Bundle> {
    public static final int UNINSTALLED = 1;
    public static final int INSTALLED = 2;
    public static final int RESOLVED = 4;
    public static final int STARTING = 8;
    public static final int STOPPING = 16;
    public static final int ACTIVE = 32;
    public static final int START_TRANSIENT = 1;
    public static final int START_ACTIVATION_POLICY = 2;
    public static final int STOP_TRANSIENT = 1;
    public static final int SIGNERS_ALL = 1;
    public static final int SIGNERS_TRUSTED = 2;

    public int getState();

    public void start(int var1) throws BundleException;

    public void start() throws BundleException;

    public void stop(int var1) throws BundleException;

    public void stop() throws BundleException;

    public void update(InputStream var1) throws BundleException;

    public void update() throws BundleException;

    public void uninstall() throws BundleException;

    public Dictionary<String, String> getHeaders();

    public long getBundleId();

    public String getLocation();

    public ServiceReference<?>[] getRegisteredServices();

    public ServiceReference<?>[] getServicesInUse();

    public boolean hasPermission(Object var1);

    public URL getResource(String var1);

    public Dictionary<String, String> getHeaders(String var1);

    public String getSymbolicName();

    public Class<?> loadClass(String var1) throws ClassNotFoundException;

    public Enumeration<URL> getResources(String var1) throws IOException;

    public Enumeration<String> getEntryPaths(String var1);

    public URL getEntry(String var1);

    public long getLastModified();

    public Enumeration<URL> findEntries(String var1, String var2, boolean var3);

    public BundleContext getBundleContext();

    public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(int var1);

    public Version getVersion();

    public <A> A adapt(Class<A> var1);

    public File getDataFile(String var1);
}

