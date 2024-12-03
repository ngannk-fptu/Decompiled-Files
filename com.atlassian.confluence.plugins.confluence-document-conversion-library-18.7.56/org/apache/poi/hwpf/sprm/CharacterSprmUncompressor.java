/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hwpf.model.Colorref;
import org.apache.poi.hwpf.model.Hyphenation;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.sprm.SprmIterator;
import org.apache.poi.hwpf.sprm.SprmOperation;
import org.apache.poi.hwpf.sprm.SprmUncompressor;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.CharacterProperties;
import org.apache.poi.hwpf.usermodel.DateAndTime;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor;
import org.apache.poi.hwpf.usermodel.ShadingDescriptor80;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class CharacterSprmUncompressor
extends SprmUncompressor {
    private static final Logger LOG = LogManager.getLogger(CharacterSprmUncompressor.class);

    @Deprecated
    public static CharacterProperties uncompressCHP(CharacterProperties parent, byte[] grpprl, int offset) {
        CharacterProperties newProperties = parent.copy();
        CharacterSprmUncompressor.applySprms(parent, grpprl, offset, true, newProperties);
        return newProperties;
    }

    public static CharacterProperties uncompressCHP(StyleSheet styleSheet, CharacterProperties parStyle, byte[] grpprl, int offset) {
        CharacterProperties newProperties;
        if (parStyle == null) {
            parStyle = new CharacterProperties();
            newProperties = new CharacterProperties();
        } else {
            newProperties = parStyle.copy();
        }
        Integer style = CharacterSprmUncompressor.getIstd(grpprl, offset);
        if (style != null) {
            try {
                CharacterSprmUncompressor.applySprms(parStyle, styleSheet.getCHPX(style), 0, false, newProperties);
            }
            catch (Exception exc) {
                LOG.atError().withThrowable(exc).log("Unable to apply all style {} CHP SPRMs to CHP", (Object)style);
            }
        }
        CharacterProperties styleProperties = newProperties;
        newProperties = styleProperties.copy();
        try {
            CharacterSprmUncompressor.applySprms(styleProperties, grpprl, offset, true, newProperties);
        }
        catch (Exception exc) {
            LOG.atError().withThrowable(exc).log("Unable to process all direct CHP SPRMs");
        }
        return newProperties;
    }

    private static void applySprms(CharacterProperties parentProperties, byte[] grpprl, int offset, boolean warnAboutNonChpSprms, CharacterProperties targetProperties) {
        SprmIterator sprmIt = new SprmIterator(grpprl, offset);
        while (sprmIt.hasNext()) {
            SprmOperation sprm = sprmIt.next();
            if (sprm.getType() != 2) {
                if (!warnAboutNonChpSprms) continue;
                LOG.atWarn().log("Non-CHP SPRM returned by SprmIterator: {}", (Object)sprm);
                continue;
            }
            CharacterSprmUncompressor.unCompressCHPOperation(parentProperties, targetProperties, sprm);
        }
    }

    private static Integer getIstd(byte[] grpprl, int offset) {
        Integer style = null;
        try {
            SprmIterator sprmIt = new SprmIterator(grpprl, offset);
            while (sprmIt.hasNext()) {
                SprmOperation sprm = sprmIt.next();
                if (sprm.getType() != 2 || sprm.getOperation() != 48) continue;
                style = sprm.getOperand();
            }
        }
        catch (Exception exc) {
            LOG.atError().withThrowable(exc).log("Unable to extract istd from direct CHP SPRM");
        }
        return style;
    }

    static void unCompressCHPOperation(CharacterProperties oldCHP, CharacterProperties newCHP, SprmOperation sprm) {
        switch (sprm.getOperation()) {
            case 0: {
                newCHP.setFRMarkDel(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 1: {
                newCHP.setFRMark(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 2: {
                newCHP.setFFldVanish(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 3: {
                newCHP.setFcPic(sprm.getOperand());
                newCHP.setFSpec(true);
                break;
            }
            case 4: {
                newCHP.setIbstRMark((short)sprm.getOperand());
                break;
            }
            case 5: {
                newCHP.setDttmRMark(new DateAndTime(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 6: {
                newCHP.setFData(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 7: {
                break;
            }
            case 8: {
                int operand = sprm.getOperand();
                short chsDiff = (short)(operand & 0xFF);
                newCHP.setFChsDiff(CharacterSprmUncompressor.getFlag(chsDiff));
                newCHP.setChse((short)(operand & 0xFFFF00));
                break;
            }
            case 9: {
                newCHP.setFSpec(true);
                newCHP.setFtcSym(LittleEndian.getShort(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                newCHP.setXchSym(LittleEndian.getShort(sprm.getGrpprl(), sprm.getGrpprlOffset() + 2));
                break;
            }
            case 10: {
                newCHP.setFOle2(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 11: {
                break;
            }
            case 12: {
                newCHP.setIcoHighlight((byte)sprm.getOperand());
                newCHP.setFHighlight(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 13: {
                break;
            }
            case 14: {
                newCHP.setFcObj(sprm.getOperand());
                break;
            }
            case 15: {
                break;
            }
            case 16: {
                break;
            }
            case 17: {
                break;
            }
            case 18: {
                break;
            }
            case 19: {
                break;
            }
            case 20: {
                break;
            }
            case 21: {
                break;
            }
            case 22: {
                break;
            }
            case 23: {
                break;
            }
            case 24: {
                break;
            }
            case 25: {
                break;
            }
            case 26: {
                break;
            }
            case 27: {
                break;
            }
            case 28: {
                break;
            }
            case 29: {
                break;
            }
            case 30: {
                break;
            }
            case 31: {
                break;
            }
            case 32: {
                break;
            }
            case 33: {
                break;
            }
            case 34: {
                break;
            }
            case 35: {
                break;
            }
            case 36: {
                break;
            }
            case 37: {
                break;
            }
            case 38: {
                break;
            }
            case 39: {
                break;
            }
            case 40: {
                break;
            }
            case 41: {
                break;
            }
            case 42: {
                break;
            }
            case 43: {
                break;
            }
            case 44: {
                break;
            }
            case 45: {
                break;
            }
            case 46: {
                break;
            }
            case 47: {
                break;
            }
            case 48: {
                newCHP.setIstd(sprm.getOperand());
                break;
            }
            case 49: {
                break;
            }
            case 50: {
                newCHP.setFBold(false);
                newCHP.setFItalic(false);
                newCHP.setFOutline(false);
                newCHP.setFStrike(false);
                newCHP.setFShadow(false);
                newCHP.setFSmallCaps(false);
                newCHP.setFCaps(false);
                newCHP.setFVanish(false);
                newCHP.setKul((byte)0);
                newCHP.setIco((byte)0);
                break;
            }
            case 51: {
                boolean fSpec = newCHP.isFSpec();
                newCHP = oldCHP.copy();
                newCHP.setFSpec(fSpec);
                return;
            }
            case 52: {
                break;
            }
            case 53: {
                newCHP.setFBold(CharacterSprmUncompressor.getCHPFlag((byte)sprm.getOperand(), oldCHP.isFBold()));
                break;
            }
            case 54: {
                newCHP.setFItalic(CharacterSprmUncompressor.getCHPFlag((byte)sprm.getOperand(), oldCHP.isFItalic()));
                break;
            }
            case 55: {
                newCHP.setFStrike(CharacterSprmUncompressor.getCHPFlag((byte)sprm.getOperand(), oldCHP.isFStrike()));
                break;
            }
            case 56: {
                newCHP.setFOutline(CharacterSprmUncompressor.getCHPFlag((byte)sprm.getOperand(), oldCHP.isFOutline()));
                break;
            }
            case 57: {
                newCHP.setFShadow(CharacterSprmUncompressor.getCHPFlag((byte)sprm.getOperand(), oldCHP.isFShadow()));
                break;
            }
            case 58: {
                newCHP.setFSmallCaps(CharacterSprmUncompressor.getCHPFlag((byte)sprm.getOperand(), oldCHP.isFSmallCaps()));
                break;
            }
            case 59: {
                newCHP.setFCaps(CharacterSprmUncompressor.getCHPFlag((byte)sprm.getOperand(), oldCHP.isFCaps()));
                break;
            }
            case 60: {
                newCHP.setFVanish(CharacterSprmUncompressor.getCHPFlag((byte)sprm.getOperand(), oldCHP.isFVanish()));
                break;
            }
            case 61: {
                newCHP.setFtcAscii((short)sprm.getOperand());
                break;
            }
            case 62: {
                newCHP.setKul((byte)sprm.getOperand());
                break;
            }
            case 63: {
                boolean fAdjust;
                byte hpsPos;
                int operand = sprm.getOperand();
                int hps = operand & 0xFF;
                if (hps != 0) {
                    newCHP.setHps(hps);
                }
                byte cInc = (byte)((operand & 0xFF00) >>> 8);
                if ((cInc = (byte)(cInc >>> 1)) != 0) {
                    newCHP.setHps(Math.max(newCHP.getHps() + cInc * 2, 2));
                }
                if ((hpsPos = (byte)((operand & 0xFF0000) >>> 16)) != -128) {
                    newCHP.setHpsPos(hpsPos);
                }
                boolean bl = fAdjust = (operand & 0x100) > 0;
                if (fAdjust && (hpsPos & 0xFF) != 128 && hpsPos != 0 && oldCHP.getHpsPos() == 0) {
                    newCHP.setHps(Math.max(newCHP.getHps() + -2, 2));
                }
                if (!fAdjust || hpsPos != 0 || oldCHP.getHpsPos() == 0) break;
                newCHP.setHps(Math.max(newCHP.getHps() + 2, 2));
                break;
            }
            case 64: {
                newCHP.setDxaSpace(sprm.getOperand());
                break;
            }
            case 65: {
                newCHP.setLidDefault((short)sprm.getOperand());
                break;
            }
            case 66: {
                newCHP.setIco((byte)sprm.getOperand());
                break;
            }
            case 67: {
                newCHP.setHps(sprm.getOperand());
                break;
            }
            case 68: {
                byte hpsLvl = (byte)sprm.getOperand();
                newCHP.setHps(Math.max(newCHP.getHps() + hpsLvl * 2, 2));
                break;
            }
            case 69: {
                newCHP.setHpsPos((short)sprm.getOperand());
                break;
            }
            case 70: {
                if (sprm.getOperand() != 0) {
                    if (oldCHP.getHpsPos() != 0) break;
                    newCHP.setHps(Math.max(newCHP.getHps() + -2, 2));
                    break;
                }
                if (oldCHP.getHpsPos() == 0) break;
                newCHP.setHps(Math.max(newCHP.getHps() + 2, 2));
                break;
            }
            case 71: {
                break;
            }
            case 72: {
                newCHP.setIss((byte)sprm.getOperand());
                break;
            }
            case 73: {
                newCHP.setHps(LittleEndian.getShort(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 74: {
                short increment = LittleEndian.getShort(sprm.getGrpprl(), sprm.getGrpprlOffset());
                newCHP.setHps(Math.max(newCHP.getHps() + increment, 8));
                break;
            }
            case 75: {
                newCHP.setHpsKern(sprm.getOperand());
                break;
            }
            case 76: {
                break;
            }
            case 77: {
                float percentage = (float)sprm.getOperand() / 100.0f;
                int add = (int)(percentage * (float)newCHP.getHps());
                newCHP.setHps(newCHP.getHps() + add);
                break;
            }
            case 78: {
                Hyphenation hyphenation = new Hyphenation((short)sprm.getOperand());
                newCHP.setHresi(hyphenation);
                break;
            }
            case 79: {
                newCHP.setFtcAscii((short)sprm.getOperand());
                break;
            }
            case 80: {
                newCHP.setFtcFE((short)sprm.getOperand());
                break;
            }
            case 81: {
                newCHP.setFtcOther((short)sprm.getOperand());
                break;
            }
            case 82: {
                break;
            }
            case 83: {
                newCHP.setFDStrike(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 84: {
                newCHP.setFImprint(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 85: {
                newCHP.setFSpec(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 86: {
                newCHP.setFObj(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 87: {
                byte[] buf = sprm.getGrpprl();
                int offset = sprm.getGrpprlOffset();
                newCHP.setFPropRMark(buf[offset] != 0);
                newCHP.setIbstPropRMark(LittleEndian.getShort(buf, offset + 1));
                newCHP.setDttmPropRMark(new DateAndTime(buf, offset + 3));
                break;
            }
            case 88: {
                newCHP.setFEmboss(CharacterSprmUncompressor.getFlag(sprm.getOperand()));
                break;
            }
            case 89: {
                newCHP.setSfxtText((byte)sprm.getOperand());
                break;
            }
            case 90: {
                break;
            }
            case 91: {
                break;
            }
            case 92: {
                break;
            }
            case 93: {
                break;
            }
            case 94: {
                break;
            }
            case 95: {
                break;
            }
            case 96: {
                break;
            }
            case 97: {
                break;
            }
            case 98: {
                byte[] xstDispFldRMark = new byte[32];
                byte[] buf = sprm.getGrpprl();
                int offset = sprm.getGrpprlOffset();
                newCHP.setFDispFldRMark(0 != buf[offset]);
                newCHP.setIbstDispFldRMark(LittleEndian.getShort(buf, offset + 1));
                newCHP.setDttmDispFldRMark(new DateAndTime(buf, offset + 3));
                System.arraycopy(buf, offset + 7, xstDispFldRMark, 0, 32);
                newCHP.setXstDispFldRMark(xstDispFldRMark);
                break;
            }
            case 99: {
                newCHP.setIbstRMarkDel((short)sprm.getOperand());
                break;
            }
            case 100: {
                newCHP.setDttmRMarkDel(new DateAndTime(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 101: {
                newCHP.setBrc(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            }
            case 102: {
                ShadingDescriptor80 oldDescriptor = new ShadingDescriptor80(sprm.getGrpprl(), sprm.getGrpprlOffset());
                ShadingDescriptor newDescriptor = oldDescriptor.toShadingDescriptor();
                newCHP.setShd(newDescriptor);
                break;
            }
            case 103: {
                break;
            }
            case 104: {
                break;
            }
            case 105: {
                break;
            }
            case 106: {
                break;
            }
            case 107: {
                break;
            }
            case 108: {
                break;
            }
            case 109: {
                newCHP.setLidDefault((short)sprm.getOperand());
                break;
            }
            case 110: {
                newCHP.setLidFE((short)sprm.getOperand());
                break;
            }
            case 111: {
                newCHP.setIdctHint((byte)sprm.getOperand());
                break;
            }
            case 112: {
                newCHP.setCv(new Colorref(sprm.getOperand()));
                break;
            }
            case 113: {
                break;
            }
            case 114: {
                break;
            }
            case 115: {
                break;
            }
            case 116: {
                break;
            }
            case 117: {
                newCHP.setFNoProof(CharacterSprmUncompressor.getCHPFlag((byte)sprm.getOperand(), oldCHP.isFNoProof()));
                break;
            }
            default: {
                LOG.atDebug().log("Unknown CHP sprm ignored: {}", (Object)sprm);
            }
        }
    }

    private static boolean getCHPFlag(byte x, boolean oldVal) {
        if (x == 0) {
            return false;
        }
        if (x == 1) {
            return true;
        }
        if ((x & 0x81) == 128) {
            return oldVal;
        }
        if ((x & 0x81) == 129) {
            return !oldVal;
        }
        return false;
    }
}

