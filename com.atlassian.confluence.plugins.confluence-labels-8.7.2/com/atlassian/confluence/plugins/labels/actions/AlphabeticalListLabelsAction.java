/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.Evented
 *  com.atlassian.confluence.event.events.label.LabelListViewEvent
 *  com.atlassian.confluence.util.actions.AlphabeticalLabelGroupingSupport
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.labels.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.label.LabelListViewEvent;
import com.atlassian.confluence.plugins.labels.actions.AbstractLabelDisplayingAction;
import com.atlassian.confluence.util.actions.AlphabeticalLabelGroupingSupport;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class AlphabeticalListLabelsAction
extends AbstractLabelDisplayingAction
implements Evented<LabelListViewEvent> {
    private String startsWith;
    private List contents;
    private AlphabeticalLabelGroupingSupport alphaSupport = null;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public LabelListViewEvent getEventToPublish(String result) {
        return new LabelListViewEvent((Object)this, this.getSpace(), "alpha");
    }

    public List getItems() {
        return this.getContentsWith(this.getStartsWith());
    }

    public AlphabeticalLabelGroupingSupport getAlphaSupport() {
        if (this.alphaSupport == null) {
            List labels = this.getSpace() != null ? this.labelManager.getLabelsInSpace(this.getSpace().getKey()) : this.labelManager.getRecentlyUsedLabels(1000);
            this.alphaSupport = new AlphabeticalLabelGroupingSupport((Collection)labels);
        }
        return this.alphaSupport;
    }

    public boolean hasContents(String letter) {
        return this.getAlphaSupport().hasContent(letter);
    }

    public List getContentsWith(String startsWith) {
        List items = !StringUtils.isNotEmpty((CharSequence)startsWith) ? this.getAlphaSupport().getContents(startsWith) : this.getAlphaSupport().getContents(startsWith.toLowerCase());
        Collections.sort(items);
        return items;
    }

    public void setStartsWith(String startsWith) {
        this.startsWith = startsWith;
    }

    public String getStartsWith() {
        return this.startsWith;
    }

    public List getContents() {
        if (this.contents == null) {
            this.contents = this.getAlphaSupport().getContents();
        }
        return this.contents;
    }
}

