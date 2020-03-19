package com.cy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cy.entity.LonLat;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author: 𝓛.𝓕.𝓠-0416
 */
public interface LonLatMapper extends BaseMapper<LonLat> {
    @Select({"select * from lon_lat"})
    List<LonLat> findAll();
}
