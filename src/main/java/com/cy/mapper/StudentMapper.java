package com.cy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cy.entity.Student;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * @author: 𝓛.𝓕.𝓠
 */
public interface StudentMapper extends BaseMapper<Student> {
    //计算数据总数
    @Select("select count(id) from stu_info")
    int count();

    @Override
    int insert(Student stu);

    //删除一条数据
    @Delete("delete from stu_info where id=#{id}")
    void delete(String id);

    //查询一个
    @Select({"select * from stu_info where id=#{id}"})
    Student findOne(@Param("id")String id);
    //分页
    @Select({"select * from stu_info limit #{pageNo},#{limitNo}"})
    List<Student> findPages(@Param("pageNo") int pageNo, @Param("limitNo") int limitNo);

    //查找全部
    @Select({"select * from stu_info"})
    List<Student> findAll();

    //精确查找
    @Select("<script>select * from stu_info where 1=1" +
            " <if test='keyword != null'>and yjhm = #{yjhm}</if>" +
            "<if test='start != null'>and rq &gt;=#{start}</if>" +
            "<if test='end != null'>and rq &lt;= #{end}</if> limit #{pageNo},#{limitNo}</script>")
    List<Student> find(@Param("yjhm") String yjhm, @Param("start") Date start, @Param("end") Date end, @Param("pageNo") int pageNo, @Param("limitNo") int limitNo);

    @Select("<script>select * from stu_info where 1=1 " +
            "<if test='yjhm != null'>and yjhm = #{yjhm}</if>" +
            "<if test='start != null'>and rq &gt;=#{start}</if>" +
            "<if test='end != null'>and rq &lt;= #{end}</if></script>")
    List<Student> findBy(@Param("yjhm") String yjhm, @Param("start") Date start, @Param("end") Date end);
}
