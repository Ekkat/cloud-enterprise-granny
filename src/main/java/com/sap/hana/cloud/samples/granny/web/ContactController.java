package com.sap.hana.cloud.samples.granny.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.sap.hana.cloud.samples.granny.model.Address;
import com.sap.hana.cloud.samples.granny.model.Contact;
import com.sap.hana.cloud.samples.granny.model.EmailAddress;
import com.sap.hana.cloud.samples.granny.model.PhoneNumber;
import com.sap.hana.cloud.samples.granny.srv.ContactService;
import com.sap.hana.cloud.samples.granny.srv.ServiceException;
import com.sap.hana.cloud.samples.granny.util.LocaleUtils;

/**
 * Handles requests for the application home page.
 */
@Controller()
@Primary
public class ContactController
{
	/**
	 * The {@link ContactService} to be used.
	 */
	@Autowired
	ContactService contactService;
	
	@ModelAttribute("contactList")
	public List<Contact> getContactList()
	{
		// get all contacts
		return contactService.getAllContactes();
	}
	
	@ModelAttribute("countryList")
	public SortedMap<String, String> getCountryList(Locale locale)
	{
		return LocaleUtils.getCountryList(locale);
	}
	
	/**
	 * Populates the first screen. 
	 * 
	 * @param locale The {@link Locale} associated with this request
	 * @return <code>home</code>
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Locale locale)
	{
		ModelAndView retVal = new ModelAndView("contact");
		
	    return retVal;
	}
	
	/**
	 * Simply dispatches to the "about" page. 
	 * 
	 * @param model The {@link Model} associated with this request
	 * @return <code>about</code>
	 */
	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public ModelAndView about()
	{
		ModelAndView retVal = new ModelAndView("about");
		
	    return retVal;
	}
	
	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public void PJAX(HttpServletResponse response) throws IOException
	{
		response.getWriter().println("PJAX enabled!");
	}
	
	
	
	@RequestMapping(value="/contact", method = RequestMethod.POST, params="save")
	public ModelAndView save(@ModelAttribute("contact") @Valid Contact contact, BindingResult bindingResult, Model model)
	{
		ModelAndView retVal = null;
		
		if (bindingResult.hasErrors())
		{
			retVal = new ModelAndView("contact");
		}
		else
		{
			contact = contactService.saveContact(contact);
			retVal = new ModelAndView("contact");
		}
		
		retVal.addObject(bindingResult);
		retVal.addObject(getContactList());
		retVal.addObject(contact);
		
		return retVal;
	}
	
	@RequestMapping(value="/contact", method = RequestMethod.POST)
	public ModelAndView submit(@ModelAttribute("contact") @Valid Contact contact, BindingResult bindingResult, Model model) 
	{
		return this.save(contact, bindingResult, model);
	}
	
	
	@RequestMapping(value="/contact", method = RequestMethod.POST, params="addAddress")
	public ModelAndView addAddress(@ModelAttribute("contact") Contact contact, BindingResult bindingResult, Model model)
	{
		ModelAndView retVal = new ModelAndView("contact");
		
		
		
		return retVal;
	}
	
	@RequestMapping(value="/contact", method = RequestMethod.POST, params="addPhoneNumber")
	public ModelAndView addPhoneNumber(@ModelAttribute("contact") Contact contact, BindingResult bindingResult, Model model)
	{
		ModelAndView retVal = new ModelAndView("contact");
		
		if (contact.getPhoneNumbers() == null)
		{
			contact.setPhoneNumbers(new ArrayList<PhoneNumber>(1));
		}
		
		contact.getPhoneNumbers().add(new PhoneNumber());
		retVal.addObject(contact);
		
		return retVal;
	}
	
	@RequestMapping(value="/contact", method = RequestMethod.POST, params="deletePhoneNumber")
	public ModelAndView deletePhoneNumber(@ModelAttribute("contact") Contact contact, BindingResult bindingResult, Model model, @RequestParam("deletePhoneNumber") Integer index)
	{
		ModelAndView retVal = new ModelAndView("contact");
		
		if (contact.getPhoneNumbers() != null)
		{
			contact.getPhoneNumbers().remove(index.intValue());
		}
		
		retVal.addObject(contact);
		
		return retVal;
	}
	
