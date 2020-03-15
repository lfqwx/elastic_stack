package com.cy.entity;

import com.cy.search.StudentSuggest;
import lombok.Data;

import java.util.List;

/**
 * @author: 𝓛.𝓕.𝓠
 */
@Data
public class IndexTemplate {
    public String id;
    public String name;
    public String gender;
    public String minzu;
    public String university;
    public String professional;
    public String start_year;
    public String end_year;
    public String addr;
    public String degree;
    public String id_card;
    public String phone;
    public List<StudentSuggest> suggests;
}
