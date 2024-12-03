/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 *  org.glassfish.gmbal.ManagedAttribute
 *  org.glassfish.gmbal.ManagedData
 */
package com.sun.xml.ws.developer;

import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.developer.MemberSubmissionAddressing;
import javax.xml.ws.WebServiceFeature;
import org.glassfish.gmbal.ManagedAttribute;
import org.glassfish.gmbal.ManagedData;

@ManagedData
public class MemberSubmissionAddressingFeature
extends WebServiceFeature {
    public static final String ID = "http://java.sun.com/xml/ns/jaxws/2004/08/addressing";
    public static final String IS_REQUIRED = "ADDRESSING_IS_REQUIRED";
    private boolean required;
    private MemberSubmissionAddressing.Validation validation = MemberSubmissionAddressing.Validation.LAX;

    public MemberSubmissionAddressingFeature() {
        this.enabled = true;
    }

    public MemberSubmissionAddressingFeature(boolean enabled) {
        this.enabled = enabled;
    }

    public MemberSubmissionAddressingFeature(boolean enabled, boolean required) {
        this.enabled = enabled;
        this.required = required;
    }

    @FeatureConstructor(value={"enabled", "required", "validation"})
    public MemberSubmissionAddressingFeature(boolean enabled, boolean required, MemberSubmissionAddressing.Validation validation) {
        this.enabled = enabled;
        this.required = required;
        this.validation = validation;
    }

    @ManagedAttribute
    public String getID() {
        return ID;
    }

    @ManagedAttribute
    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setValidation(MemberSubmissionAddressing.Validation validation) {
        this.validation = validation;
    }

    @ManagedAttribute
    public MemberSubmissionAddressing.Validation getValidation() {
        return this.validation;
    }
}

