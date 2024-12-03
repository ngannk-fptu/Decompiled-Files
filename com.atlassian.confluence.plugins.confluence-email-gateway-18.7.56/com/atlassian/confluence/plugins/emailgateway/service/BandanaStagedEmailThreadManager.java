/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThread;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadKey;
import com.atlassian.confluence.plugins.emailgateway.service.StagedEmailThreadManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BandanaStagedEmailThreadManager
implements StagedEmailThreadManager {
    private static final BandanaContext BANDANA_CONTEXT = new ConfluenceBandanaContext("email-to-page");
    private static final String KEY_PREFIX = "staged-email:";
    private final BandanaManager bandanaManager;

    public BandanaStagedEmailThreadManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    public void storeStagedEmailThread(StagedEmailThread stagedEmailThread) {
        this.bandanaManager.setValue(BANDANA_CONTEXT, BandanaStagedEmailThreadManager.buildBandanaKey(stagedEmailThread.getKey()), (Object)stagedEmailThread);
    }

    @Override
    public StagedEmailThread findStagedEmailThread(StagedEmailThreadKey stagedEmailThreadKey) {
        return (StagedEmailThread)this.bandanaManager.getValue(BANDANA_CONTEXT, BandanaStagedEmailThreadManager.buildBandanaKey(stagedEmailThreadKey));
    }

    @Override
    public void deleteStagedEmailThread(StagedEmailThreadKey stagedEmailThreadKey) {
        this.bandanaManager.removeValue(BANDANA_CONTEXT, BandanaStagedEmailThreadManager.buildBandanaKey(stagedEmailThreadKey));
    }

    @Override
    public Iterator<StagedEmailThread> iterator() {
        return new Iterator<StagedEmailThread>(){
            private final Iterable<String> keys;
            private final Iterator<String> keyIterator;
            private boolean shouldRead;
            private StagedEmailThread last;
            public boolean ended;
            {
                this.keys = BandanaStagedEmailThreadManager.this.bandanaManager.getKeys(BANDANA_CONTEXT);
                this.keyIterator = this.keys.iterator();
                this.shouldRead = true;
                this.last = null;
                this.ended = false;
            }

            private void doRead() {
                while (this.keyIterator.hasNext()) {
                    String key = this.keyIterator.next();
                    if (!key.startsWith(BandanaStagedEmailThreadManager.KEY_PREFIX)) continue;
                    this.last = (StagedEmailThread)BandanaStagedEmailThreadManager.this.bandanaManager.getValue(BANDANA_CONTEXT, key);
                    return;
                }
                this.ended = true;
            }

            @Override
            public boolean hasNext() {
                if (this.shouldRead) {
                    this.doRead();
                }
                this.shouldRead = false;
                return !this.ended;
            }

            @Override
            public StagedEmailThread next() {
                if (this.shouldRead) {
                    this.doRead();
                }
                if (this.ended) {
                    throw new NoSuchElementException();
                }
                this.shouldRead = true;
                return this.last;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove StagedEmailThreads via the deleteStagedEmailThread method");
            }
        };
    }

    private static String buildBandanaKey(StagedEmailThreadKey stagedEmailThreadKey) {
        return KEY_PREFIX + stagedEmailThreadKey.getToken();
    }
}

