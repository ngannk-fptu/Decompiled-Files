/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.model.IRIElement;
import org.apache.abdera.model.Person;
import org.apache.abdera.util.Constants;

public abstract class PersonWrapper
extends ExtensibleElementWrapper
implements Person,
Constants {
    protected PersonWrapper(Element internal) {
        super(internal);
    }

    public PersonWrapper(Factory factory, QName qname) {
        super(factory, qname);
    }

    public String getEmail() {
        Element email = this.getEmailElement();
        return email != null ? email.getText() : null;
    }

    public Element getEmailElement() {
        return this.getInternal().getFirstChild(EMAIL);
    }

    public String getName() {
        Element name = this.getNameElement();
        return name != null ? name.getText() : null;
    }

    public Element getNameElement() {
        return this.getInternal().getFirstChild(NAME);
    }

    public IRI getUri() {
        IRIElement iri = this.getUriElement();
        return iri != null ? iri.getResolvedValue() : null;
    }

    public IRIElement getUriElement() {
        return (IRIElement)this.getInternal().getFirstChild(URI);
    }

    public Element setEmail(String email) {
        ExtensibleElement internal = this.getExtInternal();
        Element el = this.getEmailElement();
        if (email != null) {
            if (el == null) {
                el = internal.getFactory().newEmail(internal);
            }
            el.setText(email);
            return el;
        }
        if (el != null) {
            el.discard();
        }
        return null;
    }

    public Person setEmailElement(Element element) {
        ExtensibleElement internal = this.getExtInternal();
        Element el = this.getEmailElement();
        if (el != null) {
            el.discard();
        }
        if (element != null) {
            internal.addExtension(element);
        }
        return this;
    }

    public Element setName(String name) {
        ExtensibleElement internal = this.getExtInternal();
        Element el = this.getNameElement();
        if (name != null) {
            if (el == null) {
                el = internal.getFactory().newName(internal);
            }
            el.setText(name);
            return el;
        }
        if (el != null) {
            el.discard();
        }
        return null;
    }

    public Person setNameElement(Element element) {
        ExtensibleElement internal = this.getExtInternal();
        Element el = this.getNameElement();
        if (el != null) {
            el.discard();
        }
        if (element != null) {
            internal.addExtension(element);
        }
        return this;
    }

    public IRIElement setUri(String uri) {
        ExtensibleElement internal = this.getExtInternal();
        IRIElement el = this.getUriElement();
        if (uri != null) {
            if (el == null) {
                el = internal.getFactory().newUri(internal);
            }
            el.setText(uri.toString());
            return el;
        }
        if (el != null) {
            el.discard();
        }
        return null;
    }

    public Person setUriElement(IRIElement element) {
        ExtensibleElement internal = this.getExtInternal();
        IRIElement el = this.getUriElement();
        if (el != null) {
            el.discard();
        }
        if (element != null) {
            internal.addExtension(element);
        }
        return this;
    }
}

