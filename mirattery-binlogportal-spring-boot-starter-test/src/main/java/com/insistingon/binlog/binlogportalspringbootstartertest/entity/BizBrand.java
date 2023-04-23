package com.insistingon.binlog.binlogportalspringbootstartertest.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BizBrand {

    private Long id;


    private String brandName;


    private String industry;
    /**
     * 企业图标
     */
    private String brandIcon;


    private Integer online;


    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;


    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;


    private Integer deleted;

    /**
     * 录入员
     */
    private String editor;


    private String brandDesc;

    /**
     * 别称
     */
    private String alias;

    private static final long serialVersionUID = 1L;
}
