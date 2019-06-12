package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Test
	public void contextLoads() {
	}
    @Test
    public void testCal() {
	    int i = 0%3;
	    int j = 1%3;
	    int k = 3%3;
	    System.out.println(i);
	    System.out.println(j);
	    System.out.println(k);
    }


}
