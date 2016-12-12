/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.fi.muni.pa165.pneuservis.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Maros Staurovsky
 */
@Controller
@RequestMapping("/logout")
public class logoutController {
    @Autowired 
    private HttpSession session;
    
    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String logout(RedirectAttributes redirect, HttpServletRequest request, HttpServletResponse response) {
        
        session.removeAttribute("authenticated");
        return "redirect:/";
    }
}