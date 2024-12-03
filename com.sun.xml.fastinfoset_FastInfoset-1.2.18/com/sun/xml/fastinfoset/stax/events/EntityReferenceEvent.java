/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.stax.events.EventBase;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;

public class EntityReferenceEvent
extends EventBase
implements EntityReference {
    private EntityDeclaration _entityDeclaration;
    private String _entityName;

    public EntityReferenceEvent() {
        this.init();
    }

    public EntityReferenceEvent(String entityName, EntityDeclaration entityDeclaration) {
        this.init();
        this._entityName = entityName;
        this._entityDeclaration = entityDeclaration;
    }

    @Override
    public String getName() {
        return this._entityName;
    }

    @Override
    public EntityDeclaration getDeclaration() {
        return this._entityDeclaration;
    }

    public void setName(String name) {
        this._entityName = name;
    }

    public void setDeclaration(EntityDeclaration declaration) {
        this._entityDeclaration = declaration;
    }

    public String toString() {
        String text = this._entityDeclaration.getReplacementText();
        if (text == null) {
            text = "";
        }
        return "&" + this.getName() + ";='" + text + "'";
    }

    protected void init() {
        this.setEventType(9);
    }
}

