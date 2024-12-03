/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.storeconfig;

import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.storeconfig.IStoreFactory;

public class StoreDescription {
    private String id;
    private String tag;
    private String tagClass;
    private boolean standard = false;
    private boolean backup = false;
    private boolean externalAllowed = false;
    private boolean externalOnly = false;
    private boolean myDefault = false;
    private boolean attributes = true;
    private String storeFactoryClass;
    private IStoreFactory storeFactory;
    private String storeWriterClass;
    private boolean children = false;
    private List<String> transientAttributes;
    private List<String> transientChildren;
    private boolean storeSeparate = false;

    public boolean isExternalAllowed() {
        return this.externalAllowed;
    }

    public void setExternalAllowed(boolean external) {
        this.externalAllowed = external;
    }

    public boolean isExternalOnly() {
        return this.externalOnly;
    }

    public void setExternalOnly(boolean external) {
        this.externalOnly = external;
    }

    public boolean isStandard() {
        return this.standard;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    public boolean isBackup() {
        return this.backup;
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }

    public boolean isDefault() {
        return this.myDefault;
    }

    public void setDefault(boolean aDefault) {
        this.myDefault = aDefault;
    }

    public String getStoreFactoryClass() {
        return this.storeFactoryClass;
    }

    public void setStoreFactoryClass(String storeFactoryClass) {
        this.storeFactoryClass = storeFactoryClass;
    }

    public IStoreFactory getStoreFactory() {
        return this.storeFactory;
    }

    public void setStoreFactory(IStoreFactory storeFactory) {
        this.storeFactory = storeFactory;
    }

    public String getStoreWriterClass() {
        return this.storeWriterClass;
    }

    public void setStoreWriterClass(String storeWriterClass) {
        this.storeWriterClass = storeWriterClass;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagClass() {
        return this.tagClass;
    }

    public void setTagClass(String tagClass) {
        this.tagClass = tagClass;
    }

    public List<String> getTransientAttributes() {
        return this.transientAttributes;
    }

    public void setTransientAttributes(List<String> transientAttributes) {
        this.transientAttributes = transientAttributes;
    }

    public void addTransientAttribute(String attribute) {
        if (this.transientAttributes == null) {
            this.transientAttributes = new ArrayList<String>();
        }
        this.transientAttributes.add(attribute);
    }

    public void removeTransientAttribute(String attribute) {
        if (this.transientAttributes != null) {
            this.transientAttributes.remove(attribute);
        }
    }

    public List<String> getTransientChildren() {
        return this.transientChildren;
    }

    public void setTransientChildren(List<String> transientChildren) {
        this.transientChildren = transientChildren;
    }

    public void addTransientChild(String classname) {
        if (this.transientChildren == null) {
            this.transientChildren = new ArrayList<String>();
        }
        this.transientChildren.add(classname);
    }

    public void removeTransientChild(String classname) {
        if (this.transientChildren != null) {
            this.transientChildren.remove(classname);
        }
    }

    public boolean isTransientChild(String classname) {
        if (this.transientChildren != null) {
            return this.transientChildren.contains(classname);
        }
        return false;
    }

    public boolean isTransientAttribute(String attribute) {
        if (this.transientAttributes != null) {
            return this.transientAttributes.contains(attribute);
        }
        return false;
    }

    public String getId() {
        if (this.id != null) {
            return this.id;
        }
        return this.getTagClass();
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAttributes() {
        return this.attributes;
    }

    public void setAttributes(boolean attributes) {
        this.attributes = attributes;
    }

    public boolean isStoreSeparate() {
        return this.storeSeparate;
    }

    public void setStoreSeparate(boolean storeSeparate) {
        this.storeSeparate = storeSeparate;
    }

    public boolean isChildren() {
        return this.children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }
}

