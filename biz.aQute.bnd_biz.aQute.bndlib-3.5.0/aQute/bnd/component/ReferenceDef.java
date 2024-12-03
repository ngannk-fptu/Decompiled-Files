/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.component;

import aQute.bnd.component.AnnotationReader;
import aQute.bnd.component.ComponentDef;
import aQute.bnd.component.FieldCollectionType;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.version.Version;
import aQute.bnd.xmlattribute.ExtensionDef;
import aQute.bnd.xmlattribute.Namespaces;
import aQute.bnd.xmlattribute.XMLAttributeFinder;
import aQute.lib.tag.Tag;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ReferenceScope;

class ReferenceDef
extends ExtensionDef {
    String className;
    String bindDescriptor;
    Version version = AnnotationReader.V1_0;
    String name;
    String service;
    ReferenceCardinality cardinality;
    ReferencePolicy policy;
    ReferencePolicyOption policyOption;
    String target;
    String bind;
    String unbind;
    String updated;
    ReferenceScope scope;
    String field;
    FieldOption fieldOption;
    FieldCollectionType fieldCollectionType;

    public ReferenceDef(XMLAttributeFinder finder) {
        super(finder);
    }

    public void prepare(Analyzer analyzer) throws Exception {
        String error;
        if (this.name == null) {
            analyzer.error("No name for a reference", new Object[0]);
        }
        if (this.updated != null && !this.updated.equals("-") || this.policyOption != null) {
            this.updateVersion(AnnotationReader.V1_2);
        }
        if (this.target != null && (error = Verifier.validateFilter(this.target)) != null) {
            analyzer.error("Invalid target filter %s for %s", this.target, this.name);
        }
        if (this.service == null) {
            analyzer.error("No interface specified on %s", this.name);
        }
        if (this.scope != null || this.field != null) {
            this.updateVersion(AnnotationReader.V1_3);
        }
    }

    public Tag getTag(Namespaces namespaces) {
        Tag ref = new Tag("reference", new Object[0]);
        ref.addAttribute("name", this.name);
        if (this.cardinality != null) {
            ref.addAttribute("cardinality", this.cardinality.toString());
        }
        if (this.policy != null) {
            ref.addAttribute("policy", this.policy.toString());
        }
        ref.addAttribute("interface", this.service);
        if (this.target != null) {
            ref.addAttribute("target", this.target);
        }
        if (this.bind != null && !"-".equals(this.bind)) {
            ref.addAttribute("bind", this.bind);
        }
        if (this.unbind != null && !"-".equals(this.unbind)) {
            ref.addAttribute("unbind", this.unbind);
        }
        if (this.updated != null && !"-".equals(this.updated)) {
            ref.addAttribute("updated", this.updated);
        }
        if (this.policyOption != null) {
            ref.addAttribute("policy-option", this.policyOption.toString());
        }
        if (this.scope != null) {
            ref.addAttribute("scope", this.scope.toString());
        }
        if (this.field != null) {
            ref.addAttribute("field", this.field);
        }
        if (this.fieldOption != null) {
            ref.addAttribute("field-option", this.fieldOption.toString());
        }
        if (this.fieldCollectionType != null) {
            ref.addAttribute("field-collection-type", this.fieldCollectionType.toString());
        }
        this.addAttributes(ref, namespaces);
        return ref;
    }

    public String toString() {
        return this.name;
    }

    void updateVersion(Version version) {
        this.version = ComponentDef.max(this.version, version);
    }
}

