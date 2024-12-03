/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.support.SupportInformationService
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Controller
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.ModelAndView
 */
package com.atlassian.crowd.embedded.admin.support;

import com.atlassian.crowd.embedded.admin.util.HtmlEncoder;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.support.SupportInformationService;
import java.util.HashMap;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value={"/support/**"})
public class SupportController {
    private final HtmlEncoder htmlEncoder;
    private final SupportInformationService supportInformationService;
    private final CrowdService crowdService;

    @Autowired
    public SupportController(HtmlEncoder htmlEncoder, SupportInformationService supportInformationService, CrowdService crowdService) {
        this.htmlEncoder = htmlEncoder;
        this.supportInformationService = supportInformationService;
        this.crowdService = crowdService;
    }

    @RequestMapping(value={"directories"}, method={RequestMethod.GET})
    public ModelAndView directories(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap<String, Object> model = new HashMap<String, Object>();
        User user = Optional.ofNullable(request.getRemoteUser()).map(arg_0 -> ((CrowdService)this.crowdService).getUser(arg_0)).orElse(null);
        model.put("supportInformation", this.supportInformationService.getSupportInformation(user));
        model.put("context", model);
        model.put("req", request);
        model.put("htmlEncoder", this.htmlEncoder);
        return new ModelAndView("support-directories", model);
    }

    @RequestMapping(value={"download"}, method={RequestMethod.GET})
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = Optional.ofNullable(request.getRemoteUser()).map(arg_0 -> ((CrowdService)this.crowdService).getUser(arg_0)).orElse(null);
        response.setContentType("text/plain; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=atlassian-directory-configuration.txt");
        response.getWriter().println(this.supportInformationService.getSupportInformation(user));
        response.getWriter().flush();
    }
}

