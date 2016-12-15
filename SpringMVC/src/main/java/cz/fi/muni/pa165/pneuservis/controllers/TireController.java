/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fi.muni.pa165.pneuservis.controllers;

import cz.fi.muni.pa165.pneuservis.dto.PersonDTO;
import cz.fi.muni.pa165.pneuservis.dto.TireDTO;
import static cz.fi.muni.pa165.pneuservis.enums.PersonType.EMPLOYEE;
import cz.fi.muni.pa165.pneuservis.enums.TireManufacturer;
import cz.fi.muni.pa165.pneuservis.enums.TireType;
import cz.fi.muni.pa165.pneuservis.facade.PersonFacade;
import cz.fi.muni.pa165.pneuservis.facade.TireFacade;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Matej Sipka
 */
@Controller
@RequestMapping("/tires")
public class TireController {

    private TireDTO tmp;
    final static Logger log = LoggerFactory.getLogger(TireController.class);

    @Autowired
    private TireFacade tireFacade;

    @Autowired
    private PersonFacade personFacade;

    @Autowired
    private HttpSession session;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {

        model = checkForPermissions(model);
        model.addAttribute("tires", tireFacade.findAll());

        return "tires/list";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model) {

        model = checkForPermissions(model);
        model.addAttribute("tireCreate", new TireDTO());
        model.addAttribute("manufacturers", TireManufacturer.values());
        model.addAttribute("seasons", TireType.values());

        return "tires/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute("tireCreate") TireDTO tireDTO,
            BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes,
            UriComponentsBuilder uriBuilder) {
        
        if (bindingResult.hasErrors()) {
            for (ObjectError ge : bindingResult.getGlobalErrors()) {
                log.trace("ObjectError: {}", ge);
            }
            for (FieldError fe : bindingResult.getFieldErrors()) {
                model.addAttribute(fe.getField() + "_error", true);
                log.trace("FieldError: {}", fe);
            }
            return "tires/create";
        }
        
        tireFacade.create(tireDTO);
        int id = tireDTO.getCatalogNumber();
        redirectAttributes.addFlashAttribute("alert_success", "Tire with a catalog number " + id + " was created");
        return "redirect:tires/list";
    }

    private Model checkForPermissions(Model model) {
        PersonDTO person = PersonDTO.class.cast(session.getAttribute("authenticated"));
        if (person != null) {
            if (personFacade.findById(person.getId()).getPersonType() == EMPLOYEE) {
                model.addAttribute("Admin", person.getLogin());
            } else {
                model.addAttribute("User", person.getLogin());
            }
        }

        return model;
    }

}
