/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.hmef.attribute.MAPIAttribute;
import org.apache.poi.hmef.attribute.MAPIStringAttribute;
import org.apache.poi.hmef.attribute.TNEFAttribute;
import org.apache.poi.hmef.attribute.TNEFDateAttribute;
import org.apache.poi.hmef.attribute.TNEFMAPIAttribute;
import org.apache.poi.hmef.attribute.TNEFProperty;
import org.apache.poi.hmef.attribute.TNEFStringAttribute;
import org.apache.poi.hsmf.datatypes.MAPIProperty;

public final class Attachment {
    private final List<TNEFAttribute> attributes = new ArrayList<TNEFAttribute>();
    private final List<MAPIAttribute> mapiAttributes = new ArrayList<MAPIAttribute>();

    protected void addAttribute(TNEFAttribute attr) {
        this.attributes.add(attr);
        if (attr instanceof TNEFMAPIAttribute) {
            TNEFMAPIAttribute tnefMAPI = (TNEFMAPIAttribute)attr;
            this.mapiAttributes.addAll(tnefMAPI.getMAPIAttributes());
        }
    }

    public TNEFAttribute getAttribute(TNEFProperty id) {
        for (TNEFAttribute attr : this.attributes) {
            if (attr.getProperty() != id) continue;
            return attr;
        }
        return null;
    }

    public MAPIAttribute getMAPIAttribute(MAPIProperty id) {
        for (MAPIAttribute attr : this.mapiAttributes) {
            if (attr.getProperty() != id) continue;
            return attr;
        }
        return null;
    }

    public List<TNEFAttribute> getAttributes() {
        return this.attributes;
    }

    public List<MAPIAttribute> getMAPIAttributes() {
        return this.mapiAttributes;
    }

    private String getString(MAPIProperty id) {
        return MAPIStringAttribute.getAsString(this.getMAPIAttribute(id));
    }

    private String getString(TNEFProperty id) {
        return TNEFStringAttribute.getAsString(this.getAttribute(id));
    }

    public String getFilename() {
        return this.getString(TNEFProperty.ID_ATTACHTITLE);
    }

    public String getLongFilename() {
        return this.getString(MAPIProperty.ATTACH_LONG_FILENAME);
    }

    public String getExtension() {
        return this.getString(MAPIProperty.ATTACH_EXTENSION);
    }

    public Date getModifiedDate() {
        return TNEFDateAttribute.getAsDate(this.getAttribute(TNEFProperty.ID_ATTACHMODIFYDATE));
    }

    public byte[] getContents() {
        TNEFAttribute contents = this.getAttribute(TNEFProperty.ID_ATTACHDATA);
        if (contents == null) {
            throw new IllegalArgumentException("Attachment corrupt - no Data section");
        }
        return contents.getData();
    }

    public byte[] getRenderedMetaFile() {
        TNEFAttribute meta = this.getAttribute(TNEFProperty.ID_ATTACHMETAFILE);
        if (meta == null) {
            return null;
        }
        return meta.getData();
    }
}

