package hk.xhy.mate7utils.model;

/**
 * Created by xuhaoyang on 16/7/20.
 */
public class Result<T> {

    /**
     * 结果是否正确
     * @return
     */
    public boolean isSuccess() {
        return this.status != null && this.status.equals("success");
    }

    private int code;
    private T info;
    private String msg;
    private String status;
    private int totalPage;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Result{");
        sb.append("code=").append(code);
        sb.append(", info=").append(info);
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", totalPage=").append(totalPage);
        sb.append('}');
        return sb.toString();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
