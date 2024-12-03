/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 */
package com.atlassian.confluence.plugins.periodic.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import java.util.Set;

@EventName(value="confluence.periodic.analytics.languages.available")
class PeriodicAvailableLanguagesAnalyticsEvent
implements PeriodicEvent {
    private final Set<String> languages;

    PeriodicAvailableLanguagesAnalyticsEvent(Set<String> languages) {
        this.languages = languages;
    }

    public boolean getAr_SA() {
        return this.languages.contains("ar_SA");
    }

    public boolean getAz_AZ() {
        return this.languages.contains("az_AZ");
    }

    public boolean getBe_BY() {
        return this.languages.contains("be_BY");
    }

    public boolean getBg_BG() {
        return this.languages.contains("Bg_BG");
    }

    public boolean getBs_BA() {
        return this.languages.contains("bs_BA");
    }

    public boolean getCa_ES() {
        return this.languages.contains("ca_ES");
    }

    public boolean getCs_CZ() {
        return this.languages.contains("cs_CZ");
    }

    public boolean getDa_DK() {
        return this.languages.contains("da_DK");
    }

    public boolean getDe_CH() {
        return this.languages.contains("de_CH");
    }

    public boolean getDe_DE() {
        return this.languages.contains("de_DE");
    }

    public boolean getEl_GR() {
        return this.languages.contains("el_GR");
    }

    public boolean getEn_US() {
        return this.languages.contains("en_US");
    }

    public boolean getEs_AR() {
        return this.languages.contains("es_AR");
    }

    public boolean getEs_ES() {
        return this.languages.contains("es_ES");
    }

    public boolean getEs_MX() {
        return this.languages.contains("es_MX");
    }

    public boolean getEs_VE() {
        return this.languages.contains("es_VE");
    }

    public boolean getEt_EE() {
        return this.languages.contains("et_EE");
    }

    public boolean getEu_ES() {
        return this.languages.contains("eu_ES");
    }

    public boolean getFa_IR() {
        return this.languages.contains("fa_IR");
    }

    public boolean getFi_FI() {
        return this.languages.contains("fi_FI");
    }

    public boolean getFr_BE() {
        return this.languages.contains("fr_BE");
    }

    public boolean getFr_CA() {
        return this.languages.contains("fr_CA");
    }

    public boolean getFr_FR() {
        return this.languages.contains("fr_FR");
    }

    public boolean getHi_IN() {
        return this.languages.contains("hi_IN");
    }

    public boolean getHu_HU() {
        return this.languages.contains("hu_HU");
    }

    public boolean getIn_ID() {
        return this.languages.contains("in_ID");
    }

    public boolean getIs_IS() {
        return this.languages.contains("is_IS");
    }

    public boolean getIt_IT() {
        return this.languages.contains("it_IT");
    }

    public boolean getIw_IL() {
        return this.languages.contains("iw_IL");
    }

    public boolean getJa_JP() {
        return this.languages.contains("ja_JP");
    }

    public boolean getKa_GE() {
        return this.languages.contains("ka_GE");
    }

    public boolean getKo_KR() {
        return this.languages.contains("ko_KR");
    }

    public boolean getLt_LT() {
        return this.languages.contains("lt_LT");
    }

    public boolean getLv_LV() {
        return this.languages.contains("lv_LV");
    }

    public boolean getMk_MK() {
        return this.languages.contains("mk_MK");
    }

    public boolean getNl_BE() {
        return this.languages.contains("nl_BE");
    }

    public boolean getNl_NL() {
        return this.languages.contains("nl_NL");
    }

    public boolean getNo_NO() {
        return this.languages.contains("no_NO");
    }

    public boolean getPl_PL() {
        return this.languages.contains("pl_PL");
    }

    public boolean getPt_BR() {
        return this.languages.contains("pt_BR");
    }

    public boolean getPt_PT() {
        return this.languages.contains("pt_PT");
    }

    public boolean getRo_RO() {
        return this.languages.contains("ro_RO");
    }

    public boolean getRu_RU() {
        return this.languages.contains("ru_RU");
    }

    public boolean getSk_SK() {
        return this.languages.contains("sk_SK");
    }

    public boolean getSl_SI() {
        return this.languages.contains("sl_SI");
    }

    public boolean getSr_RS() {
        return this.languages.contains("sr_RS");
    }

    public boolean getSv_SE() {
        return this.languages.contains("sv_SE");
    }

    public boolean getTh_TH() {
        return this.languages.contains("th_TH");
    }

    public boolean getTr_TR() {
        return this.languages.contains("tr_TR");
    }

    public boolean getUk_UA() {
        return this.languages.contains("uk_UA");
    }

    public boolean getVi_VN() {
        return this.languages.contains("vi_VN");
    }

    public boolean getZh_CN() {
        return this.languages.contains("zh_CN");
    }

    public boolean getZh_TW() {
        return this.languages.contains("zh_TW");
    }
}

