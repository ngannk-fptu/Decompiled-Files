/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  org.apache.lucene.analysis.ja.dict.UserDictionary
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.lucene.analysis.analyzer;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.dc.filestore.api.FileStore;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.apache.lucene.analysis.ja.dict.UserDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDictionaryFactory {
    private static final Logger log = LoggerFactory.getLogger(UserDictionaryFactory.class);
    private final FileStore.Path userDictFile;

    public static UserDictionaryFactory create(FileStore.Path fileStore, String dictionaryFileName) {
        return new UserDictionaryFactory(fileStore.path(new String[]{"config", dictionaryFileName}));
    }

    @VisibleForTesting
    UserDictionaryFactory(FileStore.Path userDictFile) {
        this.userDictFile = userDictFile;
    }

    public UserDictionary getUserDictionary() {
        try {
            if (this.userDictFile.fileExists()) {
                return (UserDictionary)this.userDictFile.fileReader().read(stream -> new UserDictionary((Reader)new InputStreamReader(stream, StandardCharsets.UTF_8)));
            }
        }
        catch (UnsupportedEncodingException e) {
            log.debug("Unsupported encoding: ", (Throwable)e);
        }
        catch (IOException e) {
            log.debug("Error constructing " + UserDictionary.class, (Throwable)e);
        }
        return null;
    }
}

