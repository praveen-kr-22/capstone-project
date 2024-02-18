package com.armorcode.capstone.config;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

public class PaginationConfig implements PageableHandlerMethodArgumentResolverCustomizer {


    @Override
    public void customize(PageableHandlerMethodArgumentResolver customize) {
        customize.setFallbackPageable(PageRequest.of(0, 50));
    }
}
