package com.cy;

import com.cy.entity.Student;
import com.cy.mapper.StudentMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author: 𝓛.𝓕.𝓠
 */
public class MapperTest extends ApplicationTests {
    @Autowired
    private StudentMapper mapper;
    @Test
    public void findAll(){
        List<Student> list = mapper.findAll();
        System.out.println("list.size:"+list.size());
    }
}
