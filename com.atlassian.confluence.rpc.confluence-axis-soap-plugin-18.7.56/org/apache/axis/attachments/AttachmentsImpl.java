/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.mail.Multipart
 *  javax.mail.internet.MimeMultipart
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.attachments;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.xml.soap.MimeHeaders;
import org.apache.axis.AxisFault;
import org.apache.axis.Part;
import org.apache.axis.SOAPPart;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.AttachmentUtils;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.attachments.DimeBodyPart;
import org.apache.axis.attachments.DimeMultiPart;
import org.apache.axis.attachments.DimeTypeNameFormat;
import org.apache.axis.attachments.ManagedMemoryDataSource;
import org.apache.axis.attachments.MimeUtils;
import org.apache.axis.attachments.MultiPartDimeInputStream;
import org.apache.axis.attachments.MultiPartInputStream;
import org.apache.axis.attachments.MultiPartRelatedInputStream;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class AttachmentsImpl
implements Attachments {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$attachments$AttachmentsImpl == null ? (class$org$apache$axis$attachments$AttachmentsImpl = AttachmentsImpl.class$("org.apache.axis.attachments.AttachmentsImpl")) : class$org$apache$axis$attachments$AttachmentsImpl).getName());
    private HashMap attachments = new HashMap();
    private LinkedList orderedAttachments = new LinkedList();
    protected SOAPPart soapPart = null;
    protected MultiPartInputStream mpartStream = null;
    protected int sendtype = 1;
    protected String contentLocation = null;
    private HashMap stackDataHandler = new HashMap();
    MimeMultipart multipart = null;
    DimeMultiPart dimemultipart = null;
    static /* synthetic */ Class class$org$apache$axis$attachments$AttachmentsImpl;
    static /* synthetic */ Class class$javax$activation$DataHandler;

    public AttachmentsImpl(Object intialContents, String contentType, String contentLocation) throws AxisFault {
        StringTokenizer st;
        if (contentLocation != null && (contentLocation = contentLocation.trim()).length() == 0) {
            contentLocation = null;
        }
        this.contentLocation = contentLocation;
        if (contentType != null && !contentType.equals("  ") && (st = new StringTokenizer(contentType, " \t;")).hasMoreTokens()) {
            String mimetype = st.nextToken();
            if (mimetype.equalsIgnoreCase("multipart/related")) {
                this.sendtype = 2;
                this.mpartStream = new MultiPartRelatedInputStream(contentType, (InputStream)intialContents);
                if (null == contentLocation && (contentLocation = this.mpartStream.getContentLocation()) != null && (contentLocation = contentLocation.trim()).length() == 0) {
                    contentLocation = null;
                }
                this.soapPart = new SOAPPart(null, this.mpartStream, false);
            } else if (mimetype.equalsIgnoreCase("application/dime")) {
                try {
                    this.mpartStream = new MultiPartDimeInputStream((InputStream)intialContents);
                    this.soapPart = new SOAPPart(null, this.mpartStream, false);
                }
                catch (Exception e) {
                    throw AxisFault.makeFault(e);
                }
                this.sendtype = 3;
            }
        }
    }

    private void mergeinAttachments() throws AxisFault {
        if (this.mpartStream != null) {
            Collection atts = this.mpartStream.getAttachments();
            if (this.contentLocation == null) {
                this.contentLocation = this.mpartStream.getContentLocation();
            }
            this.mpartStream = null;
            this.setAttachmentParts(atts);
        }
    }

    public Part removeAttachmentPart(String reference) throws AxisFault {
        this.multipart = null;
        this.dimemultipart = null;
        this.mergeinAttachments();
        Part removedPart = this.getAttachmentByReference(reference);
        if (removedPart != null) {
            this.attachments.remove(removedPart.getContentId());
            this.attachments.remove(removedPart.getContentLocation());
            this.orderedAttachments.remove(removedPart);
        }
        return removedPart;
    }

    public Part addAttachmentPart(Part newPart) throws AxisFault {
        this.multipart = null;
        this.dimemultipart = null;
        this.mergeinAttachments();
        Part oldPart = this.attachments.put(newPart.getContentId(), newPart);
        if (oldPart != null) {
            this.orderedAttachments.remove(oldPart);
            this.attachments.remove(oldPart.getContentLocation());
        }
        this.orderedAttachments.add(newPart);
        if (newPart.getContentLocation() != null) {
            this.attachments.put(newPart.getContentLocation(), newPart);
        }
        return oldPart;
    }

    public Part createAttachmentPart(Object datahandler) throws AxisFault {
        Integer key = new Integer(datahandler.hashCode());
        if (this.stackDataHandler.containsKey(key)) {
            return (Part)this.stackDataHandler.get(key);
        }
        this.multipart = null;
        this.dimemultipart = null;
        this.mergeinAttachments();
        if (!(datahandler instanceof DataHandler)) {
            throw new AxisFault(Messages.getMessage("unsupportedAttach", datahandler.getClass().getName(), (class$javax$activation$DataHandler == null ? (class$javax$activation$DataHandler = AttachmentsImpl.class$("javax.activation.DataHandler")) : class$javax$activation$DataHandler).getName()));
        }
        AttachmentPart ret = new AttachmentPart((DataHandler)datahandler);
        this.addAttachmentPart(ret);
        this.stackDataHandler.put(key, ret);
        return ret;
    }

    public void setAttachmentParts(Collection parts) throws AxisFault {
        this.removeAllAttachments();
        if (parts != null && !parts.isEmpty()) {
            Iterator i = parts.iterator();
            while (i.hasNext()) {
                Object part = i.next();
                if (null == part) continue;
                if (part instanceof Part) {
                    this.addAttachmentPart((Part)part);
                    continue;
                }
                this.createAttachmentPart(part);
            }
        }
    }

    public Part getAttachmentByReference(String reference) throws AxisFault {
        if (null == reference) {
            return null;
        }
        if (0 == (reference = reference.trim()).length()) {
            return null;
        }
        this.mergeinAttachments();
        Part ret = (Part)this.attachments.get(reference);
        if (null != ret) {
            return ret;
        }
        if (!reference.startsWith("cid:") && null != this.contentLocation) {
            String fqreference = this.contentLocation;
            if (!fqreference.endsWith("/")) {
                fqreference = fqreference + "/";
            }
            fqreference = reference.startsWith("/") ? fqreference + reference.substring(1) : fqreference + reference;
            ret = (AttachmentPart)this.attachments.get(fqreference);
        }
        if (null == ret && reference.startsWith("cid:")) {
            ret = (Part)this.attachments.get(reference.substring(4));
        }
        return ret;
    }

    public Collection getAttachments() throws AxisFault {
        this.mergeinAttachments();
        return new LinkedList(this.orderedAttachments);
    }

    public Part getRootPart() {
        return this.soapPart;
    }

    public void setRootPart(Part newRoot) {
        try {
            this.soapPart = (SOAPPart)newRoot;
            this.multipart = null;
            this.dimemultipart = null;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(Messages.getMessage("onlySOAPParts"));
        }
    }

    public long getContentLength() throws AxisFault {
        this.mergeinAttachments();
        int sendtype = this.sendtype == 1 ? 2 : this.sendtype;
        try {
            if (sendtype == 2) {
                return MimeUtils.getContentLength((Multipart)(this.multipart != null ? this.multipart : (this.multipart = MimeUtils.createMP(this.soapPart.getAsString(), this.orderedAttachments))));
            }
            if (sendtype == 3) {
                return this.createDimeMessage().getTransmissionSize();
            }
        }
        catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
        return 0L;
    }

    protected DimeMultiPart createDimeMessage() throws AxisFault {
        int sendtype;
        int n = sendtype = this.sendtype == 1 ? 2 : this.sendtype;
        if (sendtype == 3 && this.dimemultipart == null) {
            this.dimemultipart = new DimeMultiPart();
            this.dimemultipart.addBodyPart(new DimeBodyPart(this.soapPart.getAsBytes(), DimeTypeNameFormat.URI, "http://schemas.xmlsoap.org/soap/envelope/", "uuid:714C6C40-4531-442E-A498-3AC614200295"));
            Iterator i = this.orderedAttachments.iterator();
            while (i.hasNext()) {
                AttachmentPart part = (AttachmentPart)i.next();
                DataHandler dh = AttachmentUtils.getActivationDataHandler(part);
                this.dimemultipart.addBodyPart(new DimeBodyPart(dh, part.getContentId()));
            }
        }
        return this.dimemultipart;
    }

    public void writeContentToStream(OutputStream os) throws AxisFault {
        int sendtype = this.sendtype == 1 ? 2 : this.sendtype;
        try {
            this.mergeinAttachments();
            if (sendtype == 2) {
                MimeUtils.writeToMultiPartStream(os, this.multipart != null ? this.multipart : (this.multipart = MimeUtils.createMP(this.soapPart.getAsString(), this.orderedAttachments)));
                Iterator i = this.orderedAttachments.iterator();
                while (i.hasNext()) {
                    AttachmentPart part = (AttachmentPart)i.next();
                    DataHandler dh = AttachmentUtils.getActivationDataHandler(part);
                    DataSource ds = dh.getDataSource();
                    if (ds == null || !(ds instanceof ManagedMemoryDataSource)) continue;
                    ((ManagedMemoryDataSource)ds).delete();
                }
            } else if (sendtype == 3) {
                this.createDimeMessage().write(os);
            }
        }
        catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    public String getContentType() throws AxisFault {
        int sendtype;
        this.mergeinAttachments();
        int n = sendtype = this.sendtype == 1 ? 2 : this.sendtype;
        if (sendtype == 2) {
            return MimeUtils.getContentType(this.multipart != null ? this.multipart : (this.multipart = MimeUtils.createMP(this.soapPart.getAsString(), this.orderedAttachments)));
        }
        return "application/dime";
    }

    public int getAttachmentCount() {
        try {
            this.mergeinAttachments();
            this.soapPart.saveChanges();
            return this.orderedAttachments.size();
        }
        catch (AxisFault e) {
            log.warn((Object)Messages.getMessage("exception00"), (Throwable)e);
            return 0;
        }
    }

    public boolean isAttachment(Object value) {
        return AttachmentUtils.isAttachment(value);
    }

    public void removeAllAttachments() {
        try {
            this.multipart = null;
            this.dimemultipart = null;
            this.mergeinAttachments();
            this.attachments.clear();
            this.orderedAttachments.clear();
            this.stackDataHandler.clear();
        }
        catch (AxisFault af) {
            log.warn((Object)Messages.getMessage("exception00"), (Throwable)af);
        }
    }

    public Iterator getAttachments(MimeHeaders headers) {
        Vector<Part> vecParts = new Vector<Part>();
        Iterator iterator = this.GetAttachmentsIterator();
        while (iterator.hasNext()) {
            Part part = (Part)iterator.next();
            if (!(part instanceof AttachmentPart) || !((AttachmentPart)part).matches(headers)) continue;
            vecParts.add(part);
        }
        return vecParts.iterator();
    }

    private Iterator GetAttachmentsIterator() {
        Iterator iterator = this.attachments.values().iterator();
        return iterator;
    }

    public Part createAttachmentPart() throws AxisFault {
        return new AttachmentPart();
    }

    public void setSendType(int sendtype) {
        if (sendtype < 1) {
            throw new IllegalArgumentException("");
        }
        if (sendtype > 4) {
            throw new IllegalArgumentException("");
        }
        this.sendtype = sendtype;
    }

    public int getSendType() {
        return this.sendtype;
    }

    public void dispose() {
        Iterator iterator = this.GetAttachmentsIterator();
        while (iterator.hasNext()) {
            Part part = (Part)iterator.next();
            if (!(part instanceof AttachmentPart)) continue;
            AttachmentPart apart = (AttachmentPart)part;
            apart.dispose();
        }
    }

    public static int getSendType(String value) {
        if (value.equalsIgnoreCase("MIME")) {
            return 2;
        }
        if (value.equalsIgnoreCase("DIME")) {
            return 3;
        }
        if (value.equalsIgnoreCase("NONE")) {
            return 4;
        }
        return 1;
    }

    public static String getSendTypeString(int value) {
        if (value == 2) {
            return "MIME";
        }
        if (value == 3) {
            return "DIME";
        }
        if (value == 4) {
            return "NONE";
        }
        return null;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

