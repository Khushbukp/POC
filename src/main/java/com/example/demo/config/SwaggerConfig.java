package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static com.google.common.collect.Lists.newArrayList;


@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.demo"))
                .build()
                .apiInfo(apiInfo())
                .globalOperationParameters(newArrayList(new ParameterBuilder()
                        .name("Authorization")
                        .description("Authentication header")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .build(),new ParameterBuilder()
                        .name("X-B3-TraceId")
                        .description("TraceId")
                        .modelRef(new ModelRef("string"))
                        .parameterType("header")
                        .build()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("FDR API - LAW FIRM")
                .description("")
                .contact(new Contact("Freedom Financial Network", "", ""))
                .license("© 2018. All rights reserved. Freedom Debt Relief, LLC")
                .version("1.0")
                .build();
    }
}
