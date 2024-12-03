/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.ssl;

import java.io.File;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Scanner;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyStoreScanner
extends ContainerLifeCycle
implements Scanner.DiscreteListener {
    private static final Logger LOG = LoggerFactory.getLogger(KeyStoreScanner.class);
    private final SslContextFactory sslContextFactory;
    private final File keystoreFile;
    private final Scanner _scanner;

    public KeyStoreScanner(SslContextFactory sslContextFactory) {
        this.sslContextFactory = sslContextFactory;
        try {
            Resource keystoreResource = sslContextFactory.getKeyStoreResource();
            File monitoredFile = keystoreResource.getFile();
            if (monitoredFile == null || !monitoredFile.exists()) {
                throw new IllegalArgumentException("keystore file does not exist");
            }
            if (monitoredFile.isDirectory()) {
                throw new IllegalArgumentException("expected keystore file not directory");
            }
            this.keystoreFile = monitoredFile;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Monitored Keystore File: {}", (Object)monitoredFile);
            }
        }
        catch (IOException e) {
            throw new IllegalArgumentException("could not obtain keystore file", e);
        }
        File parentFile = this.keystoreFile.getParentFile();
        if (!parentFile.exists() || !parentFile.isDirectory()) {
            throw new IllegalArgumentException("error obtaining keystore dir");
        }
        this._scanner = new Scanner(null, false);
        this._scanner.addDirectory(parentFile.toPath());
        this._scanner.setScanInterval(1);
        this._scanner.setReportDirs(false);
        this._scanner.setReportExistingFilesOnStartup(false);
        this._scanner.setScanDepth(1);
        this._scanner.addListener(this);
        this.addBean(this._scanner);
    }

    private Path getRealKeyStorePath() {
        try {
            return this.keystoreFile.toPath().toRealPath(new LinkOption[0]);
        }
        catch (IOException e) {
            return this.keystoreFile.toPath();
        }
    }

    @Override
    public void fileAdded(String filename) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("fileAdded {} - keystoreFile.toReal {}", (Object)filename, (Object)this.getRealKeyStorePath());
        }
        if (this.keystoreFile.toPath().toString().equals(filename)) {
            this.reload();
        }
    }

    @Override
    public void fileChanged(String filename) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("fileChanged {} - keystoreFile.toReal {}", (Object)filename, (Object)this.getRealKeyStorePath());
        }
        if (this.keystoreFile.toPath().toString().equals(filename)) {
            this.reload();
        }
    }

    @Override
    public void fileRemoved(String filename) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("fileRemoved {} - keystoreFile.toReal {}", (Object)filename, (Object)this.getRealKeyStorePath());
        }
        if (this.keystoreFile.toPath().toString().equals(filename)) {
            this.reload();
        }
    }

    @ManagedOperation(value="Scan for changes in the SSL Keystore", impact="ACTION")
    public boolean scan(long waitMillis) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("scanning");
        }
        CompletableFuture cf = new CompletableFuture();
        try {
            this._scanner.scan(Callback.from(() -> this._scanner.scan(Callback.from(() -> cf.complete(true), cf::completeExceptionally)), cf::completeExceptionally));
            return (Boolean)cf.get(waitMillis, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ManagedOperation(value="Reload the SSL Keystore", impact="ACTION")
    public void reload() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("reloading keystore file {}", (Object)this.keystoreFile);
        }
        try {
            this.sslContextFactory.reload(scf -> {});
        }
        catch (Throwable t) {
            LOG.warn("Keystore Reload Failed", t);
        }
    }

    @ManagedAttribute(value="scanning interval to detect changes which need reloaded")
    public int getScanInterval() {
        return this._scanner.getScanInterval();
    }

    public void setScanInterval(int scanInterval) {
        this._scanner.setScanInterval(scanInterval);
    }
}

