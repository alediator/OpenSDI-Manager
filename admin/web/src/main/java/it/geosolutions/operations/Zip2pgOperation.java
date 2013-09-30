/*
 *  OpenSDI Manager
 *  Copyright (C) 2013 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.operations;

import it.geosolutions.geobatch.services.rest.GeoBatchRESTClient;
import it.geosolutions.geobatch.services.rest.RESTFlowService;
import it.geosolutions.geobatch.services.rest.model.RESTRunInfo;
import it.geosolutions.geostore.core.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class Zip2pgOperation implements LocalOperation {
	
	/**
	 * The name of this Operation
	 */
	public static String name = "Zip2pg";
	
	/**
	 * The path were to GET the form and POST the request
	 * Typically all lower case
	 */
	private String path = "zip2pg";
	
	/**
	 * File extension this Operation will work on
	 */
	private String[] extensions = {"zip"};
	
	/**
	 * Directory where to scan for files
	 */
	private String basedirString = "G:/OpenSDIManager/test_shapes/";

	private String geobatchRestUrl = "http://localhost:8081/geobatch/rest/";

	private String geobatchUsername = "admin";

	private String geobatchPassword = "admin";

	/**
	 * Getter
	 * @return the basedirString
	 */
	public String getBasedirString() {
		return basedirString;
	}

	/**
	 * Setter
	 * @param basedirString the basedirString to set
	 */
	public void setBasedirString(String basedirString) {
		this.basedirString = basedirString;
	}
/*
	@RequestMapping(value = "/operation/zip2pg/{fileName:.+}", method = RequestMethod.GET)
	public String zip2pg(@PathVariable(value = "fileName") String fileName, ModelMap model) {
		
		model.addAttribute("fileName", fileName);
		
		return "snipplets/modal/zip2pg";

	}
*/
	/**
	 *  This is the actual Flow Launcher
	 *  It connects to GeoBatch sending the parameters
	 */
	//@RequestMapping(value = "/operation/zip2pg/{fileName:.+}", method = RequestMethod.POST)
	public String zip2pg(@PathVariable(value = "fileName") String fileName,@ModelAttribute("user") User user, ModelMap model) {

		System.out.println("Handling by zip2pg : zip2pg original method");

		String response = "Zip2pg running";
		try {
	        GeoBatchRESTClient client = new GeoBatchRESTClient();
	        client.setGeostoreRestUrl(getGeobatchRestUrl());
	        client.setUsername(getGeobatchUsername());
	        client.setPassword(getGeobatchPassword());
	        
	        // TODO: check ping to GeoBatch (see test)
	        
	        RESTFlowService service = client.getFlowService();
	        RESTRunInfo runInfo = new RESTRunInfo();
	        List<String> flist = new ArrayList<String>();
			flist.add(basedirString+fileName);
	        runInfo.setFileList(flist);
	        
	        // TODO: fastFail or not?
	        response = service.runLocal("ds2ds_zip2pg", true, runInfo);

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("messageType", "error");
			model.addAttribute("notLocalizedMessage", "Couldn't run Zip2pg");
			return "common/messages";

		}
		model.addAttribute("messageType", "success");
		model.addAttribute("notLocalizedMessage", response);

		return "common/messages";

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	@Override
	public String getRESTPath() {
		return getPath();
	}

	@Override
	public List<String> getExtensions() {
		List<String> l = new ArrayList<String>();
		for (String s : extensions) {
			l.add(s);
		}
		return l;
	}

	@Override
	public boolean isMultiple() {
		return false;
	}

	// TODO: This jsp should be placed in a common folder, set in the OperationManager (OperationMapping)
	@Override
	public String getJsp() {
		return "zip2pg";
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the geostoreRestUrl
	 */
	public String getGeobatchRestUrl() {
		return geobatchRestUrl;
	}

	/**
	 * @param geobatchRestUrl the geostoreRestUrl to set
	 */
	public void setGeobatchRestUrl(String geobatchRestUrl) {
		this.geobatchRestUrl = geobatchRestUrl;
	}

	/**
	 * @return the geostoreUsername
	 */
	public String getGeobatchUsername() {
		return geobatchUsername;
	}

	/**
	 * @param geobatchUsername the geostoreUsername to set
	 */
	public void setGeobatchUsername(String geobatchUsername) {
		this.geobatchUsername = geobatchUsername;
	}

	/**
	 * @return the geostorePassword
	 */
	public String getGeobatchPassword() {
		return geobatchPassword;
	}

	/**
	 * @param geobatchPassword the geostorePassword to set
	 */
	public void setGeobatchPassword(String geobatchPassword) {
		this.geobatchPassword = geobatchPassword;
	}

	@Override
	public String getJsp(ModelMap model, HttpServletRequest request, List<MultipartFile> files) {
		
		System.out.println("getJSP di zip2pg");
		
		if(model.containsKey("gotParam"))
			model.addAttribute("fileName", model.get("gotParam"));
		else {
			model.addAttribute("fileName", "dummy.zip");
		}
		//TODO: set model!
		//model.addAttribute("fileName", fileName);
		return "snipplets/modal/zip2pg";

	}

	@Override
	public Object getBlob(Object inputParam) {
		
		// TODO: look for a HttpServletRequest
		String fileName = (String)inputParam;
        RESTRunInfo runInfo = new RESTRunInfo();
        List<String> flist = new ArrayList<String>();
		// TODO: more flexible
        flist.add(basedirString+fileName);
        runInfo.setFileList(flist);
        
		return runInfo;
	}

	@Override
	public String getFlowID() {
		// TODO: parametric!!!
		return "ds2ds_zip2pg";
	}

}