/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.EditableLabelable;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelableType;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.core.bean.EntityObject;
import java.io.Serializable;

public class Labelling
extends EntityObject
implements Serializable {
    private ContentEntityObject content;
    private PageTemplate pageTemplate;
    private Label label;
    private ConfluenceUser owningUser;
    private String labelableType;
    private Long labelableId;

    @Deprecated
    public Labelling(Label label, EditableLabelable labelable, String user) {
        if (!label.isPersistent()) {
            throw new IllegalStateException("A label must be persistent to use it to label content.");
        }
        this.label = label;
        this.owningUser = FindUserHelper.getUserByUsername(user);
        this.setLabelable(labelable);
        this.verifyUnion();
    }

    public Labelling(Label label, EditableLabelable labelable, ConfluenceUser user) {
        if (!label.isPersistent()) {
            throw new IllegalStateException("A label must be persistent to use it to label content.");
        }
        this.label = label;
        this.owningUser = user;
        this.setLabelable(labelable);
        this.verifyUnion();
    }

    public Labelling() {
    }

    public EditableLabelable getLableable() {
        this.verifyUnion();
        return this.getLabelableNoVerify();
    }

    private EditableLabelable getLabelableNoVerify() {
        if (this.content != null) {
            return this.content;
        }
        if (this.pageTemplate != null) {
            return this.pageTemplate;
        }
        return null;
    }

    private void verifyUnion() {
        ContentEntityObject content = this.getContent();
        PageTemplate pageTemplate = this.getPageTemplate();
        if (content != null && pageTemplate == null || pageTemplate == null && content == null || pageTemplate != null && content == null) {
            return;
        }
        throw new IllegalStateException("At least one of and only one of content and pageTemplate should be not null :\n" + content + ",\n" + pageTemplate);
    }

    protected void setLabelable(EditableLabelable labelable) {
        this.setContent(null);
        this.setPageTemplate(null);
        if (labelable instanceof ContentEntityObject) {
            this.setContent((ContentEntityObject)labelable);
        } else if (labelable instanceof PageTemplate) {
            this.setPageTemplate((PageTemplate)labelable);
        }
        this.updateSummaryValues();
    }

    private void updateSummaryValues() {
        if (this.getLabelableNoVerify() != null) {
            this.labelableId = this.getLableable().getId();
            this.labelableType = LabelableType.getTypeString(this.getLableable().getClass());
        } else {
            this.labelableId = null;
            this.labelableType = null;
        }
    }

    public Label getLabel() {
        return this.label;
    }

    private void setLabel(Label label) {
        this.label = label;
    }

    public ConfluenceUser getOwningUser() {
        return this.owningUser;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Labelling labelling = (Labelling)o;
        if (this.content != null ? !this.content.equals(labelling.content) : labelling.content != null) {
            return false;
        }
        return !(this.label != null ? this.label.getId() != labelling.label.getId() : labelling.label != null);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 27 * result + (this.content != null ? this.content.hashCode() : 0);
        result = 27 * result + (this.label != null ? this.label.hashCode() : 0);
        return result;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Labelling[");
        buffer.append("labelable: ").append((String)(this.getLableable() != null ? "" + this.getLableable() : "null")).append(", ");
        buffer.append("label: ").append((String)(this.label != null ? "" + this.label.getId() : "null")).append(", ");
        buffer.append("owner: ").append(this.owningUser != null ? this.owningUser.getName() : "null").append("]");
        return buffer.toString();
    }

    protected PageTemplate getPageTemplate() {
        return this.pageTemplate;
    }

    protected ContentEntityObject getContent() {
        return this.content;
    }

    protected void setPageTemplate(PageTemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
        this.updateSummaryValues();
    }

    protected void setContent(ContentEntityObject ceo) {
        this.content = ceo;
        this.updateSummaryValues();
    }

    private void setLabelableId(Long id) {
        this.labelableId = id;
    }

    private Long getLabelableId() {
        return this.labelableId;
    }

    private void setLabelableType(String type) {
        this.labelableType = type;
    }

    private String getLabelableType() {
        return this.labelableType;
    }

    public Labelling copy() {
        return this.copy(this.getLableable());
    }

    public Labelling copy(EditableLabelable labelable) {
        Labelling labelling = new Labelling(this.getLabel(), labelable, this.owningUser);
        this.labelableId = labelable.getId();
        return labelling;
    }
}

