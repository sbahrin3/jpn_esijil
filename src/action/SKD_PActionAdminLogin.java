package com.jpn.skd.action;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.jpn.skd.db.SKD_PDBBundleMgmt;
import com.jpn.skd.db.SKD_PDBLogin;
import com.jpn.skd.db.SKD_PDBRequestCounterMgmt;
import com.jpn.skd.db.SKD_PDBRequestMgmt;
import com.jpn.skd.db.SKD_PDBSemakanHarian;
import com.jpn.skd.form.SKD_PFormLogin;
import com.jpn.skd.jbean.SKD_PJBLogin;
import com.jpn.util.CommonUtil;
import com.jpn.util.CommonUtilData;
import com.jpn.util.DateManipulator;

public class SKD_PActionAdminLogin extends org.apache.struts.action.Action { 
	
	 private static org.apache.log4j.Logger log = Logger.getLogger(SKD_PActionLogin.class);
	
	CommonUtil common = new CommonUtil();
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,         
	HttpServletRequest request,HttpServletResponse response) throws Exception { 
		
		SKD_PFormLogin loginForm = (SKD_PFormLogin) form;
		
		String id = loginForm.getUserId();
		String pwd = loginForm.getUserPwd();
		String selBranchCd = loginForm.getSelBranchCd();
		String selBranchRole = loginForm.getSelBranchRole();

		SKD_PActionLogin login = new SKD_PActionLogin();
		String map = login.directUserPage(mapping, loginForm, request, response);
				
		return mapping.findForward(map); 

	} 

}
