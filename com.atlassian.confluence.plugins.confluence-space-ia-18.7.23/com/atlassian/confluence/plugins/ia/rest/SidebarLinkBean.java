/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.ia.rest;

import com.atlassian.confluence.plugins.ia.SidebarLink;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SidebarLinkBean
implements Comparable<SidebarLinkBean> {
    @XmlElement
    private int id;
    @XmlElement
    private String collectorKey;
    @XmlElement
    private String title;
    @XmlElement
    private String url;
    @XmlElement
    private int position;
    @XmlElement
    private String styleClass;
    @XmlElement
    private boolean hidden;
    @XmlElement
    private boolean canHide;
    @XmlElement
    private String tooltip;
    @XmlElement
    private String urlWithoutContextPath;

    @Deprecated
    public SidebarLinkBean(SidebarLink link, String title, String url, String styleClass, boolean hidden, boolean canHide, String urlWithoutContextPath) {
        this.id = link.getID();
        this.title = title;
        this.url = url;
        this.styleClass = styleClass;
        this.position = link.getPosition();
        this.hidden = hidden;
        this.canHide = canHide;
        this.collectorKey = link.getWebItemKey();
        this.urlWithoutContextPath = urlWithoutContextPath;
    }

    public SidebarLinkBean(int id, String collectorKey, String title, String url, int position, String styleClass, boolean hidden, boolean canHide, String tooltip, String urlWithoutContextPath) {
        this.id = id;
        this.collectorKey = collectorKey;
        this.title = title;
        this.url = url;
        this.position = position;
        this.styleClass = styleClass;
        this.hidden = hidden;
        this.canHide = canHide;
        this.tooltip = tooltip;
        this.urlWithoutContextPath = urlWithoutContextPath;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPosition() {
        return this.position;
    }

    public String getStyleClass() {
        return this.styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public boolean getHidden() {
        return this.hidden;
    }

    public boolean getCanHide() {
        return this.canHide;
    }

    public void setCanHide(boolean canHide) {
        this.canHide = canHide;
    }

    public String getCollectorKey() {
        return this.collectorKey;
    }

    public void setCollectorKey(String key) {
        this.collectorKey = key;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getUrlWithoutContextPath() {
        return this.urlWithoutContextPath;
    }

    public void setUrlWithoutContextPath(String urlWithoutContextPath) {
        this.urlWithoutContextPath = urlWithoutContextPath;
    }

    @Override
    public int compareTo(SidebarLinkBean linkBean) {
        return this.position - linkBean.getPosition();
    }
}

