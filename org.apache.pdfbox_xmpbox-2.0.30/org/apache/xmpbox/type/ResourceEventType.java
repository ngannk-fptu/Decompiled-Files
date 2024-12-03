/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.util.Calendar;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="stEvt", namespace="http://ns.adobe.com/xap/1.0/sType/ResourceEvent#")
public class ResourceEventType
extends AbstractStructuredType {
    @PropertyType(type=Types.Choice, card=Cardinality.Simple)
    public static final String ACTION = "action";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String CHANGED = "changed";
    @PropertyType(type=Types.GUID, card=Cardinality.Simple)
    public static final String INSTANCE_ID = "instanceID";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String PARAMETERS = "parameters";
    @PropertyType(type=Types.AgentName, card=Cardinality.Simple)
    public static final String SOFTWARE_AGENT = "softwareAgent";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String WHEN = "when";

    public ResourceEventType(XMPMetadata metadata) {
        super(metadata);
        this.addNamespace(this.getNamespace(), this.getPreferedPrefix());
    }

    public String getInstanceID() {
        return this.getPropertyValueAsString(INSTANCE_ID);
    }

    public void setInstanceID(String value) {
        this.addSimpleProperty(INSTANCE_ID, value);
    }

    public String getSoftwareAgent() {
        return this.getPropertyValueAsString(SOFTWARE_AGENT);
    }

    public void setSoftwareAgent(String value) {
        this.addSimpleProperty(SOFTWARE_AGENT, value);
    }

    public Calendar getWhen() {
        return this.getDatePropertyAsCalendar(WHEN);
    }

    public void setWhen(Calendar value) {
        this.addSimpleProperty(WHEN, value);
    }

    public String getAction() {
        return this.getPropertyValueAsString(ACTION);
    }

    public void setAction(String value) {
        this.addSimpleProperty(ACTION, value);
    }

    public String getChanged() {
        return this.getPropertyValueAsString(CHANGED);
    }

    public void setChanged(String value) {
        this.addSimpleProperty(CHANGED, value);
    }

    public String getParameters() {
        return this.getPropertyValueAsString(PARAMETERS);
    }

    public void setParameters(String value) {
        this.addSimpleProperty(PARAMETERS, value);
    }
}

