/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.util.repository;

import aQute.bnd.service.RepositoryPlugin;
import aQute.lib.exceptions.Exceptions;
import aQute.lib.io.IO;
import aQute.libg.reporter.slf4j.Slf4jReporter;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.FileNotFoundException;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadListenerPromise
implements Success<File, Void>,
Failure {
    private static final Logger logger = LoggerFactory.getLogger(DownloadListenerPromise.class);
    final RepositoryPlugin.DownloadListener[] dls;
    final Promise<File> promise;
    private final Reporter reporter;
    private final String task;
    private File linked;

    public DownloadListenerPromise(Reporter reporter, String task, Promise<File> promise, RepositoryPlugin.DownloadListener ... downloadListeners) {
        this.reporter = Slf4jReporter.getAlternative(DownloadListenerPromise.class, reporter);
        this.task = task;
        this.promise = promise;
        this.dls = downloadListeners;
        logger.debug("{}: starting", (Object)task);
        promise.then(this).then(null, this);
    }

    @Override
    public Promise<Void> call(Promise<File> resolved) throws Exception {
        File file = resolved.getValue();
        if (file == null) {
            throw new FileNotFoundException("Download failed");
        }
        logger.debug("{}: success {}", (Object)this, (Object)file);
        if (this.linked != null) {
            IO.createSymbolicLinkOrCopy(this.linked, file);
        }
        for (RepositoryPlugin.DownloadListener dl : this.dls) {
            try {
                dl.success(file);
            }
            catch (Throwable e) {
                this.reporter.warning("%s: Success callback failed to %s: %s", this, dl, e);
            }
        }
        return null;
    }

    @Override
    public void fail(Promise<?> resolved) throws Exception {
        Throwable failure = resolved.getFailure();
        logger.debug("{}: failure", (Object)this, (Object)failure);
        String reason = Exceptions.toString(failure);
        for (RepositoryPlugin.DownloadListener dl : this.dls) {
            try {
                dl.failure(null, reason);
            }
            catch (Throwable e) {
                this.reporter.warning("%s: Fail callback failed to %s: %s", this, dl, e);
            }
        }
    }

    public String toString() {
        return this.task;
    }

    public void linkTo(File linked) {
        this.linked = linked;
    }
}

