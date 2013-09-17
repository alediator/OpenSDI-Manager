package it.geosolutions.nrl.mvc;

import it.geosolutions.geostore.core.model.User;
import it.geosolutions.geostore.core.model.UserAttribute;
import it.geosolutions.geostore.services.rest.AdministratorGeoStoreClient;
import it.geosolutions.geostore.services.rest.model.RESTUser;
import it.geosolutions.nrl.mvc.model.statistics.FileBrowser;
import it.geosolutions.nrl.utils.ControllerUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FilesController {
	@Autowired
	AdministratorGeoStoreClient geoStoreClient;

	Integer pageSize = 10;

	@RequestMapping(value = "/files", method = RequestMethod.GET)
	public String userList(ModelMap model) {
		//UserList ul = geoStoreClient.getUsers(page, pageSize);
		//UserList ul1 = geoStoreClient.getUsers(page + 1, pageSize);
		/*if(ul.getList().size()>0){
			model.addAttribute("next",page+1);
		}
		if (ul != null) {
			List<RESTUser> users = ul.getList();
			model.addAttribute("users", users);
		}*/
		FileBrowser fb = new FileBrowser();
		fb.setBaseDir("target/surefire-reports");
		fb.setRegex(null);
		model.addAttribute("fileBrowser",fb);	
		ControllerUtils.setCommonModel(model);
		model.addAttribute("context", "files");

		return "template";

	}

	@RequestMapping(value = "/files/create", method = RequestMethod.GET)
	public String createUser(ModelMap model) {
		User user = new User();
		List<UserAttribute> attrs = new ArrayList<UserAttribute>();
		UserAttribute email = new UserAttribute();
		email.setName("email");
		attrs.add(email);
		user.setAttribute(attrs);
		model.addAttribute("user", user);
		model.addAttribute("context", "create");

		return "snipplets/modal/createuser";

	}

	@RequestMapping(value = "/files/create", method = RequestMethod.POST)
	public String createUser(@ModelAttribute("user") User user, ModelMap model) {

		try {
			geoStoreClient.insert(user);
		} catch (Exception e) {
			model.addAttribute("messageType", "error");
			model.addAttribute("notLocalizedMessage", "Couldn't save User");
			return "common/messages";

		}
		model.addAttribute("messageType", "success");
		model.addAttribute("notLocalizedMessage", "User Saved successfully");

		return "common/messages";

	}

	@RequestMapping(value = "/files/edit/{id}", method = RequestMethod.GET)
	public String editUser(@PathVariable(value = "id") Long id, ModelMap model) {
		RESTUser user = geoStoreClient.getUser(id);
		model.addAttribute("user", user);

		return "snipplets/modal/createuser";

	}
	
	@RequestMapping(value = "/files/edit/{id}", method = RequestMethod.POST)
	public String editUser(@PathVariable(value = "id") Long id,@ModelAttribute("user") User user, ModelMap model) {

		try {
			geoStoreClient.update(id,user);
		} catch (Exception e) {
			model.addAttribute("messageType", "error");
			model.addAttribute("notLocalizedMessage", "Couldn't save User");
			return "common/messages";

		}
		model.addAttribute("messageType", "success");
		model.addAttribute("notLocalizedMessage", "User Saved successfully");

		return "common/messages";

	}

	@RequestMapping(value = "/files/delete/{id}", method = RequestMethod.GET)
	public String deleteUser(@PathVariable(value = "id") Long id, ModelMap model) {
		RESTUser user = geoStoreClient.getUser(id);
		model.addAttribute("user", user);
		model.addAttribute("resource",user.getName());

		return "snipplets/modal/confirmdelete";

	}
	@RequestMapping(value = "/files/delete/{id}", method = RequestMethod.POST)
	public String deleteUser(@PathVariable(value = "id") Long id,@ModelAttribute("user") User user, ModelMap model) {

		try {
			geoStoreClient.deleteUser(id);
		} catch (Exception e) {
			model.addAttribute("messageType", "error");
			model.addAttribute("notLocalizedMessage", "Couldn't delete User");
			return "common/messages";

		}
		model.addAttribute("messageType", "success");
		model.addAttribute("notLocalizedMessage", "User Saved successfully");

		return "common/messages";

	}
	
	// GETTERS AND SETTERS
	public AdministratorGeoStoreClient getGeoStoreClient() {
		return geoStoreClient;
	}

	public void setGeoStoreClient(AdministratorGeoStoreClient geoStoreClient) {
		this.geoStoreClient = geoStoreClient;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}