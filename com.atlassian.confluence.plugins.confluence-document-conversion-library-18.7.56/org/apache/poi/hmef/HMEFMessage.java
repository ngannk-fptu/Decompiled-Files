/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hmef;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.poi.hmef.Attachment;
import org.apache.poi.hmef.attribute.MAPIAttribute;
import org.apache.poi.hmef.attribute.MAPIStringAttribute;
import org.apache.poi.hmef.attribute.TNEFAttribute;
import org.apache.poi.hmef.attribute.TNEFMAPIAttribute;
import org.apache.poi.hmef.attribute.TNEFProperty;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.util.LittleEndian;

public final class HMEFMessage {
    public static final int HEADER_SIGNATURE = 574529400;
    private int fileId;
    private final List<TNEFAttribute> messageAttributes = new ArrayList<TNEFAttribute>();
    private final List<MAPIAttribute> mapiAttributes = new ArrayList<MAPIAttribute>();
    private final List<Attachment> attachments = new ArrayList<Attachment>();

    public HMEFMessage(InputStream inp) throws IOException {
        try {
            int sig = LittleEndian.readInt(inp);
            if (sig != 574529400) {
                throw new IllegalArgumentException("TNEF signature not detected in file, expected 574529400 but got " + sig);
            }
            this.fileId = LittleEndian.readUShort(inp);
            this.process(inp);
        }
        finally {
            inp.close();
        }
    }

    private void process(InputStream inp) throws IOException {
        int level;
        do {
            level = inp.read();
            switch (level) {
                case 1: {
                    this.processMessage(inp);
                    break;
                }
                case 2: {
                    this.processAttachment(inp);
                    break;
                }
                case -1: 
                case 10: 
                case 13: {
                    break;
                }
                default: {
                    throw new IllegalStateException("Unhandled level " + level);
                }
            }
        } while (level != -1);
    }

    void processMessage(InputStream inp) throws IOException {
        TNEFAttribute attr = TNEFAttribute.create(inp);
        this.messageAttributes.add(attr);
        if (attr instanceof TNEFMAPIAttribute) {
            TNEFMAPIAttribute tnefMAPI = (TNEFMAPIAttribute)attr;
            this.mapiAttributes.addAll(tnefMAPI.getMAPIAttributes());
        }
    }

    void processAttachment(InputStream inp) throws IOException {
        TNEFAttribute attr = TNEFAttribute.create(inp);
        if (this.attachments.isEmpty() || attr.getProperty() == TNEFProperty.ID_ATTACHRENDERDATA) {
            this.attachments.add(new Attachment());
        }
        Attachment attach = this.attachments.get(this.attachments.size() - 1);
        attach.addAttribute(attr);
    }

    public List<TNEFAttribute> getMessageAttributes() {
        return Collections.unmodifiableList(this.messageAttributes);
    }

    public List<MAPIAttribute> getMessageMAPIAttributes() {
        return Collections.unmodifiableList(this.mapiAttributes);
    }

    public List<Attachment> getAttachments() {
        return Collections.unmodifiableList(this.attachments);
    }

    public TNEFAttribute getMessageAttribute(TNEFProperty id) {
        for (TNEFAttribute attr : this.messageAttributes) {
            if (attr.getProperty() != id) continue;
            return attr;
        }
        return null;
    }

    public MAPIAttribute getMessageMAPIAttribute(MAPIProperty id) {
        for (MAPIAttribute attr : this.mapiAttributes) {
            if (attr.getProperty().id != id.id) continue;
            return attr;
        }
        return null;
    }

    private String getString(MAPIProperty id) {
        return MAPIStringAttribute.getAsString(this.getMessageMAPIAttribute(id));
    }

    public String getSubject() {
        return this.getString(MAPIProperty.CONVERSATION_TOPIC);
    }

    public String getBody() {
        return this.getString(MAPIProperty.RTF_COMPRESSED);
    }
}

