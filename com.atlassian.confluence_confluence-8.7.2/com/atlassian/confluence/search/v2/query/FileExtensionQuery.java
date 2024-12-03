/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FileExtensionQuery
implements SearchQuery {
    public static final String KEY = "fileExtension";
    private final String fileExtension;

    public FileExtensionQuery(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.singletonList(this.fileExtension);
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    @Override
    public SearchQuery expand() {
        return new ConstantScoreQuery(new TermQuery(SearchFieldNames.ATTACHMENT_FILE_EXTENSION, this.getFileExtension()));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileExtensionQuery)) {
            return false;
        }
        FileExtensionQuery that = (FileExtensionQuery)o;
        return Objects.equals(this.getFileExtension(), that.getFileExtension());
    }

    public int hashCode() {
        return Objects.hash(this.getFileExtension());
    }
}

