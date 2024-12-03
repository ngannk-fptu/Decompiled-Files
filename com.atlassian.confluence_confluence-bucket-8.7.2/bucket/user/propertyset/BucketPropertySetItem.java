/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.hibernate.PropertySetItem
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package bucket.user.propertyset;

import com.opensymphony.module.propertyset.hibernate.PropertySetItem;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BucketPropertySetItem
extends PropertySetItem {
    String textVal;

    public BucketPropertySetItem() {
    }

    public BucketPropertySetItem(String entityName, long entityId, String key) {
        super(entityName, entityId, key);
    }

    public String getTextVal() {
        return this.textVal;
    }

    public void setTextVal(String textVal) {
        this.textVal = textVal;
    }

    public String toString() {
        return new ToStringBuilder((Object)this, ToStringStyle.SHORT_PREFIX_STYLE).append("entityId", this.getEntityId()).append("entityName", (Object)this.getEntityName()).append("key", (Object)this.getKey()).append("type", this.getType()).append("boolean", this.getBooleanVal()).append("string", (Object)this.getStringVal()).append("text", (Object)this.getTextVal()).append("int", this.getIntVal()).append("double", this.getDoubleVal()).append("long", this.getLongVal()).append("date", (Object)this.getDateVal()).toString();
    }
}

