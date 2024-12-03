/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import org.apache.xerces.impl.dtd.XMLAttributeDecl;
import org.apache.xerces.impl.dtd.XMLContentSpec;
import org.apache.xerces.impl.dtd.XMLDTDDescription;
import org.apache.xerces.impl.dtd.XMLElementDecl;
import org.apache.xerces.impl.dtd.XMLEntityDecl;
import org.apache.xerces.impl.dtd.XMLNotationDecl;
import org.apache.xerces.impl.dtd.XMLSimpleType;
import org.apache.xerces.impl.dtd.models.CMAny;
import org.apache.xerces.impl.dtd.models.CMBinOp;
import org.apache.xerces.impl.dtd.models.CMLeaf;
import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.dtd.models.CMUniOp;
import org.apache.xerces.impl.dtd.models.ContentModelValidator;
import org.apache.xerces.impl.dtd.models.DFAContentModel;
import org.apache.xerces.impl.dtd.models.MixedContentModel;
import org.apache.xerces.impl.dtd.models.SimpleContentModel;
import org.apache.xerces.impl.dv.DatatypeValidator;
import org.apache.xerces.impl.validation.EntityState;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDSource;

public class DTDGrammar
implements XMLDTDHandler,
XMLDTDContentModelHandler,
EntityState,
Grammar {
    public static final int TOP_LEVEL_SCOPE = -1;
    private static final int CHUNK_SHIFT = 8;
    private static final int CHUNK_SIZE = 256;
    private static final int CHUNK_MASK = 255;
    private static final int INITIAL_CHUNK_COUNT = 4;
    private static final short LIST_FLAG = 128;
    private static final short LIST_MASK = -129;
    private static final boolean DEBUG = false;
    protected XMLDTDSource fDTDSource = null;
    protected XMLDTDContentModelSource fDTDContentModelSource = null;
    protected int fCurrentElementIndex;
    protected int fCurrentAttributeIndex;
    protected boolean fReadingExternalDTD = false;
    private SymbolTable fSymbolTable;
    protected XMLDTDDescription fGrammarDescription = null;
    private int fElementDeclCount = 0;
    private QName[][] fElementDeclName = new QName[4][];
    private short[][] fElementDeclType = new short[4][];
    private int[][] fElementDeclContentSpecIndex = new int[4][];
    private ContentModelValidator[][] fElementDeclContentModelValidator = new ContentModelValidator[4][];
    private int[][] fElementDeclFirstAttributeDeclIndex = new int[4][];
    private int[][] fElementDeclLastAttributeDeclIndex = new int[4][];
    private int fAttributeDeclCount = 0;
    private QName[][] fAttributeDeclName = new QName[4][];
    private boolean fIsImmutable = false;
    private short[][] fAttributeDeclType = new short[4][];
    private String[][][] fAttributeDeclEnumeration = new String[4][][];
    private short[][] fAttributeDeclDefaultType = new short[4][];
    private DatatypeValidator[][] fAttributeDeclDatatypeValidator = new DatatypeValidator[4][];
    private String[][] fAttributeDeclDefaultValue = new String[4][];
    private String[][] fAttributeDeclNonNormalizedDefaultValue = new String[4][];
    private int[][] fAttributeDeclNextAttributeDeclIndex = new int[4][];
    private int fContentSpecCount = 0;
    private short[][] fContentSpecType = new short[4][];
    private Object[][] fContentSpecValue = new Object[4][];
    private Object[][] fContentSpecOtherValue = new Object[4][];
    private int fEntityCount = 0;
    private String[][] fEntityName = new String[4][];
    private String[][] fEntityValue = new String[4][];
    private String[][] fEntityPublicId = new String[4][];
    private String[][] fEntitySystemId = new String[4][];
    private String[][] fEntityBaseSystemId = new String[4][];
    private String[][] fEntityNotation = new String[4][];
    private byte[][] fEntityIsPE = new byte[4][];
    private byte[][] fEntityInExternal = new byte[4][];
    private int fNotationCount = 0;
    private String[][] fNotationName = new String[4][];
    private String[][] fNotationPublicId = new String[4][];
    private String[][] fNotationSystemId = new String[4][];
    private String[][] fNotationBaseSystemId = new String[4][];
    private QNameHashtable fElementIndexMap = new QNameHashtable();
    private QNameHashtable fEntityIndexMap = new QNameHashtable();
    private QNameHashtable fNotationIndexMap = new QNameHashtable();
    private boolean fMixed;
    private final QName fQName = new QName();
    private final QName fQName2 = new QName();
    protected final XMLAttributeDecl fAttributeDecl = new XMLAttributeDecl();
    private int fLeafCount = 0;
    private int fEpsilonIndex = -1;
    private XMLElementDecl fElementDecl = new XMLElementDecl();
    private XMLEntityDecl fEntityDecl = new XMLEntityDecl();
    private XMLSimpleType fSimpleType = new XMLSimpleType();
    private XMLContentSpec fContentSpec = new XMLContentSpec();
    Hashtable fElementDeclTab = new Hashtable();
    private short[] fOpStack = null;
    private int[] fNodeIndexStack = null;
    private int[] fPrevNodeIndexStack = null;
    private int fDepth = 0;
    private boolean[] fPEntityStack = new boolean[4];
    private int fPEDepth = 0;
    private int[][] fElementDeclIsExternal = new int[4][];
    private int[][] fAttributeDeclIsExternal = new int[4][];
    int valueIndex = -1;
    int prevNodeIndex = -1;
    int nodeIndex = -1;

    public DTDGrammar(SymbolTable symbolTable, XMLDTDDescription xMLDTDDescription) {
        this.fSymbolTable = symbolTable;
        this.fGrammarDescription = xMLDTDDescription;
    }

    @Override
    public XMLGrammarDescription getGrammarDescription() {
        return this.fGrammarDescription;
    }

    public boolean getElementDeclIsExternal(int n) {
        if (n < 0) {
            return false;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        return this.fElementDeclIsExternal[n2][n3] != 0;
    }

    public boolean getAttributeDeclIsExternal(int n) {
        if (n < 0) {
            return false;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        return this.fAttributeDeclIsExternal[n2][n3] != 0;
    }

    public int getAttributeDeclIndex(int n, String string) {
        if (n == -1) {
            return -1;
        }
        int n2 = this.getFirstAttributeDeclIndex(n);
        while (n2 != -1) {
            this.getAttributeDecl(n2, this.fAttributeDecl);
            if (this.fAttributeDecl.name.rawname == string || string.equals(this.fAttributeDecl.name.rawname)) {
                return n2;
            }
            n2 = this.getNextAttributeDeclIndex(n2);
        }
        return -1;
    }

    @Override
    public void startDTD(XMLLocator xMLLocator, Augmentations augmentations) throws XNIException {
        this.fOpStack = null;
        this.fNodeIndexStack = null;
        this.fPrevNodeIndexStack = null;
    }

    @Override
    public void startParameterEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        if (this.fPEDepth == this.fPEntityStack.length) {
            boolean[] blArray = new boolean[this.fPEntityStack.length * 2];
            System.arraycopy(this.fPEntityStack, 0, blArray, 0, this.fPEntityStack.length);
            this.fPEntityStack = blArray;
        }
        this.fPEntityStack[this.fPEDepth] = this.fReadingExternalDTD;
        ++this.fPEDepth;
    }

    @Override
    public void startExternalSubset(XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        this.fReadingExternalDTD = true;
    }

    @Override
    public void endParameterEntity(String string, Augmentations augmentations) throws XNIException {
        --this.fPEDepth;
        this.fReadingExternalDTD = this.fPEntityStack[this.fPEDepth];
    }

    @Override
    public void endExternalSubset(Augmentations augmentations) throws XNIException {
        this.fReadingExternalDTD = false;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void elementDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        XMLElementDecl xMLElementDecl = (XMLElementDecl)this.fElementDeclTab.get(string);
        if (xMLElementDecl != null) {
            if (xMLElementDecl.type != -1) return;
            this.fCurrentElementIndex = this.getElementDeclIndex(string);
        } else {
            this.fCurrentElementIndex = this.createElementDecl();
        }
        XMLElementDecl xMLElementDecl2 = new XMLElementDecl();
        this.fQName.setValues(null, string, string, null);
        xMLElementDecl2.name.setValues(this.fQName);
        xMLElementDecl2.contentModelValidator = null;
        xMLElementDecl2.scope = -1;
        if (string2.equals("EMPTY")) {
            xMLElementDecl2.type = 1;
        } else if (string2.equals("ANY")) {
            xMLElementDecl2.type = 0;
        } else if (string2.startsWith("(")) {
            xMLElementDecl2.type = string2.indexOf("#PCDATA") > 0 ? (short)2 : (short)3;
        }
        this.fElementDeclTab.put(string, xMLElementDecl2);
        this.fElementDecl = xMLElementDecl2;
        this.addContentSpecToElement(xMLElementDecl2);
        this.setElementDecl(this.fCurrentElementIndex, this.fElementDecl);
        int n = this.fCurrentElementIndex >> 8;
        int n2 = this.fCurrentElementIndex & 0xFF;
        this.ensureElementDeclCapacity(n);
        this.fElementDeclIsExternal[n][n2] = this.fReadingExternalDTD || this.fPEDepth > 0 ? 1 : 0;
    }

    @Override
    public void attributeDecl(String string, String string2, String string3, String[] stringArray, String string4, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
        if (!this.fElementDeclTab.containsKey(string)) {
            this.fCurrentElementIndex = this.createElementDecl();
            XMLElementDecl xMLElementDecl = new XMLElementDecl();
            xMLElementDecl.name.setValues(null, string, string, null);
            xMLElementDecl.scope = -1;
            this.fElementDeclTab.put(string, xMLElementDecl);
            this.setElementDecl(this.fCurrentElementIndex, xMLElementDecl);
        }
        int n = this.getElementDeclIndex(string);
        if (this.getAttributeDeclIndex(n, string2) != -1) {
            return;
        }
        this.fCurrentAttributeIndex = this.createAttributeDecl();
        this.fSimpleType.clear();
        if (string4 != null) {
            if (string4.equals("#FIXED")) {
                this.fSimpleType.defaultType = 1;
            } else if (string4.equals("#IMPLIED")) {
                this.fSimpleType.defaultType = 0;
            } else if (string4.equals("#REQUIRED")) {
                this.fSimpleType.defaultType = (short)2;
            }
        }
        this.fSimpleType.defaultValue = xMLString != null ? xMLString.toString() : null;
        this.fSimpleType.nonNormalizedDefaultValue = xMLString2 != null ? xMLString2.toString() : null;
        this.fSimpleType.enumeration = stringArray;
        if (string3.equals("CDATA")) {
            this.fSimpleType.type = 0;
        } else if (string3.equals("ID")) {
            this.fSimpleType.type = (short)3;
        } else if (string3.startsWith("IDREF")) {
            this.fSimpleType.type = (short)4;
            if (string3.indexOf("S") > 0) {
                this.fSimpleType.list = true;
            }
        } else if (string3.equals("ENTITIES")) {
            this.fSimpleType.type = 1;
            this.fSimpleType.list = true;
        } else if (string3.equals("ENTITY")) {
            this.fSimpleType.type = 1;
        } else if (string3.equals("NMTOKENS")) {
            this.fSimpleType.type = (short)5;
            this.fSimpleType.list = true;
        } else if (string3.equals("NMTOKEN")) {
            this.fSimpleType.type = (short)5;
        } else if (string3.startsWith("NOTATION")) {
            this.fSimpleType.type = (short)6;
        } else if (string3.startsWith("ENUMERATION")) {
            this.fSimpleType.type = (short)2;
        } else {
            System.err.println("!!! unknown attribute type " + string3);
        }
        this.fQName.setValues(null, string2, string2, null);
        this.fAttributeDecl.setValues(this.fQName, this.fSimpleType, false);
        this.setAttributeDecl(n, this.fCurrentAttributeIndex, this.fAttributeDecl);
        int n2 = this.fCurrentAttributeIndex >> 8;
        int n3 = this.fCurrentAttributeIndex & 0xFF;
        this.ensureAttributeDeclCapacity(n2);
        this.fAttributeDeclIsExternal[n2][n3] = this.fReadingExternalDTD || this.fPEDepth > 0 ? 1 : 0;
    }

    @Override
    public void internalEntityDecl(String string, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
        int n = this.getEntityDeclIndex(string);
        if (n == -1) {
            n = this.createEntityDecl();
            boolean bl = string.startsWith("%");
            boolean bl2 = this.fReadingExternalDTD || this.fPEDepth > 0;
            XMLEntityDecl xMLEntityDecl = new XMLEntityDecl();
            xMLEntityDecl.setValues(string, null, null, null, null, xMLString.toString(), bl, bl2);
            this.setEntityDecl(n, xMLEntityDecl);
        }
    }

    @Override
    public void externalEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        int n = this.getEntityDeclIndex(string);
        if (n == -1) {
            n = this.createEntityDecl();
            boolean bl = string.startsWith("%");
            boolean bl2 = this.fReadingExternalDTD || this.fPEDepth > 0;
            XMLEntityDecl xMLEntityDecl = new XMLEntityDecl();
            xMLEntityDecl.setValues(string, xMLResourceIdentifier.getPublicId(), xMLResourceIdentifier.getLiteralSystemId(), xMLResourceIdentifier.getBaseSystemId(), null, null, bl, bl2);
            this.setEntityDecl(n, xMLEntityDecl);
        }
    }

    @Override
    public void unparsedEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        XMLEntityDecl xMLEntityDecl = new XMLEntityDecl();
        boolean bl = string.startsWith("%");
        boolean bl2 = this.fReadingExternalDTD || this.fPEDepth > 0;
        xMLEntityDecl.setValues(string, xMLResourceIdentifier.getPublicId(), xMLResourceIdentifier.getLiteralSystemId(), xMLResourceIdentifier.getBaseSystemId(), string2, null, bl, bl2);
        int n = this.getEntityDeclIndex(string);
        if (n == -1) {
            n = this.createEntityDecl();
            this.setEntityDecl(n, xMLEntityDecl);
        }
    }

    @Override
    public void notationDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        XMLNotationDecl xMLNotationDecl = new XMLNotationDecl();
        xMLNotationDecl.setValues(string, xMLResourceIdentifier.getPublicId(), xMLResourceIdentifier.getLiteralSystemId(), xMLResourceIdentifier.getBaseSystemId());
        int n = this.getNotationDeclIndex(string);
        if (n == -1) {
            n = this.createNotationDecl();
            this.setNotationDecl(n, xMLNotationDecl);
        }
    }

    @Override
    public void endDTD(Augmentations augmentations) throws XNIException {
        this.fIsImmutable = true;
        if (this.fGrammarDescription.getRootName() == null) {
            int n = 0;
            String string = null;
            int n2 = this.fElementDeclCount;
            ArrayList<String> arrayList = new ArrayList<String>(n2);
            for (int i = 0; i < n2; ++i) {
                int n3 = i >> 8;
                n = i & 0xFF;
                string = this.fElementDeclName[n3][n].rawname;
                arrayList.add(string);
            }
            this.fGrammarDescription.setPossibleRoots(arrayList);
        }
    }

    @Override
    public void setDTDSource(XMLDTDSource xMLDTDSource) {
        this.fDTDSource = xMLDTDSource;
    }

    @Override
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startAttlist(String string, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endAttlist(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startConditional(short s, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void ignoredCharacters(XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endConditional(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void setDTDContentModelSource(XMLDTDContentModelSource xMLDTDContentModelSource) {
        this.fDTDContentModelSource = xMLDTDContentModelSource;
    }

    @Override
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return this.fDTDContentModelSource;
    }

    @Override
    public void startContentModel(String string, Augmentations augmentations) throws XNIException {
        XMLElementDecl xMLElementDecl = (XMLElementDecl)this.fElementDeclTab.get(string);
        if (xMLElementDecl != null) {
            this.fElementDecl = xMLElementDecl;
        }
        this.fDepth = 0;
        this.initializeContentModelStack();
    }

    @Override
    public void startGroup(Augmentations augmentations) throws XNIException {
        ++this.fDepth;
        this.initializeContentModelStack();
        this.fMixed = false;
    }

    @Override
    public void pcdata(Augmentations augmentations) throws XNIException {
        this.fMixed = true;
    }

    @Override
    public void element(String string, Augmentations augmentations) throws XNIException {
        this.fNodeIndexStack[this.fDepth] = this.fMixed ? (this.fNodeIndexStack[this.fDepth] == -1 ? this.addUniqueLeafNode(string) : this.addContentSpecNode((short)4, this.fNodeIndexStack[this.fDepth], this.addUniqueLeafNode(string))) : this.addContentSpecNode((short)0, string);
    }

    @Override
    public void separator(short s, Augmentations augmentations) throws XNIException {
        if (!this.fMixed) {
            if (this.fOpStack[this.fDepth] != 5 && s == 0) {
                if (this.fPrevNodeIndexStack[this.fDepth] != -1) {
                    this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]);
                }
                this.fPrevNodeIndexStack[this.fDepth] = this.fNodeIndexStack[this.fDepth];
                this.fOpStack[this.fDepth] = 4;
            } else if (this.fOpStack[this.fDepth] != 4 && s == 1) {
                if (this.fPrevNodeIndexStack[this.fDepth] != -1) {
                    this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]);
                }
                this.fPrevNodeIndexStack[this.fDepth] = this.fNodeIndexStack[this.fDepth];
                this.fOpStack[this.fDepth] = 5;
            }
        }
    }

    @Override
    public void occurrence(short s, Augmentations augmentations) throws XNIException {
        if (!this.fMixed) {
            if (s == 2) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)1, this.fNodeIndexStack[this.fDepth], -1);
            } else if (s == 3) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)2, this.fNodeIndexStack[this.fDepth], -1);
            } else if (s == 4) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode((short)3, this.fNodeIndexStack[this.fDepth], -1);
            }
        }
    }

    @Override
    public void endGroup(Augmentations augmentations) throws XNIException {
        if (!this.fMixed) {
            int n;
            if (this.fPrevNodeIndexStack[this.fDepth] != -1) {
                this.fNodeIndexStack[this.fDepth] = this.addContentSpecNode(this.fOpStack[this.fDepth], this.fPrevNodeIndexStack[this.fDepth], this.fNodeIndexStack[this.fDepth]);
            }
            this.fNodeIndexStack[this.fDepth] = n = this.fNodeIndexStack[this.fDepth--];
        }
    }

    @Override
    public void any(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void empty(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endContentModel(Augmentations augmentations) throws XNIException {
    }

    public boolean isNamespaceAware() {
        return false;
    }

    public SymbolTable getSymbolTable() {
        return this.fSymbolTable;
    }

    public int getFirstElementDeclIndex() {
        return this.fElementDeclCount >= 0 ? 0 : -1;
    }

    public int getNextElementDeclIndex(int n) {
        return n < this.fElementDeclCount - 1 ? n + 1 : -1;
    }

    public int getElementDeclIndex(String string) {
        int n = this.fElementIndexMap.get(string);
        return n;
    }

    public int getElementDeclIndex(QName qName) {
        return this.getElementDeclIndex(qName.rawname);
    }

    public short getContentSpecType(int n) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return -1;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        if (this.fElementDeclType[n2][n3] == -1) {
            return -1;
        }
        return (short)(this.fElementDeclType[n2][n3] & 0xFFFFFF7F);
    }

    public boolean getElementDecl(int n, XMLElementDecl xMLElementDecl) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return false;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        xMLElementDecl.name.setValues(this.fElementDeclName[n2][n3]);
        if (this.fElementDeclType[n2][n3] == -1) {
            xMLElementDecl.type = (short)-1;
            xMLElementDecl.simpleType.list = false;
        } else {
            xMLElementDecl.type = (short)(this.fElementDeclType[n2][n3] & 0xFFFFFF7F);
            boolean bl = xMLElementDecl.simpleType.list = (this.fElementDeclType[n2][n3] & 0x80) != 0;
        }
        if (xMLElementDecl.type == 3 || xMLElementDecl.type == 2) {
            xMLElementDecl.contentModelValidator = this.getElementContentModelValidator(n);
        }
        xMLElementDecl.simpleType.datatypeValidator = null;
        xMLElementDecl.simpleType.defaultType = (short)-1;
        xMLElementDecl.simpleType.defaultValue = null;
        return true;
    }

    QName getElementDeclName(int n) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return null;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        return this.fElementDeclName[n2][n3];
    }

    public int getFirstAttributeDeclIndex(int n) {
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        return this.fElementDeclFirstAttributeDeclIndex[n2][n3];
    }

    public int getNextAttributeDeclIndex(int n) {
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        return this.fAttributeDeclNextAttributeDeclIndex[n2][n3];
    }

    public boolean getAttributeDecl(int n, XMLAttributeDecl xMLAttributeDecl) {
        boolean bl;
        short s;
        if (n < 0 || n >= this.fAttributeDeclCount) {
            return false;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        xMLAttributeDecl.name.setValues(this.fAttributeDeclName[n2][n3]);
        if (this.fAttributeDeclType[n2][n3] == -1) {
            s = -1;
            bl = false;
        } else {
            s = (short)(this.fAttributeDeclType[n2][n3] & 0xFFFFFF7F);
            bl = (this.fAttributeDeclType[n2][n3] & 0x80) != 0;
        }
        xMLAttributeDecl.simpleType.setValues(s, this.fAttributeDeclName[n2][n3].localpart, this.fAttributeDeclEnumeration[n2][n3], bl, this.fAttributeDeclDefaultType[n2][n3], this.fAttributeDeclDefaultValue[n2][n3], this.fAttributeDeclNonNormalizedDefaultValue[n2][n3], this.fAttributeDeclDatatypeValidator[n2][n3]);
        return true;
    }

    public boolean isCDATAAttribute(QName qName, QName qName2) {
        int n = this.getElementDeclIndex(qName);
        return !this.getAttributeDecl(n, this.fAttributeDecl) || this.fAttributeDecl.simpleType.type == 0;
    }

    public int getEntityDeclIndex(String string) {
        if (string == null) {
            return -1;
        }
        return this.fEntityIndexMap.get(string);
    }

    public boolean getEntityDecl(int n, XMLEntityDecl xMLEntityDecl) {
        if (n < 0 || n >= this.fEntityCount) {
            return false;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        xMLEntityDecl.setValues(this.fEntityName[n2][n3], this.fEntityPublicId[n2][n3], this.fEntitySystemId[n2][n3], this.fEntityBaseSystemId[n2][n3], this.fEntityNotation[n2][n3], this.fEntityValue[n2][n3], this.fEntityIsPE[n2][n3] != 0, this.fEntityInExternal[n2][n3] != 0);
        return true;
    }

    public int getNotationDeclIndex(String string) {
        if (string == null) {
            return -1;
        }
        return this.fNotationIndexMap.get(string);
    }

    public boolean getNotationDecl(int n, XMLNotationDecl xMLNotationDecl) {
        if (n < 0 || n >= this.fNotationCount) {
            return false;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        xMLNotationDecl.setValues(this.fNotationName[n2][n3], this.fNotationPublicId[n2][n3], this.fNotationSystemId[n2][n3], this.fNotationBaseSystemId[n2][n3]);
        return true;
    }

    public boolean getContentSpec(int n, XMLContentSpec xMLContentSpec) {
        if (n < 0 || n >= this.fContentSpecCount) {
            return false;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        xMLContentSpec.type = this.fContentSpecType[n2][n3];
        xMLContentSpec.value = this.fContentSpecValue[n2][n3];
        xMLContentSpec.otherValue = this.fContentSpecOtherValue[n2][n3];
        return true;
    }

    public int getContentSpecIndex(int n) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return -1;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        return this.fElementDeclContentSpecIndex[n2][n3];
    }

    public String getContentSpecAsString(int n) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return null;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        int n4 = this.fElementDeclContentSpecIndex[n2][n3];
        XMLContentSpec xMLContentSpec = new XMLContentSpec();
        if (this.getContentSpec(n4, xMLContentSpec)) {
            StringBuffer stringBuffer = new StringBuffer();
            int n5 = xMLContentSpec.type & 0xF;
            switch (n5) {
                case 0: {
                    stringBuffer.append('(');
                    if (xMLContentSpec.value == null && xMLContentSpec.otherValue == null) {
                        stringBuffer.append("#PCDATA");
                    } else {
                        stringBuffer.append(xMLContentSpec.value);
                    }
                    stringBuffer.append(')');
                    break;
                }
                case 1: {
                    this.getContentSpec(((int[])xMLContentSpec.value)[0], xMLContentSpec);
                    short s = xMLContentSpec.type;
                    if (s == 0) {
                        stringBuffer.append('(');
                        stringBuffer.append(xMLContentSpec.value);
                        stringBuffer.append(')');
                    } else if (s == 3 || s == 2 || s == 1) {
                        stringBuffer.append('(');
                        this.appendContentSpec(xMLContentSpec, stringBuffer, true, n5);
                        stringBuffer.append(')');
                    } else {
                        this.appendContentSpec(xMLContentSpec, stringBuffer, true, n5);
                    }
                    stringBuffer.append('?');
                    break;
                }
                case 2: {
                    this.getContentSpec(((int[])xMLContentSpec.value)[0], xMLContentSpec);
                    short s = xMLContentSpec.type;
                    if (s == 0) {
                        stringBuffer.append('(');
                        if (xMLContentSpec.value == null && xMLContentSpec.otherValue == null) {
                            stringBuffer.append("#PCDATA");
                        } else if (xMLContentSpec.otherValue != null) {
                            stringBuffer.append("##any:uri=").append(xMLContentSpec.otherValue);
                        } else if (xMLContentSpec.value == null) {
                            stringBuffer.append("##any");
                        } else {
                            this.appendContentSpec(xMLContentSpec, stringBuffer, true, n5);
                        }
                        stringBuffer.append(')');
                    } else if (s == 3 || s == 2 || s == 1) {
                        stringBuffer.append('(');
                        this.appendContentSpec(xMLContentSpec, stringBuffer, true, n5);
                        stringBuffer.append(')');
                    } else {
                        this.appendContentSpec(xMLContentSpec, stringBuffer, true, n5);
                    }
                    stringBuffer.append('*');
                    break;
                }
                case 3: {
                    this.getContentSpec(((int[])xMLContentSpec.value)[0], xMLContentSpec);
                    short s = xMLContentSpec.type;
                    if (s == 0) {
                        stringBuffer.append('(');
                        if (xMLContentSpec.value == null && xMLContentSpec.otherValue == null) {
                            stringBuffer.append("#PCDATA");
                        } else if (xMLContentSpec.otherValue != null) {
                            stringBuffer.append("##any:uri=").append(xMLContentSpec.otherValue);
                        } else if (xMLContentSpec.value == null) {
                            stringBuffer.append("##any");
                        } else {
                            stringBuffer.append(xMLContentSpec.value);
                        }
                        stringBuffer.append(')');
                    } else if (s == 3 || s == 2 || s == 1) {
                        stringBuffer.append('(');
                        this.appendContentSpec(xMLContentSpec, stringBuffer, true, n5);
                        stringBuffer.append(')');
                    } else {
                        this.appendContentSpec(xMLContentSpec, stringBuffer, true, n5);
                    }
                    stringBuffer.append('+');
                    break;
                }
                case 4: 
                case 5: {
                    this.appendContentSpec(xMLContentSpec, stringBuffer, true, n5);
                    break;
                }
                case 6: {
                    stringBuffer.append("##any");
                    if (xMLContentSpec.otherValue == null) break;
                    stringBuffer.append(":uri=");
                    stringBuffer.append(xMLContentSpec.otherValue);
                    break;
                }
                case 7: {
                    stringBuffer.append("##other:uri=");
                    stringBuffer.append(xMLContentSpec.otherValue);
                    break;
                }
                case 8: {
                    stringBuffer.append("##local");
                    break;
                }
                default: {
                    stringBuffer.append("???");
                }
            }
            return stringBuffer.toString();
        }
        return null;
    }

    public void printElements() {
        int n = 0;
        XMLElementDecl xMLElementDecl = new XMLElementDecl();
        while (this.getElementDecl(n++, xMLElementDecl)) {
            System.out.println("element decl: " + xMLElementDecl.name + ", " + xMLElementDecl.name.rawname);
        }
    }

    public void printAttributes(int n) {
        int n2 = this.getFirstAttributeDeclIndex(n);
        System.out.print(n);
        System.out.print(" [");
        while (n2 != -1) {
            System.out.print(' ');
            System.out.print(n2);
            this.printAttribute(n2);
            if ((n2 = this.getNextAttributeDeclIndex(n2)) == -1) continue;
            System.out.print(",");
        }
        System.out.println(" ]");
    }

    protected void addContentSpecToElement(XMLElementDecl xMLElementDecl) {
        if ((this.fDepth == 0 || this.fDepth == 1 && xMLElementDecl.type == 2) && this.fNodeIndexStack != null) {
            if (xMLElementDecl.type == 2) {
                int n = this.addUniqueLeafNode(null);
                this.fNodeIndexStack[0] = this.fNodeIndexStack[0] == -1 ? n : this.addContentSpecNode((short)4, n, this.fNodeIndexStack[0]);
            }
            this.setContentSpecIndex(this.fCurrentElementIndex, this.fNodeIndexStack[this.fDepth]);
        }
    }

    protected ContentModelValidator getElementContentModelValidator(int n) {
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        ContentModelValidator contentModelValidator = this.fElementDeclContentModelValidator[n2][n3];
        if (contentModelValidator != null) {
            return contentModelValidator;
        }
        short s = this.fElementDeclType[n2][n3];
        if (s == 4) {
            return null;
        }
        int n4 = this.fElementDeclContentSpecIndex[n2][n3];
        XMLContentSpec xMLContentSpec = new XMLContentSpec();
        this.getContentSpec(n4, xMLContentSpec);
        if (s == 2) {
            ChildrenList childrenList = new ChildrenList();
            this.contentSpecTree(n4, xMLContentSpec, childrenList);
            contentModelValidator = new MixedContentModel(childrenList.qname, childrenList.type, 0, childrenList.length, false);
        } else if (s == 3) {
            contentModelValidator = this.createChildModel(n4);
        } else {
            throw new RuntimeException("Unknown content type for a element decl in getElementContentModelValidator() in AbstractDTDGrammar class");
        }
        this.fElementDeclContentModelValidator[n2][n3] = contentModelValidator;
        return contentModelValidator;
    }

    protected int createElementDecl() {
        int n = this.fElementDeclCount >> 8;
        int n2 = this.fElementDeclCount & 0xFF;
        this.ensureElementDeclCapacity(n);
        this.fElementDeclName[n][n2] = new QName();
        this.fElementDeclType[n][n2] = -1;
        this.fElementDeclContentModelValidator[n][n2] = null;
        this.fElementDeclFirstAttributeDeclIndex[n][n2] = -1;
        this.fElementDeclLastAttributeDeclIndex[n][n2] = -1;
        return this.fElementDeclCount++;
    }

    protected void setElementDecl(int n, XMLElementDecl xMLElementDecl) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return;
        }
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        this.fElementDeclName[n2][n3].setValues(xMLElementDecl.name);
        this.fElementDeclType[n2][n3] = xMLElementDecl.type;
        this.fElementDeclContentModelValidator[n2][n3] = xMLElementDecl.contentModelValidator;
        if (xMLElementDecl.simpleType.list) {
            short[] sArray = this.fElementDeclType[n2];
            int n4 = n3;
            sArray[n4] = (short)(sArray[n4] | 0x80);
        }
        this.fElementIndexMap.put(xMLElementDecl.name.rawname, n);
    }

    protected void putElementNameMapping(QName qName, int n, int n2) {
    }

    protected void setFirstAttributeDeclIndex(int n, int n2) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return;
        }
        int n3 = n >> 8;
        int n4 = n & 0xFF;
        this.fElementDeclFirstAttributeDeclIndex[n3][n4] = n2;
    }

    protected void setContentSpecIndex(int n, int n2) {
        if (n < 0 || n >= this.fElementDeclCount) {
            return;
        }
        int n3 = n >> 8;
        int n4 = n & 0xFF;
        this.fElementDeclContentSpecIndex[n3][n4] = n2;
    }

    protected int createAttributeDecl() {
        int n = this.fAttributeDeclCount >> 8;
        int n2 = this.fAttributeDeclCount & 0xFF;
        this.ensureAttributeDeclCapacity(n);
        this.fAttributeDeclName[n][n2] = new QName();
        this.fAttributeDeclType[n][n2] = -1;
        this.fAttributeDeclDatatypeValidator[n][n2] = null;
        this.fAttributeDeclEnumeration[n][n2] = null;
        this.fAttributeDeclDefaultType[n][n2] = 0;
        this.fAttributeDeclDefaultValue[n][n2] = null;
        this.fAttributeDeclNonNormalizedDefaultValue[n][n2] = null;
        this.fAttributeDeclNextAttributeDeclIndex[n][n2] = -1;
        return this.fAttributeDeclCount++;
    }

    protected void setAttributeDecl(int n, int n2, XMLAttributeDecl xMLAttributeDecl) {
        int n3 = n2 >> 8;
        int n4 = n2 & 0xFF;
        this.fAttributeDeclName[n3][n4].setValues(xMLAttributeDecl.name);
        this.fAttributeDeclType[n3][n4] = xMLAttributeDecl.simpleType.type;
        if (xMLAttributeDecl.simpleType.list) {
            short[] sArray = this.fAttributeDeclType[n3];
            int n5 = n4;
            sArray[n5] = (short)(sArray[n5] | 0x80);
        }
        this.fAttributeDeclEnumeration[n3][n4] = xMLAttributeDecl.simpleType.enumeration;
        this.fAttributeDeclDefaultType[n3][n4] = xMLAttributeDecl.simpleType.defaultType;
        this.fAttributeDeclDatatypeValidator[n3][n4] = xMLAttributeDecl.simpleType.datatypeValidator;
        this.fAttributeDeclDefaultValue[n3][n4] = xMLAttributeDecl.simpleType.defaultValue;
        this.fAttributeDeclNonNormalizedDefaultValue[n3][n4] = xMLAttributeDecl.simpleType.nonNormalizedDefaultValue;
        int n6 = n >> 8;
        int n7 = n & 0xFF;
        int n8 = this.fElementDeclFirstAttributeDeclIndex[n6][n7];
        while (n8 != -1 && n8 != n2) {
            n3 = n8 >> 8;
            n4 = n8 & 0xFF;
            n8 = this.fAttributeDeclNextAttributeDeclIndex[n3][n4];
        }
        if (n8 == -1) {
            if (this.fElementDeclFirstAttributeDeclIndex[n6][n7] == -1) {
                this.fElementDeclFirstAttributeDeclIndex[n6][n7] = n2;
            } else {
                n8 = this.fElementDeclLastAttributeDeclIndex[n6][n7];
                n3 = n8 >> 8;
                n4 = n8 & 0xFF;
                this.fAttributeDeclNextAttributeDeclIndex[n3][n4] = n2;
            }
            this.fElementDeclLastAttributeDeclIndex[n6][n7] = n2;
        }
    }

    protected int createContentSpec() {
        int n = this.fContentSpecCount >> 8;
        int n2 = this.fContentSpecCount & 0xFF;
        this.ensureContentSpecCapacity(n);
        this.fContentSpecType[n][n2] = -1;
        this.fContentSpecValue[n][n2] = null;
        this.fContentSpecOtherValue[n][n2] = null;
        return this.fContentSpecCount++;
    }

    protected void setContentSpec(int n, XMLContentSpec xMLContentSpec) {
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        this.fContentSpecType[n2][n3] = xMLContentSpec.type;
        this.fContentSpecValue[n2][n3] = xMLContentSpec.value;
        this.fContentSpecOtherValue[n2][n3] = xMLContentSpec.otherValue;
    }

    protected int createEntityDecl() {
        int n = this.fEntityCount >> 8;
        int n2 = this.fEntityCount & 0xFF;
        this.ensureEntityDeclCapacity(n);
        this.fEntityIsPE[n][n2] = 0;
        this.fEntityInExternal[n][n2] = 0;
        return this.fEntityCount++;
    }

    protected void setEntityDecl(int n, XMLEntityDecl xMLEntityDecl) {
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        this.fEntityName[n2][n3] = xMLEntityDecl.name;
        this.fEntityValue[n2][n3] = xMLEntityDecl.value;
        this.fEntityPublicId[n2][n3] = xMLEntityDecl.publicId;
        this.fEntitySystemId[n2][n3] = xMLEntityDecl.systemId;
        this.fEntityBaseSystemId[n2][n3] = xMLEntityDecl.baseSystemId;
        this.fEntityNotation[n2][n3] = xMLEntityDecl.notation;
        this.fEntityIsPE[n2][n3] = xMLEntityDecl.isPE ? (byte)1 : 0;
        this.fEntityInExternal[n2][n3] = xMLEntityDecl.inExternal ? (byte)1 : 0;
        this.fEntityIndexMap.put(xMLEntityDecl.name, n);
    }

    protected int createNotationDecl() {
        int n = this.fNotationCount >> 8;
        this.ensureNotationDeclCapacity(n);
        return this.fNotationCount++;
    }

    protected void setNotationDecl(int n, XMLNotationDecl xMLNotationDecl) {
        int n2 = n >> 8;
        int n3 = n & 0xFF;
        this.fNotationName[n2][n3] = xMLNotationDecl.name;
        this.fNotationPublicId[n2][n3] = xMLNotationDecl.publicId;
        this.fNotationSystemId[n2][n3] = xMLNotationDecl.systemId;
        this.fNotationBaseSystemId[n2][n3] = xMLNotationDecl.baseSystemId;
        this.fNotationIndexMap.put(xMLNotationDecl.name, n);
    }

    protected int addContentSpecNode(short s, String string) {
        int n = this.createContentSpec();
        this.fContentSpec.setValues(s, string, null);
        this.setContentSpec(n, this.fContentSpec);
        return n;
    }

    protected int addUniqueLeafNode(String string) {
        int n = this.createContentSpec();
        this.fContentSpec.setValues((short)0, string, null);
        this.setContentSpec(n, this.fContentSpec);
        return n;
    }

    protected int addContentSpecNode(short s, int n, int n2) {
        int n3 = this.createContentSpec();
        int[] nArray = new int[1];
        int[] nArray2 = new int[1];
        nArray[0] = n;
        nArray2[0] = n2;
        this.fContentSpec.setValues(s, nArray, nArray2);
        this.setContentSpec(n3, this.fContentSpec);
        return n3;
    }

    protected void initializeContentModelStack() {
        if (this.fOpStack == null) {
            this.fOpStack = new short[8];
            this.fNodeIndexStack = new int[8];
            this.fPrevNodeIndexStack = new int[8];
        } else if (this.fDepth == this.fOpStack.length) {
            short[] sArray = new short[this.fDepth * 2];
            System.arraycopy(this.fOpStack, 0, sArray, 0, this.fDepth);
            this.fOpStack = sArray;
            int[] nArray = new int[this.fDepth * 2];
            System.arraycopy(this.fNodeIndexStack, 0, nArray, 0, this.fDepth);
            this.fNodeIndexStack = nArray;
            nArray = new int[this.fDepth * 2];
            System.arraycopy(this.fPrevNodeIndexStack, 0, nArray, 0, this.fDepth);
            this.fPrevNodeIndexStack = nArray;
        }
        this.fOpStack[this.fDepth] = -1;
        this.fNodeIndexStack[this.fDepth] = -1;
        this.fPrevNodeIndexStack[this.fDepth] = -1;
    }

    boolean isImmutable() {
        return this.fIsImmutable;
    }

    private void appendContentSpec(XMLContentSpec xMLContentSpec, StringBuffer stringBuffer, boolean bl, int n) {
        int n2 = xMLContentSpec.type & 0xF;
        switch (n2) {
            case 0: {
                if (xMLContentSpec.value == null && xMLContentSpec.otherValue == null) {
                    stringBuffer.append("#PCDATA");
                    break;
                }
                if (xMLContentSpec.value == null && xMLContentSpec.otherValue != null) {
                    stringBuffer.append("##any:uri=").append(xMLContentSpec.otherValue);
                    break;
                }
                if (xMLContentSpec.value == null) {
                    stringBuffer.append("##any");
                    break;
                }
                stringBuffer.append(xMLContentSpec.value);
                break;
            }
            case 1: {
                if (n == 3 || n == 2 || n == 1) {
                    this.getContentSpec(((int[])xMLContentSpec.value)[0], xMLContentSpec);
                    stringBuffer.append('(');
                    this.appendContentSpec(xMLContentSpec, stringBuffer, true, n2);
                    stringBuffer.append(')');
                } else {
                    this.getContentSpec(((int[])xMLContentSpec.value)[0], xMLContentSpec);
                    this.appendContentSpec(xMLContentSpec, stringBuffer, true, n2);
                }
                stringBuffer.append('?');
                break;
            }
            case 2: {
                if (n == 3 || n == 2 || n == 1) {
                    this.getContentSpec(((int[])xMLContentSpec.value)[0], xMLContentSpec);
                    stringBuffer.append('(');
                    this.appendContentSpec(xMLContentSpec, stringBuffer, true, n2);
                    stringBuffer.append(')');
                } else {
                    this.getContentSpec(((int[])xMLContentSpec.value)[0], xMLContentSpec);
                    this.appendContentSpec(xMLContentSpec, stringBuffer, true, n2);
                }
                stringBuffer.append('*');
                break;
            }
            case 3: {
                if (n == 3 || n == 2 || n == 1) {
                    stringBuffer.append('(');
                    this.getContentSpec(((int[])xMLContentSpec.value)[0], xMLContentSpec);
                    this.appendContentSpec(xMLContentSpec, stringBuffer, true, n2);
                    stringBuffer.append(')');
                } else {
                    this.getContentSpec(((int[])xMLContentSpec.value)[0], xMLContentSpec);
                    this.appendContentSpec(xMLContentSpec, stringBuffer, true, n2);
                }
                stringBuffer.append('+');
                break;
            }
            case 4: 
            case 5: {
                if (bl) {
                    stringBuffer.append('(');
                }
                short s = xMLContentSpec.type;
                int n3 = ((int[])xMLContentSpec.otherValue)[0];
                this.getContentSpec(((int[])xMLContentSpec.value)[0], xMLContentSpec);
                this.appendContentSpec(xMLContentSpec, stringBuffer, xMLContentSpec.type != s, n2);
                if (s == 4) {
                    stringBuffer.append('|');
                } else {
                    stringBuffer.append(',');
                }
                this.getContentSpec(n3, xMLContentSpec);
                this.appendContentSpec(xMLContentSpec, stringBuffer, true, n2);
                if (!bl) break;
                stringBuffer.append(')');
                break;
            }
            case 6: {
                stringBuffer.append("##any");
                if (xMLContentSpec.otherValue == null) break;
                stringBuffer.append(":uri=");
                stringBuffer.append(xMLContentSpec.otherValue);
                break;
            }
            case 7: {
                stringBuffer.append("##other:uri=");
                stringBuffer.append(xMLContentSpec.otherValue);
                break;
            }
            case 8: {
                stringBuffer.append("##local");
                break;
            }
            default: {
                stringBuffer.append("???");
            }
        }
    }

    private void printAttribute(int n) {
        XMLAttributeDecl xMLAttributeDecl = new XMLAttributeDecl();
        if (this.getAttributeDecl(n, xMLAttributeDecl)) {
            System.out.print(" { ");
            System.out.print(xMLAttributeDecl.name.localpart);
            System.out.print(" }");
        }
    }

    private synchronized ContentModelValidator createChildModel(int n) {
        Object object;
        XMLContentSpec xMLContentSpec = new XMLContentSpec();
        this.getContentSpec(n, xMLContentSpec);
        if ((xMLContentSpec.type & 0xF) != 6 && (xMLContentSpec.type & 0xF) != 7 && (xMLContentSpec.type & 0xF) != 8) {
            if (xMLContentSpec.type == 0) {
                if (xMLContentSpec.value == null && xMLContentSpec.otherValue == null) {
                    throw new RuntimeException("ImplementationMessages.VAL_NPCD");
                }
                this.fQName.setValues(null, (String)xMLContentSpec.value, (String)xMLContentSpec.value, (String)xMLContentSpec.otherValue);
                return new SimpleContentModel(xMLContentSpec.type, this.fQName, null);
            }
            if (xMLContentSpec.type == 4 || xMLContentSpec.type == 5) {
                object = new XMLContentSpec();
                XMLContentSpec xMLContentSpec2 = new XMLContentSpec();
                this.getContentSpec(((int[])xMLContentSpec.value)[0], (XMLContentSpec)object);
                this.getContentSpec(((int[])xMLContentSpec.otherValue)[0], xMLContentSpec2);
                if (((XMLContentSpec)object).type == 0 && xMLContentSpec2.type == 0) {
                    this.fQName.setValues(null, (String)((XMLContentSpec)object).value, (String)((XMLContentSpec)object).value, (String)((XMLContentSpec)object).otherValue);
                    this.fQName2.setValues(null, (String)xMLContentSpec2.value, (String)xMLContentSpec2.value, (String)xMLContentSpec2.otherValue);
                    return new SimpleContentModel(xMLContentSpec.type, this.fQName, this.fQName2);
                }
            } else if (xMLContentSpec.type == 1 || xMLContentSpec.type == 2 || xMLContentSpec.type == 3) {
                object = new XMLContentSpec();
                this.getContentSpec(((int[])xMLContentSpec.value)[0], (XMLContentSpec)object);
                if (((XMLContentSpec)object).type == 0) {
                    this.fQName.setValues(null, (String)((XMLContentSpec)object).value, (String)((XMLContentSpec)object).value, (String)((XMLContentSpec)object).otherValue);
                    return new SimpleContentModel(xMLContentSpec.type, this.fQName, null);
                }
            } else {
                throw new RuntimeException("ImplementationMessages.VAL_CST");
            }
        }
        this.fLeafCount = 0;
        this.fLeafCount = 0;
        object = this.buildSyntaxTree(n, xMLContentSpec);
        return new DFAContentModel((CMNode)object, this.fLeafCount, false);
    }

    private final CMNode buildSyntaxTree(int n, XMLContentSpec xMLContentSpec) {
        CMNode cMNode = null;
        this.getContentSpec(n, xMLContentSpec);
        if ((xMLContentSpec.type & 0xF) == 6) {
            cMNode = new CMAny(xMLContentSpec.type, (String)xMLContentSpec.otherValue, this.fLeafCount++);
        } else if ((xMLContentSpec.type & 0xF) == 7) {
            cMNode = new CMAny(xMLContentSpec.type, (String)xMLContentSpec.otherValue, this.fLeafCount++);
        } else if ((xMLContentSpec.type & 0xF) == 8) {
            cMNode = new CMAny(xMLContentSpec.type, null, this.fLeafCount++);
        } else if (xMLContentSpec.type == 0) {
            this.fQName.setValues(null, (String)xMLContentSpec.value, (String)xMLContentSpec.value, (String)xMLContentSpec.otherValue);
            cMNode = new CMLeaf(this.fQName, this.fLeafCount++);
        } else {
            int n2 = ((int[])xMLContentSpec.value)[0];
            int n3 = ((int[])xMLContentSpec.otherValue)[0];
            if (xMLContentSpec.type == 4 || xMLContentSpec.type == 5) {
                cMNode = new CMBinOp(xMLContentSpec.type, this.buildSyntaxTree(n2, xMLContentSpec), this.buildSyntaxTree(n3, xMLContentSpec));
            } else if (xMLContentSpec.type == 2) {
                cMNode = new CMUniOp(xMLContentSpec.type, this.buildSyntaxTree(n2, xMLContentSpec));
            } else if (xMLContentSpec.type == 2 || xMLContentSpec.type == 1 || xMLContentSpec.type == 3) {
                cMNode = new CMUniOp(xMLContentSpec.type, this.buildSyntaxTree(n2, xMLContentSpec));
            } else {
                throw new RuntimeException("ImplementationMessages.VAL_CST");
            }
        }
        return cMNode;
    }

    private void contentSpecTree(int n, XMLContentSpec xMLContentSpec, ChildrenList childrenList) {
        this.getContentSpec(n, xMLContentSpec);
        if (xMLContentSpec.type == 0 || (xMLContentSpec.type & 0xF) == 6 || (xMLContentSpec.type & 0xF) == 8 || (xMLContentSpec.type & 0xF) == 7) {
            if (childrenList.length == childrenList.qname.length) {
                QName[] qNameArray = new QName[childrenList.length * 2];
                System.arraycopy(childrenList.qname, 0, qNameArray, 0, childrenList.length);
                childrenList.qname = qNameArray;
                int[] nArray = new int[childrenList.length * 2];
                System.arraycopy(childrenList.type, 0, nArray, 0, childrenList.length);
                childrenList.type = nArray;
            }
            childrenList.qname[childrenList.length] = new QName(null, (String)xMLContentSpec.value, (String)xMLContentSpec.value, (String)xMLContentSpec.otherValue);
            childrenList.type[childrenList.length] = xMLContentSpec.type;
            ++childrenList.length;
            return;
        }
        int n2 = xMLContentSpec.value != null ? ((int[])xMLContentSpec.value)[0] : -1;
        int n3 = -1;
        if (xMLContentSpec.otherValue == null) {
            return;
        }
        n3 = ((int[])xMLContentSpec.otherValue)[0];
        if (xMLContentSpec.type == 4 || xMLContentSpec.type == 5) {
            this.contentSpecTree(n2, xMLContentSpec, childrenList);
            this.contentSpecTree(n3, xMLContentSpec, childrenList);
            return;
        }
        if (xMLContentSpec.type == 1 || xMLContentSpec.type == 2 || xMLContentSpec.type == 3) {
            this.contentSpecTree(n2, xMLContentSpec, childrenList);
            return;
        }
        throw new RuntimeException("Invalid content spec type seen in contentSpecTree() method of AbstractDTDGrammar class : " + xMLContentSpec.type);
    }

    private void ensureElementDeclCapacity(int n) {
        if (n >= this.fElementDeclName.length) {
            this.fElementDeclIsExternal = DTDGrammar.resize(this.fElementDeclIsExternal, this.fElementDeclIsExternal.length * 2);
            this.fElementDeclName = DTDGrammar.resize(this.fElementDeclName, this.fElementDeclName.length * 2);
            this.fElementDeclType = DTDGrammar.resize(this.fElementDeclType, this.fElementDeclType.length * 2);
            this.fElementDeclContentModelValidator = DTDGrammar.resize(this.fElementDeclContentModelValidator, this.fElementDeclContentModelValidator.length * 2);
            this.fElementDeclContentSpecIndex = DTDGrammar.resize(this.fElementDeclContentSpecIndex, this.fElementDeclContentSpecIndex.length * 2);
            this.fElementDeclFirstAttributeDeclIndex = DTDGrammar.resize(this.fElementDeclFirstAttributeDeclIndex, this.fElementDeclFirstAttributeDeclIndex.length * 2);
            this.fElementDeclLastAttributeDeclIndex = DTDGrammar.resize(this.fElementDeclLastAttributeDeclIndex, this.fElementDeclLastAttributeDeclIndex.length * 2);
        } else if (this.fElementDeclName[n] != null) {
            return;
        }
        this.fElementDeclIsExternal[n] = new int[256];
        this.fElementDeclName[n] = new QName[256];
        this.fElementDeclType[n] = new short[256];
        this.fElementDeclContentModelValidator[n] = new ContentModelValidator[256];
        this.fElementDeclContentSpecIndex[n] = new int[256];
        this.fElementDeclFirstAttributeDeclIndex[n] = new int[256];
        this.fElementDeclLastAttributeDeclIndex[n] = new int[256];
    }

    private void ensureAttributeDeclCapacity(int n) {
        if (n >= this.fAttributeDeclName.length) {
            this.fAttributeDeclIsExternal = DTDGrammar.resize(this.fAttributeDeclIsExternal, this.fAttributeDeclIsExternal.length * 2);
            this.fAttributeDeclName = DTDGrammar.resize(this.fAttributeDeclName, this.fAttributeDeclName.length * 2);
            this.fAttributeDeclType = DTDGrammar.resize(this.fAttributeDeclType, this.fAttributeDeclType.length * 2);
            this.fAttributeDeclEnumeration = DTDGrammar.resize(this.fAttributeDeclEnumeration, this.fAttributeDeclEnumeration.length * 2);
            this.fAttributeDeclDefaultType = DTDGrammar.resize(this.fAttributeDeclDefaultType, this.fAttributeDeclDefaultType.length * 2);
            this.fAttributeDeclDatatypeValidator = DTDGrammar.resize(this.fAttributeDeclDatatypeValidator, this.fAttributeDeclDatatypeValidator.length * 2);
            this.fAttributeDeclDefaultValue = DTDGrammar.resize(this.fAttributeDeclDefaultValue, this.fAttributeDeclDefaultValue.length * 2);
            this.fAttributeDeclNonNormalizedDefaultValue = DTDGrammar.resize(this.fAttributeDeclNonNormalizedDefaultValue, this.fAttributeDeclNonNormalizedDefaultValue.length * 2);
            this.fAttributeDeclNextAttributeDeclIndex = DTDGrammar.resize(this.fAttributeDeclNextAttributeDeclIndex, this.fAttributeDeclNextAttributeDeclIndex.length * 2);
        } else if (this.fAttributeDeclName[n] != null) {
            return;
        }
        this.fAttributeDeclIsExternal[n] = new int[256];
        this.fAttributeDeclName[n] = new QName[256];
        this.fAttributeDeclType[n] = new short[256];
        this.fAttributeDeclEnumeration[n] = new String[256][];
        this.fAttributeDeclDefaultType[n] = new short[256];
        this.fAttributeDeclDatatypeValidator[n] = new DatatypeValidator[256];
        this.fAttributeDeclDefaultValue[n] = new String[256];
        this.fAttributeDeclNonNormalizedDefaultValue[n] = new String[256];
        this.fAttributeDeclNextAttributeDeclIndex[n] = new int[256];
    }

    private void ensureEntityDeclCapacity(int n) {
        if (n >= this.fEntityName.length) {
            this.fEntityName = DTDGrammar.resize(this.fEntityName, this.fEntityName.length * 2);
            this.fEntityValue = DTDGrammar.resize(this.fEntityValue, this.fEntityValue.length * 2);
            this.fEntityPublicId = DTDGrammar.resize(this.fEntityPublicId, this.fEntityPublicId.length * 2);
            this.fEntitySystemId = DTDGrammar.resize(this.fEntitySystemId, this.fEntitySystemId.length * 2);
            this.fEntityBaseSystemId = DTDGrammar.resize(this.fEntityBaseSystemId, this.fEntityBaseSystemId.length * 2);
            this.fEntityNotation = DTDGrammar.resize(this.fEntityNotation, this.fEntityNotation.length * 2);
            this.fEntityIsPE = DTDGrammar.resize(this.fEntityIsPE, this.fEntityIsPE.length * 2);
            this.fEntityInExternal = DTDGrammar.resize(this.fEntityInExternal, this.fEntityInExternal.length * 2);
        } else if (this.fEntityName[n] != null) {
            return;
        }
        this.fEntityName[n] = new String[256];
        this.fEntityValue[n] = new String[256];
        this.fEntityPublicId[n] = new String[256];
        this.fEntitySystemId[n] = new String[256];
        this.fEntityBaseSystemId[n] = new String[256];
        this.fEntityNotation[n] = new String[256];
        this.fEntityIsPE[n] = new byte[256];
        this.fEntityInExternal[n] = new byte[256];
    }

    private void ensureNotationDeclCapacity(int n) {
        if (n >= this.fNotationName.length) {
            this.fNotationName = DTDGrammar.resize(this.fNotationName, this.fNotationName.length * 2);
            this.fNotationPublicId = DTDGrammar.resize(this.fNotationPublicId, this.fNotationPublicId.length * 2);
            this.fNotationSystemId = DTDGrammar.resize(this.fNotationSystemId, this.fNotationSystemId.length * 2);
            this.fNotationBaseSystemId = DTDGrammar.resize(this.fNotationBaseSystemId, this.fNotationBaseSystemId.length * 2);
        } else if (this.fNotationName[n] != null) {
            return;
        }
        this.fNotationName[n] = new String[256];
        this.fNotationPublicId[n] = new String[256];
        this.fNotationSystemId[n] = new String[256];
        this.fNotationBaseSystemId[n] = new String[256];
    }

    private void ensureContentSpecCapacity(int n) {
        if (n >= this.fContentSpecType.length) {
            this.fContentSpecType = DTDGrammar.resize(this.fContentSpecType, this.fContentSpecType.length * 2);
            this.fContentSpecValue = DTDGrammar.resize(this.fContentSpecValue, this.fContentSpecValue.length * 2);
            this.fContentSpecOtherValue = DTDGrammar.resize(this.fContentSpecOtherValue, this.fContentSpecOtherValue.length * 2);
        } else if (this.fContentSpecType[n] != null) {
            return;
        }
        this.fContentSpecType[n] = new short[256];
        this.fContentSpecValue[n] = new Object[256];
        this.fContentSpecOtherValue[n] = new Object[256];
    }

    private static byte[][] resize(byte[][] byArray, int n) {
        byte[][] byArrayArray = new byte[n][];
        System.arraycopy(byArray, 0, byArrayArray, 0, byArray.length);
        return byArrayArray;
    }

    private static short[][] resize(short[][] sArray, int n) {
        short[][] sArrayArray = new short[n][];
        System.arraycopy(sArray, 0, sArrayArray, 0, sArray.length);
        return sArrayArray;
    }

    private static int[][] resize(int[][] nArray, int n) {
        int[][] nArrayArray = new int[n][];
        System.arraycopy(nArray, 0, nArrayArray, 0, nArray.length);
        return nArrayArray;
    }

    private static DatatypeValidator[][] resize(DatatypeValidator[][] datatypeValidatorArray, int n) {
        DatatypeValidator[][] datatypeValidatorArray2 = new DatatypeValidator[n][];
        System.arraycopy(datatypeValidatorArray, 0, datatypeValidatorArray2, 0, datatypeValidatorArray.length);
        return datatypeValidatorArray2;
    }

    private static ContentModelValidator[][] resize(ContentModelValidator[][] contentModelValidatorArray, int n) {
        ContentModelValidator[][] contentModelValidatorArray2 = new ContentModelValidator[n][];
        System.arraycopy(contentModelValidatorArray, 0, contentModelValidatorArray2, 0, contentModelValidatorArray.length);
        return contentModelValidatorArray2;
    }

    private static Object[][] resize(Object[][] objectArray, int n) {
        Object[][] objectArray2 = new Object[n][];
        System.arraycopy(objectArray, 0, objectArray2, 0, objectArray.length);
        return objectArray2;
    }

    private static QName[][] resize(QName[][] qNameArray, int n) {
        QName[][] qNameArray2 = new QName[n][];
        System.arraycopy(qNameArray, 0, qNameArray2, 0, qNameArray.length);
        return qNameArray2;
    }

    private static String[][] resize(String[][] stringArray, int n) {
        String[][] stringArray2 = new String[n][];
        System.arraycopy(stringArray, 0, stringArray2, 0, stringArray.length);
        return stringArray2;
    }

    private static String[][][] resize(String[][][] stringArray, int n) {
        String[][][] stringArray2 = new String[n][][];
        System.arraycopy(stringArray, 0, stringArray2, 0, stringArray.length);
        return stringArray2;
    }

    @Override
    public boolean isEntityDeclared(String string) {
        return this.getEntityDeclIndex(string) != -1;
    }

    @Override
    public boolean isEntityUnparsed(String string) {
        int n = this.getEntityDeclIndex(string);
        if (n > -1) {
            int n2 = n >> 8;
            int n3 = n & 0xFF;
            return this.fEntityNotation[n2][n3] != null;
        }
        return false;
    }

    protected static final class QNameHashtable {
        private static final int INITIAL_BUCKET_SIZE = 4;
        private static final int HASHTABLE_SIZE = 101;
        private static final int MAX_HASH_COLLISIONS = 40;
        private static final int MULTIPLIERS_SIZE = 32;
        private static final int MULTIPLIERS_MASK = 31;
        private Object[][] fHashTable = new Object[101][];
        private int fTableSize = 101;
        private int fCount = 0;
        private int[] fHashMultipliers;

        protected QNameHashtable() {
        }

        public void put(String string, int n) {
            int n2 = (this.hash(string) & Integer.MAX_VALUE) % this.fTableSize;
            Object[] objectArray = this.fHashTable[n2];
            if (objectArray == null) {
                objectArray = new Object[9];
                objectArray[0] = new int[]{1};
                objectArray[1] = string;
                objectArray[2] = new int[]{n};
                this.fHashTable[n2] = objectArray;
                if (++this.fCount > this.fTableSize) {
                    this.rehash();
                }
            } else {
                int n3;
                int n4 = ((int[])objectArray[0])[0];
                int n5 = 1 + 2 * n4;
                if (n5 == objectArray.length) {
                    n3 = n4 + 4;
                    Object[] objectArray2 = new Object[1 + 2 * n3];
                    System.arraycopy(objectArray, 0, objectArray2, 0, n5);
                    objectArray = objectArray2;
                    this.fHashTable[n2] = objectArray;
                }
                n3 = 0;
                int n6 = 1;
                for (int i = 0; i < n4; ++i) {
                    if ((String)objectArray[n6] == string) {
                        ((int[])objectArray[n6 + 1])[0] = n;
                        n3 = 1;
                        break;
                    }
                    n6 += 2;
                }
                if (n3 == 0) {
                    objectArray[n5++] = string;
                    objectArray[n5] = new int[]{n};
                    ((int[])objectArray[0])[0] = ++n4;
                    if (++this.fCount > this.fTableSize) {
                        this.rehash();
                    } else if (n4 > 40) {
                        this.rebalance();
                    }
                }
            }
        }

        public int get(String string) {
            int n = (this.hash(string) & Integer.MAX_VALUE) % this.fTableSize;
            Object[] objectArray = this.fHashTable[n];
            if (objectArray == null) {
                return -1;
            }
            int n2 = ((int[])objectArray[0])[0];
            int n3 = 1;
            for (int i = 0; i < n2; ++i) {
                if ((String)objectArray[n3] == string) {
                    return ((int[])objectArray[n3 + 1])[0];
                }
                n3 += 2;
            }
            return -1;
        }

        public int hash(String string) {
            if (this.fHashMultipliers == null) {
                return string.hashCode();
            }
            return this.hash0(string);
        }

        private int hash0(String string) {
            int n = 0;
            int n2 = string.length();
            int[] nArray = this.fHashMultipliers;
            for (int i = 0; i < n2; ++i) {
                n = n * nArray[i & 0x1F] + string.charAt(i);
            }
            return n;
        }

        private void rehash() {
            this.rehashCommon(this.fHashTable.length * 2 + 1);
        }

        private void rebalance() {
            if (this.fHashMultipliers == null) {
                this.fHashMultipliers = new int[32];
            }
            PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
            this.rehashCommon(this.fHashTable.length);
        }

        private void rehashCommon(int n) {
            int n2 = this.fHashTable.length;
            Object[][] objectArray = this.fHashTable;
            Object[][] objectArrayArray = new Object[n][];
            this.fHashTable = objectArrayArray;
            this.fTableSize = this.fHashTable.length;
            for (int i = 0; i < n2; ++i) {
                Object[] objectArray2 = objectArray[i];
                if (objectArray2 == null) continue;
                int n3 = ((int[])objectArray2[0])[0];
                boolean bl = false;
                int n4 = 1;
                for (int j = 0; j < n3; ++j) {
                    String string = (String)objectArray2[n4];
                    Object object = objectArray2[n4 + 1];
                    int n5 = (this.hash(string) & Integer.MAX_VALUE) % this.fTableSize;
                    Object[] objectArray3 = this.fHashTable[n5];
                    if (objectArray3 == null) {
                        if (bl) {
                            objectArray3 = new Object[9];
                            objectArray3[0] = new int[]{1};
                        } else {
                            objectArray3 = objectArray2;
                            ((int[])objectArray3[0])[0] = 1;
                            bl = true;
                        }
                        objectArray3[1] = string;
                        objectArray3[2] = object;
                        this.fHashTable[n5] = objectArray3;
                    } else {
                        int n6 = ((int[])objectArray3[0])[0];
                        int n7 = 1 + 2 * n6;
                        if (n7 == objectArray3.length) {
                            int n8 = n6 + 4;
                            Object[] objectArray4 = new Object[1 + 2 * n8];
                            System.arraycopy(objectArray3, 0, objectArray4, 0, n7);
                            objectArray3 = objectArray4;
                            this.fHashTable[n5] = objectArray3;
                        }
                        objectArray3[n7++] = string;
                        objectArray3[n7] = object;
                        ((int[])objectArray3[0])[0] = ++n6;
                    }
                    n4 += 2;
                }
            }
        }

        private static final class PrimeNumberSequenceGenerator {
            private static int[] PRIMES = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727};

            private PrimeNumberSequenceGenerator() {
            }

            static void generateSequence(int[] nArray) {
                Random random = new Random();
                for (int i = 0; i < nArray.length; ++i) {
                    nArray[i] = PRIMES[random.nextInt(PRIMES.length)];
                }
            }
        }
    }

    private static class ChildrenList {
        public int length = 0;
        public QName[] qname = new QName[2];
        public int[] type = new int[2];
    }
}

