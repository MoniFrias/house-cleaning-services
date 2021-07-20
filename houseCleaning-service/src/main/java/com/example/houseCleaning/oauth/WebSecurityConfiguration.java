package com.example.houseCleaning.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration

public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter{

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
	    return super.authenticationManagerBean();
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails userLogin=User.builder().username("login").password(passwEncoder().encode("pass")).
    			roles("LOGIN").build();
		UserDetails userAdmin=User.builder().username("admin").password(passwEncoder().encode("secret")).
    			roles("ADMIN").build();
        return new InMemoryUserDetailsManager(userAdmin,userLogin);
    }

	@Bean
    public PasswordEncoder passwEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

	@Override
	public void configure(WebSecurity web) throws Exception {
		 web.ignoring().antMatchers("/houseCleaning/createAccountCustomer",
				 "/houseCleaning/createAccountEmployee", "/houseCleaning/bookService");
	}	

}
