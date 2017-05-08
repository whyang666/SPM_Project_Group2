import org.junit.Test;

import java.util.Scanner;

/**
 * Created by win7 on 2017/5/8.
 */
public class test
{
    public static void main(String [] art){
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String a = scanner.nextLine();
            if (!a.matches("[a-zA-Z0-9]{6,16}")) {
                System.out.println("cuo");
            }else{
                System.out.println("dui");
            }
        }


    }
}
