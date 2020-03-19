package com.cy;

import com.cy.entity.Student;
import com.cy.mapper.LonLatMapper;
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
    @Autowired
    private LonLatMapper lonLatMapper;
    @Test
    public void findAll(){
        List<Student> list = mapper.findAll();
        System.out.println("list.size:"+list.size());
    }
    @Test
    public void findLonLat(){
        List list = lonLatMapper.findAll();
        System.out.println(list.size());
    }
}
