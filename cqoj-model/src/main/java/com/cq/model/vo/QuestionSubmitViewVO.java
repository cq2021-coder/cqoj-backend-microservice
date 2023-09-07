package com.cq.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QuestionSubmitViewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long questionId;
    private String questionTitle;
    /**
     * 编程语言
     */
    private String language;

    @JsonIgnore
    private String judgeInfo;

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 消耗时间（ms）
     */
    private String time;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 创建时间
     */
    private Date createTime;
}
