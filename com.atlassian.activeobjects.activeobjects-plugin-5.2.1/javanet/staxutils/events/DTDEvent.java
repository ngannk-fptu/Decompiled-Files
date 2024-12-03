/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import java.util.Collections;
import java.util.List;
import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.stream.Location;
import javax.xml.stream.events.DTD;

public class DTDEvent
extends AbstractXMLEvent
implements DTD {
    protected String declaration;
    protected List entities;
    protected List notations;

    public DTDEvent(String declaration, Location location) {
        super(location);
        this.declaration = declaration;
    }

    public DTDEvent(String declaration, List entities, List notations, Location location) {
        super(location);
        this.declaration = declaration;
        this.entities = entities == null ? Collections.EMPTY_LIST : entities;
        this.notations = notations == null ? Collections.EMPTY_LIST : notations;
    }

    public DTDEvent(DTD that) {
        super(that);
        this.declaration = that.getDocumentTypeDeclaration();
        this.entities = that.getEntities();
        this.notations = that.getNotations();
    }

    public int getEventType() {
        return 11;
    }

    public String getDocumentTypeDeclaration() {
        return this.declaration;
    }

    public List getEntities() {
        return this.entities;
    }

    public List getNotations() {
        return this.notations;
    }

    public Object getProcessedDTD() {
        return null;
    }
}

