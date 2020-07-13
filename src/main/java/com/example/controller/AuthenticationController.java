package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.model.User;
import com.example.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.AuthenticationRequest;
import com.example.model.AuthenticationResponse;
import com.example.model.MyUserDetails;
import com.example.service.JPAUserDetailsService;
import com.example.util.JwtUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/v1")
@CrossOrigin
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private JPAUserDetailsService userDetailsService;

	@PostMapping("/register")
	public ResponseEntity<?> save(@RequestBody User user){
		return ResponseEntity.ok(userDetailsService.save(user));
	}

	@PostMapping(value = "/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
												   HttpServletResponse res) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		//Authentication authenticate =
		System.out.println("mohamed 1");
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				authenticationRequest.getUsername(), authenticationRequest.getPassword())
		);
		System.out.println("mohamed 2");
		//if authentication was successful, else throw an exception
		System.out.println("mohamed 3");
		final MyUserDetails userDetails = (MyUserDetails) userDetailsService
			.loadUserByUsername(authenticationRequest.getUsername());
		System.out.println("mohamed 4");
		final String jwt = jwtTokenUtil.generateToken(userDetails);

		//set the jwt token in cookie (optional)
		// create a cookie
		CookieUtil.create(res,"jwt",jwt,true,7 * 24 * 60 * 60);

		AuthenticationResponse response = new AuthenticationResponse(jwt);

		response.setId(userDetails.getId());
		response.setUsername(userDetails.getUsername());
		List<String> roles = new ArrayList<String>();
		userDetails.getAuthorities().forEach((a) -> roles.add(a.getAuthority()));
		response.setRoles(roles);

		return new ResponseEntity<>(response, responseHeaders, HttpStatus.OK);
	
	}

	@GetMapping(value = "/hello")
	public ResponseEntity<?> greetHello() throws Exception {

	HttpHeaders responseHeaders = new HttpHeaders();
	return new ResponseEntity<>("Hello World", responseHeaders, HttpStatus.OK);

	}
}