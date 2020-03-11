package com.pinyougou.grouppojo;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

public class Specification implements Serializable {

    private TbSpecification specification;
    private List<TbSpecificationOption> options;

    public Specification() {
    }

    public Specification(TbSpecification specification, List<TbSpecificationOption> options) {
        this.specification = specification;
        this.options = options;
    }

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getOptions() {
        return options;
    }

    public void setOptions(List<TbSpecificationOption> options) {
        this.options = options;
    }
}
