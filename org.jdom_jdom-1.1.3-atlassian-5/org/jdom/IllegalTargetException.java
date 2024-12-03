/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

public class IllegalTargetException
extends IllegalArgumentException {
    private static final String CVS_ID = "@(#) $RCSfile: IllegalTargetException.java,v $ $Revision: 1.15 $ $Date: 2007/11/10 05:28:59 $ $Name:  $";

    IllegalTargetException(String target, String reason) {
        super(new StringBuffer().append("The target \"").append(target).append("\" is not legal for JDOM/XML Processing Instructions: ").append(reason).append(".").toString());
    }

    public IllegalTargetException(String reason) {
        super(reason);
    }
}

