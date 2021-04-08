package com.szcinda.rpa.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by EalenXie on 2018/6/4 14:09
 * 这里个人示例,可自定义相关属性
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class JobEntity extends BaseEntity {
    private String name;
    private String jobQueue;
    private String cron;
    private String script;
    private String params;
    private String description;
    private String status;
    private String orgId;
}
