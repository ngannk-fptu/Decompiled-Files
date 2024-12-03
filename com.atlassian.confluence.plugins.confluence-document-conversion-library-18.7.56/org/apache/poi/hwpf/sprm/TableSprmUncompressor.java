/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hwpf.sprm.SprmBuffer;
import org.apache.poi.hwpf.sprm.SprmIterator;
import org.apache.poi.hwpf.sprm.SprmOperation;
import org.apache.poi.hwpf.sprm.SprmUncompressor;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.TableCellDescriptor;
import org.apache.poi.hwpf.usermodel.TableProperties;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class TableSprmUncompressor
extends SprmUncompressor {
    private static final Logger LOG = LogManager.getLogger(TableSprmUncompressor.class);

    public static TableProperties uncompressTAP(SprmBuffer sprmBuffer) {
        TableProperties tableProperties;
        SprmOperation sprmOperation = sprmBuffer.findSprm((short)-10744);
        if (sprmOperation != null) {
            byte[] grpprl = sprmOperation.getGrpprl();
            int offset = sprmOperation.getGrpprlOffset();
            short itcMac = grpprl[offset];
            tableProperties = new TableProperties(itcMac);
        } else {
            LOG.atWarn().log("Some table rows didn't specify number of columns in SPRMs");
            tableProperties = new TableProperties(1);
        }
        SprmIterator iterator = sprmBuffer.iterator();
        while (iterator.hasNext()) {
            SprmOperation sprm = iterator.next();
            if (sprm.getType() != 5) continue;
            try {
                TableSprmUncompressor.unCompressTAPOperation(tableProperties, sprm);
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                LOG.atError().withThrowable(ex).log("Unable to apply {}", (Object)sprm);
            }
        }
        return tableProperties;
    }

    static void unCompressTAPOperation(TableProperties newTAP, SprmOperation sprm) {
        switch (sprm.getOperation()) {
            case 0: {
                newTAP.setJc((short)sprm.getOperand());
                break;
            }
            case 1: {
                short[] rgdxaCenter = newTAP.getRgdxaCenter();
                int itcMac = newTAP.getItcMac();
                int adjust = sprm.getOperand() - (rgdxaCenter[0] + newTAP.getDxaGapHalf());
                int x = 0;
                while (x < itcMac) {
                    int n = x++;
                    rgdxaCenter[n] = (short)(rgdxaCenter[n] + (short)adjust);
                }
                break;
            }
            case 2: {
                short[] rgdxaCenter = newTAP.getRgdxaCenter();
                if (rgdxaCenter != null) {
                    int adjust = newTAP.getDxaGapHalf() - sprm.getOperand();
                    rgdxaCenter[0] = (short)(rgdxaCenter[0] + (short)adjust);
                }
                newTAP.setDxaGapHalf(sprm.getOperand());
                break;
            }
            case 3: {
                newTAP.setFCantSplit(TableSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 4: {
                newTAP.setFTableHeader(TableSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 5: {
                byte[] buf = sprm.getGrpprl();
                int offset = sprm.getGrpprlOffset();
                newTAP.setBrcTop(new BorderCode(buf, offset));
                newTAP.setBrcLeft(new BorderCode(buf, offset += 4));
                newTAP.setBrcBottom(new BorderCode(buf, offset += 4));
                newTAP.setBrcRight(new BorderCode(buf, offset += 4));
                newTAP.setBrcHorizontal(new BorderCode(buf, offset += 4));
                newTAP.setBrcVertical(new BorderCode(buf, offset += 4));
                break;
            }
            case 6: {
                break;
            }
            case 7: {
                newTAP.setDyaRowHeight(sprm.getOperand());
                break;
            }
            case 8: {
                byte[] grpprl = sprm.getGrpprl();
                int offset = sprm.getGrpprlOffset();
                int itcMac = grpprl[offset];
                short[] rgdxaCenter = new short[itcMac + 1];
                TableCellDescriptor[] rgtc = new TableCellDescriptor[itcMac];
                newTAP.setItcMac((short)itcMac);
                newTAP.setRgdxaCenter(rgdxaCenter);
                newTAP.setRgtc(rgtc);
                for (int x = 0; x < itcMac; ++x) {
                    rgdxaCenter[x] = LittleEndian.getShort(grpprl, offset + (1 + x * 2));
                }
                int startOfTCs = offset + (1 + (itcMac + 1) * 2);
                int endOfSprm = offset + sprm.size() - 6;
                boolean hasTCs = startOfTCs < endOfSprm;
                for (int x = 0; x < itcMac; ++x) {
                    rgtc[x] = hasTCs && offset + (1 + (itcMac + 1) * 2 + x * 20) < grpprl.length ? TableCellDescriptor.convertBytesToTC(grpprl, offset + (1 + (itcMac + 1) * 2 + x * 20)) : new TableCellDescriptor();
                }
                rgdxaCenter[itcMac] = LittleEndian.getShort(grpprl, offset + (1 + itcMac * 2));
                break;
            }
            case 9: {
                break;
            }
            case 10: {
                break;
            }
            case 32: {
                break;
            }
            case 33: {
                int param = sprm.getOperand();
                int index = (param & 0xFF000000) >> 24;
                int count = (param & 0xFF0000) >> 16;
                int width = param & 0xFFFF;
                short itcMac = newTAP.getItcMac();
                short[] rgdxaCenter = new short[itcMac + count + 1];
                TableCellDescriptor[] rgtc = new TableCellDescriptor[itcMac + count];
                if (index >= itcMac) {
                    index = itcMac;
                    System.arraycopy(newTAP.getRgdxaCenter(), 0, rgdxaCenter, 0, itcMac + 1);
                    System.arraycopy(newTAP.getRgtc(), 0, rgtc, 0, itcMac);
                } else {
                    System.arraycopy(newTAP.getRgdxaCenter(), 0, rgdxaCenter, 0, index + 1);
                    System.arraycopy(newTAP.getRgdxaCenter(), index + 1, rgdxaCenter, index + count, itcMac - index);
                    System.arraycopy(newTAP.getRgtc(), 0, rgtc, 0, index);
                    System.arraycopy(newTAP.getRgtc(), index, rgtc, index + count, itcMac - index);
                }
                for (int x = index; x < index + count; ++x) {
                    rgtc[x] = new TableCellDescriptor();
                    rgdxaCenter[x] = (short)(rgdxaCenter[x - 1] + width);
                }
                rgdxaCenter[index + count] = (short)(rgdxaCenter[index + count - 1] + width);
                break;
            }
            case 34: 
            case 35: 
            case 36: 
            case 37: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 44: {
                break;
            }
            case 52: {
                int itcFirst = sprm.getGrpprl()[sprm.getGrpprlOffset()];
                byte itcLim = sprm.getGrpprl()[sprm.getGrpprlOffset() + 1];
                byte grfbrc = sprm.getGrpprl()[sprm.getGrpprlOffset() + 2];
                byte ftsWidth = sprm.getGrpprl()[sprm.getGrpprlOffset() + 3];
                short wWidth = LittleEndian.getShort(sprm.getGrpprl(), sprm.getGrpprlOffset() + 4);
                for (int c = itcFirst; c < itcLim; ++c) {
                    TableCellDescriptor tableCellDescriptor = newTAP.getRgtc()[c];
                    if ((grfbrc & 1) != 0) {
                        tableCellDescriptor.setFtsCellPaddingTop(ftsWidth);
                        tableCellDescriptor.setWCellPaddingTop(wWidth);
                    }
                    if ((grfbrc & 2) != 0) {
                        tableCellDescriptor.setFtsCellPaddingLeft(ftsWidth);
                        tableCellDescriptor.setWCellPaddingLeft(wWidth);
                    }
                    if ((grfbrc & 4) != 0) {
                        tableCellDescriptor.setFtsCellPaddingBottom(ftsWidth);
                        tableCellDescriptor.setWCellPaddingBottom(wWidth);
                    }
                    if ((grfbrc & 8) == 0) continue;
                    tableCellDescriptor.setFtsCellPaddingRight(ftsWidth);
                    tableCellDescriptor.setWCellPaddingRight(wWidth);
                }
                break;
            }
        }
    }
}

