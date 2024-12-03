/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAPHeaderBlock;

public interface SOAPHeader
extends OMElement {
    public SOAPHeaderBlock addHeaderBlock(String var1, OMNamespace var2) throws OMException;

    public Iterator getHeadersToProcess(RolePlayer var1);

    public Iterator examineHeaderBlocks(String var1);

    public Iterator extractHeaderBlocks(String var1);

    public Iterator examineMustUnderstandHeaderBlocks(String var1);

    public Iterator examineAllHeaderBlocks();

    public Iterator extractAllHeaderBlocks();

    public ArrayList getHeaderBlocksWithNSURI(String var1);

    public Iterator getHeadersToProcess(RolePlayer var1, String var2);
}

