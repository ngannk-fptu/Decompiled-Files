/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.xerces.impl.xs.SchemaGrammar;

public class XSGrammarBucket {
    Hashtable fGrammarRegistry = new Hashtable();
    SchemaGrammar fNoNSGrammar = null;

    public SchemaGrammar getGrammar(String string) {
        if (string == null) {
            return this.fNoNSGrammar;
        }
        return (SchemaGrammar)this.fGrammarRegistry.get(string);
    }

    public void putGrammar(SchemaGrammar schemaGrammar) {
        if (schemaGrammar.getTargetNamespace() == null) {
            this.fNoNSGrammar = schemaGrammar;
        } else {
            this.fGrammarRegistry.put(schemaGrammar.getTargetNamespace(), schemaGrammar);
        }
    }

    public boolean putGrammar(SchemaGrammar schemaGrammar, boolean bl) {
        int n;
        SchemaGrammar schemaGrammar2 = this.getGrammar(schemaGrammar.fTargetNamespace);
        if (schemaGrammar2 != null) {
            return schemaGrammar2 == schemaGrammar;
        }
        if (!bl) {
            this.putGrammar(schemaGrammar);
            return true;
        }
        Vector vector = schemaGrammar.getImportedGrammars();
        if (vector == null) {
            this.putGrammar(schemaGrammar);
            return true;
        }
        Vector vector2 = (Vector)vector.clone();
        for (n = 0; n < vector2.size(); ++n) {
            SchemaGrammar schemaGrammar3 = (SchemaGrammar)vector2.elementAt(n);
            SchemaGrammar schemaGrammar4 = this.getGrammar(schemaGrammar3.fTargetNamespace);
            if (schemaGrammar4 == null) {
                Vector vector3 = schemaGrammar3.getImportedGrammars();
                if (vector3 == null) continue;
                for (int i = vector3.size() - 1; i >= 0; --i) {
                    schemaGrammar4 = (SchemaGrammar)vector3.elementAt(i);
                    if (vector2.contains(schemaGrammar4)) continue;
                    vector2.addElement(schemaGrammar4);
                }
                continue;
            }
            if (schemaGrammar4 == schemaGrammar3) continue;
            return false;
        }
        this.putGrammar(schemaGrammar);
        for (n = vector2.size() - 1; n >= 0; --n) {
            this.putGrammar((SchemaGrammar)vector2.elementAt(n));
        }
        return true;
    }

    public boolean putGrammar(SchemaGrammar schemaGrammar, boolean bl, boolean bl2) {
        int n;
        if (!bl2) {
            return this.putGrammar(schemaGrammar, bl);
        }
        SchemaGrammar schemaGrammar2 = this.getGrammar(schemaGrammar.fTargetNamespace);
        if (schemaGrammar2 == null) {
            this.putGrammar(schemaGrammar);
        }
        if (!bl) {
            return true;
        }
        Vector vector = schemaGrammar.getImportedGrammars();
        if (vector == null) {
            return true;
        }
        Vector vector2 = (Vector)vector.clone();
        for (n = 0; n < vector2.size(); ++n) {
            SchemaGrammar schemaGrammar3 = (SchemaGrammar)vector2.elementAt(n);
            SchemaGrammar schemaGrammar4 = this.getGrammar(schemaGrammar3.fTargetNamespace);
            if (schemaGrammar4 == null) {
                Vector vector3 = schemaGrammar3.getImportedGrammars();
                if (vector3 == null) continue;
                for (int i = vector3.size() - 1; i >= 0; --i) {
                    schemaGrammar4 = (SchemaGrammar)vector3.elementAt(i);
                    if (vector2.contains(schemaGrammar4)) continue;
                    vector2.addElement(schemaGrammar4);
                }
                continue;
            }
            vector2.remove(schemaGrammar3);
        }
        for (n = vector2.size() - 1; n >= 0; --n) {
            this.putGrammar((SchemaGrammar)vector2.elementAt(n));
        }
        return true;
    }

    public SchemaGrammar[] getGrammars() {
        int n = this.fGrammarRegistry.size() + (this.fNoNSGrammar == null ? 0 : 1);
        SchemaGrammar[] schemaGrammarArray = new SchemaGrammar[n];
        Enumeration enumeration = this.fGrammarRegistry.elements();
        int n2 = 0;
        while (enumeration.hasMoreElements()) {
            schemaGrammarArray[n2++] = (SchemaGrammar)enumeration.nextElement();
        }
        if (this.fNoNSGrammar != null) {
            schemaGrammarArray[n - 1] = this.fNoNSGrammar;
        }
        return schemaGrammarArray;
    }

    public void reset() {
        this.fNoNSGrammar = null;
        this.fGrammarRegistry.clear();
    }
}

