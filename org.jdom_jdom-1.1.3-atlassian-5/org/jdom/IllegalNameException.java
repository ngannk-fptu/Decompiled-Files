/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

public class IllegalNameException
extends IllegalArgumentException {
    private static final String CVS_ID = "@(#) $RCSfile: IllegalNameException.java,v $ $Revision: 1.14 $ $Date: 2007/11/10 05:28:59 $ $Name:  $";

    IllegalNameException(String name, String construct, String reason) {
        super(new StringBuffer().append("The name \"").append(name).append("\" is not legal for JDOM/XML ").append(construct).append("s: ").append(reason).append(".").toString());
    }

    IllegalNameException(String name, String construct) {
        super(new StringBuffer().append("The name \"").append(name).append("\" is not legal for JDOM/XML ").append(construct).append("s.").toString());
    }

    public IllegalNameException(String reason) {
        super(reason);
    }
}

