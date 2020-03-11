package entities;

import java.io.Serializable;
import java.util.List;

//  返回分页结果用的bea
//  total是查询结果总数(不只是当前页的数量, 是所有符合查询条件的数据的条数)
//  rows是当前页的数据
public class PageResult implements Serializable {
    private long total;
    private List rows;

    public PageResult() {
    }

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
