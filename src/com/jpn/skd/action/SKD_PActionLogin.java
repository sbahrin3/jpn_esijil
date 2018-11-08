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

public class SKD_PActionLogin extends org.apache.struts.action.Action { 

	private static org.apache.log4j.Logger log = Logger.getLogger(SKD_PActionLogin.class);

	CommonUtil common = new CommonUtil();

	String role 	= "";
	String role1 	= "";
	String role2 	= "";
	String role3 	= "";
	String userRole = "";
	String userBranch 		= "";
	String userFullName 	= "";
	String userPndhnFlag 	= "";
	String userSubRole 		= "";
	String userSubRoleDtl	= "";
	String userRoleDtl 		= "";
	String userSubRoleApvd1	= "";
	String userSubRoleApvd2	= "";
	String p_tarikhAwal = "";
	String p_tarikhAkhir = "";

	int countNewReq 		= 0;
	int countRegOut 		= 0;
	int countRegIn 			= 0;
	int countNewReqApvd2 	= 0;
	int countNewReqPndhn 	= 0;
	int countReqPndhnOut	= 0;
	int countReqPndhnApvd2	= 0;
	int countRegPndhnIn     = 0;
	int countNewReqBackend 	= 0; 
	int countRegOutBackend 	= 0;
	int countRegInBackend	= 0;
	int countNewBackendReqApvd2 = 0;



	public ActionForward execute(ActionMapping mapping,ActionForm form,         
			HttpServletRequest request,HttpServletResponse response) throws Exception { 

		String map = CommonUtil.FAILED;

		map = directUserPage(mapping, form, request, response);
	
		return mapping.findForward(map); 

	} 

