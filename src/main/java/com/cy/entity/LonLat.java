package com.cy.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: 𝓛.𝓕.𝓠-0416
 */
@Data
@TableName("lon_lat")
public class LonLat {
    @TableId(value = "id")
    private int id;
    private String school;
    private String lon;//经度
    private String lat;//纬度
    private int rs;
    private int nv;
    private int by;
    private int zy;
}
