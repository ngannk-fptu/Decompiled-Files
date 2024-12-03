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

final class Include
extends TopLevelElement {
    private Stylesheet _included = null;

    Include() {
    }

    public Stylesheet getIncludedStylesheet() {
        return this._included;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parseContents(Parser parser) {
        XSLTC xsltc = parser.getXSLTC();
        Stylesheet context = parser.getCurrentStylesheet();
        String docToLoad = this.getAttribute("href");
        try {
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
            this._included = parser.makeStylesheet(root);
            if (this._included == null) {
                return;
            }
            this._included.setSourceLoader(loader);
            this._included.setSystemId(docToLoad);
            this._included.setParentStylesheet(context);
            this._included.setIncludingStylesheet(context);
            this._included.setTemplateInlining(context.getTemplateInlining());
            int precedence = context.getImportPrecedence();
            this._included.setImportPrecedence(precedence);
            parser.setCurrentStylesheet(this._included);
            this._included.parseContents(parser);
            Enumeration elements = this._included.elements();
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

