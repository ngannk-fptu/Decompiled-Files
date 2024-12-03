/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.om.impl.llom;

import java.io.IOException;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.attachments.utils.DataHandlerUtils;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMLeafNode;
import org.apache.axiom.om.impl.llom.OMNamespaceImpl;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;

public class OMTextImpl
extends OMLeafNode
implements OMText,
OMConstants {
    public static final OMNamespace XOP_NS = new OMNamespaceImpl("http://www.w3.org/2004/08/xop/include", "xop");
    private int nodeType;
    protected String value = null;
    protected char[] charArray;
    private boolean calcNS;
    protected OMNamespace textNS;
    protected String mimeType;
    protected boolean optimize = false;
    protected boolean isBinary = false;
    private String contentID = null;
    private Object dataHandlerObject = null;
    private static final String EMTPY_STRING = "";

    public OMTextImpl(String s, OMFactory factory) {
        this(s, 4, factory);
    }

    public OMTextImpl(String s, int nodeType, OMFactory factory) {
        this(null, s, nodeType, factory, false);
    }

    public OMTextImpl(OMContainer parent, String text, OMFactory factory) {
        this(parent, text, 4, factory, false);
    }

    public OMTextImpl(OMContainer parent, OMTextImpl source, OMFactory factory) {
        super(parent, factory, false);
        this.value = source.value;
        this.nodeType = source.nodeType;
        if (source.charArray != null) {
            this.charArray = new char[source.charArray.length];
            System.arraycopy(source.charArray, 0, this.charArray, 0, source.charArray.length);
        }
        this.calcNS = false;
        this.textNS = null;
        this.optimize = source.optimize;
        this.mimeType = source.mimeType;
        this.isBinary = source.isBinary;
        this.contentID = source.contentID;
        this.dataHandlerObject = source.dataHandlerObject;
    }

    public OMTextImpl(OMContainer parent, String text, int nodeType, OMFactory factory, boolean fromBuilder) {
        super(parent, factory, fromBuilder);
        this.value = text == null ? EMTPY_STRING : text;
        this.nodeType = nodeType;
    }

    public OMTextImpl(OMContainer parent, char[] charArray, int nodeType, OMFactory factory) {
        super(parent, factory, false);
        this.charArray = charArray;
        this.nodeType = nodeType;
    }

    public OMTextImpl(OMContainer parent, QName text, OMFactory factory) {
        this(parent, text, 4, factory);
    }

    public OMTextImpl(OMContainer parent, QName text, int nodeType, OMFactory factory) {
        super(parent, factory, false);
        if (text == null) {
            throw new IllegalArgumentException("QName text arg cannot be null!");
        }
        this.calcNS = true;
        this.textNS = ((OMElementImpl)parent).handleNamespace(text.getNamespaceURI(), text.getPrefix());
        this.value = this.textNS == null ? text.getLocalPart() : this.textNS.getPrefix() + ":" + text.getLocalPart();
        this.nodeType = nodeType;
    }

    public OMTextImpl(String s, String mimeType, boolean optimize, OMFactory factory) {
        this(null, s, mimeType, optimize, factory);
    }

    public OMTextImpl(OMContainer parent, String s, String mimeType, boolean optimize, OMFactory factory) {
        this(parent, s, factory);
        this.mimeType = mimeType;
        this.optimize = optimize;
        this.isBinary = true;
        this.nodeType = 4;
    }

    public OMTextImpl(Object dataHandler, OMFactory factory) {
        this(null, dataHandler, true, factory, false);
    }

    public OMTextImpl(OMContainer parent, Object dataHandler, boolean optimize, OMFactory factory, boolean fromBuilder) {
        super(parent, factory, fromBuilder);
        this.dataHandlerObject = dataHandler;
        this.isBinary = true;
        this.optimize = optimize;
        this.nodeType = 4;
    }

    public OMTextImpl(String contentID, DataHandlerProvider dataHandlerProvider, boolean optimize, OMFactory factory) {
        super(factory);
        this.contentID = contentID;
        this.dataHandlerObject = dataHandlerProvider;
        this.isBinary = true;
        this.optimize = optimize;
        this.nodeType = 4;
    }

    public final int getType() {
        return this.nodeType;
    }

    private void writeOutput(XMLStreamWriter writer) throws XMLStreamException {
        int type = this.getType();
        if (type == 4 || type == 6) {
            writer.writeCharacters(this.getText());
        } else if (type == 12) {
            writer.writeCData(this.getText());
        } else if (type == 9) {
            writer.writeEntityRef(this.getText());
        }
    }

    public String getText() throws OMException {
        if (this.charArray != null || this.value != null) {
            return this.getTextFromProperPlace();
        }
        try {
            return Base64Utils.encode((DataHandler)this.getDataHandler());
        }
        catch (Exception e) {
            throw new OMException(e);
        }
    }

    public char[] getTextCharacters() {
        if (this.charArray != null) {
            return this.charArray;
        }
        if (this.value != null) {
            return this.value.toCharArray();
        }
        try {
            return Base64Utils.encodeToCharArray((DataHandler)this.getDataHandler());
        }
        catch (IOException ex) {
            throw new OMException(ex);
        }
    }

    public boolean isCharacters() {
        return this.charArray != null;
    }

    private String getTextFromProperPlace() {
        return this.charArray != null ? new String(this.charArray) : this.value;
    }

    public QName getTextAsQName() throws OMException {
        return ((OMElement)((Object)this.parent)).resolveQName(this.getTextFromProperPlace());
    }

    public OMNamespace getNamespace() {
        int colon;
        String text;
        if (this.calcNS) {
            return this.textNS;
        }
        this.calcNS = true;
        if (this.getParent() != null && (text = this.getTextFromProperPlace()) != null && (colon = text.indexOf(58)) > 0) {
            this.textNS = ((OMElementImpl)this.getParent()).findNamespaceURI(text.substring(0, colon));
            if (this.textNS != null) {
                this.charArray = null;
                this.value = text.substring(colon + 1);
            }
        }
        return this.textNS;
    }

    public boolean isOptimized() {
        return this.optimize;
    }

    public void setOptimize(boolean value) {
        this.optimize = value;
        if (value) {
            this.isBinary = true;
        }
    }

    public void setBinary(boolean value) {
        this.isBinary = value;
    }

    public boolean isBinary() {
        return this.isBinary;
    }

    public Object getDataHandler() {
        if ((this.value != null || this.charArray != null) && this.isBinary) {
            String text = this.getTextFromProperPlace();
            return DataHandlerUtils.getDataHandlerFromText(text, this.mimeType);
        }
        if (this.dataHandlerObject == null) {
            throw new OMException("No DataHandler available");
        }
        if (this.dataHandlerObject instanceof DataHandlerProvider) {
            try {
                this.dataHandlerObject = ((DataHandlerProvider)this.dataHandlerObject).getDataHandler();
            }
            catch (IOException ex) {
                throw new OMException(ex);
            }
        }
        return this.dataHandlerObject;
    }

    public String getContentID() {
        if (this.contentID == null) {
            this.contentID = UIDGenerator.generateContentId();
        }
        return this.contentID;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        if (!this.isBinary) {
            this.writeOutput(writer);
        } else {
            try {
                if (this.dataHandlerObject instanceof DataHandlerProvider) {
                    XMLStreamWriterUtils.writeDataHandler(writer, (DataHandlerProvider)this.dataHandlerObject, this.contentID, this.optimize);
                } else {
                    XMLStreamWriterUtils.writeDataHandler(writer, (DataHandler)this.getDataHandler(), this.contentID, this.optimize);
                }
            }
            catch (IOException ex) {
                throw new OMException("Error reading data handler", ex);
            }
        }
    }

    public void buildWithAttachments() {
        if (this.isOptimized()) {
            ((DataHandler)this.getDataHandler()).getDataSource();
        }
    }

    public void setContentID(String cid) {
        this.contentID = cid;
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        if (this.isBinary && options.isFetchDataHandlers()) {
            ((DataHandler)this.getDataHandler()).getDataSource();
        }
        return this.factory.createOMText(targetParent, this);
    }
}

