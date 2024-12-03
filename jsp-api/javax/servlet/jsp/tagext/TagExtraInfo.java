/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagInfo;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.VariableInfo;

public abstract class TagExtraInfo {
    private TagInfo tagInfo;
    private static final VariableInfo[] ZERO_VARIABLE_INFO = new VariableInfo[0];

    public VariableInfo[] getVariableInfo(TagData data) {
        return ZERO_VARIABLE_INFO;
    }

    public boolean isValid(TagData data) {
        return true;
    }

    public ValidationMessage[] validate(TagData data) {
        ValidationMessage[] result = null;
        if (!this.isValid(data)) {
            result = new ValidationMessage[]{new ValidationMessage(data.getId(), "isValid() == false")};
        }
        return result;
    }

    public final void setTagInfo(TagInfo tagInfo) {
        this.tagInfo = tagInfo;
    }

    public final TagInfo getTagInfo() {
        return this.tagInfo;
    }
}

