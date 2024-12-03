/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Metrics$Builder
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.lucene.index.AtomicReader
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.DocsEnum
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.BitsFilteredDocIdSet
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.Bits
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.OpenBitSet
 */
package com.atlassian.confluence.impl.search.v2.lucene.filter;

import com.atlassian.confluence.impl.search.v2.lucene.ContentPermissionSearchUtils;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.OpenBitSet;

public class ContentPermissionsFilter
extends Filter {
    private final Set<String> userCredentials;

    @Deprecated
    public ContentPermissionsFilter(User user, Iterable<String> groupNames) {
        this(ContentPermissionsFilter.getUserKey(user), groupNames);
    }

    public ContentPermissionsFilter() {
        this.userCredentials = Collections.emptySet();
    }

    public ContentPermissionsFilter(UserKey userKey, Iterable<String> groupNames) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        if (userKey != null) {
            builder.add((Object)ContentPermissionSearchUtils.getEncodedUserKey(userKey));
        }
        if (groupNames != null) {
            for (String groupName : groupNames) {
                if (groupName == null) continue;
                String encodedGroupName = ContentPermissionSearchUtils.getEncodedGroupName(groupName);
                builder.add((Object)encodedGroupName);
            }
        }
        this.userCredentials = builder.build();
    }

    @VisibleForTesting
    public Set<String> getUserCredentials() {
        return this.userCredentials;
    }

    public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
        try (Ticker ignored = this.startTimer();){
            DocIdSet docIdSet = this.getPermittedDocs(context.reader(), acceptDocs);
            return docIdSet;
        }
    }

    private Ticker startTimer() {
        return Timers.timerWithMetric((String)"ContentPermissionFilter.bits", (Metrics.Builder)Metrics.metric((String)"ContentPermissionFilter").tag("credentialsSize", this.userCredentials.size())).start(new String[0]);
    }

    private static UserKey getUserKey(User user) {
        ConfluenceUser confluenceUser;
        if (user != null && (confluenceUser = FindUserHelper.getUser(user)) != null) {
            return confluenceUser.getKey();
        }
        return null;
    }

    private DocIdSet getPermittedDocs(AtomicReader reader, Bits acceptDocs) throws IOException {
        OpenBitSet result = new OpenBitSet((long)reader.maxDoc());
        result.set(0L, (long)reader.maxDoc());
        Terms terms = reader.terms(SearchFieldNames.CONTENT_PERMISSION_SETS);
        if (terms != null) {
            BytesRef bytesRef;
            TermsEnum termsEnum = terms.iterator(null);
            DocsEnum docsEnum = null;
            while ((bytesRef = termsEnum.next()) != null) {
                if (this.checkEncodedCredentialSets(() -> ((BytesRef)bytesRef).utf8ToString())) continue;
                docsEnum = termsEnum.docs(acceptDocs, docsEnum, 0);
                while (docsEnum.nextDoc() != Integer.MAX_VALUE) {
                    result.fastClear(docsEnum.docID());
                }
            }
        }
        return BitsFilteredDocIdSet.wrap((DocIdSet)result, (Bits)acceptDocs);
    }

    private boolean checkEncodedCredentialSets(Supplier<String> encodedCredentials) {
        if (this.userCredentials.isEmpty()) {
            return false;
        }
        Iterable<Set<String>> credentialSets = ContentPermissionsFilter.getEncodedCredentialSets(encodedCredentials.get());
        for (Set<String> encodedCredentialSet : credentialSets) {
            if (this.checkEncodedCredentialSet(encodedCredentialSet)) continue;
            return false;
        }
        return true;
    }

    private boolean checkEncodedCredentialSet(Set<String> encodedCredentialSet) {
        return CollectionUtils.containsAny(this.userCredentials, encodedCredentialSet);
    }

    static Iterable<Set<String>> getEncodedCredentialSets(String encodedCredentials) {
        if (StringUtils.isEmpty((CharSequence)encodedCredentials)) {
            return Collections.emptyList();
        }
        return () -> ContentPermissionSearchUtils.decodeContentPermissionSets(encodedCredentials);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        ContentPermissionsFilter that = (ContentPermissionsFilter)((Object)o);
        return Objects.equals(this.userCredentials, that.userCredentials);
    }

    public int hashCode() {
        return this.userCredentials != null ? this.userCredentials.hashCode() : 0;
    }
}