	/**
	 * Dapatkan pengesahan id pengguna dan kata laluan. Jika sah ke paparan laman utama
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String directUserPage(ActionMapping mapping,ActionForm form,         
			HttpServletRequest request,HttpServletResponse response) throws Exception{

		String map = CommonUtil.FAILED; // "failure"

		boolean roleStorPusat = false;
		boolean status = false;

		role1 			= common.getValueForKey("role.stor1");
		role2 			= common.getValueForKey("role.counter");
		role3 			= common.getValueForKey("role.stor3");
		userSubRoleApvd1 	= common.getValueForKey("subrole.pelulus1");
		userSubRoleApvd2 	= common.getValueForKey("subrole.pelulus2");

		SKD_PFormLogin loginForm = (SKD_PFormLogin) form;
		String id = loginForm.getUserId();
		String pwd = loginForm.getUserPwd();
		String selBranchCd = loginForm.getSelBranchCd();
		String selBranchRole = loginForm.getSelBranchRole();

		SKD_PJBLogin loginJB = new SKD_PJBLogin();
		SKD_PDBLogin loginDB = new SKD_PDBLogin();

		BeanUtils.copyProperties(loginJB, loginForm);	
		String encryptPwdFromDB = loginDB.getPassword(id);

		if(encryptPwdFromDB!=null){

			String encryptPwd = CommonUtil.encrypt(pwd, id);

			if(encryptPwd.equals(encryptPwdFromDB)){

				//set user session
				if(selBranchCd.equals("") && selBranchRole.equals("")){

					userRole = CommonUtilData.checkUserRole(id, 1);
					userBranch = CommonUtilData.checkUserBranch(id);

				}else{					

					userRole = selBranchRole;
					userBranch = selBranchCd;

				}

				userFullName = CommonUtilData.getUserFullName(id);
				userPndhnFlag = CommonUtilData.getPndhnFlag(id);
				userSubRole = CommonUtilData.checkUserRole(id, 2);
				userSubRoleDtl = CommonUtilData.getRoleByRoleCode(userSubRole, "2");
				userRoleDtl = CommonUtilData.getRoleByRoleCode(userRole, "1");				

				if(userRole.equals(role1) || userRole.equals(role3)){
					String storType = "";

					storType = common.checkJenisStor(userBranch);

					if(storType.equals("SP")){
						roleStorPusat = true;
					}
				}

				HttpSession userAuth  = request.getSession(true);
				loginForm.setUserId(loginForm.getUserId());
				loginForm.setUserRole(userRole);
				loginForm.setUserBranch(userBranch);
				loginForm.setUserStorPusat(roleStorPusat);
				loginForm.setUserFullName(userFullName);
				loginForm.setUserFullName(userFullName);
				loginForm.setuserSubRoleDtl(userSubRoleDtl);
				loginForm.setuserRoleDtl(userRoleDtl);
				loginForm.setCheckPM(DateManipulator.compareHourMin());
				loginForm.setUserBranchDtl(loginForm.getUserBranch());
				loginForm.setUserSubRole(userSubRole);
				loginForm.setUserpndhnFlag(userPndhnFlag);
				userAuth.setAttribute("auth-user", loginForm);
				
				
				//*** Penambahan kod di sini
				//Simpan user id dalam session
				HttpSession session = request.getSession(false);
				session.setAttribute("user_id", loginForm.getUserId());
				//Simpan Aktiviti Log in
				SKD_Logger.in(loginForm.getUserId());
				//***

				map = displayDashboard(userRole, userBranch, roleStorPusat, userSubRole, p_tarikhAwal, p_tarikhAkhir, userAuth, request);


				if(!map.equals(CommonUtil.FAILED)){

					status = true;
					log.info("user:"+loginForm.getUserId()+" login using branch "+loginForm.getUserBranch());

				}

			}

		}

		if(encryptPwdFromDB==null || status==false){

			ArrayList errList = new ArrayList();
			errList.add("error.invalid.user");
			ActionErrors error = common.getError(request, errList);
			request.setAttribute(String.valueOf(new ActionMessage("view.error.msg")), error);

		}

		return map;

	}

	/**
	 * Untuk paparan laman utama
	 * @param userRole
	 * @param userBranch
	 * @param roleStorPusat
	 * @param userSubRole
	 * @param p_tarikhAwal
	 * @param p_tarikhAkhir
	 * @param userAuth
	 * @param request
	 * @return
	 */
	public String displayDashboard (String userRole, String userBranch, boolean roleStorPusat, String userSubRole, 
			String p_tarikhAwal, String p_tarikhAkhir, HttpSession userAuth, HttpServletRequest request){

		String map = CommonUtil.FAILED;
		SKD_PDBRequestMgmt dbRequest = new SKD_PDBRequestMgmt();

		role1 			= common.getValueForKey("role.stor1");
		role2 			= common.getValueForKey("role.counter");
		role3 			= common.getValueForKey("role.stor3");
		userSubRoleApvd1 	= common.getValueForKey("subrole.pelulus1");
		userSubRoleApvd2 	= common.getValueForKey("subrole.pelulus2");


		HttpSession session = request.getSession(false);
		ArrayList selectAlert = dbRequest.selectAlert(userRole,userBranch, roleStorPusat, userSubRole);
		session.setAttribute("selectAlert", selectAlert);

		int countSelectAlert = selectAlert.size();
		session.setAttribute("selectAlertSize", countSelectAlert);

		if(userRole.equals(role1) || (userRole.equals(role3))){

			SKD_PDBBundleMgmt dbBundle = new SKD_PDBBundleMgmt();
			SKD_PDBRequestCounterMgmt dbCounter = new SKD_PDBRequestCounterMgmt();

			ArrayList registerOutList = dbRequest.displayApprovedList(userBranch, "LULUS", "1", roleStorPusat,1,1, p_tarikhAwal, p_tarikhAkhir);
			ArrayList registerOutListBackend = dbRequest.displayApprovedList(userBranch, "LULUS", "1", roleStorPusat,1,2,p_tarikhAwal,p_tarikhAkhir);
			ArrayList registerOutPndhnList = dbRequest.displayApplPndhn(userBranch,2,2);

			ArrayList registerInList = dbCounter.displayApprovedRequest(userBranch,1, 1, p_tarikhAwal, p_tarikhAkhir);
			ArrayList registerinPndhnList = dbCounter.displayApprovedRequest(userBranch, 4, 1, p_tarikhAwal, p_tarikhAkhir ); // daftar terima pindahan
			ArrayList registerInListBackend = dbCounter.displayApprovedRequest(userBranch,1, 2, p_tarikhAwal, p_tarikhAkhir);

			ArrayList bundleList = dbBundle.displayStatParasBundleLst(userBranch,"MIN");

			//if(userSubRole.equals(userSubRoleApvd1) || userSubRole.equals("")){

			ArrayList newRequestList = dbRequest.displayAppl(userBranch, roleStorPusat,1,1);
			countNewReq = newRequestList.size();

			//count permohonan menunggu kelulusan utk jsn pmhnan=2
			ArrayList newBRequestList = dbRequest.displayAppl(userBranch, roleStorPusat,1,2);
			countNewReqBackend = newBRequestList.size();

			ArrayList pndhnRequestList = dbRequest.displayApplPndhn(userBranch,1,0);
			countNewReqPndhn = pndhnRequestList.size();

			//}

			//if(userSubRole.equals(userSubRoleApvd2) || userSubRole.equals("")){

			ArrayList newRequestListApvd2 = dbRequest.displayAppl(userBranch, roleStorPusat,2,1);
			countNewReqApvd2 = newRequestListApvd2.size();

			ArrayList newBRequestListApvd2 = dbRequest.displayAppl(userBranch, roleStorPusat,2,2);
			countNewBackendReqApvd2 = newBRequestListApvd2.size();

			ArrayList pndhnRequestListApvd2 = dbRequest.displayApplPndhn(userBranch,2,1);
			countReqPndhnApvd2 = pndhnRequestListApvd2.size();

			//	}

			countRegOut = registerOutList.size();
			countRegOutBackend = registerOutListBackend.size();
			countRegIn 	= registerInList.size();
			countRegInBackend 	= registerInListBackend.size();
			countReqPndhnOut = registerOutPndhnList.size();
			countRegPndhnIn = registerinPndhnList.size();

			if(countNewReqPndhn>0 || countReqPndhnApvd2>0 || countReqPndhnOut>0 || countRegPndhnIn>0)
				userAuth.setAttribute("titlePndhn", "(Permohonan Pindahan)");

			session.setAttribute("bundleList", bundleList);
			session.setAttribute("newRequestListSize", countNewReq);
			session.setAttribute("registerOutListSize", countRegOut);					
			session.setAttribute("registerInListSize", countRegIn);
			session.setAttribute("newRequestApvd2ListSize", countNewReqApvd2);
			session.setAttribute("newPndhnListSize", countNewReqPndhn);
			session.setAttribute("registerOutPndhnListSize", countReqPndhnOut);
			session.setAttribute("pndhnReqApvd2ListSize", countReqPndhnApvd2);
			session.setAttribute("registerInPndhnListSize", countRegPndhnIn);
			session.setAttribute("newRequestListSizeBackend", countNewReqBackend);
			session.setAttribute("registerOutListSizeBackend", countRegOutBackend);					
			session.setAttribute("registerInListSizeBackend", countRegInBackend);
			session.setAttribute("newRequestBackendApvd2ListSize", countNewBackendReqApvd2);

			SKD_PActionOptCollections opt = new SKD_PActionOptCollections();
			map= opt.displayMenuList(String.valueOf(CommonUtil.FOUR), CommonUtil.THIRTY_THREE, String.valueOf(CommonUtil.TWELVE), request);


		}else if(userRole.equals(role2)){

			SKD_PDBRequestCounterMgmt dbCounter = new SKD_PDBRequestCounterMgmt();
			SKD_PDBBundleMgmt dbBundle = new SKD_PDBBundleMgmt();

			ArrayList registerInList = dbCounter.displayApprovedRequest(userBranch,1, 1, p_tarikhAwal, p_tarikhAkhir);
			ArrayList bundleList = dbBundle.displayStatParasBundleLst(userBranch,"MIN");

			countRegIn = registerInList.size();

			userAuth.setAttribute("bundleList", bundleList);	
			session.setAttribute("registerInListSize", countRegIn);

			SKD_PActionOptCollections opt = new SKD_PActionOptCollections();
			map= opt.displayMenuList(String.valueOf(CommonUtil.FOUR), CommonUtil.THIRTY_FOUR, String.valueOf(CommonUtil.TWELVE), request);

		}

		return map;


	}

}
