/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.model;

import java.util.Iterator;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFComment;

public interface Comments {
    @Internal
    public void setSheet(Sheet var1);

    public int getNumberOfComments();

    public int getNumberOfAuthors();

    public String getAuthor(long var1);

    public int findAuthor(String var1);

    public XSSFComment findCellComment(CellAddress var1);

    public boolean removeComment(CellAddress var1);

    public Iterator<CellAddress> getCellAddresses();

    public XSSFComment createNewComment(ClientAnchor var1);

    public void referenceUpdated(CellAddress var1, XSSFComment var2);

    public void commentUpdated(XSSFComment var1);
}

