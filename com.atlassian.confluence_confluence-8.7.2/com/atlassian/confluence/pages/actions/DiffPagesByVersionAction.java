/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.AbstractDiffPagesAction;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.util.breadcrumbs.spaceia.ContentDetailAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Arrays;

@RequiresAnyConfluenceAccess
public class DiffPagesByVersionAction
extends AbstractDiffPagesAction
implements ContentDetailAction {
    private int originalVersion;
    private int revisedVersion;
    private AbstractPage revisedPage;
    private int[] selectedPageVersions;

    @Override
    public void validate() {
        super.validate();
        if (this.originalVersion < 1 || this.revisedVersion < 1) {
            this.addActionError(this.getText("error.must.specify.two.versions"));
        }
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        return super.execute();
    }

    public void setOriginalVersion(int originalVersion) {
        this.originalVersion = originalVersion;
    }

    public void setRevisedVersion(int revisedVersion) {
        this.revisedVersion = revisedVersion;
    }

    @Override
    public AbstractPage getOriginalPage() {
        if (this.originalPage == null && this.originalVersion != 0) {
            this.originalPage = (AbstractPage)this.pageManager.getOtherVersion(this.getPage(), this.originalVersion);
        }
        return this.originalPage;
    }

    @Override
    public AbstractPage getRevisedPage() {
        if (this.revisedPage == null && this.revisedVersion != 0) {
            this.revisedPage = (AbstractPage)this.pageManager.getOtherVersion(this.getPage(), this.revisedVersion);
        }
        return this.revisedPage;
    }

    public void setSelectedPageVersions(int[] selectedPageVersions) {
        this.selectedPageVersions = selectedPageVersions;
        Arrays.sort(selectedPageVersions);
        if (selectedPageVersions.length == 2) {
            this.setOriginalVersion(selectedPageVersions[0]);
            this.setRevisedVersion(selectedPageVersions[1]);
        }
    }

    public boolean isSelectedVersion(int version) {
        return this.selectedPageVersions != null && Arrays.binarySearch(this.selectedPageVersions, version) >= 0;
    }
}

