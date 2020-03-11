package cn.goals.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldSpring {

    @Autowired
    private Environment env;

    @RequestMapping("/info")
    public String info() {
        return "我操牛逼 " + env.getProperty("url");
    }

    @RequestMapping("/info2")
    public String info2() {
        return "2222222我操牛逼 " + env.getProperty("url");
    }

    @RequestMapping("/info3")
    public String info3() {
        return "223332我操牛逼 " + env.getProperty("url");
    }
}
