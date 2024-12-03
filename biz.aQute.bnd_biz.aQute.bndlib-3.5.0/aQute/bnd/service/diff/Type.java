/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.diff;

public enum Type {
    ACCESS,
    BUNDLE,
    API,
    MANIFEST,
    PACKAGE,
    CLASS,
    INTERFACE,
    ANNOTATION,
    ENUM,
    EXTENDS,
    IMPLEMENTS,
    FIELD,
    METHOD,
    ANNOTATED,
    PROPERTY,
    RESOURCE,
    SHA,
    CUSTOM,
    CLAUSE,
    HEADER,
    PARAMETER,
    CLASS_VERSION,
    RESOURCES,
    CONSTANT,
    RETURN,
    VERSION,
    DEPRECATED,
    REPO,
    PROGRAM,
    REVISION;


    public boolean isInherited() {
        return false;
    }
}

