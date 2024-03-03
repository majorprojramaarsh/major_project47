package com.lostitem.controller;
import java.io.File;
import java.security.Principal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpSession;

import com.lostitem.dao.ItemRepository;
import com.lostitem.dao.UserRepository;
import com.lostitem.entities.Items;
import com.lostitem.entities.User;
import com.lostitem.helper.Message;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	// method for adding common data to response
		@ModelAttribute
		public void addCommonData(Model model, Principal principal) {
			String userName = principal.getName();
			System.out.println("USERNAME " + userName);

			// get the user using username(Email)

			User user = userRepository.getUserByUserName(userName);
			System.out.println("USER " + user);
			model.addAttribute("user", user);

		}
	
	
	// dash board home
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	
	// open add form handler
	    @GetMapping("/add-item")
		public String openAddItemForm(Model model)
		{
			model.addAttribute("title", "Add an Item");
			model.addAttribute("item", new Items());
			return "normal/add_item_form";
		}
	    
		// processing add item form
	    
		@PostMapping("/process-item")
		public String processItem(@ModelAttribute Items item,@RequestParam("profileImage") MultipartFile file,Principal principal, HttpSession session)
		{
			try
			{
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			
			// processing and uploading file..
			
			if (file.isEmpty())
			{
				// if the file is empty then try our message
				
				System.out.println("File is empty");
				item.setItem_image("packaging.png");
				
			}
			else
			{
				// upload the file to folder and update the name in item
				
				item.setItem_image(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded");
			}
			item.setUser(user);
            user.getItems().add(item);
            this.userRepository.save(user);
            System.out.println("DATA " + item);
			System.out.println("Added !!");
			//successful message.......
			session.setAttribute("message", new Message("Your item has been added successfully !! Add more..", "success"));
			}
			catch (Exception e)
			{
				System.out.println("ERROR " + e.getMessage());
				e.printStackTrace();
				//error message
				session.setAttribute("message", new Message("Something went wrong !! Try again..", "danger"));	
			}
			
			return "normal/add_item_form";
		}
		

		// show items handler
		// per page = 5 items [n]
		// current page = 0 [page]
		@GetMapping("/show-items/{page}")
		public String showItems(@PathVariable("page") Integer page,Model m,Principal principal)
		{
			m.addAttribute("title", "Show listed items");
			
			// items ki list ko bhejni hai
			String userName = principal.getName();
            User user = this.userRepository.getUserByUserName(userName);
            
            // currentPage-page
    		// Contact Per page - 5
            
    		Pageable pageable = PageRequest.of(page, 5);

			Page<Items> items = this.itemRepository.findItemsByUser(user.getId(), pageable);
			m.addAttribute("items", items);
			m.addAttribute("currentPage", page);
			m.addAttribute("totalPages",items.getTotalPages());
			
			return "normal/show_items";
		}
		
		// showing particular item details.
		@GetMapping("/{item_id}/item")
		public String showItemDetail(@PathVariable("item_id") Integer item_id, Model model, Principal principal)
		{
			System.out.println("Item's ID " + item_id);
			
			Optional<Items> itemOptional = this.itemRepository.findById(item_id);
			Items item = itemOptional.get();
			
			String userName = principal.getName();
			User user = this.userRepository.getUserByUserName(userName);
			
			if(user.getId()==item.getUser().getId())
			{
				model.addAttribute("item", item);
				model.addAttribute("title",item.getItem_name());
			}
			
			return "normal/item_detail";
		}

		// delete items handler
		@GetMapping("/delete/{item_id}")
		@Transactional
		public String deleteItem(@PathVariable("item_id") Integer item_id,Model model, HttpSession session,Principal principal)
		{
			System.out.println("Item's ID " + item_id);
			
			Items item = this.itemRepository.findById(item_id).get();
			
			User user = this.userRepository.getUserByUserName(principal.getName());
			
			user.getItems().remove(item);
			
			this.userRepository.save(user);

			System.out.println("DELETED");
			
			session.setAttribute("message", new Message("Item deleted successfully...", "success"));
			
			return "redirect:/user/show-items/0";
		}
		
		// open update form handler
		@PostMapping("/update-item/{item_id}")
		public String updateForm(@PathVariable("item_id") Integer item_id, Model m)
		{
			m.addAttribute("title", "Update Item");
			Items item = this.itemRepository.findById(item_id).get();
			m.addAttribute("item", item);
			return "normal/update_form";
		}
		
		// update items handler
		@RequestMapping(value = "/process-update", method = RequestMethod.POST)
		public String updateHandler(@ModelAttribute Items item, @RequestParam("profileImage") MultipartFile file,Model m, HttpSession session, Principal principal)
		{
			try
			{
				// old item details
				Items olditemDetail = this.itemRepository.findById(item.getItem_id()).get();
				
				// image
				if (!file.isEmpty())
				{
					// file work
					// rewrite
					
					//delete old photo
					File deleteFile = new ClassPathResource("static/img").getFile();
					File file1 = new File(deleteFile,olditemDetail.getItem_image());
					file1.delete();
					
					//update new photo
					
					File saveFile = new ClassPathResource("static/img").getFile();
					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
	                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					item.setItem_image(file.getOriginalFilename());
				}
				else
				{
					item.setItem_image(olditemDetail.getItem_image());
				}
				
				User user = this.userRepository.getUserByUserName(principal.getName());
				item.setUser(user);
				this.itemRepository.save(item);
				session.setAttribute("message", new Message("Your item has been updated successfully...", "success"));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("ITEM NAME " + item.getItem_name());
			System.out.println("ITEM ID " + item.getItem_id());
			return "redirect:/user/"+item.getItem_id()+"/item";
		}
		
		// profile handler
		@GetMapping("/profile")
		public String yourProfile(Model model)
		{
			model.addAttribute("title", "Profile Page");
			return "normal/profile";
		}
		
		// settings handler
		@GetMapping("/settings")
		public String openSettings() {
			return "normal/settings";
		}
		
		// change password..handler
		@PostMapping("/change-password")
		public String changePassword(@RequestParam("oldPassword") String oldPassword,
				@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
			System.out.println("OLD PASSWORD " + oldPassword);
			System.out.println("NEW PASSWORD " + newPassword);

			String userName = principal.getName();
			User currentUser = this.userRepository.getUserByUserName(userName);
			System.out.println(currentUser.getPassword());

			if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
				// change the password

				currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
				this.userRepository.save(currentUser);
				session.setAttribute("message", new Message("Your password is successfully changed..", "success"));

			} else {
				// error...
				session.setAttribute("message", new Message("Please Enter correct old password !!", "danger"));
				return "redirect:/user/settings";
			}

			return "redirect:/user/index";
		}
		
		
}
