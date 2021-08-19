/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.console.config;

import com.alibaba.nacos.core.code.ControllerMethodsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;
import java.time.ZoneId;

/**
 * English
 * <p>
 * Console config.
 *
 * <p></p>
 * 中文：
 * <p>
 *     配置类
 * </p>
 * @author yshen
 * @author nkorange
 * @since 1.2.0
 */
@Component
@EnableScheduling
@PropertySource("/application.properties")
public class ConsoleConfig {

    /**
     * naming、console(自身)、config下server相关的包全部缓存在ControllerMethodsCache类的两个ConcurrentHashMap中
     */
    @Autowired
    private ControllerMethodsCache methodsCache;
    
    /**
     * English
     * <p>
     * Init.
     * <p></p>
     * 中文：
     * <p>
     *     初始化
     * </p>
     */
    @PostConstruct
    public void init() {
        methodsCache.initClassMethod("com.alibaba.nacos.core.controller");
        methodsCache.initClassMethod("com.alibaba.nacos.naming.controllers");
        methodsCache.initClassMethod("com.alibaba.nacos.config.server.controller");
        methodsCache.initClassMethod("com.alibaba.nacos.console.controller");
    }

    /**
     * 注册跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        //封装具体跨域信息
        CorsConfiguration config = new CorsConfiguration();
        //允许发送cookie
        config.setAllowCredentials(true);
        //开放所有域
        config.addAllowedOrigin("*");
        //允许所有Http请求中携带的Header信息
        config.addAllowedHeader("*");
        //有效时长
        config.setMaxAge(18000L);
        //允许所有Http请求方法
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //对所有路径实行上述全局跨域访问权限
        source.registerCorsConfiguration("/**", config);
        //注册到CorsFilter中
        return new CorsFilter(source);
    }

    /**
     * 注册全局默认Jackson序列化
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        //设定序列化时区为系统默认时区
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(ZoneId.systemDefault().toString());
    }
}
