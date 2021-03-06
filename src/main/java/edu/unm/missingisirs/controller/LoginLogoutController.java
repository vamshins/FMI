/**
 * 
 */
package edu.unm.missingisirs.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles and retrieves the login or denied page depending on the URI template
 */
@Controller
@RequestMapping("/auth")
@Scope("session")
public class LoginLogoutController {
        
	protected static Logger logger = Logger.getLogger("controller");

	/**
	 * Handles and retrieves the login JSP page
	 * 
	 * @return the name of the JSP page
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String getLoginPage(@RequestParam(value="error", required=false) boolean error, 
			ModelMap model, HttpServletRequest request) {
		
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("cn") != null) {
			logger.info("Session is already present for user - " + session.getAttribute("username"));
			logger.info("SESSION ID : " + session.getId());
			return "uploadfile";
		} else {
			logger.debug("Received request to show login page");
			// Add an error message to the model if login is unsuccessful
			// The 'error' parameter is set to true based on the when the authentication has failed. 
			// We declared this under the authentication-failure-url attribute inside the spring-security.xml
			/* See below:
			 <form-login 
					login-page="/missingisirs/auth/login" 
					authentication-failure-url="/missingisirs/auth/login?error=true" 
					default-target-url="/missingisirs/main/common"/>
			 */
			if (error == true) {
				// Assign an error message
				model.put("error", "You have entered an invalid username or password!");
			} else {
				model.put("error", "");
			}
			// This will resolve to /WEB-INF/jsp/loginpage.jsp
			return "loginpage";
		}
	}
	
	/**
	 * Handles and retrieves the denied JSP page. This is shown whenever a regular user
	 * tries to access an admin only page.
	 * 
	 * @return the name of the JSP page
	 */
	@RequestMapping(value = "/denied", method = RequestMethod.GET)
 	public String getDeniedPage() {
		logger.debug("Received request to show denied page");
		
		// This will resolve to /WEB-INF/jsp/deniedpage.jsp
		return "deniedpage";
	}
	
	@RequestMapping(value = "/auth/logout", method = RequestMethod.GET)
	public String logOut(HttpSession session) {
//		SecurityContextHolder.clearContext();
		session.invalidate();
		return "loginpage";
	}
}