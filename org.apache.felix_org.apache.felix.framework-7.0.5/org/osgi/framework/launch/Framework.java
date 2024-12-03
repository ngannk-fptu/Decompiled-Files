/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.osgi.framework.launch;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

@ProviderType
public interface Framework
extends Bundle {
    public void init() throws BundleException;

    public void init(FrameworkListener ... var1) throws BundleException;

    public FrameworkEvent waitForStop(long var1) throws InterruptedException;

    @Override
    public void start() throws BundleException;

    @Override
    public void start(int var1) throws BundleException;

    @Override
    public void stop() throws BundleException;

    @Override
    public void stop(int var1) throws BundleException;

    @Override
    public void uninstall() throws BundleException;

    @Override
    public void update() throws BundleException;

    @Override
    public void update(InputStream var1) throws BundleException;

    @Override
    public long getBundleId();

    @Override
    public String getLocation();

    @Override
    public String getSymbolicName();

    @Override
    public Enumeration<String> getEntryPaths(String var1);

    @Override
    public URL getEntry(String var1);

    @Override
    public long getLastModified();

    @Override
    public Enumeration<URL> findEntries(String var1, String var2, boolean var3);

    @Override
    public <A> A adapt(Class<A> var1);
}

