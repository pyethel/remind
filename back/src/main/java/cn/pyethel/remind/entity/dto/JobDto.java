package cn.pyethel.remind.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * date: 2020/12/17 23:20
 *
 * @author pyethel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDto {
    @NotBlank(message = "未登录")
    String token;
    @NotBlank(message = "任务时间不能为空")
    String time;
    @NotBlank(message = "任务描述不能为空")
    String input;
}
