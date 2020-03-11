package cn.goals.demo;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Consumer {

    @JmsListener(destination = "spring_boot_map")
    public void receive1(Map msg) {
        System.out.println("2: "+msg);
    }

    @JmsListener(destination = "spring_boot_map")
    public void receive2(Map msg) {
        System.out.println("1: "+msg);
    }
}
