package sg.edu.nus.iss.app.day14workshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import sg.edu.nus.iss.app.day14workshop.models.Contact;
import sg.edu.nus.iss.app.day14workshop.service.ContactsRedis;

// controller class for handling web requests related to address book
@Controller
public class AddressBookController {
    
    // service for interacting with the Redis database
    @Autowired
    private ContactsRedis ctcRedisSvc;

    // method to handle requests for contact form page
    @GetMapping (path="/")
    public String contactForm(Model model) {
        // add a new Contact object to the model
        model.addAttribute("contact", new Contact());
        return "contact";
    }

    // method to handle requests for saving a new contact
    @PostMapping("/contact")
    public String saveContact(@Valid Contact contact, BindingResult result, Model model, HttpServletResponse response) {
        // check if there are any validation errors
        // return the "contact" view if there are errors
        if (result.hasErrors()) {
            return "contact";
        }
        // save the contact to the Redis database
        ctcRedisSvc.save(contact);
        // add the saved contact to the model
        model.addAttribute("contact", contact);
        // set the response status to "created" and return the "showContact" view
        response.setStatus(HttpServletResponse.SC_CREATED);
        return "showContact";
    }

    // method to handle requests for retrieving all contacts
    @GetMapping("/contact")
    public String getAllContact(Model model, @RequestParam(name="startIndex") Integer startIndex) {
        // retrieve all contacts from the Redis database
        List<Contact> result = ctcRedisSvc.findAll(startIndex);
        // add the contacts to the model and return the "listContact" view
        model.addAttribute("contacts", result);
        return "listContact";
    }

    // method to handle requests for retrieving contact information by id
    @GetMapping(path="/contact/{contactId}")
    public String getContactInfoById(Model model, @PathVariable(value = "contactId") String contactId) {
        // retrieve the contact by id from the Redis database
        Contact ctc = ctcRedisSvc.findById(contactId);
        // set the id of the contact
        ctc.setId(contactId);
        // add the contact to the model and return the "showContact" view
        model.addAttribute("contact", ctc);
        return "showContact";

    }
}
