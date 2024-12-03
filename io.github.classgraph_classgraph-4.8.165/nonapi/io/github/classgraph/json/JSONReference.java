/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.json;

class JSONReference {
    Object idObject;

    public JSONReference(Object idObject) {
        if (idObject == null) {
            throw new IllegalArgumentException("idObject cannot be null");
        }
        this.idObject = idObject;
    }
}

