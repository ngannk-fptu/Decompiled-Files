/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Streams
 *  javax.xml.bind.DatatypeConverter
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.lucene.util.IndexableBinaryStringTools
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.util.IndexableBinaryStringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentPermissionSearchUtils {
    private static final Logger log = LoggerFactory.getLogger(ContentPermissionSearchUtils.class);
    public static final String ESCAPE_CHAR = "#";
    private static final Pattern HASH_PATTERN = Pattern.compile("\\#");
    private static final Pattern PIPE_PATTERN = Pattern.compile("\\|");
    private static final Pattern AMPERSAND_PATTERN = Pattern.compile("\\&");
    private static final String ESCAPED_HASH = "#h";
    private static final String ESCAPED_PIPE = "#p";
    private static final String ESCAPED_AMPERSAND = "#a";
    private static final String GROUP_PREFIX = "g";
    private static final String USER_PREFIX = "u";

    public static Collection<String> getEncodedPermissionsCollection(ContentPermissionSet contentPermissionSet) {
        return Streams.stream((Iterable)contentPermissionSet).map(ContentPermissionSearchUtils::getEncodedContentPermission).collect(Collectors.toSet());
    }

    public static String getEncodedContentPermission(ContentPermission perm) {
        if (perm == null) {
            return null;
        }
        String result = "";
        if (perm.isUserPermission()) {
            result = ContentPermissionSearchUtils.getEncodedUserKey(perm.getUserSubject());
        } else if (perm.isGroupPermission()) {
            result = ContentPermissionSearchUtils.getEncodedGroupName(perm.getGroupName());
        } else {
            log.debug("Permission cannot be encoded: " + perm);
        }
        return result;
    }

    static String escapeEntityName(String entityName) {
        entityName = HASH_PATTERN.matcher(entityName).replaceAll(ESCAPED_HASH);
        entityName = PIPE_PATTERN.matcher(entityName).replaceAll(ESCAPED_PIPE);
        entityName = AMPERSAND_PATTERN.matcher(entityName).replaceAll(ESCAPED_AMPERSAND);
        return entityName;
    }

    public static String getEncodedUserKey(ConfluenceUser user) {
        if (user == null) {
            return "";
        }
        return ContentPermissionSearchUtils.getEncodedUserKey(user.getKey());
    }

    public static String getEncodedUserKey(UserKey userKey) {
        if (userKey == null) {
            return "";
        }
        String key = ContentPermissionSearchUtils.compressKey(userKey.getStringValue());
        return USER_PREFIX + ContentPermissionSearchUtils.escapeEntityName(key);
    }

    static String compressKey(String key) {
        byte[] bytes = DatatypeConverter.parseHexBinary((String)key);
        int encodedLength = IndexableBinaryStringTools.getEncodedLength((byte[])bytes, (int)0, (int)bytes.length);
        char[] outputArray = new char[encodedLength];
        IndexableBinaryStringTools.encode((byte[])bytes, (int)0, (int)bytes.length, (char[])outputArray, (int)0, (int)encodedLength);
        return new String(outputArray);
    }

    public static String getEncodedGroupName(String groupname) {
        if (StringUtils.isEmpty((CharSequence)groupname)) {
            return groupname;
        }
        return GROUP_PREFIX + ContentPermissionSearchUtils.escapeEntityName(groupname);
    }

    public static Iterator<Set<String>> decodeContentPermissionSets(String encodedContentPermissionSets) {
        return new LazyContentPermissionSetDecoder(encodedContentPermissionSets);
    }

    private static class LazyContentPermissionSetDecoder
    implements Iterator<Set<String>> {
        private final String encodedCredentials;
        private final Set<String> contentPermissionSet;
        private int segmentStart;
        private int charIndex;

        public LazyContentPermissionSetDecoder(String encodedCredentials) {
            this.encodedCredentials = encodedCredentials;
            this.contentPermissionSet = new HashSet<String>();
            this.segmentStart = 0;
            this.charIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return this.charIndex < this.encodedCredentials.length();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Set<String> next() {
            while (this.hasNext()) {
                char currentChar = this.encodedCredentials.charAt(this.charIndex);
                try {
                    if (currentChar == '|') {
                        this.recordContentPermissionSegment();
                    }
                    if (currentChar != '&') continue;
                    this.recordContentPermissionSegment();
                    try {
                        ImmutableSet immutableSet = ImmutableSet.copyOf(this.contentPermissionSet);
                        this.contentPermissionSet.clear();
                        return immutableSet;
                    }
                    catch (Throwable throwable) {
                        this.contentPermissionSet.clear();
                        throw throwable;
                    }
                }
                finally {
                    ++this.charIndex;
                }
            }
            this.contentPermissionSet.add(this.encodedCredentials.substring(this.segmentStart));
            return ImmutableSet.copyOf(this.contentPermissionSet);
        }

        private void recordContentPermissionSegment() {
            this.contentPermissionSet.add(this.encodedCredentials.substring(this.segmentStart, this.charIndex));
            this.segmentStart = this.charIndex + 1;
        }
    }
}

