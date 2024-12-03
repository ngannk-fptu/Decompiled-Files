/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.GlobalDeclState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.util.HashSet;
import java.util.Set;

public class SchemaIncludedState
extends GlobalDeclState {
    protected String expectedTargetNamespace;
    private String previousElementFormDefault;
    private String previousAttributeFormDefault;
    private String previousFinalDefault;
    private String previousBlockDefault;
    private String previousChameleonTargetNamespace;
    private boolean ignoreContents = false;

    protected SchemaIncludedState(String expectedTargetNamespace) {
        this.expectedTargetNamespace = expectedTargetNamespace;
    }

    protected State createChildState(StartTagInfo tag) {
        if (this.ignoreContents) {
            return new IgnoreState();
        }
        return super.createChildState(tag);
    }

    protected void startSelf() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        super.startSelf();
        this.previousElementFormDefault = reader.elementFormDefault;
        this.previousAttributeFormDefault = reader.attributeFormDefault;
        this.previousFinalDefault = reader.finalDefault;
        this.previousBlockDefault = reader.blockDefault;
        this.previousChameleonTargetNamespace = reader.chameleonTargetNamespace;
        reader.chameleonTargetNamespace = null;
        String targetNs = this.startTag.getAttribute("targetNamespace");
        if (targetNs == null) {
            if (this.expectedTargetNamespace == null) {
                targetNs = "";
            } else {
                targetNs = this.expectedTargetNamespace;
                reader.chameleonTargetNamespace = this.expectedTargetNamespace;
            }
        } else if (this.expectedTargetNamespace != null && !this.expectedTargetNamespace.equals(targetNs)) {
            reader.reportError("XMLSchemaReader.InconsistentTargetNamespace", (Object)targetNs, (Object)this.expectedTargetNamespace);
        }
        Set<String> s = reader.parsedFiles.get(targetNs);
        if (s == null) {
            s = new HashSet<String>();
            reader.parsedFiles.put(targetNs, s);
        }
        if (s.contains(this.location.getSystemId())) {
            this.ignoreContents = true;
        } else {
            s.add(this.location.getSystemId());
        }
        this.onTargetNamespaceResolved(targetNs, this.ignoreContents);
        String form = this.startTag.getDefaultedAttribute("elementFormDefault", "unqualified");
        if (form.equals("qualified")) {
            reader.elementFormDefault = targetNs;
        } else {
            reader.elementFormDefault = "";
            if (!form.equals("unqualified")) {
                reader.reportError("GrammarReader.BadAttributeValue", (Object)"elementFormDefault", (Object)form);
            }
        }
        form = this.startTag.getDefaultedAttribute("attributeFormDefault", "unqualified");
        if (form.equals("qualified")) {
            reader.attributeFormDefault = targetNs;
        } else {
            reader.attributeFormDefault = "";
            if (!form.equals("unqualified")) {
                reader.reportError("GrammarReader.BadAttributeValue", (Object)"attributeFormDefault", (Object)form);
            }
        }
        reader.finalDefault = this.startTag.getAttribute("finalDefault");
        reader.blockDefault = this.startTag.getAttribute("blockDefault");
    }

    protected void onTargetNamespaceResolved(String targetNs, boolean ignoreContents) {
    }

    protected void endSelf() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        reader.elementFormDefault = this.previousElementFormDefault;
        reader.attributeFormDefault = this.previousAttributeFormDefault;
        reader.finalDefault = this.previousFinalDefault;
        reader.blockDefault = this.previousBlockDefault;
        reader.chameleonTargetNamespace = this.previousChameleonTargetNamespace;
        super.endSelf();
    }
}

