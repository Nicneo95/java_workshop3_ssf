package com.example.workshop3.controller;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.workshop3.models.Contact;
import com.example.workshop3.util.Contacts;

import jakarta.validation.Valid;

import org.springframework.validation.BindingResult;

@Controller
@RequestMapping(path = "/addressbook")
public class AddressbookController {

    @Autowired
    Contacts ctcz;

    @Autowired
    ApplicationArguments appArgs;

    private String dataDir;

    @GetMapping
    public String showAddressbookForm(Model model) {
        model.addAttribute("contact", new Contact());
        return "addressbook";
    }

    @PostMapping
    public String saveContact(@Valid Contact contact, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            return "addressbook";
        }

        ctcz.saveContact(contact, model, appArgs, dataDir);
        return "showContact";
    }

    @GetMapping("{contactId}")
    public String getContactById(Model model, @PathVariable String contactId) {
        ctcz.getContactById(model, contactId, appArgs, dataDir);
        return "showContact";
    }

    @GetMapping(path = "/list")
    public String getAllContacts(Model model) {
        ctcz.getAllContactInURI(model, appArgs, dataDir);
        return "contacts";
    }
}