/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Controller
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.ModelAndView
 *  org.springframework.web.servlet.View
 *  org.springframework.web.servlet.view.RedirectView
 */
package com.atlassian.crowd.embedded.admin.list;

import com.atlassian.crowd.embedded.admin.list.NewDirectoryCommand;
import com.atlassian.crowd.embedded.admin.list.NewDirectoryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(value={"/configure/new/**"})
public final class NewDirectoryController {
    private final String redirectPath;

    @Autowired
    public NewDirectoryController(@Qualifier(value="directoriesListTimoutRedirect") String redirectPath) {
        this.redirectPath = redirectPath;
    }

    @RequestMapping(method={RequestMethod.POST})
    public final ModelAndView handle(@ModelAttribute NewDirectoryCommand command) throws Exception {
        NewDirectoryType directoryType = command.getNewDirectoryType();
        if (directoryType == null) {
            return new ModelAndView((View)new RedirectView(this.redirectPath, true));
        }
        return new ModelAndView((View)new RedirectView(directoryType.getFormUrl(), true));
    }
}

