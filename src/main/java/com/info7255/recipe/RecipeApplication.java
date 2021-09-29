package com.info7255.recipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.info7255.recipe.service","com.info7255.recipe.config","com.info7255.recipe.controller","com.info7255.recipe.model"})
@ComponentScan(basePackages = {"com.info7255.recipe.service","com.info7255.recipe.controller"})
//@ComponentScan(basePackages = {"com.info7255.recipe.service"})
public class RecipeApplication {
	private static ApplicationContext applicationContext;
	public static void main(String[] args) {

		applicationContext=SpringApplication.run(RecipeApplication.class, args);
//		checkBeansPresence("accountController");
//		checkBeansPresence("accountServiceImpl");
//		checkBeansPresence("accountRepository");
//		checkBeansPresence("jedisConnectionFactory");
//		for (String beanName : applicationContext.getBeanDefinitionNames()) {
//			System.out.println(beanName);
//		}
	}
	private static void checkBeansPresence(String... beans) {
		for (String beanName : beans) {
			System.out.println("Is " + beanName + " in ApplicationContext: " +
					applicationContext.containsBean(beanName));
		}
	}
}
