/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Enumeration;
import org.apache.xalan.xsltc.compiler.Param;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SourceLoader;
import org.apache.xalan.xsltc.compiler.Stylesheet;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.SyntaxTreeNode;
import org.apache.xalan.xsltc.compiler.TopLevelElement;
import org.apache.xalan.xsltc.compiler.Variable;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xml.utils.SystemIDResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

final class Import
extends TopLevelElement {
    private Stylesheet _imported = null;

    Import() {
    }

    public Stylesheet getImportedStylesheet() {
        return this._imported;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parseContents(Parser parser) {
        XSLTC xsltc = parser.getXSLTC();
        Stylesheet context = parser.getCurrentStylesheet();
        try {
            String docToLoad = this.getAttribute("href");
            if (context.checkForLoop(docToLoad)) {
                ErrorMsg msg = new ErrorMsg("CIRCULAR_INCLUDE_ERR", (Object)docToLoad, this);
                parser.reportError(2, msg);
                return;
            }
            InputSource input = null;
            XMLReader reader = null;
            String currLoadedDoc = context.getSystemId();
            SourceLoader loader = context.getSourceLoader();
            if (loader != null && (input = loader.loadSource(docToLoad, currLoadedDoc, xsltc)) != null) {
                docToLoad = input.getSystemId();
                reader = xsltc.getXMLReader();
            }
            if (input == null) {
                docToLoad = SystemIDResolver.getAbsoluteURI(docToLoad, currLoadedDoc);
                input = new InputSource(docToLoad);
            }
            if (input == null) {
                ErrorMsg msg = new ErrorMsg("FILE_NOT_FOUND_ERR", (Object)docToLoad, this);
                parser.reportError(2, msg);
                return;
            }
            SyntaxTreeNode root = reader != null ? parser.parse(reader, input) : parser.parse(input);
            if (root == null) {
                return;
            }
            this._imported = parser.makeStylesheet(root);
            if (this._imported == null) {
                return;
            }
            this._imported.setSourceLoader(loader);
            this._imported.setSystemId(docToLoad);
            this._imported.setParentStylesheet(context);
            this._imported.setImportingStylesheet(context);
            this._imported.setTemplateInlining(context.getTemplateInlining());
            int currPrecedence = parser.getCurrentImportPrecedence();
            int nextPrecedence = parser.getNextImportPrecedence();
            this._imported.setImportPrecedence(currPrecedence);
            context.setImportPrecedence(nextPrecedence);
            parser.setCurrentStylesheet(this._imported);
            this._imported.parseContents(parser);
            Enumeration elements = this._imported.elements();
            Stylesheet topStylesheet = parser.getTopLevelStylesheet();
            while (elements.hasMoreElements()) {
                Object element = elements.nextElement();
                if (!(element instanceof TopLevelElement)) continue;
                if (element instanceof Variable) {
                    topStylesheet.addVariable((Variable)element);
                    continue;
                }
                if (element instanceof Param) {
                    topStylesheet.addParam((Param)element);
                    continue;
                }
                topStylesheet.addElement((TopLevelElement)element);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            parser.setCurrentStylesheet(context);
        }
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
    }
}

