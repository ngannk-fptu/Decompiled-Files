/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.feed.GadgetFeedHostConnectionException
 *  com.atlassian.gadgets.feed.GadgetFeedParsingException
 *  com.atlassian.gadgets.feed.GadgetFeedReaderFactory
 *  com.atlassian.gadgets.feed.NonAtomGadgetSpecFeedException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.gadgets.admin.actions;

import com.atlassian.confluence.plugins.gadgets.admin.actions.ViewGadgetsAdminAction;
import com.atlassian.gadgets.feed.GadgetFeedHostConnectionException;
import com.atlassian.gadgets.feed.GadgetFeedParsingException;
import com.atlassian.gadgets.feed.GadgetFeedReaderFactory;
import com.atlassian.gadgets.feed.NonAtomGadgetSpecFeedException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;

public class AddOrRemoveGadgetFeedAction
extends ViewGadgetsAdminAction {
    private GadgetFeedReaderFactory gadgetFeedReaderFactory;
    private String gadgetFeedToAdd;
    private String gadgetFeedToRemove;

    public String doAddGadgetFeed() {
        if (StringUtils.isNotBlank((CharSequence)this.gadgetFeedToAdd)) {
            if (!this.subscribedGadgetFeedStore.containsFeed(this.gadgetFeedToAdd)) {
                try {
                    URI uri = new URI(this.gadgetFeedToAdd);
                    this.gadgetFeedReaderFactory.getFeedReader(uri);
                    this.subscribedGadgetFeedStore.addFeed(uri);
                    return "success";
                }
                catch (URISyntaxException e) {
                    this.addFieldError("gadgetFeedToAdd", this.getText("gadgets.feed.invalid.uri"));
                }
                catch (GadgetFeedParsingException e) {
                    this.addFieldError("gadgetFeedToAdd", this.getText("gadgets.feed.parse.error", new String[]{this.gadgetFeedToAdd}));
                }
                catch (NonAtomGadgetSpecFeedException e) {
                    this.addFieldError("gadgetFeedToAdd", this.getText("gadgets.feed.nonatom.error"));
                }
                catch (GadgetFeedHostConnectionException e) {
                    this.addFieldError("gadgetFeedToAdd", this.getText("gadgets.feed.connect.error", new String[]{this.gadgetFeedToAdd}));
                }
                catch (Exception e) {
                    this.addFieldError("gadgetFeedToAdd", this.getText("gadgets.feed.invalid.uri"));
                }
            }
            return "input";
        }
        this.addFieldError("gadgetFeedToAdd", this.getText("gadgets.feed.invalid.uri"));
        return "input";
    }

    public String getGadgetFeedToAdd() {
        return this.gadgetFeedToAdd;
    }

    public String doRemoveGadgetFeed() {
        this.subscribedGadgetFeedStore.removeFeed(this.gadgetFeedToRemove);
        return "success";
    }

    public void setGadgetFeedToAdd(String gadgetFeedToAdd) {
        this.gadgetFeedToAdd = gadgetFeedToAdd;
    }

    public void setGadgetFeedToRemove(String gadgetFeedToRemove) {
        this.gadgetFeedToRemove = gadgetFeedToRemove;
    }

    public void setGadgetFeedReaderFactory(GadgetFeedReaderFactory gadgetFeedReaderFactory) {
        this.gadgetFeedReaderFactory = gadgetFeedReaderFactory;
    }
}

