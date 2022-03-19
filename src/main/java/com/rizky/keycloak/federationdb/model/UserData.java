package com.rizky.keycloak.federationdb.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class UserData extends AbstractUserAdapterFederatedStorage  implements UserModel  {

	private ComponentModel model1;
	private UserDatabase userDb;
	private String firstName,lastName;
	private boolean emailVerified;

	public UserData(KeycloakSession session, RealmModel realm,
			ComponentModel storageProviderModel) {
		super(session, realm, storageProviderModel);
		model1=storageProviderModel;
		// TODO Auto-generated constructor stub
	}

	private String userId,email,userName,role;
	private boolean enabled;


	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}



	@Override
	public List<String> getAttribute(String arg0) {
		
		List<String> list1=new LinkedList<String>();
		list1.add("from External DB");
		return list1;
	}

	@Override
	public Map<String, List<String>> getAttributes() {
		
		Map<String,List<String>>  map1=new HashMap<String, List<String>>();
		List<String> list1=new LinkedList<String>();
		list1.add("from External DB");
		map1.put("attribute1", list1);
		return map1;
	}

	@Override
	public Long getCreatedTimestamp() {
		
		return System.currentTimeMillis();
	}

	
	@Override
	public String getFirstName() {
		
		return firstName;
	}



	@Override
	public String getId() {

		if (storageId == null) {
			storageId = new StorageId(model1.getId(), userId);
		}
		return storageId.getId();

	}

	@Override
	public String getLastName() {
		return lastName;
	}


	@Override
	public String getUsername() {
		
		return userName;
	}

	@Override
	public boolean isEmailVerified() {

		return emailVerified;
	}

	@Override
	public boolean isEnabled() {

		return enabled;
	}


	@Override
	public void setCreatedTimestamp(Long arg0) {
		

	}

	@Override
	public void setEmailVerified(boolean arg0) {
		
		this.emailVerified=arg0;
	}

	@Override
	public void setEnabled(boolean arg0) {
		
		enabled=arg0;

	}


	@Override
	public void setFirstName(String arg0) {
		this.firstName=arg0;

	}

	@Override
	public void setLastName(String arg0) {

		this.lastName=arg0;

	}


	@Override
	public void setSingleAttribute(String arg0, String arg1) {
		

	}

	@Override
	public void setUsername(String arg0) {
		
		userName=arg0;


	}

	@Override
	public String getEmail() {
		
		return email;
	}

	@Override
	public void setEmail(String email) {
		
		this.email=email;

	}



}
