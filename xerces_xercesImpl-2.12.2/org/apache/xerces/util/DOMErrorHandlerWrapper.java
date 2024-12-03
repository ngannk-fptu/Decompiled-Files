/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.io.PrintWriter;
import java.util.Hashtable;
import org.apache.xerces.dom.DOMErrorImpl;
import org.apache.xerces.dom.DOMLocatorImpl;
import org.apache.xerces.util.XMLErrorCode;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLParseException;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMLocator;
import org.w3c.dom.Node;

public class DOMErrorHandlerWrapper
implements XMLErrorHandler,
DOMErrorHandler {
    protected DOMErrorHandler fDomErrorHandler;
    boolean eStatus = true;
    protected PrintWriter fOut;
    public Node fCurrentNode;
    protected final XMLErrorCode fErrorCode = new XMLErrorCode(null, null);
    protected final DOMErrorImpl fDOMError = new DOMErrorImpl();

    public DOMErrorHandlerWrapper() {
        this.fOut = new PrintWriter(System.err);
    }

    public DOMErrorHandlerWrapper(DOMErrorHandler dOMErrorHandler) {
        this.fDomErrorHandler = dOMErrorHandler;
    }

    public void setErrorHandler(DOMErrorHandler dOMErrorHandler) {
        this.fDomErrorHandler = dOMErrorHandler;
    }

    public DOMErrorHandler getErrorHandler() {
        return this.fDomErrorHandler;
    }

    @Override
    public void warning(String string, String string2, XMLParseException xMLParseException) throws XNIException {
        this.fDOMError.fSeverity = 1;
        this.fDOMError.fException = xMLParseException;
        this.fDOMError.fType = string2;
        this.fDOMError.fMessage = xMLParseException.getMessage();
        this.fDOMError.fRelatedData = this.fDOMError.fMessage;
        DOMLocatorImpl dOMLocatorImpl = this.fDOMError.fLocator;
        if (dOMLocatorImpl != null) {
            dOMLocatorImpl.fColumnNumber = xMLParseException.getColumnNumber();
            dOMLocatorImpl.fLineNumber = xMLParseException.getLineNumber();
            dOMLocatorImpl.fUtf16Offset = xMLParseException.getCharacterOffset();
            dOMLocatorImpl.fUri = xMLParseException.getExpandedSystemId();
            dOMLocatorImpl.fRelatedNode = this.fCurrentNode;
        }
        if (this.fDomErrorHandler != null) {
            this.fDomErrorHandler.handleError(this.fDOMError);
        }
    }

    @Override
    public void error(String string, String string2, XMLParseException xMLParseException) throws XNIException {
        this.fDOMError.fSeverity = (short)2;
        this.fDOMError.fException = xMLParseException;
        this.fDOMError.fType = string2;
        this.fDOMError.fMessage = xMLParseException.getMessage();
        this.fDOMError.fRelatedData = this.fDOMError.fMessage;
        DOMLocatorImpl dOMLocatorImpl = this.fDOMError.fLocator;
        if (dOMLocatorImpl != null) {
            dOMLocatorImpl.fColumnNumber = xMLParseException.getColumnNumber();
            dOMLocatorImpl.fLineNumber = xMLParseException.getLineNumber();
            dOMLocatorImpl.fUtf16Offset = xMLParseException.getCharacterOffset();
            dOMLocatorImpl.fUri = xMLParseException.getExpandedSystemId();
            dOMLocatorImpl.fRelatedNode = this.fCurrentNode;
        }
        if (this.fDomErrorHandler != null) {
            this.fDomErrorHandler.handleError(this.fDOMError);
        }
    }

    @Override
    public void fatalError(String string, String string2, XMLParseException xMLParseException) throws XNIException {
        this.fDOMError.fSeverity = (short)3;
        this.fDOMError.fException = xMLParseException;
        this.fErrorCode.setValues(string, string2);
        String string3 = DOMErrorTypeMap.getDOMErrorType(this.fErrorCode);
        this.fDOMError.fType = string3 != null ? string3 : string2;
        this.fDOMError.fMessage = xMLParseException.getMessage();
        this.fDOMError.fRelatedData = this.fDOMError.fMessage;
        DOMLocatorImpl dOMLocatorImpl = this.fDOMError.fLocator;
        if (dOMLocatorImpl != null) {
            dOMLocatorImpl.fColumnNumber = xMLParseException.getColumnNumber();
            dOMLocatorImpl.fLineNumber = xMLParseException.getLineNumber();
            dOMLocatorImpl.fUtf16Offset = xMLParseException.getCharacterOffset();
            dOMLocatorImpl.fUri = xMLParseException.getExpandedSystemId();
            dOMLocatorImpl.fRelatedNode = this.fCurrentNode;
        }
        if (this.fDomErrorHandler != null) {
            this.fDomErrorHandler.handleError(this.fDOMError);
        }
    }

    @Override
    public boolean handleError(DOMError dOMError) {
        this.printError(dOMError);
        return this.eStatus;
    }

    private void printError(DOMError dOMError) {
        short s = dOMError.getSeverity();
        this.fOut.print("[");
        if (s == 1) {
            this.fOut.print("Warning");
        } else if (s == 2) {
            this.fOut.print("Error");
        } else {
            this.fOut.print("FatalError");
            this.eStatus = false;
        }
        this.fOut.print("] ");
        DOMLocator dOMLocator = dOMError.getLocation();
        if (dOMLocator != null) {
            String string;
            this.fOut.print(dOMLocator.getLineNumber());
            this.fOut.print(":");
            this.fOut.print(dOMLocator.getColumnNumber());
            this.fOut.print(":");
            this.fOut.print(dOMLocator.getByteOffset());
            this.fOut.print(",");
            this.fOut.print(dOMLocator.getUtf16Offset());
            Node node = dOMLocator.getRelatedNode();
            if (node != null) {
                this.fOut.print("[");
                this.fOut.print(node.getNodeName());
                this.fOut.print("]");
            }
            if ((string = dOMLocator.getUri()) != null) {
                int n = string.lastIndexOf(47);
                if (n != -1) {
                    string = string.substring(n + 1);
                }
                this.fOut.print(": ");
                this.fOut.print(string);
            }
        }
        this.fOut.print(":");
        this.fOut.print(dOMError.getMessage());
        this.fOut.println();
        this.fOut.flush();
    }

    private static class DOMErrorTypeMap {
        private static Hashtable fgDOMErrorTypeTable = new Hashtable();

        public static String getDOMErrorType(XMLErrorCode xMLErrorCode) {
            return (String)fgDOMErrorTypeTable.get(xMLErrorCode);
        }

        private DOMErrorTypeMap() {
        }

        static {
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInCDSect"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "TwoColonsInQName"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ColonNotLegalWithNS"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInProlog"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "CDEndInContent"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "CDSectUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "DoctypeNotAllowed"), "doctype-not-allowed");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ETagRequired"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementUnterminated"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EqRequiredInAttribute"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "OpenQuoteExpected"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "CloseQuoteExpected"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ETagUnterminated"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MarkupNotRecognizedInContent"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "DoctypeIllegalInContent"), "doctype-not-allowed");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInAttValue"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInPI"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInInternalSubset"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "QuoteRequiredInAttValue"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "LessthanInAttValue"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttributeValueUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PITargetRequired"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredInPI"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PIUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ReservedPITarget"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PI_NOT_IN_ONE_ENTITY"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PINotInOneEntity"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid"), "unsupported-encoding");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported"), "unsupported-encoding");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInEntityValue"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInExternalSubset"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInIgnoreSect"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInPublicID"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInSystemID"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredAfterSYSTEM"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "QuoteRequiredInSystemID"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SystemIDUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredAfterPUBLIC"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "QuoteRequiredInPublicID"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PublicIDUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PubidCharIllegal"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SpaceRequiredBetweenPublicAndSystem"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ROOT_ELEMENT_TYPE_REQUIRED"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "DoctypedeclUnterminated"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PEReferenceWithinMarkup"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MARKUP_NOT_RECOGNIZED_IN_DTD"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ELEMENTDECL"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_TYPE_REQUIRED_IN_ELEMENTDECL"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_CONTENTSPEC_IN_ELEMENTDECL"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENTSPEC_REQUIRED_IN_ELEMENTDECL"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementDeclUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_OPEN_PAREN_OR_ELEMENT_TYPE_REQUIRED_IN_CHILDREN"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CLOSE_PAREN_REQUIRED_IN_CHILDREN"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_TYPE_REQUIRED_IN_MIXED_CONTENT"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CLOSE_PAREN_REQUIRED_IN_MIXED"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MixedContentUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ELEMENT_TYPE_IN_ATTLISTDECL"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_TYPE_REQUIRED_IN_ATTLISTDECL"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ATTRIBUTE_NAME_IN_ATTDEF"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttNameRequiredInAttDef"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ATTTYPE_IN_ATTDEF"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttTypeRequiredInAttDef"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_DEFAULTDECL_IN_ATTDEF"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ATTRIBUTE_DEFINITION"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_NOTATION_IN_NOTATIONTYPE"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_OPEN_PAREN_REQUIRED_IN_NOTATIONTYPE"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NAME_REQUIRED_IN_NOTATIONTYPE"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "NotationTypeUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NMTOKEN_REQUIRED_IN_ENUMERATION"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EnumerationUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DISTINCT_TOKENS_IN_ENUMERATION"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DISTINCT_NOTATION_IN_ENUMERATION"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_FIXED_IN_DEFAULTDECL"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "IncludeSectUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "IgnoreSectUnterminated"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "NameRequiredInPEReference"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "SemicolonRequiredInPEReference"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_ENTITYDECL"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_PERCENT_IN_PEDECL"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_ENTITY_NAME_IN_PEDECL"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ENTITY_NAME_REQUIRED_IN_ENTITYDECL"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_ENTITY_NAME_IN_ENTITYDECL"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_UNPARSED_ENTITYDECL"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_NDATA_IN_UNPARSED_ENTITYDECL"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NAME_REQUIRED_FOR_UNPARSED_ENTITYDECL"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityDeclUnterminated"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ENTITY_DEFINITION"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ExternalIDRequired"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_PUBIDLITERAL_IN_EXTERNALID"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_PUBIDLITERAL_IN_EXTERNALID"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_SYSTEMLITERAL_IN_EXTERNALID"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_URI_FRAGMENT_IN_SYSTEMID"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_BEFORE_NOTATION_NAME_IN_NOTATIONDECL"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NAME_REQUIRED_IN_NOTATIONDECL"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_SPACE_REQUIRED_AFTER_NOTATION_NAME_IN_NOTATIONDECL"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ExternalIDorPublicIDRequired"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "NotationDeclUnterminated"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ReferenceToExternalEntity"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ReferenceToUnparsedEntity"), "wf-invalid-character");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingNotSupported"), "unsupported-encoding");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingRequired"), "unsupported-encoding");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementXMLNSPrefix"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementPrefixUnbound"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "AttributePrefixUnbound"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "EmptyPrefixedAttName"), "wf-invalid-character-in-node-name");
            fgDOMErrorTypeTable.put(new XMLErrorCode("http://www.w3.org/TR/1998/REC-xml-19980210", "PrefixDeclared"), "wf-invalid-character-in-node-name");
        }
    }
}

