/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.util.Calendar;
import java.util.Date;
import javax.xml.namespace.QName;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.DateTime;
import org.apache.abdera.parser.stax.FOMElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

public class FOMDateTime
extends FOMElement
implements DateTime {
    private static final long serialVersionUID = -6611503566172011733L;
    private AtomDate value;

    protected FOMDateTime(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    protected FOMDateTime(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(qname, parent, factory);
    }

    protected FOMDateTime(String localName, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(localName, parent, factory, builder);
    }

    public AtomDate getValue() {
        String v;
        if (this.value == null && (v = this.getText()) != null) {
            this.value = AtomDate.valueOf(v);
        }
        return this.value;
    }

    public DateTime setValue(AtomDate dateTime) {
        this.complete();
        this.value = null;
        if (dateTime != null) {
            this.setText(dateTime.getValue());
        } else {
            this._removeAllChildren();
        }
        return this;
    }

    public DateTime setDate(Date date) {
        this.complete();
        this.value = null;
        if (date != null) {
            this.setText(AtomDate.valueOf(date).getValue());
        } else {
            this._removeAllChildren();
        }
        return this;
    }

    public DateTime setCalendar(Calendar date) {
        this.complete();
        this.value = null;
        if (date != null) {
            this.setText(AtomDate.valueOf(date).getValue());
        } else {
            this._removeAllChildren();
        }
        return this;
    }

    public DateTime setTime(long date) {
        this.complete();
        this.value = null;
        this.setText(AtomDate.valueOf(date).getValue());
        return this;
    }

    public DateTime setString(String date) {
        this.complete();
        this.value = null;
        if (date != null) {
            this.setText(AtomDate.valueOf(date).getValue());
        } else {
            this._removeAllChildren();
        }
        return this;
    }

    public Date getDate() {
        AtomDate ad = this.getValue();
        return ad != null ? ad.getDate() : null;
    }

    public Calendar getCalendar() {
        AtomDate ad = this.getValue();
        return ad != null ? ad.getCalendar() : null;
    }

    public long getTime() {
        AtomDate ad = this.getValue();
        return ad != null ? Long.valueOf(ad.getTime()) : null;
    }

    public String getString() {
        AtomDate ad = this.getValue();
        return ad != null ? ad.getValue() : null;
    }
}

