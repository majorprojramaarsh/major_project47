package com.lostitem.entities;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.ManyToOne;

@Entity
@Table(name = "Items_info")
public class Items {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int item_id;
	private String item_name;
	private String main_category;
	private String sub_category;
	private String contact_num;
	private String item_image;
	@Column(length = 1000)
	private String item_desc;
	private String contact_email;
	
	@ManyToOne
	@JsonIgnore
	private User user;

	public Items() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getMain_category() {
		return main_category;
	}

	public void setMain_category(String main_category) {
		this.main_category = main_category;
	}

	public String getSub_category() {
		return sub_category;
	}

	public void setSub_category(String sub_category) {
		this.sub_category = sub_category;
	}

	public String getContact_num() {
		return contact_num;
	}

	public void setContact_num(String contact_num) {
		this.contact_num = contact_num;
	}

	public String getItem_image() {
		return item_image;
	}

	public void setItem_image(String item_image) {
		this.item_image = item_image;
	}

	public String getItem_desc() {
		return item_desc;
	}

	public void setItem_desc(String item_desc) {
		this.item_desc = item_desc;
	}

	public String getContact_email() {
		return contact_email;
	}

	public void setContact_email(String contact_email) {
		this.contact_email = contact_email;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.item_id==((Items)obj).getItem_id();
	}

	/*@Override
	public String toString() {
		return "Items [item_id=" + item_id + ", item_name=" + item_name + ", main_category=" + main_category
				+ ", sub_category=" + sub_category + ", contact_num=" + contact_num + ", item_image=" + item_image
				+ ", item_desc=" + item_desc + ", contact_email=" + contact_email + ", user=" + user + "]";
	} */
	
	
	
}
