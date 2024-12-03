/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.templates;

import javax.xml.transform.TransformerException;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.serializer.SerializationHandler;
import org.xml.sax.SAXException;

public class ElemTextLiteral
extends ElemTemplateElement {
    static final long serialVersionUID = -7872620006767660088L;
    private boolean m_preserveSpace;
    private char[] m_ch;
    private String m_str;
    private boolean m_disableOutputEscaping = false;

    public void setPreserveSpace(boolean v) {
        this.m_preserveSpace = v;
    }

    public boolean getPreserveSpace() {
        return this.m_preserveSpace;
    }

    public void setChars(char[] v) {
        this.m_ch = v;
    }

    public char[] getChars() {
        return this.m_ch;
    }

    @Override
    public synchronized String getNodeValue() {
        if (null == this.m_str) {
            this.m_str = new String(this.m_ch);
        }
        return this.m_str;
    }

    public void setDisableOutputEscaping(boolean v) {
        this.m_disableOutputEscaping = v;
    }

    public boolean getDisableOutputEscaping() {
        return this.m_disableOutputEscaping;
    }

    @Override
    public int getXSLToken() {
        return 78;
    }

    @Override
    public String getNodeName() {
        return "#Text";
    }

    @Override
    public void execute(TransformerImpl transformer) throws TransformerException {
        try {
            SerializationHandler rth = transformer.getResultTreeHandler();
            if (transformer.getDebug()) {
                rth.flushPending();
                transformer.getTraceManager().fireTraceEvent(this);
            }
            if (this.m_disableOutputEscaping) {
                rth.processingInstruction("javax.xml.transform.disable-output-escaping", "");
            }
            rth.characters(this.m_ch, 0, this.m_ch.length);
            if (this.m_disableOutputEscaping) {
                rth.processingInstruction("javax.xml.transform.enable-output-escaping", "");
            }
        }
        catch (SAXException se) {
            throw new TransformerException(se);
        }
        finally {
            if (transformer.getDebug()) {
                try {
                    transformer.getResultTreeHandler().flushPending();
                    transformer.getTraceManager().fireTraceEndEvent(this);
                }
                catch (SAXException se) {
                    throw new TransformerException(se);
                }
            }
        }
    }
}

