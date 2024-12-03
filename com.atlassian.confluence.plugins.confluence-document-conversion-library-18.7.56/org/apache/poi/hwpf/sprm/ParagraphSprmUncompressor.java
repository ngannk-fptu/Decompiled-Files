/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.model.TabDescriptor;
import org.apache.poi.hwpf.model.types.TBDAbstractType;
import org.apache.poi.hwpf.sprm.SprmIterator;
import org.apache.poi.hwpf.sprm.SprmOperation;
import org.apache.poi.hwpf.sprm.SprmUncompressor;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.DateAndTime;
import org.apache.poi.hwpf.usermodel.DropCapSpecifier;
import org.apache.poi.hwpf.usermodel.LineSpacingDescriptor;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor80;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class ParagraphSprmUncompressor
extends SprmUncompressor {
    private static final Logger LOG = LogManager.getLogger(ParagraphSprmUncompressor.class);

    public static ParagraphProperties uncompressPAP(ParagraphProperties parent, byte[] grpprl, int offset) {
        ParagraphProperties newProperties = parent.copy();
        SprmIterator sprmIt = new SprmIterator(grpprl, offset);
        while (sprmIt.hasNext()) {
            SprmOperation sprm = sprmIt.next();
            if (sprm.getType() != 1) continue;
            try {
                ParagraphSprmUncompressor.unCompressPAPOperation(newProperties, sprm);
            }
            catch (Exception exc) {
                LOG.atError().withThrowable(exc).log("Unable to apply SPRM operation '{}'", (Object)Unbox.box(sprm.getOperation()));
            }
        }
        return newProperties;
    }

    static void unCompressPAPOperation(ParagraphProperties newPAP, SprmOperation sprm) {
        switch (sprm.getOperation()) {
            case 0: {
                newPAP.setIstd(sprm.getOperand());
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                if (newPAP.getIstd() > 9 && newPAP.getIstd() < 1) break;
                byte paramTmp = (byte)sprm.getOperand();
                newPAP.setIstd(newPAP.getIstd() + paramTmp);
                newPAP.setLvl((byte)(newPAP.getLvl() + paramTmp));
                if ((paramTmp >> 7 & 1) == 1) {
                    newPAP.setIstd(Math.max(newPAP.getIstd(), 1));
                    break;
                }
                newPAP.setIstd(Math.min(newPAP.getIstd(), 9));
                break;
            }
            case 3: {
                newPAP.setJc((byte)sprm.getOperand());
                break;
            }
            case 4: {
                newPAP.setFSideBySide(sprm.getOperand() != 0);
                break;
            }
            case 5: {
                newPAP.setFKeep(sprm.getOperand() != 0);
                break;
            }
            case 6: {
                newPAP.setFKeepFollow(sprm.getOperand() != 0);
                break;
            }
            case 7: {
                newPAP.setFPageBreakBefore(sprm.getOperand() != 0);
                break;
            }
            case 8: {
                newPAP.setBrcl((byte)sprm.getOperand());
                break;
            }
            case 9: {
                newPAP.setBrcp((byte)sprm.getOperand());
                break;
            }
            case 10: {
                newPAP.setIlvl((byte)sprm.getOperand());
                break;
            }
            case 11: {
                newPAP.setIlfo(sprm.getOperandShortSigned());
                break;
            }
            case 12: {
                newPAP.setFNoLnn(sprm.getOperand() != 0);
                break;
            }
            case 13: {
                ParagraphSprmUncompressor.handleTabs(newPAP, sprm);
                break;
            }
            case 14: {
                newPAP.setDxaRight(sprm.getOperand());
                break;
            }
            case 15: {
                newPAP.setDxaLeft(sprm.getOperand());
                break;
            }
            case 16: {
                newPAP.setDxaLeft(newPAP.getDxaLeft() + sprm.getOperand());
                newPAP.setDxaLeft(Math.max(0, newPAP.getDxaLeft()));
                break;
            }
            case 17: {
                newPAP.setDxaLeft1(sprm.getOperand());
                break;
            }
            case 18: {
                newPAP.setLspd(new LineSpacingDescriptor(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 19: {
                newPAP.setDyaBefore(sprm.getOperand());
                break;
            }
            case 20: {
                newPAP.setDyaAfter(sprm.getOperand());
                break;
            }
            case 21: {
                break;
            }
            case 22: {
                newPAP.setFInTable(sprm.getOperand() != 0);
                break;
            }
            case 23: {
                newPAP.setFTtp(sprm.getOperand() != 0);
                break;
            }
            case 24: {
                newPAP.setDxaAbs(sprm.getOperand());
                break;
            }
            case 25: {
                newPAP.setDyaAbs(sprm.getOperand());
                break;
            }
            case 26: {
                newPAP.setDxaWidth(sprm.getOperand());
                break;
            }
            case 27: {
                byte param = (byte)sprm.getOperand();
                byte pcVert = (byte)((param & 0xC) >> 2);
                byte pcHorz = (byte)(param & 3);
                if (pcVert != 3) {
                    newPAP.setPcVert(pcVert);
                }
                if (pcHorz == 3) break;
                newPAP.setPcHorz(pcHorz);
                break;
            }
            case 34: {
                newPAP.setDxaFromText(sprm.getOperand());
                break;
            }
            case 35: {
                newPAP.setWr((byte)sprm.getOperand());
                break;
            }
            case 36: {
                newPAP.setBrcTop(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 37: {
                newPAP.setBrcLeft(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 38: {
                newPAP.setBrcBottom(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 39: {
                newPAP.setBrcRight(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 40: {
                newPAP.setBrcBetween(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 41: {
                newPAP.setBrcBar(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 42: {
                newPAP.setFNoAutoHyph(sprm.getOperand() != 0);
                break;
            }
            case 43: {
                newPAP.setDyaHeight(sprm.getOperand());
                break;
            }
            case 44: {
                newPAP.setDcs(new DropCapSpecifier((short)sprm.getOperand()));
                break;
            }
            case 45: {
                newPAP.setShd(new ShadingDescriptor80((short)sprm.getOperand()).toShadingDescriptor());
                break;
            }
            case 46: {
                newPAP.setDyaFromText(sprm.getOperand());
                break;
            }
            case 47: {
                newPAP.setDxaFromText(sprm.getOperand());
                break;
            }
            case 48: {
                newPAP.setFLocked(sprm.getOperand() != 0);
                break;
            }
            case 49: {
                newPAP.setFWidowControl(sprm.getOperand() != 0);
                break;
            }
            case 51: {
                newPAP.setFKinsoku(sprm.getOperand() != 0);
                break;
            }
            case 52: {
                newPAP.setFWordWrap(sprm.getOperand() != 0);
                break;
            }
            case 53: {
                newPAP.setFOverflowPunct(sprm.getOperand() != 0);
                break;
            }
            case 54: {
                newPAP.setFTopLinePunct(sprm.getOperand() != 0);
                break;
            }
            case 55: {
                newPAP.setFAutoSpaceDE(sprm.getOperand() != 0);
                break;
            }
            case 56: {
                newPAP.setFAutoSpaceDN(sprm.getOperand() != 0);
                break;
            }
            case 57: {
                newPAP.setWAlignFont(sprm.getOperand());
                break;
            }
            case 58: {
                newPAP.setFontAlign((short)sprm.getOperand());
                break;
            }
            case 59: {
                break;
            }
            case 62: {
                byte[] buf = Arrays.copyOfRange(sprm.getGrpprl(), sprm.getGrpprlOffset(), sprm.getGrpprlOffset() + (sprm.size() - 3));
                newPAP.setAnld(buf);
                break;
            }
            case 63: {
                byte[] varParam = sprm.getGrpprl();
                int offset = sprm.getGrpprlOffset();
                newPAP.setFPropRMark(varParam[offset] != 0);
                newPAP.setIbstPropRMark(LittleEndian.getShort(varParam, offset + 1));
                newPAP.setDttmPropRMark(new DateAndTime(varParam, offset + 3));
                break;
            }
            case 64: {
                newPAP.setLvl((byte)sprm.getOperand());
                break;
            }
            case 65: {
                newPAP.setFBiDi(sprm.getOperand() != 0);
                break;
            }
            case 67: {
                newPAP.setFNumRMIns(sprm.getOperand() != 0);
                break;
            }
            case 68: {
                break;
            }
            case 69: {
                if (sprm.getSizeCode() != 6) break;
                byte[] buf = new byte[sprm.size() - 3];
                System.arraycopy(buf, 0, sprm.getGrpprl(), sprm.getGrpprlOffset(), buf.length);
                newPAP.setNumrm(buf);
                break;
            }
            case 71: {
                newPAP.setFUsePgsuSettings(sprm.getOperand() != 0);
                break;
            }
            case 72: {
                newPAP.setFAdjustRight(sprm.getOperand() != 0);
                break;
            }
            case 73: {
                newPAP.setItap(sprm.getOperand());
                break;
            }
            case 74: {
                newPAP.setItap((byte)(newPAP.getItap() + sprm.getOperand()));
                break;
            }
            case 75: {
                newPAP.setFInnerTableCell(sprm.getOperand() != 0);
                break;
            }
            case 76: {
                newPAP.setFTtpEmbedded(sprm.getOperand() != 0);
                break;
            }
            case 77: {
                ShadingDescriptor shadingDescriptor = new ShadingDescriptor(sprm.getGrpprl(), 3);
                newPAP.setShading(shadingDescriptor);
                break;
            }
            case 93: {
                newPAP.setDxaRight(sprm.getOperand());
                break;
            }
            case 94: {
                newPAP.setDxaLeft(sprm.getOperand());
                break;
            }
            case 96: {
                newPAP.setDxaLeft1(sprm.getOperand());
                break;
            }
            case 97: {
                newPAP.setJustificationLogical((byte)sprm.getOperand());
                break;
            }
            case 103: {
                newPAP.setRsid(sprm.getOperand());
                break;
            }
            default: {
                LOG.atDebug().log("Unknown PAP sprm ignored: {}", (Object)sprm);
            }
        }
    }

    private static void handleTabs(ParagraphProperties pap, SprmOperation sprm) {
        int x;
        byte[] grpprl = sprm.getGrpprl();
        int offset = sprm.getGrpprlOffset();
        int delSize = grpprl[offset++];
        int[] tabPositions = pap.getRgdxaTab();
        TabDescriptor[] tabDescriptors = pap.getRgtbd();
        HashMap<Integer, TabDescriptor> tabMap = new HashMap<Integer, TabDescriptor>();
        for (x = 0; x < tabPositions.length; ++x) {
            tabMap.put(tabPositions[x], tabDescriptors[x]);
        }
        for (x = 0; x < delSize; ++x) {
            tabMap.remove(LittleEndian.getShort(grpprl, offset));
            offset += 2;
        }
        int addSize = grpprl[offset++];
        int start = offset;
        for (int x2 = 0; x2 < addSize; ++x2) {
            Integer key = LittleEndian.getShort(grpprl, offset);
            TabDescriptor val = new TabDescriptor(grpprl, start + (TBDAbstractType.getSize() * addSize + x2));
            tabMap.put(key, val);
            offset += 2;
        }
        tabPositions = new int[tabMap.size()];
        tabDescriptors = new TabDescriptor[tabPositions.length];
        ArrayList list = new ArrayList(tabMap.keySet());
        Collections.sort(list);
        for (int x3 = 0; x3 < tabPositions.length; ++x3) {
            Integer key = (Integer)list.get(x3);
            tabPositions[x3] = key;
            tabDescriptors[x3] = tabMap.containsKey(key) ? (TabDescriptor)tabMap.get(key) : new TabDescriptor();
        }
        pap.setRgdxaTab(tabPositions);
        pap.setRgtbd(tabDescriptors);
    }
}

