/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream.events;

import java.util.List;
import javax.xml.stream.events.XMLEvent;

public interface DTD
extends XMLEvent {
    public String getDocumentTypeDeclaration();

    public Object getProcessedDTD();

    public List getNotations();

    public List getEntities();
}

