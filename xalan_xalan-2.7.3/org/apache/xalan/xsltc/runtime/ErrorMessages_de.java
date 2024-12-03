/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime;

import java.util.ListResourceBundle;

public class ErrorMessages_de
extends ListResourceBundle {
    @Override
    public Object[][] getContents() {
        return new Object[][]{{"RUN_TIME_INTERNAL_ERR", "Interner Fehler bei der Ausf\u00fchrung in ''{0}''"}, {"RUN_TIME_COPY_ERR", "Fehler bei der Ausf\u00fchrung von <xsl:copy>."}, {"DATA_CONVERSION_ERR", "Ung\u00fcltige Konvertierung von ''{0}'' in ''{1}''."}, {"EXTERNAL_FUNC_ERR", "Die externe Funktion ''{0}'' wird nicht von XSLTC unterst\u00fctzt."}, {"EQUALITY_EXPR_ERR", "Unbekannter Argumenttyp in Gleichheitsausdruck."}, {"INVALID_ARGUMENT_ERR", "Ung\u00fcltiger Argumenttyp ''{0}'' in Aufruf von ''{1}''"}, {"FORMAT_NUMBER_ERR", "Es wird versucht, Nummer ''{0}'' mit Muster ''{1}'' zu formatieren."}, {"ITERATOR_CLONE_ERR", "Iterator ''{0}'' kann nicht geklont werden."}, {"AXIS_SUPPORT_ERR", "Iterator f\u00fcr Achse ''{0}'' wird nicht unterst\u00fctzt."}, {"TYPED_AXIS_SUPPORT_ERR", "Iterator f\u00fcr Achse ''{0}'' mit Typangabe wird nicht unterst\u00fctzt."}, {"STRAY_ATTRIBUTE_ERR", "Attribut ''{0}'' befindet sich nicht in einem Element."}, {"STRAY_NAMESPACE_ERR", "Namensbereichdeklaration ''{0}''=''{1}'' befindet sich nicht in einem Element."}, {"NAMESPACE_PREFIX_ERR", "Der Namensbereich f\u00fcr Pr\u00e4fix ''{0}'' wurde nicht deklariert."}, {"DOM_ADAPTER_INIT_ERR", "DOMAdapter wurde mit dem falschen Typ f\u00fcr das Dokumentobjektmodell der Quelle erstellt."}, {"PARSER_DTD_SUPPORT_ERR", "Der von Ihnen verwendete SAX-Parser bearbeitet keine DTD-Deklarationsereignisse."}, {"NAMESPACES_SUPPORT_ERR", "Der von Ihnen verwendete SAX-Parser unterst\u00fctzt keine XML-Namensbereiche."}, {"CANT_RESOLVE_RELATIVE_URI_ERR", "Der URI-Verweis ''{0}'' konnte nicht aufgel\u00f6st werden."}, {"UNSUPPORTED_XSL_ERR", "Nicht unterst\u00fctztes XSL-Element ''{0}''"}, {"UNSUPPORTED_EXT_ERR", "Nicht erkannte XSLTC-Erweiterung ''{0}''"}, {"UNKNOWN_TRANSLET_VERSION_ERR", "Das angegebene Translet ''{0}'' wurde mit einer neueren XSLTC-Version erstellt als die verwendete Version der XSLTC-Laufzeitsoftware. Sie m\u00fcssen die Formatvorlage erneut kompilieren oder eine neuere XSLTC-Version zum Ausf\u00fchren dieses Translets verwenden."}, {"INVALID_QNAME_ERR", "Ein Attribut, dessen Wert ein QName sein muss, hatte den Wert ''{0}''."}, {"INVALID_NCNAME_ERR", "Ein Attribut, dessen Wert ein NCName sein muss, hatte den Wert ''{0}''."}, {"UNALLOWED_EXTENSION_FUNCTION_ERR", "Die Verwendung der Erweiterungsfunktion ''{0}'' ist nicht zul\u00e4ssig, wenn f\u00fcr die Funktion zur sicheren Verarbeitung der Wert ''true'' festgelegt wurde."}, {"UNALLOWED_EXTENSION_ELEMENT_ERR", "Die Verwendung des Erweiterungselements ''{0}'' ist nicht zul\u00e4ssig, wenn f\u00fcr die Funktion zur sicheren Verarbeitung der Wert ''true'' festgelegt wurde."}};
    }
}

