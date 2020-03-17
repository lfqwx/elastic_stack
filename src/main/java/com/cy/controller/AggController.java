package com.cy.controller;

import com.cy.base.ApiResponse;
import com.cy.search.AggSchool;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * @author: 𝓛.𝓕.𝓠-0416
 * 聚合分析
 */
@Controller
public class AggController {
    @Autowired
    private RestHighLevelClient esClient;
    @Autowired
    private AggSchool aggSchool;

    @GetMapping("/schoolAgg")
    @ResponseBody
    public ApiResponse schoolAgg(@RequestParam("index") String index) throws IOException {
        Map map = aggSchool.aggSchool(index);
        return ApiResponse.of(0,"",0,map);
    }
}
