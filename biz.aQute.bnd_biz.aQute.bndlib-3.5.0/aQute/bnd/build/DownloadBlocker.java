/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.build;

import aQute.bnd.service.RepositoryPlugin;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadBlocker
implements RepositoryPlugin.DownloadListener {
    private static final Logger logger = LoggerFactory.getLogger(DownloadBlocker.class);
    private volatile Stage stage = Stage.INIT;
    private String failure;
    private File file;
    private final Reporter reporter;
    private final CountDownLatch resolved;

    public DownloadBlocker(Reporter reporter) {
        this.reporter = reporter;
        this.resolved = new CountDownLatch(1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void success(File file) throws Exception {
        CountDownLatch countDownLatch = this.resolved;
        synchronized (countDownLatch) {
            if (this.resolved.getCount() == 0L) {
                throw new IllegalStateException("already resolved");
            }
            assert (this.stage == Stage.INIT);
            this.stage = Stage.SUCCESS;
            this.file = file;
            this.resolved.countDown();
        }
        logger.debug("successfully downloaded {}", (Object)file);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void failure(File file, String reason) throws Exception {
        CountDownLatch countDownLatch = this.resolved;
        synchronized (countDownLatch) {
            if (this.resolved.getCount() == 0L) {
                throw new IllegalStateException("already resolved");
            }
            assert (this.stage == Stage.INIT);
            this.stage = Stage.FAILURE;
            this.failure = reason;
            this.file = file;
            this.resolved.countDown();
        }
        if (this.reporter != null) {
            this.reporter.error("Download %s %s", reason, file);
        }
    }

    @Override
    public boolean progress(File file, int percentage) throws Exception {
        assert (this.stage == Stage.INIT);
        return true;
    }

    public String getReason() {
        try {
            this.resolved.await();
            return this.failure;
        }
        catch (InterruptedException e) {
            return "Interrupted";
        }
    }

    public Stage getStage() {
        return this.stage;
    }

    public File getFile() {
        try {
            this.resolved.await();
            return this.file;
        }
        catch (InterruptedException e) {
            return null;
        }
    }

    public String toString() {
        return "DownloadBlocker [stage=" + (Object)((Object)this.stage) + ", failure=" + this.failure + ", file=" + this.file + "]";
    }

    public static enum Stage {
        INIT,
        SUCCESS,
        FAILURE;

    }
}

