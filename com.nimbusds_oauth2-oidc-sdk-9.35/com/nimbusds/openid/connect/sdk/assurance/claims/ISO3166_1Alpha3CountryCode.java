/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1Alpha2CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1AlphaCountryCode;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1AlphaCountryCodeMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.jcip.annotations.Immutable;

@Immutable
public final class ISO3166_1Alpha3CountryCode
extends ISO3166_1AlphaCountryCode {
    private static final long serialVersionUID = -7659886425656766569L;
    public static final ISO3166_1Alpha3CountryCode ABW = new ISO3166_1Alpha3CountryCode("ABW");
    public static final ISO3166_1Alpha3CountryCode AFG = new ISO3166_1Alpha3CountryCode("AFG");
    public static final ISO3166_1Alpha3CountryCode AGO = new ISO3166_1Alpha3CountryCode("AGO");
    public static final ISO3166_1Alpha3CountryCode AIA = new ISO3166_1Alpha3CountryCode("AIA");
    public static final ISO3166_1Alpha3CountryCode ALA = new ISO3166_1Alpha3CountryCode("ALA");
    public static final ISO3166_1Alpha3CountryCode ALB = new ISO3166_1Alpha3CountryCode("ALB");
    public static final ISO3166_1Alpha3CountryCode AND = new ISO3166_1Alpha3CountryCode("AND");
    public static final ISO3166_1Alpha3CountryCode ARE = new ISO3166_1Alpha3CountryCode("ARE");
    public static final ISO3166_1Alpha3CountryCode ARG = new ISO3166_1Alpha3CountryCode("ARG");
    public static final ISO3166_1Alpha3CountryCode ARM = new ISO3166_1Alpha3CountryCode("ARM");
    public static final ISO3166_1Alpha3CountryCode ASM = new ISO3166_1Alpha3CountryCode("ASM");
    public static final ISO3166_1Alpha3CountryCode ATA = new ISO3166_1Alpha3CountryCode("ATA");
    public static final ISO3166_1Alpha3CountryCode ATF = new ISO3166_1Alpha3CountryCode("ATF");
    public static final ISO3166_1Alpha3CountryCode ATG = new ISO3166_1Alpha3CountryCode("ATG");
    public static final ISO3166_1Alpha3CountryCode AUS = new ISO3166_1Alpha3CountryCode("AUS");
    public static final ISO3166_1Alpha3CountryCode AUT = new ISO3166_1Alpha3CountryCode("AUT");
    public static final ISO3166_1Alpha3CountryCode AZE = new ISO3166_1Alpha3CountryCode("AZE");
    public static final ISO3166_1Alpha3CountryCode BDI = new ISO3166_1Alpha3CountryCode("BDI");
    public static final ISO3166_1Alpha3CountryCode BEL = new ISO3166_1Alpha3CountryCode("BEL");
    public static final ISO3166_1Alpha3CountryCode BEN = new ISO3166_1Alpha3CountryCode("BEN");
    public static final ISO3166_1Alpha3CountryCode BES = new ISO3166_1Alpha3CountryCode("BES");
    public static final ISO3166_1Alpha3CountryCode BFA = new ISO3166_1Alpha3CountryCode("BFA");
    public static final ISO3166_1Alpha3CountryCode BGD = new ISO3166_1Alpha3CountryCode("BGD");
    public static final ISO3166_1Alpha3CountryCode BGR = new ISO3166_1Alpha3CountryCode("BGR");
    public static final ISO3166_1Alpha3CountryCode BHR = new ISO3166_1Alpha3CountryCode("BHR");
    public static final ISO3166_1Alpha3CountryCode BHS = new ISO3166_1Alpha3CountryCode("BHS");
    public static final ISO3166_1Alpha3CountryCode BIH = new ISO3166_1Alpha3CountryCode("BIH");
    public static final ISO3166_1Alpha3CountryCode BLM = new ISO3166_1Alpha3CountryCode("BLM");
    public static final ISO3166_1Alpha3CountryCode BLR = new ISO3166_1Alpha3CountryCode("BLR");
    public static final ISO3166_1Alpha3CountryCode BLZ = new ISO3166_1Alpha3CountryCode("BLZ");
    public static final ISO3166_1Alpha3CountryCode BMU = new ISO3166_1Alpha3CountryCode("BMU");
    public static final ISO3166_1Alpha3CountryCode BOL = new ISO3166_1Alpha3CountryCode("BOL");
    public static final ISO3166_1Alpha3CountryCode BRA = new ISO3166_1Alpha3CountryCode("BRA");
    public static final ISO3166_1Alpha3CountryCode BRB = new ISO3166_1Alpha3CountryCode("BRB");
    public static final ISO3166_1Alpha3CountryCode BRN = new ISO3166_1Alpha3CountryCode("BRN");
    public static final ISO3166_1Alpha3CountryCode BTN = new ISO3166_1Alpha3CountryCode("BTN");
    public static final ISO3166_1Alpha3CountryCode BVT = new ISO3166_1Alpha3CountryCode("BVT");
    public static final ISO3166_1Alpha3CountryCode BWA = new ISO3166_1Alpha3CountryCode("BWA");
    public static final ISO3166_1Alpha3CountryCode CAF = new ISO3166_1Alpha3CountryCode("CAF");
    public static final ISO3166_1Alpha3CountryCode CAN = new ISO3166_1Alpha3CountryCode("CAN");
    public static final ISO3166_1Alpha3CountryCode CCK = new ISO3166_1Alpha3CountryCode("CCK");
    public static final ISO3166_1Alpha3CountryCode CHE = new ISO3166_1Alpha3CountryCode("CHE");
    public static final ISO3166_1Alpha3CountryCode CHL = new ISO3166_1Alpha3CountryCode("CHL");
    public static final ISO3166_1Alpha3CountryCode CHN = new ISO3166_1Alpha3CountryCode("CHN");
    public static final ISO3166_1Alpha3CountryCode CIV = new ISO3166_1Alpha3CountryCode("CIV");
    public static final ISO3166_1Alpha3CountryCode CMR = new ISO3166_1Alpha3CountryCode("CMR");
    public static final ISO3166_1Alpha3CountryCode COD = new ISO3166_1Alpha3CountryCode("COD");
    public static final ISO3166_1Alpha3CountryCode COG = new ISO3166_1Alpha3CountryCode("COG");
    public static final ISO3166_1Alpha3CountryCode COK = new ISO3166_1Alpha3CountryCode("COK");
    public static final ISO3166_1Alpha3CountryCode COL = new ISO3166_1Alpha3CountryCode("COL");
    public static final ISO3166_1Alpha3CountryCode COM = new ISO3166_1Alpha3CountryCode("COM");
    public static final ISO3166_1Alpha3CountryCode CPV = new ISO3166_1Alpha3CountryCode("CPV");
    public static final ISO3166_1Alpha3CountryCode CRI = new ISO3166_1Alpha3CountryCode("CRI");
    public static final ISO3166_1Alpha3CountryCode CUB = new ISO3166_1Alpha3CountryCode("CUB");
    public static final ISO3166_1Alpha3CountryCode CUW = new ISO3166_1Alpha3CountryCode("CUW");
    public static final ISO3166_1Alpha3CountryCode CXR = new ISO3166_1Alpha3CountryCode("CXR");
    public static final ISO3166_1Alpha3CountryCode CYM = new ISO3166_1Alpha3CountryCode("CYM");
    public static final ISO3166_1Alpha3CountryCode CYP = new ISO3166_1Alpha3CountryCode("CYP");
    public static final ISO3166_1Alpha3CountryCode CZE = new ISO3166_1Alpha3CountryCode("CZE");
    public static final ISO3166_1Alpha3CountryCode DEU = new ISO3166_1Alpha3CountryCode("DEU");
    public static final ISO3166_1Alpha3CountryCode DJI = new ISO3166_1Alpha3CountryCode("DJI");
    public static final ISO3166_1Alpha3CountryCode DMA = new ISO3166_1Alpha3CountryCode("DMA");
    public static final ISO3166_1Alpha3CountryCode DNK = new ISO3166_1Alpha3CountryCode("DNK");
    public static final ISO3166_1Alpha3CountryCode DOM = new ISO3166_1Alpha3CountryCode("DOM");
    public static final ISO3166_1Alpha3CountryCode DZA = new ISO3166_1Alpha3CountryCode("DZA");
    public static final ISO3166_1Alpha3CountryCode ECU = new ISO3166_1Alpha3CountryCode("ECU");
    public static final ISO3166_1Alpha3CountryCode EGY = new ISO3166_1Alpha3CountryCode("EGY");
    public static final ISO3166_1Alpha3CountryCode ERI = new ISO3166_1Alpha3CountryCode("ERI");
    public static final ISO3166_1Alpha3CountryCode ESH = new ISO3166_1Alpha3CountryCode("ESH");
    public static final ISO3166_1Alpha3CountryCode ESP = new ISO3166_1Alpha3CountryCode("ESP");
    public static final ISO3166_1Alpha3CountryCode EST = new ISO3166_1Alpha3CountryCode("EST");
    public static final ISO3166_1Alpha3CountryCode ETH = new ISO3166_1Alpha3CountryCode("ETH");
    public static final ISO3166_1Alpha3CountryCode FIN = new ISO3166_1Alpha3CountryCode("FIN");
    public static final ISO3166_1Alpha3CountryCode FJI = new ISO3166_1Alpha3CountryCode("FJI");
    public static final ISO3166_1Alpha3CountryCode FLK = new ISO3166_1Alpha3CountryCode("FLK");
    public static final ISO3166_1Alpha3CountryCode FRA = new ISO3166_1Alpha3CountryCode("FRA");
    public static final ISO3166_1Alpha3CountryCode FRO = new ISO3166_1Alpha3CountryCode("FRO");
    public static final ISO3166_1Alpha3CountryCode FSM = new ISO3166_1Alpha3CountryCode("FSM");
    public static final ISO3166_1Alpha3CountryCode GAB = new ISO3166_1Alpha3CountryCode("GAB");
    public static final ISO3166_1Alpha3CountryCode GBR = new ISO3166_1Alpha3CountryCode("GBR");
    public static final ISO3166_1Alpha3CountryCode GEO = new ISO3166_1Alpha3CountryCode("GEO");
    public static final ISO3166_1Alpha3CountryCode GGY = new ISO3166_1Alpha3CountryCode("GGY");
    public static final ISO3166_1Alpha3CountryCode GHA = new ISO3166_1Alpha3CountryCode("GHA");
    public static final ISO3166_1Alpha3CountryCode GIB = new ISO3166_1Alpha3CountryCode("GIB");
    public static final ISO3166_1Alpha3CountryCode GIN = new ISO3166_1Alpha3CountryCode("GIN");
    public static final ISO3166_1Alpha3CountryCode GLP = new ISO3166_1Alpha3CountryCode("GLP");
    public static final ISO3166_1Alpha3CountryCode GMB = new ISO3166_1Alpha3CountryCode("GMB");
    public static final ISO3166_1Alpha3CountryCode GNB = new ISO3166_1Alpha3CountryCode("GNB");
    public static final ISO3166_1Alpha3CountryCode GNQ = new ISO3166_1Alpha3CountryCode("GNQ");
    public static final ISO3166_1Alpha3CountryCode GRC = new ISO3166_1Alpha3CountryCode("GRC");
    public static final ISO3166_1Alpha3CountryCode GRD = new ISO3166_1Alpha3CountryCode("GRD");
    public static final ISO3166_1Alpha3CountryCode GRL = new ISO3166_1Alpha3CountryCode("GRL");
    public static final ISO3166_1Alpha3CountryCode GTM = new ISO3166_1Alpha3CountryCode("GTM");
    public static final ISO3166_1Alpha3CountryCode GUF = new ISO3166_1Alpha3CountryCode("GUF");
    public static final ISO3166_1Alpha3CountryCode GUM = new ISO3166_1Alpha3CountryCode("GUM");
    public static final ISO3166_1Alpha3CountryCode GUY = new ISO3166_1Alpha3CountryCode("GUY");
    public static final ISO3166_1Alpha3CountryCode HKG = new ISO3166_1Alpha3CountryCode("HKG");
    public static final ISO3166_1Alpha3CountryCode HMD = new ISO3166_1Alpha3CountryCode("HMD");
    public static final ISO3166_1Alpha3CountryCode HND = new ISO3166_1Alpha3CountryCode("HND");
    public static final ISO3166_1Alpha3CountryCode HRV = new ISO3166_1Alpha3CountryCode("HRV");
    public static final ISO3166_1Alpha3CountryCode HTI = new ISO3166_1Alpha3CountryCode("HTI");
    public static final ISO3166_1Alpha3CountryCode HUN = new ISO3166_1Alpha3CountryCode("HUN");
    public static final ISO3166_1Alpha3CountryCode IDN = new ISO3166_1Alpha3CountryCode("IDN");
    public static final ISO3166_1Alpha3CountryCode IMN = new ISO3166_1Alpha3CountryCode("IMN");
    public static final ISO3166_1Alpha3CountryCode IND = new ISO3166_1Alpha3CountryCode("IND");
    public static final ISO3166_1Alpha3CountryCode IOT = new ISO3166_1Alpha3CountryCode("IOT");
    public static final ISO3166_1Alpha3CountryCode IRL = new ISO3166_1Alpha3CountryCode("IRL");
    public static final ISO3166_1Alpha3CountryCode IRN = new ISO3166_1Alpha3CountryCode("IRN");
    public static final ISO3166_1Alpha3CountryCode IRQ = new ISO3166_1Alpha3CountryCode("IRQ");
    public static final ISO3166_1Alpha3CountryCode ISL = new ISO3166_1Alpha3CountryCode("ISL");
    public static final ISO3166_1Alpha3CountryCode ISR = new ISO3166_1Alpha3CountryCode("ISR");
    public static final ISO3166_1Alpha3CountryCode ITA = new ISO3166_1Alpha3CountryCode("ITA");
    public static final ISO3166_1Alpha3CountryCode JAM = new ISO3166_1Alpha3CountryCode("JAM");
    public static final ISO3166_1Alpha3CountryCode JEY = new ISO3166_1Alpha3CountryCode("JEY");
    public static final ISO3166_1Alpha3CountryCode JOR = new ISO3166_1Alpha3CountryCode("JOR");
    public static final ISO3166_1Alpha3CountryCode JPN = new ISO3166_1Alpha3CountryCode("JPN");
    public static final ISO3166_1Alpha3CountryCode KAZ = new ISO3166_1Alpha3CountryCode("KAZ");
    public static final ISO3166_1Alpha3CountryCode KEN = new ISO3166_1Alpha3CountryCode("KEN");
    public static final ISO3166_1Alpha3CountryCode KGZ = new ISO3166_1Alpha3CountryCode("KGZ");
    public static final ISO3166_1Alpha3CountryCode KHM = new ISO3166_1Alpha3CountryCode("KHM");
    public static final ISO3166_1Alpha3CountryCode KIR = new ISO3166_1Alpha3CountryCode("KIR");
    public static final ISO3166_1Alpha3CountryCode KNA = new ISO3166_1Alpha3CountryCode("KNA");
    public static final ISO3166_1Alpha3CountryCode KOR = new ISO3166_1Alpha3CountryCode("KOR");
    public static final ISO3166_1Alpha3CountryCode KWT = new ISO3166_1Alpha3CountryCode("KWT");
    public static final ISO3166_1Alpha3CountryCode LAO = new ISO3166_1Alpha3CountryCode("LAO");
    public static final ISO3166_1Alpha3CountryCode LBN = new ISO3166_1Alpha3CountryCode("LBN");
    public static final ISO3166_1Alpha3CountryCode LBR = new ISO3166_1Alpha3CountryCode("LBR");
    public static final ISO3166_1Alpha3CountryCode LBY = new ISO3166_1Alpha3CountryCode("LBY");
    public static final ISO3166_1Alpha3CountryCode LCA = new ISO3166_1Alpha3CountryCode("LCA");
    public static final ISO3166_1Alpha3CountryCode LIE = new ISO3166_1Alpha3CountryCode("LIE");
    public static final ISO3166_1Alpha3CountryCode LKA = new ISO3166_1Alpha3CountryCode("LKA");
    public static final ISO3166_1Alpha3CountryCode LSO = new ISO3166_1Alpha3CountryCode("LSO");
    public static final ISO3166_1Alpha3CountryCode LTU = new ISO3166_1Alpha3CountryCode("LTU");
    public static final ISO3166_1Alpha3CountryCode LUX = new ISO3166_1Alpha3CountryCode("LUX");
    public static final ISO3166_1Alpha3CountryCode LVA = new ISO3166_1Alpha3CountryCode("LVA");
    public static final ISO3166_1Alpha3CountryCode MAC = new ISO3166_1Alpha3CountryCode("MAC");
    public static final ISO3166_1Alpha3CountryCode MAF = new ISO3166_1Alpha3CountryCode("MAF");
    public static final ISO3166_1Alpha3CountryCode MAR = new ISO3166_1Alpha3CountryCode("MAR");
    public static final ISO3166_1Alpha3CountryCode MCO = new ISO3166_1Alpha3CountryCode("MCO");
    public static final ISO3166_1Alpha3CountryCode MDA = new ISO3166_1Alpha3CountryCode("MDA");
    public static final ISO3166_1Alpha3CountryCode MDG = new ISO3166_1Alpha3CountryCode("MDG");
    public static final ISO3166_1Alpha3CountryCode MDV = new ISO3166_1Alpha3CountryCode("MDV");
    public static final ISO3166_1Alpha3CountryCode MEX = new ISO3166_1Alpha3CountryCode("MEX");
    public static final ISO3166_1Alpha3CountryCode MHL = new ISO3166_1Alpha3CountryCode("MHL");
    public static final ISO3166_1Alpha3CountryCode MKD = new ISO3166_1Alpha3CountryCode("MKD");
    public static final ISO3166_1Alpha3CountryCode MLI = new ISO3166_1Alpha3CountryCode("MLI");
    public static final ISO3166_1Alpha3CountryCode MLT = new ISO3166_1Alpha3CountryCode("MLT");
    public static final ISO3166_1Alpha3CountryCode MMR = new ISO3166_1Alpha3CountryCode("MMR");
    public static final ISO3166_1Alpha3CountryCode MNE = new ISO3166_1Alpha3CountryCode("MNE");
    public static final ISO3166_1Alpha3CountryCode MNG = new ISO3166_1Alpha3CountryCode("MNG");
    public static final ISO3166_1Alpha3CountryCode MNP = new ISO3166_1Alpha3CountryCode("MNP");
    public static final ISO3166_1Alpha3CountryCode MOZ = new ISO3166_1Alpha3CountryCode("MOZ");
    public static final ISO3166_1Alpha3CountryCode MRT = new ISO3166_1Alpha3CountryCode("MRT");
    public static final ISO3166_1Alpha3CountryCode MSR = new ISO3166_1Alpha3CountryCode("MSR");
    public static final ISO3166_1Alpha3CountryCode MTQ = new ISO3166_1Alpha3CountryCode("MTQ");
    public static final ISO3166_1Alpha3CountryCode MUS = new ISO3166_1Alpha3CountryCode("MUS");
    public static final ISO3166_1Alpha3CountryCode MWI = new ISO3166_1Alpha3CountryCode("MWI");
    public static final ISO3166_1Alpha3CountryCode MYS = new ISO3166_1Alpha3CountryCode("MYS");
    public static final ISO3166_1Alpha3CountryCode MYT = new ISO3166_1Alpha3CountryCode("MYT");
    public static final ISO3166_1Alpha3CountryCode NAM = new ISO3166_1Alpha3CountryCode("NAM");
    public static final ISO3166_1Alpha3CountryCode NCL = new ISO3166_1Alpha3CountryCode("NCL");
    public static final ISO3166_1Alpha3CountryCode NER = new ISO3166_1Alpha3CountryCode("NER");
    public static final ISO3166_1Alpha3CountryCode NFK = new ISO3166_1Alpha3CountryCode("NFK");
    public static final ISO3166_1Alpha3CountryCode NGA = new ISO3166_1Alpha3CountryCode("NGA");
    public static final ISO3166_1Alpha3CountryCode NIC = new ISO3166_1Alpha3CountryCode("NIC");
    public static final ISO3166_1Alpha3CountryCode NIU = new ISO3166_1Alpha3CountryCode("NIU");
    public static final ISO3166_1Alpha3CountryCode NLD = new ISO3166_1Alpha3CountryCode("NLD");
    public static final ISO3166_1Alpha3CountryCode NOR = new ISO3166_1Alpha3CountryCode("NOR");
    public static final ISO3166_1Alpha3CountryCode NPL = new ISO3166_1Alpha3CountryCode("NPL");
    public static final ISO3166_1Alpha3CountryCode NRU = new ISO3166_1Alpha3CountryCode("NRU");
    public static final ISO3166_1Alpha3CountryCode NZL = new ISO3166_1Alpha3CountryCode("NZL");
    public static final ISO3166_1Alpha3CountryCode OMN = new ISO3166_1Alpha3CountryCode("OMN");
    public static final ISO3166_1Alpha3CountryCode PAK = new ISO3166_1Alpha3CountryCode("PAK");
    public static final ISO3166_1Alpha3CountryCode PAN = new ISO3166_1Alpha3CountryCode("PAN");
    public static final ISO3166_1Alpha3CountryCode PCN = new ISO3166_1Alpha3CountryCode("PCN");
    public static final ISO3166_1Alpha3CountryCode PER = new ISO3166_1Alpha3CountryCode("PER");
    public static final ISO3166_1Alpha3CountryCode PHL = new ISO3166_1Alpha3CountryCode("PHL");
    public static final ISO3166_1Alpha3CountryCode PLW = new ISO3166_1Alpha3CountryCode("PLW");
    public static final ISO3166_1Alpha3CountryCode PNG = new ISO3166_1Alpha3CountryCode("PNG");
    public static final ISO3166_1Alpha3CountryCode POL = new ISO3166_1Alpha3CountryCode("POL");
    public static final ISO3166_1Alpha3CountryCode PRI = new ISO3166_1Alpha3CountryCode("PRI");
    public static final ISO3166_1Alpha3CountryCode PRK = new ISO3166_1Alpha3CountryCode("PRK");
    public static final ISO3166_1Alpha3CountryCode PRT = new ISO3166_1Alpha3CountryCode("PRT");
    public static final ISO3166_1Alpha3CountryCode PRY = new ISO3166_1Alpha3CountryCode("PRY");
    public static final ISO3166_1Alpha3CountryCode PSE = new ISO3166_1Alpha3CountryCode("PSE");
    public static final ISO3166_1Alpha3CountryCode PYF = new ISO3166_1Alpha3CountryCode("PYF");
    public static final ISO3166_1Alpha3CountryCode QAT = new ISO3166_1Alpha3CountryCode("QAT");
    public static final ISO3166_1Alpha3CountryCode REU = new ISO3166_1Alpha3CountryCode("REU");
    public static final ISO3166_1Alpha3CountryCode ROU = new ISO3166_1Alpha3CountryCode("ROU");
    public static final ISO3166_1Alpha3CountryCode RUS = new ISO3166_1Alpha3CountryCode("RUS");
    public static final ISO3166_1Alpha3CountryCode RWA = new ISO3166_1Alpha3CountryCode("RWA");
    public static final ISO3166_1Alpha3CountryCode SAU = new ISO3166_1Alpha3CountryCode("SAU");
    public static final ISO3166_1Alpha3CountryCode SDN = new ISO3166_1Alpha3CountryCode("SDN");
    public static final ISO3166_1Alpha3CountryCode SEN = new ISO3166_1Alpha3CountryCode("SEN");
    public static final ISO3166_1Alpha3CountryCode SGP = new ISO3166_1Alpha3CountryCode("SGP");
    public static final ISO3166_1Alpha3CountryCode SGS = new ISO3166_1Alpha3CountryCode("SGS");
    public static final ISO3166_1Alpha3CountryCode SHN = new ISO3166_1Alpha3CountryCode("SHN");
    public static final ISO3166_1Alpha3CountryCode SJM = new ISO3166_1Alpha3CountryCode("SJM");
    public static final ISO3166_1Alpha3CountryCode SLB = new ISO3166_1Alpha3CountryCode("SLB");
    public static final ISO3166_1Alpha3CountryCode SLE = new ISO3166_1Alpha3CountryCode("SLE");
    public static final ISO3166_1Alpha3CountryCode SLV = new ISO3166_1Alpha3CountryCode("SLV");
    public static final ISO3166_1Alpha3CountryCode SMR = new ISO3166_1Alpha3CountryCode("SMR");
    public static final ISO3166_1Alpha3CountryCode SOM = new ISO3166_1Alpha3CountryCode("SOM");
    public static final ISO3166_1Alpha3CountryCode SPM = new ISO3166_1Alpha3CountryCode("SPM");
    public static final ISO3166_1Alpha3CountryCode SRB = new ISO3166_1Alpha3CountryCode("SRB");
    public static final ISO3166_1Alpha3CountryCode SSD = new ISO3166_1Alpha3CountryCode("SSD");
    public static final ISO3166_1Alpha3CountryCode STP = new ISO3166_1Alpha3CountryCode("STP");
    public static final ISO3166_1Alpha3CountryCode SUR = new ISO3166_1Alpha3CountryCode("SUR");
    public static final ISO3166_1Alpha3CountryCode SVK = new ISO3166_1Alpha3CountryCode("SVK");
    public static final ISO3166_1Alpha3CountryCode SVN = new ISO3166_1Alpha3CountryCode("SVN");
    public static final ISO3166_1Alpha3CountryCode SWE = new ISO3166_1Alpha3CountryCode("SWE");
    public static final ISO3166_1Alpha3CountryCode SWZ = new ISO3166_1Alpha3CountryCode("SWZ");
    public static final ISO3166_1Alpha3CountryCode SXM = new ISO3166_1Alpha3CountryCode("SXM");
    public static final ISO3166_1Alpha3CountryCode SYC = new ISO3166_1Alpha3CountryCode("SYC");
    public static final ISO3166_1Alpha3CountryCode SYR = new ISO3166_1Alpha3CountryCode("SYR");
    public static final ISO3166_1Alpha3CountryCode TCA = new ISO3166_1Alpha3CountryCode("TCA");
    public static final ISO3166_1Alpha3CountryCode TCD = new ISO3166_1Alpha3CountryCode("TCD");
    public static final ISO3166_1Alpha3CountryCode TGO = new ISO3166_1Alpha3CountryCode("TGO");
    public static final ISO3166_1Alpha3CountryCode THA = new ISO3166_1Alpha3CountryCode("THA");
    public static final ISO3166_1Alpha3CountryCode TJK = new ISO3166_1Alpha3CountryCode("TJK");
    public static final ISO3166_1Alpha3CountryCode TKL = new ISO3166_1Alpha3CountryCode("TKL");
    public static final ISO3166_1Alpha3CountryCode TKM = new ISO3166_1Alpha3CountryCode("TKM");
    public static final ISO3166_1Alpha3CountryCode TLS = new ISO3166_1Alpha3CountryCode("TLS");
    public static final ISO3166_1Alpha3CountryCode TON = new ISO3166_1Alpha3CountryCode("TON");
    public static final ISO3166_1Alpha3CountryCode TTO = new ISO3166_1Alpha3CountryCode("TTO");
    public static final ISO3166_1Alpha3CountryCode TUN = new ISO3166_1Alpha3CountryCode("TUN");
    public static final ISO3166_1Alpha3CountryCode TUR = new ISO3166_1Alpha3CountryCode("TUR");
    public static final ISO3166_1Alpha3CountryCode TUV = new ISO3166_1Alpha3CountryCode("TUV");
    public static final ISO3166_1Alpha3CountryCode TWN = new ISO3166_1Alpha3CountryCode("TWN");
    public static final ISO3166_1Alpha3CountryCode TZA = new ISO3166_1Alpha3CountryCode("TZA");
    public static final ISO3166_1Alpha3CountryCode UGA = new ISO3166_1Alpha3CountryCode("UGA");
    public static final ISO3166_1Alpha3CountryCode UKR = new ISO3166_1Alpha3CountryCode("UKR");
    public static final ISO3166_1Alpha3CountryCode UMI = new ISO3166_1Alpha3CountryCode("UMI");
    public static final ISO3166_1Alpha3CountryCode URY = new ISO3166_1Alpha3CountryCode("URY");
    public static final ISO3166_1Alpha3CountryCode USA = new ISO3166_1Alpha3CountryCode("USA");
    public static final ISO3166_1Alpha3CountryCode UZB = new ISO3166_1Alpha3CountryCode("UZB");
    public static final ISO3166_1Alpha3CountryCode VAT = new ISO3166_1Alpha3CountryCode("VAT");
    public static final ISO3166_1Alpha3CountryCode VCT = new ISO3166_1Alpha3CountryCode("VCT");
    public static final ISO3166_1Alpha3CountryCode VEN = new ISO3166_1Alpha3CountryCode("VEN");
    public static final ISO3166_1Alpha3CountryCode VGB = new ISO3166_1Alpha3CountryCode("VGB");
    public static final ISO3166_1Alpha3CountryCode VIR = new ISO3166_1Alpha3CountryCode("VIR");
    public static final ISO3166_1Alpha3CountryCode VNM = new ISO3166_1Alpha3CountryCode("VNM");
    public static final ISO3166_1Alpha3CountryCode VUT = new ISO3166_1Alpha3CountryCode("VUT");
    public static final ISO3166_1Alpha3CountryCode WLF = new ISO3166_1Alpha3CountryCode("WLF");
    public static final ISO3166_1Alpha3CountryCode WSM = new ISO3166_1Alpha3CountryCode("WSM");
    public static final ISO3166_1Alpha3CountryCode YEM = new ISO3166_1Alpha3CountryCode("YEM");
    public static final ISO3166_1Alpha3CountryCode ZAF = new ISO3166_1Alpha3CountryCode("ZAF");
    public static final ISO3166_1Alpha3CountryCode ZMB = new ISO3166_1Alpha3CountryCode("ZMB");
    public static final ISO3166_1Alpha3CountryCode ZWE = new ISO3166_1Alpha3CountryCode("ZWE");
    private static final Properties CODES_RESOURCE = new Properties();

    public ISO3166_1Alpha3CountryCode(String value) {
        super(value);
        if (value.length() != 3) {
            throw new IllegalArgumentException("The ISO 3166-1 alpha-3 country code must be 3 letters");
        }
    }

    public ISO3166_1Alpha2CountryCode toAlpha2CountryCode() {
        return ISO3166_1AlphaCountryCodeMapper.toAlpha2CountryCode(this);
    }

    @Override
    public String getCountryName() {
        if (CODES_RESOURCE.isEmpty()) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("iso3166_1alpha3-codes.properties");
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
        return object instanceof ISO3166_1Alpha3CountryCode && this.toString().equals(object.toString());
    }

    public static ISO3166_1Alpha3CountryCode parse(String s) throws ParseException {
        try {
            return new ISO3166_1Alpha3CountryCode(s);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }
}

