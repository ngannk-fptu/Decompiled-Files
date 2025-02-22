/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import java.util.HashMap;
import org.apache.xalan.processor.ProcessorAttributeSet;
import org.apache.xalan.processor.ProcessorCharacters;
import org.apache.xalan.processor.ProcessorDecimalFormat;
import org.apache.xalan.processor.ProcessorExsltFuncResult;
import org.apache.xalan.processor.ProcessorExsltFunction;
import org.apache.xalan.processor.ProcessorGlobalParamDecl;
import org.apache.xalan.processor.ProcessorGlobalVariableDecl;
import org.apache.xalan.processor.ProcessorImport;
import org.apache.xalan.processor.ProcessorInclude;
import org.apache.xalan.processor.ProcessorKey;
import org.apache.xalan.processor.ProcessorLRE;
import org.apache.xalan.processor.ProcessorNamespaceAlias;
import org.apache.xalan.processor.ProcessorOutputElem;
import org.apache.xalan.processor.ProcessorPreserveSpace;
import org.apache.xalan.processor.ProcessorStripSpace;
import org.apache.xalan.processor.ProcessorStylesheetDoc;
import org.apache.xalan.processor.ProcessorStylesheetElement;
import org.apache.xalan.processor.ProcessorTemplate;
import org.apache.xalan.processor.ProcessorTemplateElem;
import org.apache.xalan.processor.ProcessorText;
import org.apache.xalan.processor.ProcessorUnknown;
import org.apache.xalan.processor.XSLTAttributeDef;
import org.apache.xalan.processor.XSLTElementDef;
import org.apache.xalan.processor.XSLTElementProcessor;
import org.apache.xalan.templates.ElemApplyImport;
import org.apache.xalan.templates.ElemApplyTemplates;
import org.apache.xalan.templates.ElemAttribute;
import org.apache.xalan.templates.ElemCallTemplate;
import org.apache.xalan.templates.ElemChoose;
import org.apache.xalan.templates.ElemComment;
import org.apache.xalan.templates.ElemCopy;
import org.apache.xalan.templates.ElemCopyOf;
import org.apache.xalan.templates.ElemElement;
import org.apache.xalan.templates.ElemExsltFuncResult;
import org.apache.xalan.templates.ElemExsltFunction;
import org.apache.xalan.templates.ElemExtensionDecl;
import org.apache.xalan.templates.ElemExtensionScript;
import org.apache.xalan.templates.ElemFallback;
import org.apache.xalan.templates.ElemForEach;
import org.apache.xalan.templates.ElemIf;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemMessage;
import org.apache.xalan.templates.ElemNumber;
import org.apache.xalan.templates.ElemOtherwise;
import org.apache.xalan.templates.ElemPI;
import org.apache.xalan.templates.ElemParam;
import org.apache.xalan.templates.ElemSort;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemText;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.ElemUnknown;
import org.apache.xalan.templates.ElemValueOf;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.ElemWhen;
import org.apache.xalan.templates.ElemWithParam;
import org.apache.xml.utils.QName;

