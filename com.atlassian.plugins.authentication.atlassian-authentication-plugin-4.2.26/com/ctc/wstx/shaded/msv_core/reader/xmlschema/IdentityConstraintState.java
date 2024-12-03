/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.Field;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.IdentityConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.KeyConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.KeyRefConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.UniqueConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XPath;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.ElementDeclState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.util.StringTokenizer;
import java.util.Vector;
import org.xml.sax.Locator;

public class IdentityConstraintState
extends SimpleState {
    protected XPath[] selector;
    protected final Vector fields = new Vector();

    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("selector")) {
            String v = tag.getAttribute("xpath");
            if (v != null) {
                this.selector = this.parseSelector(v);
            } else {
                this.reader.reportError("GrammarReader.MissingAttribute", (Object)"selector", (Object)"xpath");
                this.selector = new XPath[0];
            }
            return new ChildlessState();
        }
        if (tag.localName.equals("field")) {
            String v = tag.getAttribute("xpath");
            if (v != null) {
                this.fields.add(this.parseField(v));
            } else {
                this.reader.reportError("GrammarReader.MissingAttribute", (Object)"field", (Object)"xpath");
            }
            return new ChildlessState();
        }
        return null;
    }

    protected void endSelf() {
        this.createIdentityConstraint();
        super.endSelf();
    }

    protected void createIdentityConstraint() {
        IdentityConstraint id;
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            reader.reportError("GrammarReader.MissingAttribute", (Object)this.startTag.localName, (Object)"name");
            return;
        }
        Field[] fs = this.fields.toArray(new Field[this.fields.size()]);
        if (this.startTag.localName.equals("key")) {
            id = new KeyConstraint(reader.currentSchema.targetNamespace, name, this.selector, fs);
        } else if (this.startTag.localName.equals("unique")) {
            id = new UniqueConstraint(reader.currentSchema.targetNamespace, name, this.selector, fs);
        } else if (this.startTag.localName.equals("keyref")) {
            final String refer = this.startTag.getAttribute("refer");
            if (refer == null) {
                reader.reportError("GrammarReader.MissingAttribute", (Object)this.startTag.localName, (Object)"refer");
                return;
            }
            final String[] qn = reader.splitQName(refer);
            if (qn == null) {
                reader.reportError("XMLSchemaReader.UndeclaredPrefix", qn);
                return;
            }
            final KeyRefConstraint keyRef = new KeyRefConstraint(reader.currentSchema.targetNamespace, name, this.selector, fs);
            id = keyRef;
            reader.addBackPatchJob(new GrammarReader.BackPatch(){

                public State getOwnerState() {
                    return IdentityConstraintState.this;
                }

                public void patch() {
                    XMLSchemaSchema s = reader.grammar.getByNamespace(qn[0]);
                    if (s == null) {
                        reader.reportError("XMLSchemaReader.UndefinedSchema", (Object)qn[0]);
                        return;
                    }
                    IdentityConstraint idc = s.identityConstraints.get(qn[1]);
                    if (idc == null) {
                        reader.reportError("XMLSchemaReader.UndefinedKey", (Object)refer);
                        return;
                    }
                    if (!(idc instanceof KeyConstraint)) {
                        reader.reportError("XMLSchemaReader.KeyrefReferringNonKey", (Object)refer);
                        return;
                    }
                    if (idc.fields.length != keyRef.fields.length) {
                        reader.reportError(new Locator[]{IdentityConstraintState.this.getLocation(), reader.getDeclaredLocationOf(idc)}, "XMLSchemaReader.KeyFieldNumberMismatch", new Object[]{idc.localName, new Integer(idc.fields.length), keyRef.localName, new Integer(keyRef.fields.length)});
                        return;
                    }
                    keyRef.key = (KeyConstraint)idc;
                }
            });
        } else {
            throw new Error();
        }
        if (reader.currentSchema.identityConstraints.get(name) != null) {
            reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(id)}, "XMLSchemaReader.DuplicateIdentityConstraintDefinition", new Object[]{name});
        } else {
            reader.currentSchema.identityConstraints.add(name, id);
        }
        reader.setDeclaredLocationOf(id);
        ((ElementDeclState)this.parentState).onIdentityConstraint(id);
    }

    protected XPath[] parseSelector(String xpath) {
        Vector<XPath> pathObjs = new Vector<XPath>();
        StringTokenizer paths = new StringTokenizer(xpath, "|");
        while (paths.hasMoreTokens()) {
            XPath pathObj = new XPath();
            pathObjs.add(pathObj);
            if (this.parsePath(pathObj, paths.nextToken(), false)) continue;
            return new XPath[0];
        }
        return pathObjs.toArray(new XPath[pathObjs.size()]);
    }

    protected Field parseField(String xpath) {
        Vector<XPath> pathObjs = new Vector<XPath>();
        Field field = new Field();
        StringTokenizer paths = new StringTokenizer(xpath, "|");
        while (paths.hasMoreTokens()) {
            XPath pathObj = new XPath();
            pathObjs.add(pathObj);
            if (this.parsePath(pathObj, paths.nextToken(), true)) continue;
            return new Field();
        }
        field.paths = pathObjs.toArray(new XPath[pathObjs.size()]);
        return field;
    }

    protected boolean parsePath(XPath pathObj, String xpath, boolean parseField) {
        Vector<NameClass> stepObjs = new Vector<NameClass>();
        if (xpath.startsWith(".//")) {
            pathObj.isAnyDescendant = true;
            xpath = xpath.substring(3);
        }
        StringTokenizer steps = new StringTokenizer(xpath, "/");
        stepObjs.clear();
        while (steps.hasMoreTokens()) {
            String[] qn;
            String step = steps.nextToken();
            if (step.equals(".")) continue;
            if (step.equals("*")) {
                stepObjs.add(NameClass.ALL);
                continue;
            }
            boolean attribute = false;
            if (step.charAt(0) == '@' && parseField && !steps.hasMoreTokens()) {
                attribute = true;
                step = step.substring(1);
            }
            if ((qn = this.reader.splitQName(step)) == null) {
                this.reader.reportError("XMLSchemaReader.BadXPath", (Object)step);
                return false;
            }
            if (attribute && step.indexOf(58) < 0) {
                qn[0] = "";
            }
            NameClass nc = qn[1].equals("*") ? new NamespaceNameClass(qn[0]) : new SimpleNameClass(qn[0], qn[1]);
            if (attribute) {
                pathObj.attributeStep = nc;
                continue;
            }
            stepObjs.add(nc);
        }
        pathObj.steps = stepObjs.toArray(new NameClass[stepObjs.size()]);
        return true;
    }
}

