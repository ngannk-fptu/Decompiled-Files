/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RobotDecoratorMapper
extends AbstractDecoratorMapper {
    private String decoratorName = null;
    private static final String[] botHosts = new String[]{"alltheweb.com", "alta-vista.net", "altavista.com", "atext.com", "euroseek.net", "excite.com", "fast-search.net", "google.com", "googlebot.com", "infoseek.co.jp", "infoseek.com", "inktomi.com", "inktomisearch.com", "linuxtoday.com.au", "lycos.com", "lycos.com", "northernlight.com", "pa-x.dec.com"};
    private static final String[] botAgents = new String[]{"acme.spider", "ahoythehomepagefinder", "alkaline", "appie", "arachnophilia", "architext", "aretha", "ariadne", "aspider", "atn.txt", "atomz", "auresys", "backrub", "bigbrother", "bjaaland", "blackwidow", "blindekuh", "bloodhound", "brightnet", "bspider", "cactvschemistryspider", "calif", "cassandra", "cgireader", "checkbot", "churl", "cmc", "collective", "combine", "conceptbot", "core", "cshkust", "cusco", "cyberspyder", "deweb", "dienstspider", "diibot", "direct_hit", "dnabot", "download_express", "dragonbot", "dwcp", "ebiness", "eit", "emacs", "emcspider", "esther", "evliyacelebi", "fdse", "felix", "ferret", "fetchrover", "fido", "finnish", "fireball", "fish", "fouineur", "francoroute", "freecrawl", "funnelweb", "gazz", "gcreep", "getbot", "geturl", "golem", "googlebot", "grapnel", "griffon", "gromit", "gulliver", "hambot", "harvest", "havindex", "hometown", "wired-digital", "htdig", "htmlgobble", "hyperdecontextualizer", "ibm", "iconoclast", "ilse", "imagelock", "incywincy", "informant", "infoseek", "infoseeksidewinder", "infospider", "inspectorwww", "intelliagent", "iron33", "israelisearch", "javabee", "jcrawler", "jeeves", "jobot", "joebot", "jubii", "jumpstation", "katipo", "kdd", "kilroy", "ko_yappo_robot", "labelgrabber.txt", "larbin", "legs", "linkscan", "linkwalker", "lockon", "logo_gif", "lycos", "macworm", "magpie", "mediafox", "merzscope", "meshexplorer", "mindcrawler", "moget", "momspider", "monster", "motor", "muscatferret", "mwdsearch", "myweb", "netcarta", "netmechanic", "netscoop", "newscan-online", "nhse", "nomad", "northstar", "nzexplorer", "occam", "octopus", "orb_search", "packrat", "pageboy", "parasite", "patric", "perignator", "perlcrawler", "phantom", "piltdownman", "pioneer", "pitkow", "pjspider", "pka", "plumtreewebaccessor", "poppi", "portalb", "puu", "python", "raven", "rbse", "resumerobot", "rhcs", "roadrunner", "robbie", "robi", "roverbot", "safetynetrobot", "scooter", "search_au", "searchprocess", "senrigan", "sgscout", "shaggy", "shaihulud", "sift", "simbot", "site-valet", "sitegrabber", "sitetech", "slurp", "smartspider", "snooper", "solbot", "spanner", "speedy", "spider_monkey", "spiderbot", "spiderman", "spry", "ssearcher", "suke", "sven", "tach_bw", "tarantula", "tarspider", "tcl", "techbot", "templeton", "titin", "titan", "tkwww", "tlspider", "ucsd", "udmsearch", "urlck", "valkyrie", "victoria", "visionsearch", "voyager", "vwbot", "w3index", "w3m2", "wanderer", "webbandit", "webcatcher", "webcopy", "webfetcher", "webfoot", "weblayers", "weblinker", "webmirror", "webmoose", "webquest", "webreader", "webreaper", "websnarf", "webspider", "webvac", "webwalk", "webwalker", "webwatch", "wget", "whowhere", "wmir", "wolp", "wombat", "worm", "wwwc", "wz101", "xget", "nederland.zoek"};

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        this.decoratorName = properties.getProperty("decorator");
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator result = null;
        if (this.decoratorName != null && RobotDecoratorMapper.isBot(request)) {
            result = this.getNamedDecorator(request, this.decoratorName);
        }
        return result == null ? super.getDecorator(request, page) : result;
    }

    private static boolean isBot(HttpServletRequest request) {
        String remoteHost;
        if (request == null) {
            return false;
        }
        HttpSession session = request.getSession(true);
        if (Boolean.FALSE.equals(session.getAttribute(RequestConstants.ROBOT))) {
            return false;
        }
        if (Boolean.TRUE.equals(session.getAttribute(RequestConstants.ROBOT))) {
            return true;
        }
        if ("robots.txt".indexOf(request.getRequestURI()) != -1) {
            session.setAttribute(RequestConstants.ROBOT, (Object)Boolean.TRUE);
            return true;
        }
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.trim().length() > 2) {
            if (userAgent.indexOf("MSIE") != -1 || userAgent.indexOf("Gecko") != -1 || userAgent.indexOf("Opera") != -1 || userAgent.indexOf("iCab") != -1 || userAgent.indexOf("Konqueror") != -1 || userAgent.indexOf("KMeleon") != -1 || userAgent.indexOf("4.7") != -1 || userAgent.indexOf("Lynx") != -1) {
                session.setAttribute(RequestConstants.ROBOT, (Object)Boolean.FALSE);
                return false;
            }
            for (int i = 0; i < botAgents.length; ++i) {
                if (userAgent.indexOf(botAgents[i]) == -1) continue;
                session.setAttribute(RequestConstants.ROBOT, (Object)Boolean.TRUE);
                return true;
            }
        }
        if ((remoteHost = request.getRemoteHost()) != null && remoteHost.length() > 0 && remoteHost.charAt(remoteHost.length() - 1) > '@') {
            for (int i = 0; i < botHosts.length; ++i) {
                if (remoteHost.indexOf(botHosts[i]) == -1) continue;
                session.setAttribute(RequestConstants.ROBOT, (Object)Boolean.TRUE);
                return true;
            }
        }
        session.setAttribute(RequestConstants.ROBOT, (Object)Boolean.FALSE);
        return false;
    }
}

