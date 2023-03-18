package unit.test.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author guanzhisong
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysPerfLogDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private String logId;
    /**
     * 产品线
     */
    private String product;
    /**
     * 组名
     */
    private String groupName;
    /**
     * 应用名
     */
    private String app;
    /**
     * 类名
     */
    private String clazz;
    /**
     * 方法名
     */
    private String method;
    /**
     * 实例名
     */
    private String instance;
    /**
     * 执行时间
     */
    private Integer executeTimespan;
    /**
     * 方法入参
     */
    private String paramsIn;
    /**
     * 方法出参数
     */
    private String paramsOut;
    /**
     * 调用结果0:成功,-1:系统异常,大于0业务异常
     */
    private Integer code;
    /**
     * 系统异常堆栈信息
     */
    private String errmsg;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date createTime;
}
