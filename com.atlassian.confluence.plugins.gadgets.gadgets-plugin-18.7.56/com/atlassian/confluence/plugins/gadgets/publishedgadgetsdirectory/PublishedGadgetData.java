/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory;

import com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory.PublishedGadgetBean;
import com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory.TranslationBean;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PublishedGadgetData {
    @XmlElement
    public List<PublishedGadgetBean> directoryList;
    @XmlElement
    public TranslationBean translations;

    public PublishedGadgetData() {
    }

    public PublishedGadgetData(List<PublishedGadgetBean> directoryList, TranslationBean translations) {
        this.directoryList = directoryList;
        this.translations = translations;
    }

    public PublishedGadgetData(List<PublishedGadgetBean> directoryList) {
        this.directoryList = directoryList;
        this.translations = null;
    }
}

