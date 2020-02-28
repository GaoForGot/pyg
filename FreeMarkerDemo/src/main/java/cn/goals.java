package cn;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class goals {
    public static void main(String[] args) throws IOException, TemplateException {
        //1, 初始化配置对象
        Configuration config = new Configuration(Configuration.getVersion());
        //2, 设置编码
        config.setDefaultEncoding("utf-8");
        //3, 设置模板文件路径
        config.setDirectoryForTemplateLoading(new File("C:\\Users\\xiaoz\\IdeaProjects\\pinyougou\\FreeMarkerDemo\\src\\main\\resources"));
        //4, 加载模板
        Template template = config.getTemplate("demo.ftl");
        //5, 初始化输出流对象
        FileWriter fw = new FileWriter("D:\\freemarkerdemo01.html");
        //6, 创建数据模型
        Map map = new HashMap();
        map.put("name", "高晓泽");
        map.put("content", "欢迎来到神奇的程序世界");
        map.put("success", 1);

        List goodsList=new ArrayList();

        Map goods1=new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);
        Map goods2=new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);
        Map goods3=new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);

        goodsList.add(goods1);
        goodsList.add(goods2);
        goodsList.add(goods3);
        map.put("goodsList", goodsList);

        Date today = new Date();
        map.put("today", today);
        map.put("tomorrow", new Date(today.getTime() + 1000 * 60 * 60 * 24));

        map.put("str1", "xxx");
        map.put("str2", "xxx");

        map.put("point", 2131312313);
        //7, 输出
        template.process(map, fw);
        //8, 关闭资源
        fw.close();
    }
}
