/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.api.model.content.id;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.id.ContentId;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Deprecated
@JsonIgnoreProperties(ignoreUnknown=true)
@Internal
public class AttachmentContentId
extends ContentId {
    private static final String PREFIX = "att";

    static boolean handles(String id) {
        return id.startsWith(PREFIX);
    }

    AttachmentContentId(String id) {
        this(Long.parseLong(id.substring(PREFIX.length())));
    }

    AttachmentContentId(long id) {
        super(id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AttachmentContentId)) {
            return false;
        }
        AttachmentContentId that = (AttachmentContentId)o;
        return Objects.equals(this.asLong(), that.asLong());
    }

    @Override
    public int hashCode() {
        return Objects.hash(PREFIX, this.asLong());
    }

    @Override
    public int compareTo(ContentId other) {
        if (other instanceof AttachmentContentId) {
            if (this.asLong() == other.asLong()) {
                return 0;
            }
            if (this.asLong() < other.asLong()) {
                return -1;
            }
        }
        return 1;
    }
}