public class XSLTSchema
extends XSLTElementDef {
    private HashMap m_availElems = new HashMap();

    XSLTSchema() {
        this.build();
    }

    void build() {
        XSLTAttributeDef hrefAttr = new XSLTAttributeDef(null, "href", 2, true, false, 1);
        XSLTAttributeDef elementsAttr = new XSLTAttributeDef(null, "elements", 12, true, false, 1);
        XSLTAttributeDef methodAttr = new XSLTAttributeDef(null, "method", 9, false, false, 1);
        XSLTAttributeDef versionAttr = new XSLTAttributeDef(null, "version", 13, false, false, 1);
        XSLTAttributeDef encodingAttr = new XSLTAttributeDef(null, "encoding", 1, false, false, 1);
        XSLTAttributeDef omitXmlDeclarationAttr = new XSLTAttributeDef(null, "omit-xml-declaration", 8, false, false, 1);
        XSLTAttributeDef standaloneAttr = new XSLTAttributeDef(null, "standalone", 8, false, false, 1);
        XSLTAttributeDef doctypePublicAttr = new XSLTAttributeDef(null, "doctype-public", 1, false, false, 1);
        XSLTAttributeDef doctypeSystemAttr = new XSLTAttributeDef(null, "doctype-system", 1, false, false, 1);
        XSLTAttributeDef cdataSectionElementsAttr = new XSLTAttributeDef(null, "cdata-section-elements", 19, false, false, 1);
        XSLTAttributeDef indentAttr = new XSLTAttributeDef(null, "indent", 8, false, false, 1);
        XSLTAttributeDef mediaTypeAttr = new XSLTAttributeDef(null, "media-type", 1, false, false, 1);
        XSLTAttributeDef nameAttrRequired = new XSLTAttributeDef(null, "name", 9, true, false, 1);
        XSLTAttributeDef nameAVTRequired = new XSLTAttributeDef(null, "name", 18, true, true, 2);
        XSLTAttributeDef nameAVT_NCNAMERequired = new XSLTAttributeDef(null, "name", 17, true, true, 2);
        XSLTAttributeDef nameAttrOpt_ERROR = new XSLTAttributeDef(null, "name", 9, false, false, 1);
        XSLTAttributeDef useAttr = new XSLTAttributeDef(null, "use", 5, true, false, 1);
        XSLTAttributeDef namespaceAVTOpt = new XSLTAttributeDef(null, "namespace", 2, false, true, 2);
        XSLTAttributeDef decimalSeparatorAttr = new XSLTAttributeDef(null, "decimal-separator", 6, false, 1, ".");
        XSLTAttributeDef infinityAttr = new XSLTAttributeDef(null, "infinity", 1, false, 1, "Infinity");
        XSLTAttributeDef minusSignAttr = new XSLTAttributeDef(null, "minus-sign", 6, false, 1, "-");
        XSLTAttributeDef NaNAttr = new XSLTAttributeDef(null, "NaN", 1, false, 1, "NaN");
        XSLTAttributeDef percentAttr = new XSLTAttributeDef(null, "percent", 6, false, 1, "%");
        XSLTAttributeDef perMilleAttr = new XSLTAttributeDef(null, "per-mille", 6, false, false, 1);
        XSLTAttributeDef zeroDigitAttr = new XSLTAttributeDef(null, "zero-digit", 6, false, 1, "0");
        XSLTAttributeDef digitAttr = new XSLTAttributeDef(null, "digit", 6, false, 1, "#");
        XSLTAttributeDef patternSeparatorAttr = new XSLTAttributeDef(null, "pattern-separator", 6, false, 1, ";");
        XSLTAttributeDef groupingSeparatorAttr = new XSLTAttributeDef(null, "grouping-separator", 6, false, 1, ",");
        XSLTAttributeDef useAttributeSetsAttr = new XSLTAttributeDef(null, "use-attribute-sets", 10, false, false, 1);
        XSLTAttributeDef testAttrRequired = new XSLTAttributeDef(null, "test", 5, true, false, 1);
        XSLTAttributeDef selectAttrRequired = new XSLTAttributeDef(null, "select", 5, true, false, 1);
        XSLTAttributeDef selectAttrOpt = new XSLTAttributeDef(null, "select", 5, false, false, 1);
        XSLTAttributeDef selectAttrDefNode = new XSLTAttributeDef(null, "select", 5, false, 1, "node()");
        XSLTAttributeDef selectAttrDefDot = new XSLTAttributeDef(null, "select", 5, false, 1, ".");
        XSLTAttributeDef matchAttrRequired = new XSLTAttributeDef(null, "match", 4, true, false, 1);
        XSLTAttributeDef matchAttrOpt = new XSLTAttributeDef(null, "match", 4, false, false, 1);
        XSLTAttributeDef priorityAttr = new XSLTAttributeDef(null, "priority", 7, false, false, 1);
        XSLTAttributeDef modeAttr = new XSLTAttributeDef(null, "mode", 9, false, false, 1);
        XSLTAttributeDef spaceAttr = new XSLTAttributeDef("http://www.w3.org/XML/1998/namespace", "space", false, false, false, 2, "default", 2, "preserve", 1);
        XSLTAttributeDef spaceAttrLiteral = new XSLTAttributeDef("http://www.w3.org/XML/1998/namespace", "space", 2, false, true, 1);
        XSLTAttributeDef stylesheetPrefixAttr = new XSLTAttributeDef(null, "stylesheet-prefix", 1, true, false, 1);
        XSLTAttributeDef resultPrefixAttr = new XSLTAttributeDef(null, "result-prefix", 1, true, false, 1);
        XSLTAttributeDef disableOutputEscapingAttr = new XSLTAttributeDef(null, "disable-output-escaping", 8, false, false, 1);
        XSLTAttributeDef levelAttr = new XSLTAttributeDef(null, "level", false, false, false, 1, "single", 1, "multiple", 2, "any", 3);
        levelAttr.setDefault("single");
        XSLTAttributeDef countAttr = new XSLTAttributeDef(null, "count", 4, false, false, 1);
        XSLTAttributeDef fromAttr = new XSLTAttributeDef(null, "from", 4, false, false, 1);
        XSLTAttributeDef valueAttr = new XSLTAttributeDef(null, "value", 5, false, false, 1);
        XSLTAttributeDef formatAttr = new XSLTAttributeDef(null, "format", 1, false, true, 1);
        formatAttr.setDefault("1");
        XSLTAttributeDef langAttr = new XSLTAttributeDef(null, "lang", 13, false, true, 1);
        XSLTAttributeDef letterValueAttr = new XSLTAttributeDef(null, "letter-value", false, true, false, 1, "alphabetic", 1, "traditional", 2);
        XSLTAttributeDef groupingSeparatorAVT = new XSLTAttributeDef(null, "grouping-separator", 6, false, true, 1);
        XSLTAttributeDef groupingSizeAttr = new XSLTAttributeDef(null, "grouping-size", 7, false, true, 1);
        XSLTAttributeDef dataTypeAttr = new XSLTAttributeDef(null, "data-type", false, true, true, 1, "text", 1, "number", 1);
        dataTypeAttr.setDefault("text");
        XSLTAttributeDef orderAttr = new XSLTAttributeDef(null, "order", false, true, false, 1, "ascending", 1, "descending", 2);
        orderAttr.setDefault("ascending");
        XSLTAttributeDef caseOrderAttr = new XSLTAttributeDef(null, "case-order", false, true, false, 1, "upper-first", 1, "lower-first", 2);
        XSLTAttributeDef terminateAttr = new XSLTAttributeDef(null, "terminate", 8, false, false, 1);
        terminateAttr.setDefault("no");
        XSLTAttributeDef xslExcludeResultPrefixesAttr = new XSLTAttributeDef("http://www.w3.org/1999/XSL/Transform", "exclude-result-prefixes", 20, false, false, 1);
        XSLTAttributeDef xslExtensionElementPrefixesAttr = new XSLTAttributeDef("http://www.w3.org/1999/XSL/Transform", "extension-element-prefixes", 15, false, false, 1);
        XSLTAttributeDef xslUseAttributeSetsAttr = new XSLTAttributeDef("http://www.w3.org/1999/XSL/Transform", "use-attribute-sets", 10, false, false, 1);
        XSLTAttributeDef xslVersionAttr = new XSLTAttributeDef("http://www.w3.org/1999/XSL/Transform", "version", 13, false, false, 1);
        XSLTElementDef charData = new XSLTElementDef(this, null, "text()", null, null, null, new ProcessorCharacters(), ElemTextLiteral.class);
        charData.setType(2);
        XSLTElementDef whiteSpaceOnly = new XSLTElementDef(this, null, "text()", null, null, null, null, ElemTextLiteral.class);
        charData.setType(2);
        XSLTAttributeDef resultAttr = new XSLTAttributeDef(null, "*", 3, false, true, 2);
        XSLTAttributeDef xslResultAttr = new XSLTAttributeDef("http://www.w3.org/1999/XSL/Transform", "*", 1, false, false, 2);
        XSLTElementDef[] templateElements = new XSLTElementDef[23];
        XSLTElementDef[] templateElementsAndParams = new XSLTElementDef[24];
        XSLTElementDef[] templateElementsAndSort = new XSLTElementDef[24];
        XSLTElementDef[] exsltFunctionElements = new XSLTElementDef[24];
        XSLTElementDef[] charTemplateElements = new XSLTElementDef[15];
        XSLTElementDef resultElement = new XSLTElementDef(this, null, "*", null, templateElements, new XSLTAttributeDef[]{spaceAttrLiteral, xslExcludeResultPrefixesAttr, xslExtensionElementPrefixesAttr, xslUseAttributeSetsAttr, xslVersionAttr, xslResultAttr, resultAttr}, (XSLTElementProcessor)new ProcessorLRE(), ElemLiteralResult.class, 20, true);
        XSLTElementDef unknownElement = new XSLTElementDef(this, "*", "unknown", null, templateElementsAndParams, new XSLTAttributeDef[]{xslExcludeResultPrefixesAttr, xslExtensionElementPrefixesAttr, xslUseAttributeSetsAttr, xslVersionAttr, xslResultAttr, resultAttr}, (XSLTElementProcessor)new ProcessorUnknown(), ElemUnknown.class, 20, true);
        XSLTElementDef xslValueOf = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "value-of", null, null, new XSLTAttributeDef[]{selectAttrRequired, disableOutputEscapingAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemValueOf.class, 20, true);
        XSLTElementDef xslCopyOf = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "copy-of", null, null, new XSLTAttributeDef[]{selectAttrRequired}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemCopyOf.class, 20, true);
        XSLTElementDef xslNumber = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "number", null, null, new XSLTAttributeDef[]{levelAttr, countAttr, fromAttr, valueAttr, formatAttr, langAttr, letterValueAttr, groupingSeparatorAVT, groupingSizeAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemNumber.class, 20, true);
        XSLTElementDef xslSort = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "sort", null, null, new XSLTAttributeDef[]{selectAttrDefDot, langAttr, dataTypeAttr, orderAttr, caseOrderAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemSort.class, 19, true);
        XSLTElementDef xslWithParam = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "with-param", null, templateElements, new XSLTAttributeDef[]{nameAttrRequired, selectAttrOpt}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemWithParam.class, 19, true);
        XSLTElementDef xslApplyTemplates = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "apply-templates", null, new XSLTElementDef[]{xslSort, xslWithParam}, new XSLTAttributeDef[]{selectAttrDefNode, modeAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemApplyTemplates.class, 20, true);
        XSLTElementDef xslApplyImports = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "apply-imports", null, null, new XSLTAttributeDef[0], new ProcessorTemplateElem(), ElemApplyImport.class);
        XSLTElementDef xslForEach = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "for-each", null, templateElementsAndSort, new XSLTAttributeDef[]{selectAttrRequired, spaceAttr}, new ProcessorTemplateElem(), ElemForEach.class, true, false, true, 20, true);
        XSLTElementDef xslIf = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "if", null, templateElements, new XSLTAttributeDef[]{testAttrRequired, spaceAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemIf.class, 20, true);
        XSLTElementDef xslWhen = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "when", null, templateElements, new XSLTAttributeDef[]{testAttrRequired, spaceAttr}, new ProcessorTemplateElem(), ElemWhen.class, false, true, 1, true);
        XSLTElementDef xslOtherwise = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "otherwise", null, templateElements, new XSLTAttributeDef[]{spaceAttr}, new ProcessorTemplateElem(), ElemOtherwise.class, false, false, 2, false);
        XSLTElementDef xslChoose = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "choose", null, new XSLTElementDef[]{xslWhen, xslOtherwise}, new XSLTAttributeDef[]{spaceAttr}, new ProcessorTemplateElem(), ElemChoose.class, true, false, true, 20, true);
        XSLTElementDef xslAttribute = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "attribute", null, charTemplateElements, new XSLTAttributeDef[]{nameAVTRequired, namespaceAVTOpt, spaceAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemAttribute.class, 20, true);
        XSLTElementDef xslCallTemplate = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "call-template", null, new XSLTElementDef[]{xslWithParam}, new XSLTAttributeDef[]{nameAttrRequired}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemCallTemplate.class, 20, true);
        XSLTElementDef xslVariable = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "variable", null, templateElements, new XSLTAttributeDef[]{nameAttrRequired, selectAttrOpt}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemVariable.class, 20, true);
        XSLTElementDef xslParam = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "param", null, templateElements, new XSLTAttributeDef[]{nameAttrRequired, selectAttrOpt}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemParam.class, 19, true);
        XSLTElementDef xslText = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "text", null, new XSLTElementDef[]{charData}, new XSLTAttributeDef[]{disableOutputEscapingAttr}, (XSLTElementProcessor)new ProcessorText(), ElemText.class, 20, true);
        XSLTElementDef xslProcessingInstruction = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "processing-instruction", null, charTemplateElements, new XSLTAttributeDef[]{nameAVT_NCNAMERequired, spaceAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemPI.class, 20, true);
        XSLTElementDef xslElement = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "element", null, templateElements, new XSLTAttributeDef[]{nameAVTRequired, namespaceAVTOpt, useAttributeSetsAttr, spaceAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemElement.class, 20, true);
        XSLTElementDef xslComment = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "comment", null, charTemplateElements, new XSLTAttributeDef[]{spaceAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemComment.class, 20, true);
        XSLTElementDef xslCopy = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "copy", null, templateElements, new XSLTAttributeDef[]{spaceAttr, useAttributeSetsAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemCopy.class, 20, true);
        XSLTElementDef xslMessage = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "message", null, templateElements, new XSLTAttributeDef[]{terminateAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemMessage.class, 20, true);
        XSLTElementDef xslFallback = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "fallback", null, templateElements, new XSLTAttributeDef[]{spaceAttr}, (XSLTElementProcessor)new ProcessorTemplateElem(), ElemFallback.class, 20, true);
        XSLTElementDef exsltFunction = new XSLTElementDef(this, "http://exslt.org/functions", "function", null, exsltFunctionElements, new XSLTAttributeDef[]{nameAttrRequired}, new ProcessorExsltFunction(), ElemExsltFunction.class);
        XSLTElementDef exsltResult = new XSLTElementDef(this, "http://exslt.org/functions", "result", null, templateElements, new XSLTAttributeDef[]{selectAttrOpt}, new ProcessorExsltFuncResult(), ElemExsltFuncResult.class);
        int i = 0;
        templateElements[i++] = charData;
        templateElements[i++] = xslApplyTemplates;
        templateElements[i++] = xslCallTemplate;
        templateElements[i++] = xslApplyImports;
        templateElements[i++] = xslForEach;
        templateElements[i++] = xslValueOf;
        templateElements[i++] = xslCopyOf;
        templateElements[i++] = xslNumber;
        templateElements[i++] = xslChoose;
        templateElements[i++] = xslIf;
        templateElements[i++] = xslText;
        templateElements[i++] = xslCopy;
        templateElements[i++] = xslVariable;
        templateElements[i++] = xslMessage;
        templateElements[i++] = xslFallback;
        templateElements[i++] = xslProcessingInstruction;
        templateElements[i++] = xslComment;
        templateElements[i++] = xslElement;
        templateElements[i++] = xslAttribute;
        templateElements[i++] = resultElement;
        templateElements[i++] = unknownElement;
        templateElements[i++] = exsltFunction;
        templateElements[i++] = exsltResult;
        System.arraycopy(templateElements, 0, templateElementsAndParams, 0, i);
        System.arraycopy(templateElements, 0, templateElementsAndSort, 0, i);
        System.arraycopy(templateElements, 0, exsltFunctionElements, 0, i);
        templateElementsAndParams[i] = xslParam;
        templateElementsAndSort[i] = xslSort;
        exsltFunctionElements[i] = xslParam;
        i = 0;
        charTemplateElements[i++] = charData;
        charTemplateElements[i++] = xslApplyTemplates;
        charTemplateElements[i++] = xslCallTemplate;
        charTemplateElements[i++] = xslApplyImports;
        charTemplateElements[i++] = xslForEach;
        charTemplateElements[i++] = xslValueOf;
        charTemplateElements[i++] = xslCopyOf;
        charTemplateElements[i++] = xslNumber;
        charTemplateElements[i++] = xslChoose;
        charTemplateElements[i++] = xslIf;
        charTemplateElements[i++] = xslText;
        charTemplateElements[i++] = xslCopy;
        charTemplateElements[i++] = xslVariable;
        charTemplateElements[i++] = xslMessage;
        charTemplateElements[i++] = xslFallback;
        XSLTElementDef importDef = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "import", null, null, new XSLTAttributeDef[]{hrefAttr}, (XSLTElementProcessor)new ProcessorImport(), null, 1, true);
        XSLTElementDef includeDef = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "include", null, null, new XSLTAttributeDef[]{hrefAttr}, (XSLTElementProcessor)new ProcessorInclude(), null, 20, true);
        XSLTAttributeDef[] scriptAttrs = new XSLTAttributeDef[]{new XSLTAttributeDef(null, "lang", 13, true, false, 2), new XSLTAttributeDef(null, "src", 2, false, false, 2)};
        XSLTAttributeDef[] componentAttrs = new XSLTAttributeDef[]{new XSLTAttributeDef(null, "prefix", 13, true, false, 2), new XSLTAttributeDef(null, "elements", 14, false, false, 2), new XSLTAttributeDef(null, "functions", 14, false, false, 2)};
        XSLTElementDef[] topLevelElements = new XSLTElementDef[]{includeDef, importDef, whiteSpaceOnly, unknownElement, new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "strip-space", null, null, new XSLTAttributeDef[]{elementsAttr}, (XSLTElementProcessor)new ProcessorStripSpace(), null, 20, true), new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "preserve-space", null, null, new XSLTAttributeDef[]{elementsAttr}, (XSLTElementProcessor)new ProcessorPreserveSpace(), null, 20, true), new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "output", null, null, new XSLTAttributeDef[]{methodAttr, versionAttr, encodingAttr, omitXmlDeclarationAttr, standaloneAttr, doctypePublicAttr, doctypeSystemAttr, cdataSectionElementsAttr, indentAttr, mediaTypeAttr, XSLTAttributeDef.m_foreignAttr}, (XSLTElementProcessor)new ProcessorOutputElem(), null, 20, true), new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "key", null, null, new XSLTAttributeDef[]{nameAttrRequired, matchAttrRequired, useAttr}, (XSLTElementProcessor)new ProcessorKey(), null, 20, true), new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "decimal-format", null, null, new XSLTAttributeDef[]{nameAttrOpt_ERROR, decimalSeparatorAttr, groupingSeparatorAttr, infinityAttr, minusSignAttr, NaNAttr, percentAttr, perMilleAttr, zeroDigitAttr, digitAttr, patternSeparatorAttr}, (XSLTElementProcessor)new ProcessorDecimalFormat(), null, 20, true), new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "attribute-set", null, new XSLTElementDef[]{xslAttribute}, new XSLTAttributeDef[]{nameAttrRequired, useAttributeSetsAttr}, (XSLTElementProcessor)new ProcessorAttributeSet(), null, 20, true), new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "variable", null, templateElements, new XSLTAttributeDef[]{nameAttrRequired, selectAttrOpt}, (XSLTElementProcessor)new ProcessorGlobalVariableDecl(), ElemVariable.class, 20, true), new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "param", null, templateElements, new XSLTAttributeDef[]{nameAttrRequired, selectAttrOpt}, (XSLTElementProcessor)new ProcessorGlobalParamDecl(), ElemParam.class, 20, true), new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "template", null, templateElementsAndParams, new XSLTAttributeDef[]{matchAttrOpt, nameAttrOpt_ERROR, priorityAttr, modeAttr, spaceAttr}, new ProcessorTemplate(), ElemTemplate.class, true, 20, true), new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "namespace-alias", null, null, new XSLTAttributeDef[]{stylesheetPrefixAttr, resultPrefixAttr}, (XSLTElementProcessor)new ProcessorNamespaceAlias(), null, 20, true), new XSLTElementDef(this, "http://xml.apache.org/xalan", "component", null, new XSLTElementDef[]{new XSLTElementDef(this, "http://xml.apache.org/xalan", "script", null, new XSLTElementDef[]{charData}, scriptAttrs, (XSLTElementProcessor)new ProcessorLRE(), ElemExtensionScript.class, 20, true)}, componentAttrs, new ProcessorLRE(), ElemExtensionDecl.class), new XSLTElementDef(this, "http://xml.apache.org/xslt", "component", null, new XSLTElementDef[]{new XSLTElementDef(this, "http://xml.apache.org/xslt", "script", null, new XSLTElementDef[]{charData}, scriptAttrs, (XSLTElementProcessor)new ProcessorLRE(), ElemExtensionScript.class, 20, true)}, componentAttrs, new ProcessorLRE(), ElemExtensionDecl.class), exsltFunction};
        XSLTAttributeDef excludeResultPrefixesAttr = new XSLTAttributeDef(null, "exclude-result-prefixes", 20, false, false, 2);
        XSLTAttributeDef extensionElementPrefixesAttr = new XSLTAttributeDef(null, "extension-element-prefixes", 15, false, false, 2);
        XSLTAttributeDef idAttr = new XSLTAttributeDef(null, "id", 1, false, false, 2);
        XSLTAttributeDef versionAttrRequired = new XSLTAttributeDef(null, "version", 13, true, false, 2);
        XSLTElementDef stylesheetElemDef = new XSLTElementDef(this, "http://www.w3.org/1999/XSL/Transform", "stylesheet", "transform", topLevelElements, new XSLTAttributeDef[]{extensionElementPrefixesAttr, excludeResultPrefixesAttr, idAttr, versionAttrRequired, spaceAttr}, new ProcessorStylesheetElement(), null, true, -1, false);
        importDef.setElements(new XSLTElementDef[]{stylesheetElemDef, resultElement, unknownElement});
        includeDef.setElements(new XSLTElementDef[]{stylesheetElemDef, resultElement, unknownElement});
        this.build(null, null, null, new XSLTElementDef[]{stylesheetElemDef, whiteSpaceOnly, resultElement, unknownElement}, null, new ProcessorStylesheetDoc(), null);
    }

    public HashMap getElemsAvailable() {
        return this.m_availElems;
    }

    void addAvailableElement(QName elemName) {
        this.m_availElems.put(elemName, elemName);
    }

    public boolean elementAvailable(QName elemName) {
        return this.m_availElems.containsKey(elemName);
    }
}

