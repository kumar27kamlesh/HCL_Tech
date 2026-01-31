package com.hcl.ewallet.merchant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI merchantOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Merchant Service API")
                        .description("E-Wallet Merchant Microservice")
                        .version("1.0"));
    }
}
