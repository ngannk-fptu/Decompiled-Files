/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util.filter;

import javax.xml.namespace.QName;
import org.apache.abdera.filter.ParseFilter;
import org.apache.abdera.util.filter.AbstractParseFilter;

public class SafeContentWhiteListParseFilter
extends AbstractParseFilter
implements ParseFilter {
    private static final long serialVersionUID = -4802312485715572721L;

    public boolean acceptable(QName qname) {
        if (qname.getNamespaceURI().equals("http://www.w3.org/1999/xhtml")) {
            try {
                xhtml_elements.valueOf(qname.getLocalPart().toLowerCase());
                return true;
            }
            catch (Exception exception) {
                return false;
            }
        }
        return true;
    }

    public boolean acceptable(QName qname, QName attribute) {
        if (qname.getNamespaceURI().equals("http://www.w3.org/1999/xhtml")) {
            try {
                xhtml_attributes.valueOf(xhtml_attributes.fix(attribute.getLocalPart().toLowerCase()));
                return true;
            }
            catch (Exception exception) {
                return false;
            }
        }
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum xhtml_attributes {
        abbr,
        accept,
        accept_charset,
        accesskey,
        action,
        align,
        alt,
        axis,
        border,
        cellpadding,
        cellspacing,
        CHAR,
        charoff,
        charset,
        checked,
        cite,
        CLASS,
        clear,
        cols,
        colspan,
        color,
        compact,
        coords,
        datetime,
        dir,
        disabled,
        enctype,
        FOR,
        frame,
        headers,
        height,
        href,
        hreflang,
        hspace,
        id,
        ismap,
        label,
        lang,
        longdesc,
        maxlength,
        media,
        method,
        multiple,
        name,
        nohref,
        noshade,
        nowrap,
        prompt,
        readonly,
        rel,
        rev,
        rows,
        rowspan,
        rules,
        scope,
        selected,
        shape,
        size,
        span,
        src,
        start,
        summary,
        tabindex,
        target,
        title,
        type,
        usemap,
        valign,
        value,
        vspace,
        width;


        static String fix(String v) {
            if (v.equalsIgnoreCase("char")) {
                return "CHAR";
            }
            if (v.equalsIgnoreCase("for")) {
                return "FOR";
            }
            if (v.equalsIgnoreCase("class")) {
                return "CLASS";
            }
            return v.toLowerCase();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum xhtml_elements {
        a,
        abbr,
        acronym,
        address,
        area,
        b,
        bdo,
        big,
        blockquote,
        br,
        button,
        caption,
        center,
        cite,
        code,
        col,
        colgroup,
        dd,
        del,
        dfn,
        dir,
        div,
        dl,
        dt,
        em,
        fieldset,
        font,
        form,
        h1,
        h2,
        h3,
        h4,
        h5,
        h6,
        hr,
        i,
        img,
        input,
        ins,
        kbd,
        label,
        legend,
        li,
        map,
        menu,
        ol,
        optgroup,
        option,
        p,
        pre,
        q,
        s,
        samp,
        select,
        small,
        span,
        strike,
        strong,
        sub,
        sup,
        table,
        tbody,
        td,
        textarea,
        tfoot,
        th,
        thead,
        tr,
        tt,
        u,
        ul,
        var;

    }
}

