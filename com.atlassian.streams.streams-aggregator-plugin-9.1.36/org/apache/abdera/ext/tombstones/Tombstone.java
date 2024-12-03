/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.ext.tombstones;

import java.util.Calendar;
import java.util.Date;
import org.apache.abdera.ext.tombstones.TombstonesHelper;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;

public class Tombstone
extends ExtensibleElementWrapper {
    public Tombstone(Element internal) {
        super(internal);
    }

    public Tombstone(Factory factory) {
        super(factory, TombstonesHelper.DELETED_ENTRY);
    }

    public String getRef() {
        return this.getAttributeValue("ref");
    }

    public Tombstone setRef(String id) {
        if (id != null) {
            this.setAttributeValue("ref", id);
        } else {
            this.removeAttribute("ref");
        }
        return this;
    }

    public Tombstone setRef(IRI id) {
        return this.setRef(id.toString());
    }

    public Date getWhen() {
        String v = this.getAttributeValue("when");
        return v != null ? AtomDate.parse(v) : null;
    }

    public Tombstone setWhen(Date date) {
        return this.setWhen(AtomDate.format(date));
    }

    public Tombstone setWhen(String date) {
        if (date != null) {
            this.setAttributeValue("when", date);
        } else {
            this.removeAttribute("when");
        }
        return this;
    }

    public Tombstone setWhen(long date) {
        return this.setWhen(AtomDate.valueOf(date));
    }

    public Tombstone setWhen(Calendar date) {
        return this.setWhen(AtomDate.valueOf(date));
    }

    public Tombstone setWhen(AtomDate date) {
        return this.setWhen(date.toString());
    }

    public Person getBy() {
        return (Person)this.getExtension(TombstonesHelper.BY);
    }

    public Tombstone setBy(Person person) {
        if (this.getBy() != null) {
            this.getBy().discard();
        }
        this.addExtension(person);
        return this;
    }

    public Person setBy(String name) {
        return this.setBy(name, null, null);
    }

    public Person setBy(String name, String email, String uri) {
        if (name != null) {
            Person person = this.getFactory().newPerson(TombstonesHelper.BY, this);
            person.setName(name);
            person.setEmail(email);
            person.setUri(uri);
            return person;
        }
        if (this.getBy() != null) {
            this.getBy().discard();
        }
        return null;
    }

    public Text getComment() {
        return (Text)this.getExtension(TombstonesHelper.COMMENT);
    }

    public Text setComment(String comment) {
        return this.setComment(Text.Type.TEXT, comment);
    }

    public Text setComment(Text.Type type, String comment) {
        if (comment != null) {
            Text text = this.getFactory().newText(TombstonesHelper.COMMENT, type, this);
            text.setValue(comment);
            return text;
        }
        if (this.getComment() != null) {
            this.getComment().discard();
        }
        return null;
    }
}

