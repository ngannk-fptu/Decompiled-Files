/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime;

import java.util.ListResourceBundle;

public class ErrorMessages_no
extends ListResourceBundle {
    @Override
    public Object[][] getContents() {
        return new Object[][]{{"RUN_TIME_INTERNAL_ERR", "Intern programfeil i ''{0}''"}, {"RUN_TIME_COPY_ERR", "Programfeil under utf\u00f8ing av <xsl:copy>."}, {"DATA_CONVERSION_ERR", "Ugyldig konvertering av ''{0}'' fra ''{1}''."}, {"EXTERNAL_FUNC_ERR", "Ekstern funksjon ''{0}'' er ikke st\u00f8ttet av XSLTC."}, {"EQUALITY_EXPR_ERR", "Ugyldig argument i EQUALITY uttrykk."}, {"INVALID_ARGUMENT_ERR", "Ugyldig argument ''{0}'' i kall til ''{1}''"}, {"FORMAT_NUMBER_ERR", "Fors\u00f8k p\u00e5 \u00e5 formattere nummer ''{0}'' med ''{1}''."}, {"ITERATOR_CLONE_ERR", "Kan ikke klone iterator ''{0}''."}, {"AXIS_SUPPORT_ERR", "Iterator for axis ''{0}'' er ikke st\u00e8ttet."}, {"TYPED_AXIS_SUPPORT_ERR", "Iterator for typet axis ''{0}'' er ikke st\u00e8ttet."}, {"STRAY_ATTRIBUTE_ERR", "Attributt ''{0}'' utenfor element."}, {"STRAY_NAMESPACE_ERR", "Navnedeklarasjon ''{0}''=''{1}'' utenfor element."}, {"NAMESPACE_PREFIX_ERR", "Prefix ''{0}'' er ikke deklartert."}, {"DOM_ADAPTER_INIT_ERR", "Fors\u00f8k p\u00e5 \u00e5 instansiere DOMAdapter med feil type DOM."}};
    }
}

