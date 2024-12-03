/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1Alpha3CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1AlphaCountryCode;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1AlphaCountryCodeMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.jcip.annotations.Immutable;

@Immutable
public final class ISO3166_1Alpha2CountryCode
extends ISO3166_1AlphaCountryCode {
    private static final long serialVersionUID = -7659886425656766569L;
    public static final ISO3166_1Alpha2CountryCode AD = new ISO3166_1Alpha2CountryCode("AD");
    public static final ISO3166_1Alpha2CountryCode AE = new ISO3166_1Alpha2CountryCode("AE");
    public static final ISO3166_1Alpha2CountryCode AF = new ISO3166_1Alpha2CountryCode("AF");
    public static final ISO3166_1Alpha2CountryCode AG = new ISO3166_1Alpha2CountryCode("AG");
    public static final ISO3166_1Alpha2CountryCode AI = new ISO3166_1Alpha2CountryCode("AI");
    public static final ISO3166_1Alpha2CountryCode AL = new ISO3166_1Alpha2CountryCode("AL");
    public static final ISO3166_1Alpha2CountryCode AM = new ISO3166_1Alpha2CountryCode("AM");
    public static final ISO3166_1Alpha2CountryCode AO = new ISO3166_1Alpha2CountryCode("AO");
    public static final ISO3166_1Alpha2CountryCode AQ = new ISO3166_1Alpha2CountryCode("AQ");
    public static final ISO3166_1Alpha2CountryCode AR = new ISO3166_1Alpha2CountryCode("AR");
    public static final ISO3166_1Alpha2CountryCode AS = new ISO3166_1Alpha2CountryCode("AS");
    public static final ISO3166_1Alpha2CountryCode AT = new ISO3166_1Alpha2CountryCode("AT");
    public static final ISO3166_1Alpha2CountryCode AU = new ISO3166_1Alpha2CountryCode("AU");
    public static final ISO3166_1Alpha2CountryCode AW = new ISO3166_1Alpha2CountryCode("AW");
    public static final ISO3166_1Alpha2CountryCode AX = new ISO3166_1Alpha2CountryCode("AX");
    public static final ISO3166_1Alpha2CountryCode AZ = new ISO3166_1Alpha2CountryCode("AZ");
    public static final ISO3166_1Alpha2CountryCode BA = new ISO3166_1Alpha2CountryCode("BA");
    public static final ISO3166_1Alpha2CountryCode BB = new ISO3166_1Alpha2CountryCode("BB");
    public static final ISO3166_1Alpha2CountryCode BD = new ISO3166_1Alpha2CountryCode("BD");
    public static final ISO3166_1Alpha2CountryCode BE = new ISO3166_1Alpha2CountryCode("BE");
    public static final ISO3166_1Alpha2CountryCode BF = new ISO3166_1Alpha2CountryCode("BF");
    public static final ISO3166_1Alpha2CountryCode BG = new ISO3166_1Alpha2CountryCode("BG");
    public static final ISO3166_1Alpha2CountryCode BH = new ISO3166_1Alpha2CountryCode("BH");
    public static final ISO3166_1Alpha2CountryCode BI = new ISO3166_1Alpha2CountryCode("BI");
    public static final ISO3166_1Alpha2CountryCode BJ = new ISO3166_1Alpha2CountryCode("BJ");
    public static final ISO3166_1Alpha2CountryCode BL = new ISO3166_1Alpha2CountryCode("BL");
    public static final ISO3166_1Alpha2CountryCode BM = new ISO3166_1Alpha2CountryCode("BM");
    public static final ISO3166_1Alpha2CountryCode BN = new ISO3166_1Alpha2CountryCode("BN");
    public static final ISO3166_1Alpha2CountryCode BO = new ISO3166_1Alpha2CountryCode("BO");
    public static final ISO3166_1Alpha2CountryCode BQ = new ISO3166_1Alpha2CountryCode("BQ");
    public static final ISO3166_1Alpha2CountryCode BR = new ISO3166_1Alpha2CountryCode("BR");
    public static final ISO3166_1Alpha2CountryCode BS = new ISO3166_1Alpha2CountryCode("BS");
    public static final ISO3166_1Alpha2CountryCode BT = new ISO3166_1Alpha2CountryCode("BT");
    public static final ISO3166_1Alpha2CountryCode BV = new ISO3166_1Alpha2CountryCode("BV");
    public static final ISO3166_1Alpha2CountryCode BW = new ISO3166_1Alpha2CountryCode("BW");
    public static final ISO3166_1Alpha2CountryCode BY = new ISO3166_1Alpha2CountryCode("BY");
    public static final ISO3166_1Alpha2CountryCode BZ = new ISO3166_1Alpha2CountryCode("BZ");
    public static final ISO3166_1Alpha2CountryCode CA = new ISO3166_1Alpha2CountryCode("CA");
    public static final ISO3166_1Alpha2CountryCode CC = new ISO3166_1Alpha2CountryCode("CC");
    public static final ISO3166_1Alpha2CountryCode CD = new ISO3166_1Alpha2CountryCode("CD");
    public static final ISO3166_1Alpha2CountryCode CF = new ISO3166_1Alpha2CountryCode("CF");
    public static final ISO3166_1Alpha2CountryCode CG = new ISO3166_1Alpha2CountryCode("CG");
    public static final ISO3166_1Alpha2CountryCode CH = new ISO3166_1Alpha2CountryCode("CH");
    public static final ISO3166_1Alpha2CountryCode CI = new ISO3166_1Alpha2CountryCode("CI");
    public static final ISO3166_1Alpha2CountryCode CK = new ISO3166_1Alpha2CountryCode("CK");
    public static final ISO3166_1Alpha2CountryCode CL = new ISO3166_1Alpha2CountryCode("CL");
    public static final ISO3166_1Alpha2CountryCode CM = new ISO3166_1Alpha2CountryCode("CM");
    public static final ISO3166_1Alpha2CountryCode CN = new ISO3166_1Alpha2CountryCode("CN");
    public static final ISO3166_1Alpha2CountryCode CO = new ISO3166_1Alpha2CountryCode("CO");
    public static final ISO3166_1Alpha2CountryCode CR = new ISO3166_1Alpha2CountryCode("CR");
    public static final ISO3166_1Alpha2CountryCode CU = new ISO3166_1Alpha2CountryCode("CU");
    public static final ISO3166_1Alpha2CountryCode CV = new ISO3166_1Alpha2CountryCode("CV");
    public static final ISO3166_1Alpha2CountryCode CW = new ISO3166_1Alpha2CountryCode("CW");
    public static final ISO3166_1Alpha2CountryCode CX = new ISO3166_1Alpha2CountryCode("CX");
    public static final ISO3166_1Alpha2CountryCode CY = new ISO3166_1Alpha2CountryCode("CY");
    public static final ISO3166_1Alpha2CountryCode CZ = new ISO3166_1Alpha2CountryCode("CZ");
    public static final ISO3166_1Alpha2CountryCode DE = new ISO3166_1Alpha2CountryCode("DE");
    public static final ISO3166_1Alpha2CountryCode DJ = new ISO3166_1Alpha2CountryCode("DJ");
    public static final ISO3166_1Alpha2CountryCode DK = new ISO3166_1Alpha2CountryCode("DK");
    public static final ISO3166_1Alpha2CountryCode DM = new ISO3166_1Alpha2CountryCode("DM");
    public static final ISO3166_1Alpha2CountryCode DO = new ISO3166_1Alpha2CountryCode("DO");
    public static final ISO3166_1Alpha2CountryCode DZ = new ISO3166_1Alpha2CountryCode("DZ");
    public static final ISO3166_1Alpha2CountryCode EC = new ISO3166_1Alpha2CountryCode("EC");
    public static final ISO3166_1Alpha2CountryCode EE = new ISO3166_1Alpha2CountryCode("EE");
    public static final ISO3166_1Alpha2CountryCode EG = new ISO3166_1Alpha2CountryCode("EG");
    public static final ISO3166_1Alpha2CountryCode EH = new ISO3166_1Alpha2CountryCode("EH");
    public static final ISO3166_1Alpha2CountryCode ER = new ISO3166_1Alpha2CountryCode("ER");
    public static final ISO3166_1Alpha2CountryCode ES = new ISO3166_1Alpha2CountryCode("ES");
    public static final ISO3166_1Alpha2CountryCode ET = new ISO3166_1Alpha2CountryCode("ET");
    public static final ISO3166_1Alpha2CountryCode FI = new ISO3166_1Alpha2CountryCode("FI");
    public static final ISO3166_1Alpha2CountryCode FJ = new ISO3166_1Alpha2CountryCode("FJ");
    public static final ISO3166_1Alpha2CountryCode FK = new ISO3166_1Alpha2CountryCode("FK");
    public static final ISO3166_1Alpha2CountryCode FM = new ISO3166_1Alpha2CountryCode("FM");
    public static final ISO3166_1Alpha2CountryCode FO = new ISO3166_1Alpha2CountryCode("FO");
    public static final ISO3166_1Alpha2CountryCode FR = new ISO3166_1Alpha2CountryCode("FR");
    public static final ISO3166_1Alpha2CountryCode GA = new ISO3166_1Alpha2CountryCode("GA");
    public static final ISO3166_1Alpha2CountryCode GB = new ISO3166_1Alpha2CountryCode("GB");
    public static final ISO3166_1Alpha2CountryCode GD = new ISO3166_1Alpha2CountryCode("GD");
    public static final ISO3166_1Alpha2CountryCode GE = new ISO3166_1Alpha2CountryCode("GE");
    public static final ISO3166_1Alpha2CountryCode GF = new ISO3166_1Alpha2CountryCode("GF");
    public static final ISO3166_1Alpha2CountryCode GG = new ISO3166_1Alpha2CountryCode("GG");
    public static final ISO3166_1Alpha2CountryCode GH = new ISO3166_1Alpha2CountryCode("GH");
    public static final ISO3166_1Alpha2CountryCode GI = new ISO3166_1Alpha2CountryCode("GI");
    public static final ISO3166_1Alpha2CountryCode GL = new ISO3166_1Alpha2CountryCode("GL");
    public static final ISO3166_1Alpha2CountryCode GM = new ISO3166_1Alpha2CountryCode("GM");
    public static final ISO3166_1Alpha2CountryCode GN = new ISO3166_1Alpha2CountryCode("GN");
    public static final ISO3166_1Alpha2CountryCode GP = new ISO3166_1Alpha2CountryCode("GP");
    public static final ISO3166_1Alpha2CountryCode GQ = new ISO3166_1Alpha2CountryCode("GQ");
    public static final ISO3166_1Alpha2CountryCode GR = new ISO3166_1Alpha2CountryCode("GR");
    public static final ISO3166_1Alpha2CountryCode GS = new ISO3166_1Alpha2CountryCode("GS");
    public static final ISO3166_1Alpha2CountryCode GT = new ISO3166_1Alpha2CountryCode("GT");
    public static final ISO3166_1Alpha2CountryCode GU = new ISO3166_1Alpha2CountryCode("GU");
    public static final ISO3166_1Alpha2CountryCode GW = new ISO3166_1Alpha2CountryCode("GW");
    public static final ISO3166_1Alpha2CountryCode GY = new ISO3166_1Alpha2CountryCode("GY");
    public static final ISO3166_1Alpha2CountryCode HK = new ISO3166_1Alpha2CountryCode("HK");
    public static final ISO3166_1Alpha2CountryCode HM = new ISO3166_1Alpha2CountryCode("HM");
    public static final ISO3166_1Alpha2CountryCode HN = new ISO3166_1Alpha2CountryCode("HN");
    public static final ISO3166_1Alpha2CountryCode HR = new ISO3166_1Alpha2CountryCode("HR");
    public static final ISO3166_1Alpha2CountryCode HT = new ISO3166_1Alpha2CountryCode("HT");
    public static final ISO3166_1Alpha2CountryCode HU = new ISO3166_1Alpha2CountryCode("HU");
    public static final ISO3166_1Alpha2CountryCode ID = new ISO3166_1Alpha2CountryCode("ID");
    public static final ISO3166_1Alpha2CountryCode IE = new ISO3166_1Alpha2CountryCode("IE");
    public static final ISO3166_1Alpha2CountryCode IL = new ISO3166_1Alpha2CountryCode("IL");
    public static final ISO3166_1Alpha2CountryCode IM = new ISO3166_1Alpha2CountryCode("IM");
    public static final ISO3166_1Alpha2CountryCode IN = new ISO3166_1Alpha2CountryCode("IN");
    public static final ISO3166_1Alpha2CountryCode IO = new ISO3166_1Alpha2CountryCode("IO");
    public static final ISO3166_1Alpha2CountryCode IQ = new ISO3166_1Alpha2CountryCode("IQ");
    public static final ISO3166_1Alpha2CountryCode IR = new ISO3166_1Alpha2CountryCode("IR");
    public static final ISO3166_1Alpha2CountryCode IS = new ISO3166_1Alpha2CountryCode("IS");
    public static final ISO3166_1Alpha2CountryCode IT = new ISO3166_1Alpha2CountryCode("IT");
    public static final ISO3166_1Alpha2CountryCode JE = new ISO3166_1Alpha2CountryCode("JE");
    public static final ISO3166_1Alpha2CountryCode JM = new ISO3166_1Alpha2CountryCode("JM");
    public static final ISO3166_1Alpha2CountryCode JO = new ISO3166_1Alpha2CountryCode("JO");
    public static final ISO3166_1Alpha2CountryCode JP = new ISO3166_1Alpha2CountryCode("JP");
    public static final ISO3166_1Alpha2CountryCode KE = new ISO3166_1Alpha2CountryCode("KE");
    public static final ISO3166_1Alpha2CountryCode KG = new ISO3166_1Alpha2CountryCode("KG");
    public static final ISO3166_1Alpha2CountryCode KH = new ISO3166_1Alpha2CountryCode("KH");
    public static final ISO3166_1Alpha2CountryCode KI = new ISO3166_1Alpha2CountryCode("KI");
    public static final ISO3166_1Alpha2CountryCode KM = new ISO3166_1Alpha2CountryCode("KM");
    public static final ISO3166_1Alpha2CountryCode KN = new ISO3166_1Alpha2CountryCode("KN");
    public static final ISO3166_1Alpha2CountryCode KP = new ISO3166_1Alpha2CountryCode("KP");
    public static final ISO3166_1Alpha2CountryCode KR = new ISO3166_1Alpha2CountryCode("KR");
    public static final ISO3166_1Alpha2CountryCode KW = new ISO3166_1Alpha2CountryCode("KW");
    public static final ISO3166_1Alpha2CountryCode KY = new ISO3166_1Alpha2CountryCode("KY");
    public static final ISO3166_1Alpha2CountryCode KZ = new ISO3166_1Alpha2CountryCode("KZ");
    public static final ISO3166_1Alpha2CountryCode LA = new ISO3166_1Alpha2CountryCode("LA");
    public static final ISO3166_1Alpha2CountryCode LB = new ISO3166_1Alpha2CountryCode("LB");
    public static final ISO3166_1Alpha2CountryCode LC = new ISO3166_1Alpha2CountryCode("LC");
    public static final ISO3166_1Alpha2CountryCode LI = new ISO3166_1Alpha2CountryCode("LI");
    public static final ISO3166_1Alpha2CountryCode LK = new ISO3166_1Alpha2CountryCode("LK");
    public static final ISO3166_1Alpha2CountryCode LR = new ISO3166_1Alpha2CountryCode("LR");
    public static final ISO3166_1Alpha2CountryCode LS = new ISO3166_1Alpha2CountryCode("LS");
    public static final ISO3166_1Alpha2CountryCode LT = new ISO3166_1Alpha2CountryCode("LT");
    public static final ISO3166_1Alpha2CountryCode LU = new ISO3166_1Alpha2CountryCode("LU");
    public static final ISO3166_1Alpha2CountryCode LV = new ISO3166_1Alpha2CountryCode("LV");
    public static final ISO3166_1Alpha2CountryCode LY = new ISO3166_1Alpha2CountryCode("LY");
    public static final ISO3166_1Alpha2CountryCode MA = new ISO3166_1Alpha2CountryCode("MA");
    public static final ISO3166_1Alpha2CountryCode MC = new ISO3166_1Alpha2CountryCode("MC");
    public static final ISO3166_1Alpha2CountryCode MD = new ISO3166_1Alpha2CountryCode("MD");
    public static final ISO3166_1Alpha2CountryCode ME = new ISO3166_1Alpha2CountryCode("ME");
    public static final ISO3166_1Alpha2CountryCode MF = new ISO3166_1Alpha2CountryCode("MF");
    public static final ISO3166_1Alpha2CountryCode MG = new ISO3166_1Alpha2CountryCode("MG");
    public static final ISO3166_1Alpha2CountryCode MH = new ISO3166_1Alpha2CountryCode("MH");
    public static final ISO3166_1Alpha2CountryCode MK = new ISO3166_1Alpha2CountryCode("MK");
    public static final ISO3166_1Alpha2CountryCode ML = new ISO3166_1Alpha2CountryCode("ML");
    public static final ISO3166_1Alpha2CountryCode MM = new ISO3166_1Alpha2CountryCode("MM");
    public static final ISO3166_1Alpha2CountryCode MN = new ISO3166_1Alpha2CountryCode("MN");
    public static final ISO3166_1Alpha2CountryCode MO = new ISO3166_1Alpha2CountryCode("MO");
    public static final ISO3166_1Alpha2CountryCode MP = new ISO3166_1Alpha2CountryCode("MP");
    public static final ISO3166_1Alpha2CountryCode MQ = new ISO3166_1Alpha2CountryCode("MQ");
    public static final ISO3166_1Alpha2CountryCode MR = new ISO3166_1Alpha2CountryCode("MR");
    public static final ISO3166_1Alpha2CountryCode MS = new ISO3166_1Alpha2CountryCode("MS");
    public static final ISO3166_1Alpha2CountryCode MT = new ISO3166_1Alpha2CountryCode("MT");
    public static final ISO3166_1Alpha2CountryCode MU = new ISO3166_1Alpha2CountryCode("MU");
    public static final ISO3166_1Alpha2CountryCode MV = new ISO3166_1Alpha2CountryCode("MV");
    public static final ISO3166_1Alpha2CountryCode MW = new ISO3166_1Alpha2CountryCode("MW");
    public static final ISO3166_1Alpha2CountryCode MX = new ISO3166_1Alpha2CountryCode("MX");
    public static final ISO3166_1Alpha2CountryCode MY = new ISO3166_1Alpha2CountryCode("MY");
    public static final ISO3166_1Alpha2CountryCode MZ = new ISO3166_1Alpha2CountryCode("MZ");
    public static final ISO3166_1Alpha2CountryCode NA = new ISO3166_1Alpha2CountryCode("NA");
    public static final ISO3166_1Alpha2CountryCode NC = new ISO3166_1Alpha2CountryCode("NC");
    public static final ISO3166_1Alpha2CountryCode NE = new ISO3166_1Alpha2CountryCode("NE");
    public static final ISO3166_1Alpha2CountryCode NF = new ISO3166_1Alpha2CountryCode("NF");
    public static final ISO3166_1Alpha2CountryCode NG = new ISO3166_1Alpha2CountryCode("NG");
    public static final ISO3166_1Alpha2CountryCode NI = new ISO3166_1Alpha2CountryCode("NI");
    public static final ISO3166_1Alpha2CountryCode NL = new ISO3166_1Alpha2CountryCode("NL");
    public static final ISO3166_1Alpha2CountryCode NO = new ISO3166_1Alpha2CountryCode("NO");
    public static final ISO3166_1Alpha2CountryCode NP = new ISO3166_1Alpha2CountryCode("NP");
    public static final ISO3166_1Alpha2CountryCode NR = new ISO3166_1Alpha2CountryCode("NR");
    public static final ISO3166_1Alpha2CountryCode NU = new ISO3166_1Alpha2CountryCode("NU");
    public static final ISO3166_1Alpha2CountryCode NZ = new ISO3166_1Alpha2CountryCode("NZ");
    public static final ISO3166_1Alpha2CountryCode OM = new ISO3166_1Alpha2CountryCode("OM");
    public static final ISO3166_1Alpha2CountryCode PA = new ISO3166_1Alpha2CountryCode("PA");
    public static final ISO3166_1Alpha2CountryCode PE = new ISO3166_1Alpha2CountryCode("PE");
    public static final ISO3166_1Alpha2CountryCode PF = new ISO3166_1Alpha2CountryCode("PF");
    public static final ISO3166_1Alpha2CountryCode PG = new ISO3166_1Alpha2CountryCode("PG");
    public static final ISO3166_1Alpha2CountryCode PH = new ISO3166_1Alpha2CountryCode("PH");
    public static final ISO3166_1Alpha2CountryCode PK = new ISO3166_1Alpha2CountryCode("PK");
    public static final ISO3166_1Alpha2CountryCode PL = new ISO3166_1Alpha2CountryCode("PL");
    public static final ISO3166_1Alpha2CountryCode PM = new ISO3166_1Alpha2CountryCode("PM");
    public static final ISO3166_1Alpha2CountryCode PN = new ISO3166_1Alpha2CountryCode("PN");
    public static final ISO3166_1Alpha2CountryCode PR = new ISO3166_1Alpha2CountryCode("PR");
    public static final ISO3166_1Alpha2CountryCode PS = new ISO3166_1Alpha2CountryCode("PS");
    public static final ISO3166_1Alpha2CountryCode PT = new ISO3166_1Alpha2CountryCode("PT");
    public static final ISO3166_1Alpha2CountryCode PW = new ISO3166_1Alpha2CountryCode("PW");
    public static final ISO3166_1Alpha2CountryCode PY = new ISO3166_1Alpha2CountryCode("PY");
    public static final ISO3166_1Alpha2CountryCode QA = new ISO3166_1Alpha2CountryCode("QA");
    public static final ISO3166_1Alpha2CountryCode RE = new ISO3166_1Alpha2CountryCode("RE");
    public static final ISO3166_1Alpha2CountryCode RO = new ISO3166_1Alpha2CountryCode("RO");
    public static final ISO3166_1Alpha2CountryCode RS = new ISO3166_1Alpha2CountryCode("RS");
    public static final ISO3166_1Alpha2CountryCode RU = new ISO3166_1Alpha2CountryCode("RU");
    public static final ISO3166_1Alpha2CountryCode RW = new ISO3166_1Alpha2CountryCode("RW");
    public static final ISO3166_1Alpha2CountryCode SA = new ISO3166_1Alpha2CountryCode("SA");
    public static final ISO3166_1Alpha2CountryCode SB = new ISO3166_1Alpha2CountryCode("SB");
    public static final ISO3166_1Alpha2CountryCode SC = new ISO3166_1Alpha2CountryCode("SC");
    public static final ISO3166_1Alpha2CountryCode SD = new ISO3166_1Alpha2CountryCode("SD");
    public static final ISO3166_1Alpha2CountryCode SE = new ISO3166_1Alpha2CountryCode("SE");
    public static final ISO3166_1Alpha2CountryCode SG = new ISO3166_1Alpha2CountryCode("SG");
    public static final ISO3166_1Alpha2CountryCode SH = new ISO3166_1Alpha2CountryCode("SH");
    public static final ISO3166_1Alpha2CountryCode SI = new ISO3166_1Alpha2CountryCode("SI");
    public static final ISO3166_1Alpha2CountryCode SJ = new ISO3166_1Alpha2CountryCode("SJ");
    public static final ISO3166_1Alpha2CountryCode SK = new ISO3166_1Alpha2CountryCode("SK");
    public static final ISO3166_1Alpha2CountryCode SL = new ISO3166_1Alpha2CountryCode("SL");
    public static final ISO3166_1Alpha2CountryCode SM = new ISO3166_1Alpha2CountryCode("SM");
    public static final ISO3166_1Alpha2CountryCode SN = new ISO3166_1Alpha2CountryCode("SN");
    public static final ISO3166_1Alpha2CountryCode SO = new ISO3166_1Alpha2CountryCode("SO");
    public static final ISO3166_1Alpha2CountryCode SR = new ISO3166_1Alpha2CountryCode("SR");
    public static final ISO3166_1Alpha2CountryCode SS = new ISO3166_1Alpha2CountryCode("SS");
    public static final ISO3166_1Alpha2CountryCode ST = new ISO3166_1Alpha2CountryCode("ST");
    public static final ISO3166_1Alpha2CountryCode SV = new ISO3166_1Alpha2CountryCode("SV");
    public static final ISO3166_1Alpha2CountryCode SX = new ISO3166_1Alpha2CountryCode("SX");
    public static final ISO3166_1Alpha2CountryCode SY = new ISO3166_1Alpha2CountryCode("SY");
    public static final ISO3166_1Alpha2CountryCode SZ = new ISO3166_1Alpha2CountryCode("SZ");
    public static final ISO3166_1Alpha2CountryCode TC = new ISO3166_1Alpha2CountryCode("TC");
    public static final ISO3166_1Alpha2CountryCode TD = new ISO3166_1Alpha2CountryCode("TD");
    public static final ISO3166_1Alpha2CountryCode TF = new ISO3166_1Alpha2CountryCode("TF");
    public static final ISO3166_1Alpha2CountryCode TG = new ISO3166_1Alpha2CountryCode("TG");
    public static final ISO3166_1Alpha2CountryCode TH = new ISO3166_1Alpha2CountryCode("TH");
    public static final ISO3166_1Alpha2CountryCode TJ = new ISO3166_1Alpha2CountryCode("TJ");
    public static final ISO3166_1Alpha2CountryCode TK = new ISO3166_1Alpha2CountryCode("TK");
    public static final ISO3166_1Alpha2CountryCode TL = new ISO3166_1Alpha2CountryCode("TL");
    public static final ISO3166_1Alpha2CountryCode TM = new ISO3166_1Alpha2CountryCode("TM");
    public static final ISO3166_1Alpha2CountryCode TN = new ISO3166_1Alpha2CountryCode("TN");
    public static final ISO3166_1Alpha2CountryCode TO = new ISO3166_1Alpha2CountryCode("TO");
    public static final ISO3166_1Alpha2CountryCode TR = new ISO3166_1Alpha2CountryCode("TR");
    public static final ISO3166_1Alpha2CountryCode TT = new ISO3166_1Alpha2CountryCode("TT");
    public static final ISO3166_1Alpha2CountryCode TV = new ISO3166_1Alpha2CountryCode("TV");
    public static final ISO3166_1Alpha2CountryCode TW = new ISO3166_1Alpha2CountryCode("TW");
    public static final ISO3166_1Alpha2CountryCode TZ = new ISO3166_1Alpha2CountryCode("TZ");
    public static final ISO3166_1Alpha2CountryCode UA = new ISO3166_1Alpha2CountryCode("UA");
    public static final ISO3166_1Alpha2CountryCode UG = new ISO3166_1Alpha2CountryCode("UG");
    public static final ISO3166_1Alpha2CountryCode UM = new ISO3166_1Alpha2CountryCode("UM");
    public static final ISO3166_1Alpha2CountryCode US = new ISO3166_1Alpha2CountryCode("US");
    public static final ISO3166_1Alpha2CountryCode UY = new ISO3166_1Alpha2CountryCode("UY");
    public static final ISO3166_1Alpha2CountryCode UZ = new ISO3166_1Alpha2CountryCode("UZ");
    public static final ISO3166_1Alpha2CountryCode VA = new ISO3166_1Alpha2CountryCode("VA");
    public static final ISO3166_1Alpha2CountryCode VC = new ISO3166_1Alpha2CountryCode("VC");
    public static final ISO3166_1Alpha2CountryCode VE = new ISO3166_1Alpha2CountryCode("VE");
    public static final ISO3166_1Alpha2CountryCode VG = new ISO3166_1Alpha2CountryCode("VG");
    public static final ISO3166_1Alpha2CountryCode VI = new ISO3166_1Alpha2CountryCode("VI");
    public static final ISO3166_1Alpha2CountryCode VN = new ISO3166_1Alpha2CountryCode("VN");
    public static final ISO3166_1Alpha2CountryCode VU = new ISO3166_1Alpha2CountryCode("VU");
    public static final ISO3166_1Alpha2CountryCode WF = new ISO3166_1Alpha2CountryCode("WF");
    public static final ISO3166_1Alpha2CountryCode WS = new ISO3166_1Alpha2CountryCode("WS");
    public static final ISO3166_1Alpha2CountryCode YE = new ISO3166_1Alpha2CountryCode("YE");
    public static final ISO3166_1Alpha2CountryCode YT = new ISO3166_1Alpha2CountryCode("YT");
    public static final ISO3166_1Alpha2CountryCode ZA = new ISO3166_1Alpha2CountryCode("ZA");
    public static final ISO3166_1Alpha2CountryCode ZM = new ISO3166_1Alpha2CountryCode("ZM");
    public static final ISO3166_1Alpha2CountryCode ZW = new ISO3166_1Alpha2CountryCode("ZW");
    private static final Properties CODES_RESOURCE = new Properties();

    public ISO3166_1Alpha2CountryCode(String value) {
        super(value);
        if (value.length() != 2) {
            throw new IllegalArgumentException("The ISO 3166-1 alpha-2 country code must be 2 letters");
        }
    }

    public ISO3166_1Alpha3CountryCode toAlpha3CountryCode() {
        return ISO3166_1AlphaCountryCodeMapper.toAlpha3CountryCode(this);
    }

    @Override
    public String getCountryName() {
        if (CODES_RESOURCE.isEmpty()) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("iso3166_1alpha2-codes.properties");
            try {
                CODES_RESOURCE.load(is);
            }
            catch (IOException e) {
                return null;
            }
        }
        return CODES_RESOURCE.getProperty(this.getValue());
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ISO3166_1Alpha2CountryCode && this.toString().equals(object.toString());
    }

    public static ISO3166_1Alpha2CountryCode parse(String s) throws ParseException {
        try {
            return new ISO3166_1Alpha2CountryCode(s);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }
}

