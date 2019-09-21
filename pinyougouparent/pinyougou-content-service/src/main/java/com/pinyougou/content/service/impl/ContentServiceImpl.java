package com.pinyougou.content.service.impl;

import java.util.List;

import com.pinyougou.content.service.ContentService;
import entities.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 查询全部
     */
    @Override
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbContent content) {
        contentMapper.insert(content);
        redisTemplate.boundHashOps("contents").delete(content.getCategoryId());
    }


    /**
     * 修改
     */
    @Override
    public void update(TbContent content) {
        TbContent oldContent = contentMapper.selectByPrimaryKey(content.getId());
        contentMapper.updateByPrimaryKey(content);
        long oldCat = oldContent.getCategoryId().longValue();
        long newCat = content.getCategoryId().longValue();

        //如果没更换类别, 即修改前和修改后的类别id相同, 则只需清空旧的类别的缓存
        //无论如何, 发生了改动的广告  所属的类别一定要清空缓存
        redisTemplate.boundHashOps("contents").delete(oldCat);

        //如果更换了类别id, 即修改前和修改后的类别id不同, 也应清空新的类别的缓存
        //也就是一次更新同时修改了两个类别
        if (oldCat != newCat) {
            redisTemplate.boundHashOps("contents").delete(newCat);
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //先把即将被删除的广告对象查出来, 删除掉缓存
            //否则被删除后就得不到这个对象了, 也就得不到他的类别id了, 也就删不了所属类别的缓存了
            TbContent content = contentMapper.selectByPrimaryKey(id);
            redisTemplate.boundHashOps("contents").delete(content.getCategoryId());
            contentMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbContent content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();

        if (content != null) {
            if (content.getTitle() != null && content.getTitle().length() > 0) {
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
            if (content.getUrl() != null && content.getUrl().length() > 0) {
                criteria.andUrlLike("%" + content.getUrl() + "%");
            }
            if (content.getPic() != null && content.getPic().length() > 0) {
                criteria.andPicLike("%" + content.getPic() + "%");
            }
            if (content.getStatus() != null && content.getStatus().length() > 0) {
                criteria.andStatusLike("%" + content.getStatus() + "%");
            }

        }

        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //根据分类id查询广告对象
    @Override
    public List<TbContent> findByCatId(Long catId) {
        //先从缓存中查数据
        List<TbContent> list = (List) redisTemplate.boundHashOps("contents").get(catId);
        if (list == null) {//如果缓存中没有数据
            //从数据库中查数据
            System.out.println("数据库中获取");
            TbContentExample example = new TbContentExample();
            Criteria criteria = example.createCriteria();
            criteria.andCategoryIdEqualTo(catId);
            criteria.andStatusEqualTo("1");
            example.setOrderByClause("sort_order ASC");
            list = contentMapper.selectByExample(example);
            //将数据库中数据存入缓存
            redisTemplate.boundHashOps("contents").put(catId, list);
        } else {
            System.out.println("缓存中获取");
        }
        return list;
    }
}
