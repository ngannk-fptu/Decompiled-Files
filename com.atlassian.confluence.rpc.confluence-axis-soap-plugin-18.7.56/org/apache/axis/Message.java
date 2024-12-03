/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.Part;
import org.apache.axis.SOAPPart;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.message.MimeHeaders;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;

public class Message
extends SOAPMessage
implements Serializable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$Message == null ? (class$org$apache$axis$Message = Message.class$("org.apache.axis.Message")) : class$org$apache$axis$Message).getName());
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";
    public static final String MIME_MULTIPART_RELATED = "multipart/related";
    public static final String MIME_APPLICATION_DIME = "application/dime";
    public static final String DEFAULT_ATTACHMNET_IMPL = "org.apache.axis.attachments.AttachmentsImpl";
    private static String mAttachmentsImplClassName = "org.apache.axis.attachments.AttachmentsImpl";
    public static final String MIME_UNKNOWN = "  ";
    private String messageType;
    private SOAPPart mSOAPPart;
    private Attachments mAttachments = null;
    private MimeHeaders headers;
    private boolean saveRequired = true;
    private MessageContext msgContext;
    private static Class attachImpl = null;
    private static boolean checkForAttachmentSupport = true;
    private static boolean attachmentSupportEnabled = false;
    private Hashtable mProps = new Hashtable();
    static /* synthetic */ Class class$org$apache$axis$Message;

    public static String getAttachmentImplClassName() {
        return mAttachmentsImplClassName;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public MessageContext getMessageContext() {
        return this.msgContext;
    }

    public void setMessageContext(MessageContext msgContext) {
        this.msgContext = msgContext;
    }

    public Message(Object initialContents, boolean bodyInStream) {
        this.setup(initialContents, bodyInStream, null, null, null);
    }

    public Message(Object initialContents, boolean bodyInStream, javax.xml.soap.MimeHeaders headers) {
        this.setup(initialContents, bodyInStream, null, null, headers);
    }

    public Message(Object initialContents, MimeHeaders headers) {
        this.setup(initialContents, true, null, null, headers);
    }

    public Message(Object initialContents, boolean bodyInStream, String contentType, String contentLocation) {
        this.setup(initialContents, bodyInStream, contentType, contentLocation, null);
    }

    public Message(Object initialContents) {
        this.setup(initialContents, false, null, null, null);
    }

    private static synchronized boolean isAttachmentSupportEnabled(MessageContext mc) {
        if (checkForAttachmentSupport) {
            checkForAttachmentSupport = false;
            try {
                AxisEngine ae;
                String attachImpName = DEFAULT_ATTACHMNET_IMPL;
                if (null != mc && null != (ae = mc.getAxisEngine())) {
                    attachImpName = (String)ae.getOption("attachments.implementation");
                }
                if (null == attachImpName) {
                    attachImpName = DEFAULT_ATTACHMNET_IMPL;
                }
                ClassUtils.forName("javax.activation.DataHandler");
                ClassUtils.forName("javax.mail.internet.MimeMultipart");
                attachImpl = ClassUtils.forName(attachImpName);
                attachmentSupportEnabled = true;
            }
            catch (ClassNotFoundException ex) {
            }
            catch (NoClassDefFoundError noClassDefFoundError) {
                // empty catch block
            }
            log.debug((Object)(Messages.getMessage("attachEnabled") + MIME_UNKNOWN + attachmentSupportEnabled));
        }
        return attachmentSupportEnabled;
    }

    private void setup(Object initialContents, boolean bodyInStream, String contentType, String contentLocation, javax.xml.soap.MimeHeaders mimeHeaders) {
        int delimiterIndex;
        if (contentType == null && mimeHeaders != null) {
            String[] contentTypes = mimeHeaders.getHeader("Content-Type");
            String string = contentType = contentTypes != null ? contentTypes[0] : null;
        }
        if (contentLocation == null && mimeHeaders != null) {
            String[] contentLocations = mimeHeaders.getHeader("Content-Location");
            String string = contentLocation = contentLocations != null ? contentLocations[0] : null;
        }
        if (contentType != null && (delimiterIndex = contentType.lastIndexOf("charset")) > 0) {
            int charsetIndex;
            String charsetPart = contentType.substring(delimiterIndex);
            String charset = charsetPart.substring((charsetIndex = charsetPart.indexOf(61)) + 1).trim();
            if (charset.startsWith("\"") && charset.endsWith("\"") || charset.startsWith("'") && charset.endsWith("'")) {
                charset = charset.substring(1, charset.length() - 1);
            }
            try {
                this.setProperty("javax.xml.soap.character-set-encoding", charset);
            }
            catch (SOAPException e) {
                // empty catch block
            }
        }
        if (Message.isAttachmentSupportEnabled(this.getMessageContext())) {
            Constructor<?> attachImplConstr = attachImpl.getConstructors()[0];
            try {
                this.mAttachments = (Attachments)attachImplConstr.newInstance(initialContents, contentType, contentLocation);
                this.mSOAPPart = (SOAPPart)this.mAttachments.getRootPart();
            }
            catch (InvocationTargetException ex) {
                log.fatal((Object)Messages.getMessage("invocationTargetException00"), (Throwable)ex);
                throw new RuntimeException(ex.getMessage());
            }
            catch (InstantiationException ex) {
                log.fatal((Object)Messages.getMessage("instantiationException00"), (Throwable)ex);
                throw new RuntimeException(ex.getMessage());
            }
            catch (IllegalAccessException ex) {
                log.fatal((Object)Messages.getMessage("illegalAccessException00"), (Throwable)ex);
                throw new RuntimeException(ex.getMessage());
            }
        } else if (contentType != null && contentType.startsWith("multipart")) {
            throw new RuntimeException(Messages.getMessage("noAttachments"));
        }
        if (null == this.mSOAPPart) {
            this.mSOAPPart = new SOAPPart(this, initialContents, bodyInStream);
        } else {
            this.mSOAPPart.setMessage(this);
        }
        if (this.mAttachments != null) {
            this.mAttachments.setRootPart(this.mSOAPPart);
        }
        this.headers = mimeHeaders == null ? new MimeHeaders() : new MimeHeaders(mimeHeaders);
    }

    public javax.xml.soap.SOAPPart getSOAPPart() {
        return this.mSOAPPart;
    }

    public String getSOAPPartAsString() throws AxisFault {
        return this.mSOAPPart.getAsString();
    }

    public byte[] getSOAPPartAsBytes() throws AxisFault {
        return this.mSOAPPart.getAsBytes();
    }

    public SOAPEnvelope getSOAPEnvelope() throws AxisFault {
        return this.mSOAPPart.getAsSOAPEnvelope();
    }

    public Attachments getAttachmentsImpl() {
        return this.mAttachments;
    }

    public String getContentType(SOAPConstants sc) throws AxisFault {
        boolean soap12 = false;
        if (sc != null) {
            if (sc == SOAPConstants.SOAP12_CONSTANTS) {
                soap12 = true;
            }
        } else {
            SOAPEnvelope envelope = this.getSOAPEnvelope();
            if (envelope != null && envelope.getSOAPConstants() == SOAPConstants.SOAP12_CONSTANTS) {
                soap12 = true;
            }
        }
        String encoding = XMLUtils.getEncoding(this, this.msgContext);
        String ret = sc.getContentType() + "; charset=" + encoding.toLowerCase();
        if (soap12) {
            ret = "application/soap+xml; charset=" + encoding;
        }
        if (this.getSendType() != 4 && this.mAttachments != null && 0 != this.mAttachments.getAttachmentCount()) {
            ret = this.mAttachments.getContentType();
        }
        return ret;
    }

    private int getSendType() {
        int sendType = 1;
        if (this.msgContext != null && this.msgContext.getService() != null) {
            sendType = this.msgContext.getService().getSendType();
        }
        return sendType;
    }

    public long getContentLength() throws AxisFault {
        long ret = this.mSOAPPart.getContentLength();
        if (this.mAttachments != null && 0 < this.mAttachments.getAttachmentCount()) {
            ret = this.mAttachments.getContentLength();
        }
        return ret;
    }

    public void writeTo(OutputStream os) throws SOAPException, IOException {
        if (this.getSendType() == 4 || this.mAttachments == null || 0 == this.mAttachments.getAttachmentCount()) {
            try {
                String charEncoding = XMLUtils.getEncoding(this, this.msgContext);
                this.mSOAPPart.setEncoding(charEncoding);
                this.mSOAPPart.writeTo(os);
            }
            catch (IOException e) {
                log.error((Object)Messages.getMessage("javaIOException00"), (Throwable)e);
            }
        } else {
            try {
                this.mAttachments.writeContentToStream(os);
            }
            catch (Exception e) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
            }
        }
    }

    public SOAPBody getSOAPBody() throws SOAPException {
        return this.mSOAPPart.getEnvelope().getBody();
    }

    public SOAPHeader getSOAPHeader() throws SOAPException {
        return this.mSOAPPart.getEnvelope().getHeader();
    }

    public void setProperty(String property, Object value) throws SOAPException {
        this.mProps.put(property, value);
    }

    public Object getProperty(String property) throws SOAPException {
        return this.mProps.get(property);
    }

    public String getContentDescription() {
        String[] values = this.headers.getHeader("Content-Description");
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    public void setContentDescription(String description) {
        this.headers.setHeader("Content-Description", description);
    }

    public void saveChanges() throws SOAPException {
        if (this.mAttachments != null && 0 < this.mAttachments.getAttachmentCount()) {
            try {
                this.headers.setHeader("Content-Type", this.mAttachments.getContentType());
            }
            catch (AxisFault af) {
                log.error((Object)Messages.getMessage("exception00"), (Throwable)af);
            }
        }
        this.saveRequired = false;
        try {
            this.mSOAPPart.saveChanges();
        }
        catch (AxisFault axisFault) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)axisFault);
        }
    }

    public boolean saveRequired() {
        return this.saveRequired;
    }

    public javax.xml.soap.MimeHeaders getMimeHeaders() {
        return this.headers;
    }

    public void removeAllAttachments() {
        this.mAttachments.removeAllAttachments();
    }

    public int countAttachments() {
        return this.mAttachments == null ? 0 : this.mAttachments.getAttachmentCount();
    }

    public Iterator getAttachments() {
        try {
            if (this.mAttachments != null && 0 != this.mAttachments.getAttachmentCount()) {
                return this.mAttachments.getAttachments().iterator();
            }
        }
        catch (AxisFault af) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)af);
        }
        return Collections.EMPTY_LIST.iterator();
    }

    public Iterator getAttachments(javax.xml.soap.MimeHeaders headers) {
        return this.mAttachments.getAttachments(headers);
    }

    public void addAttachmentPart(AttachmentPart attachmentpart) {
        try {
            this.mAttachments.addAttachmentPart((Part)((Object)attachmentpart));
        }
        catch (AxisFault af) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)af);
        }
    }

    public AttachmentPart createAttachmentPart() {
        if (!Message.isAttachmentSupportEnabled(this.getMessageContext())) {
            throw new RuntimeException(Messages.getMessage("noAttachments"));
        }
        try {
            return (AttachmentPart)((Object)this.mAttachments.createAttachmentPart());
        }
        catch (AxisFault af) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)af);
            return null;
        }
    }

    public void dispose() {
        if (this.mAttachments != null) {
            this.mAttachments.dispose();
        }
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

