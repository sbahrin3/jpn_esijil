package com.jpn.skd.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.jpn.util.CommonUtil;

public class SKD_PActionLogout extends org.apache.struts.action.Action { 
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,         
			HttpServletRequest request,HttpServletResponse response) throws Exception { 
		
		
		//*** Perubahan di sini
		HttpSession session = request.getSession(false);
		if (session == null){
			session=request.getSession();
		}		
		else {
			String userId = (String) session.getAttribute("user_id");
			if ( userId != null ) {
				SKD_Logger.out(userId);
			}
		}
		session.invalidate();
		//***
		
		return mapping.findForward(CommonUtil.FORWARD); 			
	}
	


}
