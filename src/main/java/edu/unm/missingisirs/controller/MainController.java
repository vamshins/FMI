package edu.unm.missingisirs.controller;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles and retrieves the common or admin page depending on the URI template.
 * A user must be log-in first he can access these pages.  Only the admin can see
 * the adminpage, however.
 */
@Controller
@RequestMapping("/main")
@Scope("session")
public class MainController {

	protected static Logger logger = Logger.getLogger("controller");
	private String username;
	
	/**
	 * Handles and retrieves the common JSP page that everyone can see
	 * 
	 * @return the name of the JSP page
	 */
    @RequestMapping(value = "/common", method = RequestMethod.GET)
    public String getCommonPage(HttpSession session) {
    	logger.debug("Received request to show common page");
    	
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();


	    if (principal instanceof UserDetails) {
	      this.username = ((UserDetails)principal).getUsername();

	    } else {
	      this.username = principal.toString();

	    }
        
	    session.setAttribute("username", username);        
	    session.setAttribute("cn", ((LdapUserDetailsImpl) principal).getDn());
	    
	    logger.info("Session Id : " + session.getId());
	    logger.info("Logged in user : " + session.getAttribute("username"));
	    logger.info("CN : " + session.getAttribute("cn"));
    	// Do your work here. Whatever you like
    	// i.e call a custom service to do your business
    	// Prepare a model to be used by the JSP page
    	
    	// This will resolve to /WEB-INF/jsp/commonpage.jsp
    	return "uploadfile";
	}
    
    /**
     * Handles and retrieves the admin JSP page that only admins can see
     * 
     * @return the name of the JSP page
     */
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAdminPage() {
    	logger.debug("Received request to show admin page");
    
    	// Do your work here. Whatever you like
    	// i.e call a custom service to do your business
    	// Prepare a model to be used by the JSP page
    	
    	// This will resolve to /WEB-INF/jsp/adminpage.jsp
    	return "adminpage";
	}
}
