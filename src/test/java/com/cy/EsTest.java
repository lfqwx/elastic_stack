package com.cy;

import com.cy.entity.Student;
import com.cy.mapper.StudentMapper;
import com.cy.search.AggSchool;
import com.cy.search.EsSearch;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * @author: 𝓛.𝓕.𝓠
 */
public class EsTest extends ApplicationTests {
    @Autowired
    private EsSearch esSearch;
    @Autowired
    private StudentMapper mapper;
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private AggSchool aggSchool;

    /**
     * 聚合分析
     */
    @Test
    public void aggSchool() throws IOException {
        //aggSchool.initial();
        aggSchool.more("1");
    }


    /**
     * 业务测试
     */


    /**
     * 文档相关操作
     */
    @Test
    public void updateById() throws IOException {
        Student one = mapper.findOne("WH0129160");
        esSearch.update(one,"gender","男");
    }


    @Test
    public void create() throws IOException {
        Student stu = mapper.findOne("WH0129160");
        esSearch.create(stu);
    }

    @Test
    public void createAll() {
        //全部写入索引
        List<Student> list = mapper.findAll();
        list.forEach(obj -> {
            try {
                esSearch.create(obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void update() throws IOException {
        Student stu = mapper.findOne("WH0129160");
        esSearch.update(stu,null,null);
    }

    @Test
    public void delete() throws IOException {
        esSearch.delete("WH0129160");
    }

}
