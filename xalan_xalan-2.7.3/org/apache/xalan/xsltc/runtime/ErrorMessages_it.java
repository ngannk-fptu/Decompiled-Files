/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime;

import java.util.ListResourceBundle;

public class ErrorMessages_it
extends ListResourceBundle {
    @Override
    public Object[][] getContents() {
        return new Object[][]{{"RUN_TIME_INTERNAL_ERR", "Errore run-time interno in ''{0}''"}, {"RUN_TIME_COPY_ERR", "Errore run-time durante l'esecuzione di <xsl:copy>."}, {"DATA_CONVERSION_ERR", "Conversione non valida da ''{0}'' a ''{1}''."}, {"EXTERNAL_FUNC_ERR", "Funzione esterna ''{0}'' non supportata da XSLTC."}, {"EQUALITY_EXPR_ERR", "Tipo di argomento sconosciuto nell'espressione di uguaglianza."}, {"INVALID_ARGUMENT_ERR", "Tipo argomento non valido ''{0}'' nella chiamata a ''{1}''"}, {"FORMAT_NUMBER_ERR", "Tentativo di formattare il numero ''{0}'' utilizzando il modello ''{1}''."}, {"ITERATOR_CLONE_ERR", "Impossibile clonare l''''iteratore ''{0}''."}, {"AXIS_SUPPORT_ERR", "Iteratore per asse ''{0}'' non supportato."}, {"TYPED_AXIS_SUPPORT_ERR", "Iteratore per l''''asse immesso ''{0}'' non sopportato."}, {"STRAY_ATTRIBUTE_ERR", "L''''attributo ''{0}'' al di fuori dell''''elemento."}, {"STRAY_NAMESPACE_ERR", "Dichiarazione dello spazio nome ''{0}''=''{1}'' al di fuori dell''''elemento."}, {"NAMESPACE_PREFIX_ERR", "Lo spazio nomi per il prefisso ''{0}'' non \u00e8 stato dichiarato. "}, {"DOM_ADAPTER_INIT_ERR", "DOMAdapter creato utilizzando il tipo di origine DOM errato."}, {"PARSER_DTD_SUPPORT_ERR", "Il parser SAX utilizzato non gestisce gli eventi di dichiarazione DTD."}, {"NAMESPACES_SUPPORT_ERR", "Il parser SAX utilizzato non dispone del supporto per gli spazi nome XML."}, {"CANT_RESOLVE_RELATIVE_URI_ERR", "Impossibile risolvere il riferimento URI ''{0}''."}, {"UNSUPPORTED_XSL_ERR", "Elemento XSL non supportato ''{0}''"}, {"UNSUPPORTED_EXT_ERR", "Estensione XSLTC non riconosciuta ''{0}''"}, {"UNKNOWN_TRANSLET_VERSION_ERR", "Il translet specificato, ''{0}'', \u00e8 stato creato utilizzando una versione di XSLTC pi\u00f9 recente della versione del run-time XSLTC che \u00e8 in uso. \u00c8 necessario ricompilare il foglio di lavoro oppure utilizzare una versione pi\u00f9 recente di XSLTC per eseguire questo translet."}, {"INVALID_QNAME_ERR", "Un attributo il cui valore deve essere un QName aveva il valore ''{0}''"}, {"INVALID_NCNAME_ERR", "Un attributo il cui valore deve essere un NCName aveva il valore ''{0}''"}, {"UNALLOWED_EXTENSION_FUNCTION_ERR", "L''''utilizzo di una funzione di estensione ''{0}'' non \u00e8 consentito quando la funzione di elaborazione sicura \u00e8 impostata su true."}, {"UNALLOWED_EXTENSION_ELEMENT_ERR", "L''''utilizzo di un elemento di estensione ''{0}'' non \u00e8 consentito quando la funzione di elaborazione sicura \u00e8 impostata su true."}};
    }
}

