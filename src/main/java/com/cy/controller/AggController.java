package com.cy.controller;

import com.cy.base.ApiResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: 𝓛.𝓕.𝓠-0416
 * 聚合分析
 */
@Controller
public class AggController {
    @Autowired
    private RestHighLevelClient esClient;

    @GetMapping("/schoolAgg")
    @ResponseBody
    public ApiResponse schoolAgg(){

        return ApiResponse.of(0,"",0,null);
    }
}
