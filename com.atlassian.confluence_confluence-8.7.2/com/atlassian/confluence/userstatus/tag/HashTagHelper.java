/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.userstatus.tag;

import com.atlassian.confluence.userstatus.tag.TagCallback;
import com.atlassian.confluence.userstatus.tag.TagExtractor;
import com.atlassian.confluence.util.LabelUtil;
import java.util.HashSet;
import java.util.Set;

@Deprecated
public class HashTagHelper {
    private static final String PREFIX = "#";
    private static final TagExtractor TAG_EXTRACTOR = new TagExtractor("#", false);

    public static Set<String> extractHashtags(String status) {
        HashSet<String> labels = new HashSet<String>();
        TAG_EXTRACTOR.parseTagsTo(status, (tag, start, end) -> {
            if (LabelUtil.isValidLabelName(tag)) {
                labels.add(tag);
            }
        });
        return labels;
    }

    public static String linkHashTags(String status) {
        final StringBuilder builder = new StringBuilder(status);
        TAG_EXTRACTOR.parseTagsTo(status, new TagCallback(){
            int offset = 0;

            @Override
            public void call(String tag, int start, int end) {
                if (LabelUtil.isValidLabelName(tag)) {
                    this.offset += HashTagHelper.insertLink(builder, tag, start, end, this.offset);
                }
            }
        });
        return builder.toString();
    }

    private static int insertLink(StringBuilder builder, String label, int start, int end, int offset) {
        String link = String.format("[#%1$s|///label/%1$s]", label);
        builder.replace(start + offset - PREFIX.length(), end + offset, link);
        return link.length() - label.length() - PREFIX.length();
    }
}

