/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.bonnie.upgrader;

import com.atlassian.lucene36.index.IndexUpgrader;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.store.FSDirectory;
import com.atlassian.lucene36.util.Version;
import java.io.File;
import java.io.IOException;

public class LuceneIndexUpgrader {
    private final IndexUpgrader indexUpgrader;

    private LuceneIndexUpgrader(Directory dir) {
        this.indexUpgrader = new IndexUpgrader(dir, Version.LUCENE_36);
    }

    public void upgrade() throws IOException {
        this.indexUpgrader.upgrade();
    }

    public static LuceneIndexUpgrader create(File path) throws IOException {
        Directory directory = LuceneIndexUpgrader.getDirectory(path);
        return new LuceneIndexUpgrader(directory);
    }

    private static Directory getDirectory(File path) throws IOException {
        if (!path.exists() && !path.mkdir()) {
            throw new IOException("Unable to create index directory '" + path.getAbsolutePath() + "'");
        }
        return FSDirectory.open(path);
    }
}

