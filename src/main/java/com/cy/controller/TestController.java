package com.cy.controller;

import com.cy.base.ApiResponse;
import com.cy.entity.Student;
import com.cy.mapper.StudentMapper;
import com.cy.search.EsSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 𝓛.𝓕.𝓠
 */
@Controller
public class TestController {
    @Autowired
    private StudentMapper mapper;
    @Autowired
    private EsSearch esSearch;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello,cy!";
    }

    //查询全部信息
    @GetMapping("/all")
    @ResponseBody
    public ApiResponse all(@RequestParam(value = "page") int page, @RequestParam(value = "limit") int limit) {
        List list = mapper.findPages((page - 1) * limit, limit);
        int count = mapper.findAll().size();
        return ApiResponse.of(0, "", count, list);
    }

    //条件查询信息
    @GetMapping("/find")
    @ResponseBody
    public ApiResponse find(@RequestParam(value = "keyword") String keyword, @RequestParam(value = "page") int page, @RequestParam(value = "limit") int limit) throws IOException {
        List list = esSearch.query(keyword, (page - 1) * limit, limit);
        List list2 = esSearch.query(keyword, 0, mapper.count());
        return ApiResponse.of(0, "", list2.size(), list);
    }
    //自动补全输入框
    @GetMapping("/autocomplete")
    @ResponseBody
    public ApiResponse autcomplete(@RequestParam("keyword") String keyword) throws IOException {
        if (keyword == null || "".equals(keyword)) return ApiResponse.of(0, "", 0, new ArrayList<>());
        List list = esSearch.suggest(keyword);
        return ApiResponse.of(0, "", list.size(), list);
    }

    //更新索引和mysql的数据
    @PostMapping("/update")
    @ResponseBody
    public ApiResponse update(@RequestParam("id") String id, @RequestParam("field") String field, @RequestParam("value") String value) throws IOException {
        Student stu = mapper.findOne(id);
        esSearch.update(stu, field, value);
        return ApiResponse.of(0, "", 0, null);
    }

}
