/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.store.FSDirectory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryUtil {
    private static final Logger log = LoggerFactory.getLogger(DirectoryUtil.class);

    public static Directory getDirectory(File path) {
        try {
            if (!path.exists() && !path.mkdirs()) {
                throw new IOException("Unable to create index directory '" + path.getAbsolutePath() + "'");
            }
            return FSDirectory.open((File)path);
        }
        catch (IOException e) {
            throw new LuceneException(e);
        }
    }
}

