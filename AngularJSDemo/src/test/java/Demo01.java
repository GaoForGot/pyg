import java.util.HashMap;
import java.util.Map;

public class Demo01 {
    public static void main(String[] args) {
        Map map = new HashMap();
        String a = "1";
        map.put(1, a);

        Integer b = (Integer) map.get(1);
        System.out.println(b);
        System.out.println(b.getClass());
    }
}
