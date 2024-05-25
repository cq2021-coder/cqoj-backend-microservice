package com.cq.question.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cq.model.entity.QuestionSubmit;
import com.cq.model.vo.QuestionSubmitViewVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 程崎
 * @description 针对表【question_submit(题目提交)】的数据库操作Mapper
 * @createDate 2023-08-08 21:24:55
 * @Entity com.cq.cqoj.model.entity.QuestionSubmit
 */
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {

    /**
     * 查询数量
     *
     * @param title    标题
     * @param language 语言
     * @return long
     */
    long countQuestionSubmit(@Param("title") String title, @Param("language") String language);

    List<QuestionSubmitViewVO> selectQuestionSubmit(@Param("title") String title, @Param("language") String language, @Param("pageIndex") long pageIndex, @Param("size") long size);

    List<QuestionSubmitViewVO> selectQuestionSubmitByUserId(@Param("title") String title, @Param("language") String language, @Param("pageIndex") long pageIndex, @Param("size") long size, @Param("userId") Long userId);
}




