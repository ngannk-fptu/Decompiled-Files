/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.crypto.IncrementalEntropySource;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.util.Properties;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class EntropyGatherer
implements Runnable {
    private static final Logger LOG = Logger.getLogger(EntropyGatherer.class.getName());
    private final long pause;
    private final AtomicBoolean seedAvailable;
    private final AtomicReference<byte[]> entropy;
    private final EntropySource baseRandom;

    EntropyGatherer(EntropySource baseRandom, AtomicBoolean seedAvailable, AtomicReference<byte[]> entropy) {
        this.baseRandom = baseRandom;
        this.seedAvailable = seedAvailable;
        this.entropy = entropy;
        this.pause = EntropyGatherer.getPause();
    }

    @Override
    public void run() {
        try {
            if (this.baseRandom instanceof IncrementalEntropySource) {
                this.entropy.set(((IncrementalEntropySource)this.baseRandom).getEntropy(this.pause));
            } else {
                this.entropy.set(this.baseRandom.getEntropy());
            }
            this.seedAvailable.set(true);
        }
        catch (InterruptedException e) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("entropy request interrupted - exiting");
            }
            Thread.currentThread().interrupt();
        }
    }

    private static long getPause() {
        String pauseSetting = Properties.getPropertyValue("org.bouncycastle.drbg.gather_pause_secs");
        if (pauseSetting != null) {
            try {
                return Long.parseLong(pauseSetting) * 1000L;
            }
            catch (Exception e) {
                return 5000L;
            }
        }
        return 5000L;
    }
}

