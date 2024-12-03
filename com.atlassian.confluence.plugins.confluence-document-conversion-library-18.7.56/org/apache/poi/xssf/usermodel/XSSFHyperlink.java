/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlink;

public class XSSFHyperlink
implements Hyperlink,
Duplicatable {
    private final HyperlinkType _type;
    private final PackageRelationship _externalRel;
    private final CTHyperlink _ctHyperlink;
    private String _location;

    protected XSSFHyperlink(HyperlinkType type) {
        this._type = type;
        this._ctHyperlink = CTHyperlink.Factory.newInstance();
        this._externalRel = null;
    }

    protected XSSFHyperlink(CTHyperlink ctHyperlink, PackageRelationship hyperlinkRel) {
        this._ctHyperlink = ctHyperlink;
        this._externalRel = hyperlinkRel;
        if (this._externalRel == null) {
            if (ctHyperlink.getLocation() != null) {
                this._type = HyperlinkType.DOCUMENT;
                this._location = ctHyperlink.getLocation();
            } else {
                if (ctHyperlink.getId() != null) {
                    throw new IllegalStateException("The hyperlink for cell " + ctHyperlink.getRef() + " references relation " + ctHyperlink.getId() + ", but that didn't exist!");
                }
                this._type = HyperlinkType.DOCUMENT;
            }
        } else {
            URI target = this._externalRel.getTargetURI();
            this._location = target.toString();
            if (ctHyperlink.getLocation() != null) {
                this._location = this._location + "#" + ctHyperlink.getLocation();
            }
            this._type = this._location.startsWith("http://") || this._location.startsWith("https://") || this._location.startsWith("ftp://") ? HyperlinkType.URL : (this._location.startsWith("mailto:") ? HyperlinkType.EMAIL : HyperlinkType.FILE);
        }
    }

    @Internal
    public XSSFHyperlink(Hyperlink other) {
        if (other instanceof XSSFHyperlink) {
            XSSFHyperlink xlink = (XSSFHyperlink)other;
            this._type = xlink.getType();
            this._location = xlink._location;
            this._externalRel = xlink._externalRel;
            this._ctHyperlink = (CTHyperlink)xlink._ctHyperlink.copy();
        } else {
            this._type = other.getType();
            this._location = other.getAddress();
            this._externalRel = null;
            this._ctHyperlink = CTHyperlink.Factory.newInstance();
            this._ctHyperlink.setDisplay(other.getLabel());
            this.setFirstColumn(other.getFirstColumn());
            this.setLastColumn(other.getLastColumn());
            this.setFirstRow(other.getFirstRow());
            this.setLastRow(other.getLastRow());
        }
    }

    @Internal
    public CTHyperlink getCTHyperlink() {
        return this._ctHyperlink;
    }

    public boolean needsRelationToo() {
        return this._type != HyperlinkType.DOCUMENT;
    }

    protected void generateRelationIfNeeded(PackagePart sheetPart) {
        if (this._externalRel == null && this.needsRelationToo()) {
            PackageRelationship rel = sheetPart.addExternalRelationship(this._location, XSSFRelation.SHEET_HYPERLINKS.getRelation());
            this._ctHyperlink.setId(rel.getId());
        }
    }

    @Override
    public HyperlinkType getType() {
        return this._type;
    }

    public String getCellRef() {
        return this._ctHyperlink.getRef();
    }

    @Override
    public String getAddress() {
        return this._location;
    }

    @Override
    public String getLabel() {
        return this._ctHyperlink.getDisplay();
    }

    public String getLocation() {
        return this._ctHyperlink.getLocation();
    }

    @Override
    public void setLabel(String label) {
        this._ctHyperlink.setDisplay(label);
    }

    public void setLocation(String location) {
        this._ctHyperlink.setLocation(location);
    }

    @Override
    public void setAddress(String address) {
        this.validate(address);
        this._location = address;
        if (this._type == HyperlinkType.DOCUMENT) {
            this.setLocation(address);
        }
    }

    private void validate(String address) {
        switch (this._type) {
            case EMAIL: 
            case FILE: 
            case URL: {
                try {
                    new URI(address);
                    break;
                }
                catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Address of hyperlink must be a valid URI", e);
                }
            }
            case DOCUMENT: {
                break;
            }
            default: {
                throw new IllegalStateException("Invalid Hyperlink type: " + (Object)((Object)this._type));
            }
        }
    }

    @Internal
    public void setCellReference(String ref) {
        this._ctHyperlink.setRef(ref);
    }

    @Internal
    public void setCellReference(CellReference ref) {
        this.setCellReference(ref.formatAsString());
    }

    private CellReference buildFirstCellReference() {
        return this.buildCellReference(false);
    }

    private CellReference buildLastCellReference() {
        return this.buildCellReference(true);
    }

    private CellReference buildCellReference(boolean lastCell) {
        String ref = this._ctHyperlink.getRef();
        if (ref == null) {
            ref = "A1";
        }
        if (ref.contains(":")) {
            AreaReference area = new AreaReference(ref, SpreadsheetVersion.EXCEL2007);
            return lastCell ? area.getLastCell() : area.getFirstCell();
        }
        return new CellReference(ref);
    }

    @Override
    public int getFirstColumn() {
        return this.buildFirstCellReference().getCol();
    }

    @Override
    public int getLastColumn() {
        return this.buildLastCellReference().getCol();
    }

    @Override
    public int getFirstRow() {
        return this.buildFirstCellReference().getRow();
    }

    @Override
    public int getLastRow() {
        return this.buildLastCellReference().getRow();
    }

    @Override
    public void setFirstColumn(int col) {
        int lastColumn = this.getLastColumn();
        if (col > lastColumn) {
            lastColumn = col;
        }
        String firstCellRef = CellReference.convertNumToColString(col) + (this.getFirstRow() + 1);
        String lastCellRef = CellReference.convertNumToColString(lastColumn) + (this.getLastRow() + 1);
        this.setCellRange(firstCellRef + ":" + lastCellRef);
    }

    @Override
    public void setLastColumn(int col) {
        int firstColumn = this.getFirstColumn();
        if (col < firstColumn) {
            firstColumn = col;
        }
        String firstCellRef = CellReference.convertNumToColString(firstColumn) + (this.getFirstRow() + 1);
        String lastCellRef = CellReference.convertNumToColString(col) + (this.getLastRow() + 1);
        this.setCellRange(firstCellRef + ":" + lastCellRef);
    }

    @Override
    public void setFirstRow(int row) {
        int lastRow = this.getLastRow();
        if (row > lastRow) {
            lastRow = row;
        }
        String firstCellRef = CellReference.convertNumToColString(this.getFirstColumn()) + (row + 1);
        String lastCellRef = CellReference.convertNumToColString(this.getLastColumn()) + (lastRow + 1);
        this.setCellRange(firstCellRef + ":" + lastCellRef);
    }

    @Override
    public void setLastRow(int row) {
        int firstRow = this.getFirstRow();
        if (row < firstRow) {
            firstRow = row;
        }
        String firstCellRef = CellReference.convertNumToColString(this.getFirstColumn()) + (firstRow + 1);
        String lastCellRef = CellReference.convertNumToColString(this.getLastColumn()) + (row + 1);
        this.setCellRange(firstCellRef + ":" + lastCellRef);
    }

    private void setCellRange(String range) {
        AreaReference ref = new AreaReference(range, SpreadsheetVersion.EXCEL2007);
        if (ref.isSingleCell()) {
            this.setCellReference(ref.getFirstCell());
        } else {
            this.setCellReference(ref.formatAsString());
        }
    }

    public String getTooltip() {
        return this._ctHyperlink.getTooltip();
    }

    public void setTooltip(String text) {
        this._ctHyperlink.setTooltip(text);
    }

    @Override
    public Duplicatable copy() {
        return new XSSFHyperlink(this);
    }
}

