
package com.microscope.lensless.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LenslessMicroscopeController {

	@RequestMapping("home")
	public String reconstructController() {
		System.out.println("Hi");
		return "form-picker.html";
	}
}
