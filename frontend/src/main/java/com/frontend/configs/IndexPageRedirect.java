package com.frontend.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class IndexPageRedirect implements WebMvcConfigurer {

  @Override
  public void addViewControllers(@SuppressWarnings("null") ViewControllerRegistry registry) {
      // Request from the top of the domain
      registry.addViewController("/")
          .setViewName("forward:/index.xhtml");

      registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
  }
}
