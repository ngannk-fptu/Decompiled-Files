/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.build;

import aQute.bnd.service.progress.ProgressPlugin;
import aQute.libg.slf4j.GradleLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingProgressPlugin
implements ProgressPlugin {
    private static final Logger logger = LoggerFactory.getLogger(LoggingProgressPlugin.class);

    @Override
    public ProgressPlugin.Task startTask(String name, int size) {
        logger.info(GradleLogging.LIFECYCLE, name);
        return new ProgressPlugin.Task(){

            @Override
            public void done(String message, Throwable e) {
                if (e != null) {
                    logger.error(message, e);
                } else {
                    logger.debug(message);
                }
            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public void worked(int units) {
            }
        };
    }
}

