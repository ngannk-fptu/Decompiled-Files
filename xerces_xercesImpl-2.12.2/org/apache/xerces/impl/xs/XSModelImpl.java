/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.impl.xs.util.XSNamedMap4Types;
import org.apache.xerces.impl.xs.util.XSNamedMapImpl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSIDCDefinition;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNamespaceItemList;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;

public final class XSModelImpl
extends AbstractList
implements XSModel,
XSNamespaceItemList {
    private static final short MAX_COMP_IDX = 16;
    private static final boolean[] GLOBAL_COMP = new boolean[]{false, true, true, true, false, true, true, false, false, false, true, true, false, false, false, true, true};
    private final int fGrammarCount;
    private final String[] fNamespaces;
    private final SchemaGrammar[] fGrammarList;
    private final SymbolHash fGrammarMap;
    private final SymbolHash fSubGroupMap;
    private final XSNamedMap[] fGlobalComponents;
    private final XSNamedMap[][] fNSComponents;
    private final StringList fNamespacesList;
    private XSObjectList fAnnotations = null;
    private final boolean fHasIDC;

    public XSModelImpl(SchemaGrammar[] schemaGrammarArray) {
        this(schemaGrammarArray, 1);
    }

    public XSModelImpl(SchemaGrammar[] schemaGrammarArray, short s) {
        int n;
        Object object;
        SchemaGrammar schemaGrammar;
        int n2 = schemaGrammarArray.length;
        int n3 = Math.max(n2 + 1, 5);
        String[] stringArray = new String[n3];
        SchemaGrammar[] schemaGrammarArray2 = new SchemaGrammar[n3];
        boolean bl = false;
        for (int i = 0; i < n2; ++i) {
            schemaGrammar = schemaGrammarArray[i];
            object = schemaGrammar.getTargetNamespace();
            stringArray[i] = object;
            schemaGrammarArray2[i] = schemaGrammar;
            if (object != SchemaSymbols.URI_SCHEMAFORSCHEMA) continue;
            bl = true;
        }
        if (!bl) {
            stringArray[n2] = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            schemaGrammarArray2[n2++] = SchemaGrammar.getS4SGrammar(s);
        }
        for (n = 0; n < n2; ++n) {
            int n4;
            SchemaGrammar schemaGrammar2 = schemaGrammarArray2[n];
            object = schemaGrammar2.getImportedGrammars();
            int n5 = n4 = object == null ? -1 : ((Vector)object).size() - 1;
            while (n4 >= 0) {
                int n6;
                schemaGrammar = (SchemaGrammar)((Vector)object).elementAt(n4);
                for (n6 = 0; n6 < n2 && schemaGrammar != schemaGrammarArray2[n6]; ++n6) {
                }
                if (n6 == n2) {
                    if (n2 == schemaGrammarArray2.length) {
                        String[] stringArray2 = new String[n2 * 2];
                        System.arraycopy(stringArray, 0, stringArray2, 0, n2);
                        stringArray = stringArray2;
                        SchemaGrammar[] schemaGrammarArray3 = new SchemaGrammar[n2 * 2];
                        System.arraycopy(schemaGrammarArray2, 0, schemaGrammarArray3, 0, n2);
                        schemaGrammarArray2 = schemaGrammarArray3;
                    }
                    stringArray[n2] = schemaGrammar.getTargetNamespace();
                    schemaGrammarArray2[n2] = schemaGrammar;
                    ++n2;
                }
                --n4;
            }
        }
        this.fNamespaces = stringArray;
        this.fGrammarList = schemaGrammarArray2;
        boolean bl2 = false;
        this.fGrammarMap = new SymbolHash(n2 * 2);
        for (n = 0; n < n2; ++n) {
            this.fGrammarMap.put(XSModelImpl.null2EmptyString(this.fNamespaces[n]), this.fGrammarList[n]);
            if (!this.fGrammarList[n].hasIDConstraints()) continue;
            bl2 = true;
        }
        this.fHasIDC = bl2;
        this.fGrammarCount = n2;
        this.fGlobalComponents = new XSNamedMap[17];
        this.fNSComponents = new XSNamedMap[n2][17];
        this.fNamespacesList = new StringListImpl(this.fNamespaces, this.fGrammarCount);
        this.fSubGroupMap = this.buildSubGroups();
    }

    private SymbolHash buildSubGroups_Org() {
        SubstitutionGroupHandler substitutionGroupHandler = new SubstitutionGroupHandler(null);
        for (int i = 0; i < this.fGrammarCount; ++i) {
            substitutionGroupHandler.addSubstitutionGroup(this.fGrammarList[i].getSubstitutionGroups());
        }
        XSNamedMap xSNamedMap = this.getComponents((short)2);
        int n = xSNamedMap.getLength();
        SymbolHash symbolHash = new SymbolHash(n * 2);
        for (int i = 0; i < n; ++i) {
            XSElementDecl xSElementDecl;
            XSObject[] xSObjectArray = substitutionGroupHandler.getSubstitutionGroup(xSElementDecl = (XSElementDecl)xSNamedMap.item(i));
            symbolHash.put(xSElementDecl, xSObjectArray.length > 0 ? new XSObjectListImpl(xSObjectArray, xSObjectArray.length) : XSObjectListImpl.EMPTY_LIST);
        }
        return symbolHash;
    }

    private SymbolHash buildSubGroups() {
        SubstitutionGroupHandler substitutionGroupHandler = new SubstitutionGroupHandler(null);
        for (int i = 0; i < this.fGrammarCount; ++i) {
            substitutionGroupHandler.addSubstitutionGroup(this.fGrammarList[i].getSubstitutionGroups());
        }
        XSObjectListImpl xSObjectListImpl = this.getGlobalElements();
        int n = xSObjectListImpl.getLength();
        SymbolHash symbolHash = new SymbolHash(n * 2);
        for (int i = 0; i < n; ++i) {
            XSElementDecl xSElementDecl;
            XSObject[] xSObjectArray = substitutionGroupHandler.getSubstitutionGroup(xSElementDecl = (XSElementDecl)xSObjectListImpl.item(i));
            symbolHash.put(xSElementDecl, xSObjectArray.length > 0 ? new XSObjectListImpl(xSObjectArray, xSObjectArray.length) : XSObjectListImpl.EMPTY_LIST);
        }
        return symbolHash;
    }

    private XSObjectListImpl getGlobalElements() {
        SymbolHash[] symbolHashArray = new SymbolHash[this.fGrammarCount];
        int n = 0;
        for (int i = 0; i < this.fGrammarCount; ++i) {
            symbolHashArray[i] = this.fGrammarList[i].fAllGlobalElemDecls;
            n += symbolHashArray[i].getLength();
        }
        if (n == 0) {
            return XSObjectListImpl.EMPTY_LIST;
        }
        Object[] objectArray = new XSObject[n];
        int n2 = 0;
        for (int i = 0; i < this.fGrammarCount; ++i) {
            symbolHashArray[i].getValues(objectArray, n2);
            n2 += symbolHashArray[i].getLength();
        }
        return new XSObjectListImpl((XSObject[])objectArray, n);
    }

    @Override
    public StringList getNamespaces() {
        return this.fNamespacesList;
    }

    @Override
    public XSNamespaceItemList getNamespaceItems() {
        return this;
    }

    @Override
    public synchronized XSNamedMap getComponents(short s) {
        if (s <= 0 || s > 16 || !GLOBAL_COMP[s]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        SymbolHash[] symbolHashArray = new SymbolHash[this.fGrammarCount];
        if (this.fGlobalComponents[s] == null) {
            block9: for (int i = 0; i < this.fGrammarCount; ++i) {
                switch (s) {
                    case 3: 
                    case 15: 
                    case 16: {
                        symbolHashArray[i] = this.fGrammarList[i].fGlobalTypeDecls;
                        continue block9;
                    }
                    case 1: {
                        symbolHashArray[i] = this.fGrammarList[i].fGlobalAttrDecls;
                        continue block9;
                    }
                    case 2: {
                        symbolHashArray[i] = this.fGrammarList[i].fGlobalElemDecls;
                        continue block9;
                    }
                    case 5: {
                        symbolHashArray[i] = this.fGrammarList[i].fGlobalAttrGrpDecls;
                        continue block9;
                    }
                    case 6: {
                        symbolHashArray[i] = this.fGrammarList[i].fGlobalGroupDecls;
                        continue block9;
                    }
                    case 11: {
                        symbolHashArray[i] = this.fGrammarList[i].fGlobalNotationDecls;
                        continue block9;
                    }
                    case 10: {
                        symbolHashArray[i] = this.fGrammarList[i].fGlobalIDConstraintDecls;
                    }
                }
            }
            this.fGlobalComponents[s] = s == 15 || s == 16 ? new XSNamedMap4Types(this.fNamespaces, symbolHashArray, this.fGrammarCount, s) : new XSNamedMapImpl(this.fNamespaces, symbolHashArray, this.fGrammarCount);
        }
        return this.fGlobalComponents[s];
    }

    @Override
    public synchronized XSNamedMap getComponentsByNamespace(short s, String string) {
        int n;
        if (s <= 0 || s > 16 || !GLOBAL_COMP[s]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        if (string != null) {
            for (n = 0; n < this.fGrammarCount && !string.equals(this.fNamespaces[n]); ++n) {
            }
        } else {
            while (n < this.fGrammarCount && this.fNamespaces[n] != null) {
                ++n;
            }
        }
        if (n == this.fGrammarCount) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        if (this.fNSComponents[n][s] == null) {
            SymbolHash symbolHash = null;
            switch (s) {
                case 3: 
                case 15: 
                case 16: {
                    symbolHash = this.fGrammarList[n].fGlobalTypeDecls;
                    break;
                }
                case 1: {
                    symbolHash = this.fGrammarList[n].fGlobalAttrDecls;
                    break;
                }
                case 2: {
                    symbolHash = this.fGrammarList[n].fGlobalElemDecls;
                    break;
                }
                case 5: {
                    symbolHash = this.fGrammarList[n].fGlobalAttrGrpDecls;
                    break;
                }
                case 6: {
                    symbolHash = this.fGrammarList[n].fGlobalGroupDecls;
                    break;
                }
                case 11: {
                    symbolHash = this.fGrammarList[n].fGlobalNotationDecls;
                    break;
                }
                case 10: {
                    symbolHash = this.fGrammarList[n].fGlobalIDConstraintDecls;
                }
            }
            this.fNSComponents[n][s] = s == 15 || s == 16 ? new XSNamedMap4Types(string, symbolHash, s) : new XSNamedMapImpl(string, symbolHash);
        }
        return this.fNSComponents[n][s];
    }

    @Override
    public XSTypeDefinition getTypeDefinition(String string, String string2) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSTypeDefinition)schemaGrammar.fGlobalTypeDecls.get(string);
    }

    public XSTypeDefinition getTypeDefinition(String string, String string2, String string3) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalTypeDecl(string, string3);
    }

    @Override
    public XSAttributeDeclaration getAttributeDeclaration(String string, String string2) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSAttributeDeclaration)schemaGrammar.fGlobalAttrDecls.get(string);
    }

    public XSAttributeDeclaration getAttributeDeclaration(String string, String string2, String string3) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalAttributeDecl(string, string3);
    }

    @Override
    public XSElementDeclaration getElementDeclaration(String string, String string2) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSElementDeclaration)schemaGrammar.fGlobalElemDecls.get(string);
    }

    public XSElementDeclaration getElementDeclaration(String string, String string2, String string3) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalElementDecl(string, string3);
    }

    @Override
    public XSAttributeGroupDefinition getAttributeGroup(String string, String string2) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSAttributeGroupDefinition)schemaGrammar.fGlobalAttrGrpDecls.get(string);
    }

    public XSAttributeGroupDefinition getAttributeGroup(String string, String string2, String string3) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalAttributeGroupDecl(string, string3);
    }

    @Override
    public XSModelGroupDefinition getModelGroupDefinition(String string, String string2) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSModelGroupDefinition)schemaGrammar.fGlobalGroupDecls.get(string);
    }

    public XSModelGroupDefinition getModelGroupDefinition(String string, String string2, String string3) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalGroupDecl(string, string3);
    }

    @Override
    public XSIDCDefinition getIDCDefinition(String string, String string2) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSIDCDefinition)schemaGrammar.fGlobalIDConstraintDecls.get(string);
    }

    public XSIDCDefinition getIDCDefinition(String string, String string2, String string3) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getIDConstraintDecl(string, string3);
    }

    @Override
    public XSNotationDeclaration getNotationDeclaration(String string, String string2) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return (XSNotationDeclaration)schemaGrammar.fGlobalNotationDecls.get(string);
    }

    public XSNotationDeclaration getNotationDeclaration(String string, String string2, String string3) {
        SchemaGrammar schemaGrammar = (SchemaGrammar)this.fGrammarMap.get(XSModelImpl.null2EmptyString(string2));
        if (schemaGrammar == null) {
            return null;
        }
        return schemaGrammar.getGlobalNotationDecl(string, string3);
    }

    @Override
    public synchronized XSObjectList getAnnotations() {
        if (this.fAnnotations != null) {
            return this.fAnnotations;
        }
        int n = 0;
        for (int i = 0; i < this.fGrammarCount; ++i) {
            n += this.fGrammarList[i].fNumAnnotations;
        }
        if (n == 0) {
            this.fAnnotations = XSObjectListImpl.EMPTY_LIST;
            return this.fAnnotations;
        }
        XSObject[] xSObjectArray = new XSAnnotationImpl[n];
        int n2 = 0;
        for (int i = 0; i < this.fGrammarCount; ++i) {
            SchemaGrammar schemaGrammar = this.fGrammarList[i];
            if (schemaGrammar.fNumAnnotations <= 0) continue;
            System.arraycopy(schemaGrammar.fAnnotations, 0, xSObjectArray, n2, schemaGrammar.fNumAnnotations);
            n2 += schemaGrammar.fNumAnnotations;
        }
        this.fAnnotations = new XSObjectListImpl(xSObjectArray, xSObjectArray.length);
        return this.fAnnotations;
    }

    private static final String null2EmptyString(String string) {
        return string == null ? XMLSymbols.EMPTY_STRING : string;
    }

    public boolean hasIDConstraints() {
        return this.fHasIDC;
    }

    @Override
    public XSObjectList getSubstitutionGroup(XSElementDeclaration xSElementDeclaration) {
        return (XSObjectList)this.fSubGroupMap.get(xSElementDeclaration);
    }

    @Override
    public int getLength() {
        return this.fGrammarCount;
    }

    @Override
    public XSNamespaceItem item(int n) {
        if (n < 0 || n >= this.fGrammarCount) {
            return null;
        }
        return this.fGrammarList[n];
    }

    public Object get(int n) {
        if (n >= 0 && n < this.fGrammarCount) {
            return this.fGrammarList[n];
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }

    @Override
    public int size() {
        return this.getLength();
    }

    @Override
    public Iterator iterator() {
        return this.listIterator0(0);
    }

    public ListIterator listIterator() {
        return this.listIterator0(0);
    }

    public ListIterator listIterator(int n) {
        if (n >= 0 && n < this.fGrammarCount) {
            return this.listIterator0(n);
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }

    private ListIterator listIterator0(int n) {
        return new XSNamespaceItemListIterator(n);
    }

    @Override
    public Object[] toArray() {
        Object[] objectArray = new Object[this.fGrammarCount];
        this.toArray0(objectArray);
        return objectArray;
    }

    @Override
    public Object[] toArray(Object[] objectArray) {
        if (objectArray.length < this.fGrammarCount) {
            Class<?> clazz = objectArray.getClass();
            Class<?> clazz2 = clazz.getComponentType();
            objectArray = (Object[])Array.newInstance(clazz2, this.fGrammarCount);
        }
        this.toArray0(objectArray);
        if (objectArray.length > this.fGrammarCount) {
            objectArray[this.fGrammarCount] = null;
        }
        return objectArray;
    }

    private void toArray0(Object[] objectArray) {
        if (this.fGrammarCount > 0) {
            System.arraycopy(this.fGrammarList, 0, objectArray, 0, this.fGrammarCount);
        }
    }

    private final class XSNamespaceItemListIterator
    implements ListIterator {
        private int index;

        public XSNamespaceItemListIterator(int n) {
            this.index = n;
        }

        @Override
        public boolean hasNext() {
            return this.index < XSModelImpl.this.fGrammarCount;
        }

        @Override
        public Object next() {
            if (this.index < XSModelImpl.this.fGrammarCount) {
                return XSModelImpl.this.fGrammarList[this.index++];
            }
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasPrevious() {
            return this.index > 0;
        }

        public Object previous() {
            if (this.index > 0) {
                return XSModelImpl.this.fGrammarList[--this.index];
            }
            throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
            return this.index;
        }

        @Override
        public int previousIndex() {
            return this.index - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(Object object) {
            throw new UnsupportedOperationException();
        }

        public void add(Object object) {
            throw new UnsupportedOperationException();
        }
    }
}

