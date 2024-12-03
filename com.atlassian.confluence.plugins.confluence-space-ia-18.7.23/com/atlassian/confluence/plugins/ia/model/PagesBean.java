/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.ia.model;

import com.atlassian.confluence.plugins.ia.model.PageNodeBean;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PagesBean {
    @XmlElement
    private final PageNodeBean parentPage;
    @XmlElement
    private final PageNodeBean currentPage;
    @XmlElement
    private final List<PageNodeBean> initialChildPages;
    @XmlElement
    private final List<PageNodeBean> remainingChildPages;
    @XmlElement
    private final String createLink;

    public PagesBean(PageNodeBean parentPage, PageNodeBean currentPage, List<PageNodeBean> initialChildPages, List<PageNodeBean> remainingChildPages, String createLink) {
        this.parentPage = parentPage;
        this.currentPage = currentPage;
        this.initialChildPages = initialChildPages;
        this.remainingChildPages = remainingChildPages;
        this.createLink = createLink;
    }

    public PageNodeBean getParentPage() {
        return this.parentPage;
    }

    public PageNodeBean getCurrentPage() {
        return this.currentPage;
    }

    public List<PageNodeBean> getInitialChildPages() {
        return this.initialChildPages;
    }

    public List<PageNodeBean> getRemainingChildPages() {
        return this.remainingChildPages;
    }

    public String getCreateLink() {
        return this.createLink;
    }
}

