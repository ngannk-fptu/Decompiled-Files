/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol.error;

import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.protocol.error.ProtocolException;
import org.apache.abdera.writer.StreamWriter;

public class Error
extends ExtensibleElementWrapper {
    public static final String NS = "http://abdera.apache.org";
    public static final QName ERROR = new QName("http://abdera.apache.org", "error");
    public static final QName CODE = new QName("http://abdera.apache.org", "code");
    public static final QName MESSAGE = new QName("http://abdera.apache.org", "message");

    public Error(Element internal) {
        super(internal);
    }

    public Error(Factory factory, QName qname) {
        super(factory, qname);
    }

    public int getCode() {
        String code = this.getSimpleExtension(CODE);
        return code != null ? Integer.parseInt(code) : -1;
    }

    public Error setCode(int code) {
        if (code > -1) {
            Object element = this.getExtension(CODE);
            if (element != null) {
                element.setText(Integer.toString(code));
            } else {
                this.addSimpleExtension(CODE, Integer.toString(code));
            }
        } else {
            Object element = this.getExtension(CODE);
            if (element != null) {
                element.discard();
            }
        }
        return this;
    }

    public String getMessage() {
        return this.getSimpleExtension(MESSAGE);
    }

    public Error setMessage(String message) {
        if (message != null) {
            Object element = this.getExtension(MESSAGE);
            if (element != null) {
                element.setText(message);
            } else {
                this.addSimpleExtension(MESSAGE, message);
            }
        } else {
            Object element = this.getExtension(MESSAGE);
            if (element != null) {
                element.discard();
            }
        }
        return this;
    }

    public void throwException() {
        throw new ProtocolException(this);
    }

    public static Error create(Abdera abdera, int code, String message) {
        return Error.create(abdera, code, message, null);
    }

    public static Error create(Abdera abdera, int code, String message, Throwable t) {
        Document doc = abdera.getFactory().newDocument();
        Error error = (Error)abdera.getFactory().newElement(ERROR, doc);
        error.setCode(code).setMessage(message);
        return error;
    }

    public static void create(StreamWriter sw, int code, String message, Throwable t) {
        sw.startDocument().startElement(ERROR).startElement(CODE).writeElementText(code).endElement().startElement(MESSAGE).writeElementText(message).endElement().endElement().endDocument();
    }
}

