/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons;

import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import java.util.Objects;

public class TwitterEmoji {
    private AtlaskitEmoticonModel atlaskitEmoticonModel;

    public TwitterEmoji(AtlaskitEmoticonModel atlaskitEmoticonModel) {
        this.atlaskitEmoticonModel = atlaskitEmoticonModel;
    }

    public String getId() {
        return this.atlaskitEmoticonModel.getId();
    }

    public String getName() {
        return this.atlaskitEmoticonModel.getName();
    }

    public String getShortcut() {
        return this.atlaskitEmoticonModel.getShortName();
    }

    public String getImagePath() {
        return this.atlaskitEmoticonModel.getRepresentation().getImagePath();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TwitterEmoji that = (TwitterEmoji)o;
        return this.getName().equals(that.getName()) && this.getId().equals(that.getId());
    }

    public int hashCode() {
        return Objects.hash(this.getName(), this.getId());
    }
}

