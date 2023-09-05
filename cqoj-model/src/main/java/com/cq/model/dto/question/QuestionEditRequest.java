package com.cq.model.dto.question;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 编辑请求
 *
 * @author 程崎
 * @since 2023/08/08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionEditRequest extends QuestionBaseRequest  implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
