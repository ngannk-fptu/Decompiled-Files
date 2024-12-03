/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime;

import java.util.ListResourceBundle;

public class ErrorMessages_ca
extends ListResourceBundle {
    @Override
    public Object[][] getContents() {
        return new Object[][]{{"RUN_TIME_INTERNAL_ERR", "S''ha produ\u00eft un error intern de temps d''execuci\u00f3 a ''{0}''"}, {"RUN_TIME_COPY_ERR", "Es produeix un error de temps d'execuci\u00f3 en executar <xsl:copy>."}, {"DATA_CONVERSION_ERR", "Conversi\u00f3 no v\u00e0lida de ''{0}'' a ''{1}''."}, {"EXTERNAL_FUNC_ERR", "XSLTC no d\u00f3na suport a la funci\u00f3 externa ''{0}''. "}, {"EQUALITY_EXPR_ERR", "L'expressi\u00f3 d'igualtat cont\u00e9 un tipus d'argument desconegut."}, {"INVALID_ARGUMENT_ERR", "El tipus d''argument ''{0}'' a la crida de ''{1}'' no \u00e9s v\u00e0lid "}, {"FORMAT_NUMBER_ERR", "S''ha intentat formatar el n\u00famero ''{0}'' fent servir el patr\u00f3 ''{1}''."}, {"ITERATOR_CLONE_ERR", "No es pot clonar l''iterador ''{0}''."}, {"AXIS_SUPPORT_ERR", "No est\u00e0 suportat l''iterador de l''eix ''{0}''. "}, {"TYPED_AXIS_SUPPORT_ERR", "No est\u00e0 suportat l''iterador de l''eix escrit ''{0}''. "}, {"STRAY_ATTRIBUTE_ERR", "L''atribut ''{0}'' es troba fora de l''element. "}, {"STRAY_NAMESPACE_ERR", "La declaraci\u00f3 de l''espai de noms ''{0}''=''{1}'' es troba fora de l''element. "}, {"NAMESPACE_PREFIX_ERR", "L''espai de noms del prefix ''{0}'' no s''ha declarat. "}, {"DOM_ADAPTER_INIT_ERR", "DOMAdapter s'ha creat mitjan\u00e7ant un tipus incorrecte de DOM d'origen."}, {"PARSER_DTD_SUPPORT_ERR", "L'analitzador SAX que feu servir no gestiona esdeveniments de declaraci\u00f3 de DTD."}, {"NAMESPACES_SUPPORT_ERR", "L'analitzador SAX que feu servir no d\u00f3na suport a espais de noms XML."}, {"CANT_RESOLVE_RELATIVE_URI_ERR", "No s''ha pogut resoldre la refer\u00e8ncia d''URI ''{0}''."}, {"UNSUPPORTED_XSL_ERR", "L''element XSL ''{0}'' no t\u00e9 suport "}, {"UNSUPPORTED_EXT_ERR", "No es reconeix l''extensi\u00f3 XSLTC ''{0}''"}, {"UNKNOWN_TRANSLET_VERSION_ERR", "La classe translet especificada, ''{0}'', es va crear fent servir una versi\u00f3 d''XSLTC m\u00e9s recent que la versi\u00f3 del temps d''execuci\u00f3 d''XSLTC que ja s''est\u00e0 utilitzant. Heu de recompilar el full d''estil o fer servir una versi\u00f3 m\u00e9s recent d''XSLTC per executar aquesta classe translet."}, {"INVALID_QNAME_ERR", "Un atribut, que ha de tenir el valor QName, tenia el valor ''{0}''"}, {"INVALID_NCNAME_ERR", "Un atribut, que ha de tenir el valor NCName, tenia el valor ''{0}''"}, {"UNALLOWED_EXTENSION_FUNCTION_ERR", "L''\u00fas de la funci\u00f3 d''extensi\u00f3 ''{0}'' no est\u00e0 perm\u00e8s, si la caracter\u00edstica de proc\u00e9s segur s''ha establert en true."}, {"UNALLOWED_EXTENSION_ELEMENT_ERR", "L''\u00fas de l''element d''extensi\u00f3 ''{0}'' no est\u00e0 perm\u00e8s, si la caracter\u00edstica de proc\u00e9s segur s''ha establert en true."}};
    }
}

