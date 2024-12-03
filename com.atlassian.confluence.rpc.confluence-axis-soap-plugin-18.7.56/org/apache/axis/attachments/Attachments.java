/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.attachments;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.soap.MimeHeaders;
import org.apache.axis.AxisFault;
import org.apache.axis.Part;

public interface Attachments
extends Serializable {
    public static final int SEND_TYPE_NOTSET = 1;
    public static final int SEND_TYPE_MIME = 2;
    public static final int SEND_TYPE_DIME = 3;
    public static final int SEND_TYPE_NONE = 4;
    public static final int SEND_TYPE_MAX = 4;
    public static final int SEND_TYPE_DEFAULT = 2;
    public static final String CIDprefix = "cid:";

    public Part addAttachmentPart(Part var1) throws AxisFault;

    public Part removeAttachmentPart(String var1) throws AxisFault;

    public void removeAllAttachments();

    public Part getAttachmentByReference(String var1) throws AxisFault;

    public Collection getAttachments() throws AxisFault;

    public Iterator getAttachments(MimeHeaders var1);

    public Part createAttachmentPart(Object var1) throws AxisFault;

    public Part createAttachmentPart() throws AxisFault;

    public void setAttachmentParts(Collection var1) throws AxisFault;

    public Part getRootPart();

    public void setRootPart(Part var1);

    public long getContentLength() throws AxisFault;

    public void writeContentToStream(OutputStream var1) throws AxisFault;

    public String getContentType() throws AxisFault;

    public int getAttachmentCount();

    public boolean isAttachment(Object var1);

    public void setSendType(int var1);

    public int getSendType();

    public void dispose();
}