	@RequestMapping(value="/contact", method = RequestMethod.POST, params="addEmail")
	public ModelAndView addEmail(@ModelAttribute("contact") Contact contact, BindingResult bindingResult, Model model)
	{
		ModelAndView retVal = new ModelAndView("contact");
		
		if (contact.getEmailAddresses() == null)
		{
			contact.setEmailAddresses(new ArrayList<EmailAddress>(1));
		}
		
		contact.getEmailAddresses().add(new EmailAddress());
		retVal.addObject(contact);
		
		return retVal;
	}
	
	@RequestMapping(value="/contact", method = RequestMethod.POST, params="deleteEmail")
	public ModelAndView deleteEmail(@ModelAttribute("contact") Contact contact, BindingResult bindingResult, Model model, @RequestParam("deleteEmail") Integer index)
	{
		ModelAndView retVal = new ModelAndView("contact");
		
		if (contact.getEmailAddresses() != null)
		{
			contact.getEmailAddresses().remove(index.intValue());
		}
		
		retVal.addObject(contact);
		
		return retVal;
	}
	
	@RequestMapping(value="/contact", method = RequestMethod.POST, params="delete")
	public ModelAndView delete(@ModelAttribute("contact") @Valid Contact contact, BindingResult bindingResult, Model model, @RequestParam("delete") String value)
	{
		ModelAndView retVal = new ModelAndView("contact");
		
		contactService.deleteContact(contact);
		
		// create an empty contact object
		contact = populateContact(new Contact());
		model.addAttribute(contact);
		
		retVal.addObject(getContactList());
		
		return retVal;
	}
            

	@RequestMapping(value = "/contact/{id}", method = RequestMethod.GET)
	// public ModelAndView getById(@RequestParam("id") String id, Model model)
	public ModelAndView getById(@PathVariable String id, Model model)
	{
		ModelAndView retVal = new ModelAndView("contact");
		
		Contact contact = contactService.getContactById(id);
		model.addAttribute(contact);
		
		return retVal;
	}

	/**
	 * Handles a {@link ServiceException} and returns some *meaningful* error message.
	 * 
	 * @param ex The {@link ServiceException} to handle
	 */
	@ExceptionHandler(ServiceException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public  @ResponseBody String handleServiceException(ServiceException ex) 
	{
		// do something really sophisticated here!
		return "We screwed up!";
	}
	
	/**
	 * Populates the specified {@link Contact} with initial (empty) address, email address and phone number
	 * information as required by the web view component. 
	 * 
	 * @param contact The {@link Contact} to populate
	 * @return The populated {@link Contact}
	 */
	@ModelAttribute("contact")
	public static Contact populateContact(Contact contact)
	{
		if (contact == null)
		{
			contact = new Contact();
		}
		
		if (contact.getAddresses() == null || contact.getAddresses().isEmpty())
		{
			Address address = new Address();
			List<Address> addressList = new ArrayList<Address>(1);
			addressList.add(address);
			contact.setAddresses(addressList);
		}
		
		if (contact.getPhoneNumbers() == null || contact.getPhoneNumbers().isEmpty())
		{
			PhoneNumber phone = new PhoneNumber();
			List<PhoneNumber> phoneNumberList = new ArrayList<PhoneNumber>(1);
			phoneNumberList.add(phone);
			contact.setPhoneNumbers(phoneNumberList);
		}
		
		if (contact.getEmailAddresses() == null || contact.getEmailAddresses().isEmpty())
		{
			EmailAddress email = new EmailAddress();
			List<EmailAddress> emailList = new ArrayList<EmailAddress>(1);
			emailList.add(email);
			contact.setEmailAddresses(emailList);
		}
		
		return contact;
	}
}
