/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.clauses;

import aQute.bnd.build.model.clauses.HeaderClause;
import aQute.bnd.header.Attrs;

public class ExportedPackage
extends HeaderClause {
    public ExportedPackage(String packageName, Attrs attribs) {
        super(packageName, attribs);
    }

    @Override
    protected boolean newlinesBetweenAttributes() {
        return false;
    }

    public void setVersionString(String version) {
        this.attribs.put("version", version);
    }

    public String getVersionString() {
        return this.attribs.get("version");
    }

    public boolean isProvided() {
        return Boolean.valueOf(this.attribs.get("provide:"));
    }

    public void setProvided(boolean provided) {
        if (provided) {
            this.attribs.put("provide:", Boolean.toString(true));
        } else {
            this.attribs.remove("provide:");
        }
    }

    @Override
    public ExportedPackage clone() {
        return new ExportedPackage(this.name, new Attrs(this.attribs));
    }

    public static ExportedPackage error(String msg) {
        return new ExportedPackage(msg, null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getName());
        if (this.isProvided()) {
            sb.append(";").append("provided:=true");
        }
        if (this.getAttribs().containsKey("version")) {
            sb.append(";version=").append(this.getAttribs().get("version"));
        }
        return sb.toString();
    }
}

