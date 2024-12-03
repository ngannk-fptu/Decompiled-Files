/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime;

import java.util.ListResourceBundle;

public class ErrorMessages_sl
extends ListResourceBundle {
    @Override
    public Object[][] getContents() {
        return new Object[][]{{"RUN_TIME_INTERNAL_ERR", "Notranja napaka izvajanja v ''{0}''"}, {"RUN_TIME_COPY_ERR", "Notranja napaka izvajanja pri izvajanju <xsl:copy>."}, {"DATA_CONVERSION_ERR", "Neveljavna pretvorba iz ''{0}'' v ''{1}''."}, {"EXTERNAL_FUNC_ERR", "XSLTC ne podpira zunanje funkcije ''{0}''."}, {"EQUALITY_EXPR_ERR", "Neznan tip argumenta v izrazu enakovrednosti."}, {"INVALID_ARGUMENT_ERR", "Neveljavna vrsta argumenta ''{0}'' pri klicu na ''{1}''"}, {"FORMAT_NUMBER_ERR", "Pokus nastavitve formata \u0161tevilke ''{0}'' z uporabo vzorca ''{1}''."}, {"ITERATOR_CLONE_ERR", "Iteratorja ''{0}'' ni mogo\u010de klonirati."}, {"AXIS_SUPPORT_ERR", "Iterator za os ''{0}'' ni podprt."}, {"TYPED_AXIS_SUPPORT_ERR", "Iterator za tipizirano os ''{0}'' ni podprt."}, {"STRAY_ATTRIBUTE_ERR", "Atribut ''{0}'' zunaj elementa."}, {"STRAY_NAMESPACE_ERR", "Deklaracija imenskega prostora ''{0}''=''{1}'' je zunaj elementa."}, {"NAMESPACE_PREFIX_ERR", "Imenski prostor za predpono ''{0}'' ni bil naveden."}, {"DOM_ADAPTER_INIT_ERR", "DOMAdapter ustvarjen z uporabo napa\u010dnega tipa izvornega DOM."}, {"PARSER_DTD_SUPPORT_ERR", "Uporabljeni raz\u010dlenjevalnik SAX ne obravnava dogodkov deklaracije DTD."}, {"NAMESPACES_SUPPORT_ERR", "Uporabljeni raz\u010dlenjevalnik SAX ne podpira imenskih prostorov XML."}, {"CANT_RESOLVE_RELATIVE_URI_ERR", "Ni mogo\u010de razre\u0161iti sklica URI ''{0}''."}, {"UNSUPPORTED_XSL_ERR", "Nepodprt XSL element ''{0}''"}, {"UNSUPPORTED_EXT_ERR", "Neprepoznana raz\u0161iritev XSLTC ''{0}''"}, {"UNKNOWN_TRANSLET_VERSION_ERR", "Navedeni translet, ''{0}'', je bil ustvarjen z uporabo XSLTC novej\u0161e razli\u010dice, kot je trenutno uporabljana razli\u010dica izvajalnega okolja XSLTC. Slogovno datoteko morate ponovno prevesti ali pa uporabiti novej\u0161o razli\u010dico XSLTC-ja, da bi zagnali ta translet."}, {"INVALID_QNAME_ERR", "Atribut, katerega vrednost mora biti QName, je imel vrednost ''{0}''"}, {"INVALID_NCNAME_ERR", "Atribut, katerega vrednost mora biti NCName, je imel vrednost ''{0}''"}, {"UNALLOWED_EXTENSION_FUNCTION_ERR", "Uporaba raz\u0161iritvene funkcije ''{0}'' ni dovoljena, ko je funkcija varne obdelave nastavljena na True."}, {"UNALLOWED_EXTENSION_ELEMENT_ERR", "Uporaba raz\u0161iritvene elementa ''{0}'' ni dovoljena, ko je funkcija varne obdelave nastavljena na True."}};
    }
}

