/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.userstatus.tag;

import com.atlassian.confluence.userstatus.tag.TagCallback;

@Deprecated
public class TagExtractor {
    public static final String TAG_END = " ";
    private String tagPrefix;
    private boolean extractFromUrls;

    public TagExtractor(String tagPrefix, boolean extractFromUrls) {
        this.tagPrefix = tagPrefix;
        this.extractFromUrls = extractFromUrls;
    }

    public void parseTagsTo(String content, TagCallback tagHandler) {
        int index = 0;
        while (index < content.length()) {
            int tagStart = content.indexOf(this.tagPrefix, index);
            if (tagStart == -1) {
                return;
            }
            if (!this.extractFromUrls && this.isInUrl(tagStart, content)) {
                index = tagStart + 1;
                continue;
            }
            int tagContentStart = tagStart + this.tagPrefix.length();
            int tagEnd = content.indexOf(TAG_END, tagContentStart);
            int nextTagStart = content.indexOf(this.tagPrefix, tagContentStart);
            if (nextTagStart != -1 && (tagEnd == -1 || nextTagStart < tagEnd)) {
                tagEnd = nextTagStart;
            }
            if (tagEnd == -1) {
                tagEnd = content.length();
            }
            if (tagEnd != tagStart + this.tagPrefix.length()) {
                tagHandler.call(content.substring(tagContentStart, tagEnd), tagContentStart, tagEnd);
            }
            index = tagEnd;
        }
    }

    private boolean isInUrl(int tagStart, String content) {
        for (int currentPos = tagStart; currentPos > 0; --currentPos) {
            switch (content.charAt(currentPos)) {
                case '&': 
                case '/': {
                    return true;
                }
                case ' ': {
                    return false;
                }
            }
        }
        return false;
    }
}

