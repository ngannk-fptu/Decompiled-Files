/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="i18n")
public class I18NEntryListEntity {
    Map<String, String> entries;

    public I18NEntryListEntity(Map<String, String> entries) {
        this.entries = entries;
    }

    public I18NEntryListEntity() {
    }
}

