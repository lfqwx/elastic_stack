package com.cy.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cy.search.StudentSuggest;
import lombok.Data;

import java.util.List;

/**
 * @author: 𝓛.𝓕.𝓠
 */
@TableName("stu_info")
@Data
public class Student {
    @TableId(value = "id")
    private String id;
    private String name;
    private String gender;//性别
    private String minzu;//民族
    private String idCard;//身份证
    private String university;//学校名称
    private String professional;
    private String degree;//年级
    private String startYear;
    private String endYear;
    private String addr;//家庭住址
    private String phone;
    public List<StudentSuggest> suggest;
}
