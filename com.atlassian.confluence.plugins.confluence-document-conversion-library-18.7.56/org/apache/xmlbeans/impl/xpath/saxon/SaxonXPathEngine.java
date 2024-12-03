/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.saxon.value.DateTimeValue
 *  net.sf.saxon.value.GDateValue
 */
package org.apache.xmlbeans.impl.xpath.saxon;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.GDateValue;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlDecimal;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlLong;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.xpath.XPathEngine;
import org.apache.xmlbeans.impl.xpath.XPathExecutionContext;
import org.apache.xmlbeans.impl.xpath.saxon.SaxonXPath;
import org.w3c.dom.Node;

public class SaxonXPathEngine
extends XPathExecutionContext
implements XPathEngine {
    private final DateFormat xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ROOT);
    private Cur _cur;
    private SaxonXPath _engine;
    private boolean _firstCall = true;
    private final long _version;

    SaxonXPathEngine(SaxonXPath xpathImpl, Cur c) {
        this._engine = xpathImpl;
        this._version = c.getLocale().version();
        this._cur = c.weakCur(this);
    }

    @Override
    public boolean next(Cur c) {
        if (!this._firstCall) {
            return false;
        }
        this._firstCall = false;
        if (this._cur != null && this._version != this._cur.getLocale().version()) {
            throw new ConcurrentModificationException("Document changed during select");
        }
        List resultsList = this._engine.selectPath(this._cur.getDom());
        for (int i = 0; i < resultsList.size(); ++i) {
            Object node = resultsList.get(i);
            Cur pos = null;
            if (!(node instanceof Node)) {
                Object obj = resultsList.get(i);
                String value = obj instanceof Date ? this.xmlDateFormat.format((Date)obj) : (obj instanceof GDateValue ? ((GDateValue)obj).getStringValue() : (obj instanceof DateTimeValue ? ((DateTimeValue)obj).getStringValue() : (obj instanceof BigDecimal ? ((BigDecimal)obj).toPlainString() : obj.toString())));
                Locale l = c.getLocale();
                try {
                    pos = l.load("<xml-fragment/>").tempCur();
                    pos.setValue(value);
                    SchemaType type = this.getType(node);
                    Locale.autoTypeDocument(pos, type, null);
                    pos.next();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                assert (node instanceof DomImpl.Dom) : "New object created in XPATH!";
                pos = ((DomImpl.Dom)node).tempCur();
            }
            c.addToSelection(pos);
            pos.release();
        }
        this.release();
        this._engine = null;
        return true;
    }

    private SchemaType getType(Object node) {
        SchemaType type = node instanceof Integer ? XmlInteger.type : (node instanceof Double ? XmlDouble.type : (node instanceof Long ? XmlLong.type : (node instanceof Float ? XmlFloat.type : (node instanceof BigDecimal ? XmlDecimal.type : (node instanceof Boolean ? XmlBoolean.type : (node instanceof String ? XmlString.type : (node instanceof GDateValue ? XmlDate.type : (node instanceof DateTimeValue ? XmlDateTime.type : XmlAnySimpleType.type))))))));
        return type;
    }

    @Override
    public void release() {
        if (this._cur != null) {
            this._cur.release();
            this._cur = null;
        }
    }
}

